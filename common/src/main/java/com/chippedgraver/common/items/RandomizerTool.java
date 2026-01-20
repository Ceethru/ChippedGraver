package com.chippedgraver.common.items;

import com.chippedgraver.common.util.FilterUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RandomizerTool extends Item {
    public RandomizerTool(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Right-click in air opens filter UI (client-side only)
        if (level.isClientSide) {
            // This will be handled by loader-specific client code via a callback
            openFilterScreen(stack);
            return InteractionResultHolder.success(stack);
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    /**
     * Called to open the filter screen. Uses ClientProxy for loader-agnostic access.
     */
    protected void openFilterScreen(ItemStack stack) {
        com.chippedgraver.client.ClientProxy.openFilterScreen(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block currentBlock = state.getBlock();
        
        // Only work on server side
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        
        // Find Chipped tags for this block
        Optional<TagKey<Block>> chippedTag = findChippedTag(level, state);
        
        if (chippedTag.isPresent()) {
            // Get all blocks in this tag group
            Optional<HolderSet.Named<Block>> entryList = level.registryAccess()
                .registry(Registries.BLOCK)
                .flatMap(registry -> registry.getTag(chippedTag.get()));
            
            if (entryList.isPresent() && entryList.get().size() > 1) {
                var registry = level.registryAccess().registry(Registries.BLOCK).orElseThrow();
                ItemStack stack = context.getItemInHand();
                
                // Collect all variant blocks (excluding current one)
                // Use the new filtering logic: if this tag group has filters, only use filtered blocks
                // If this tag group has no filters, use all blocks in the group
                List<Block> variants = entryList.get().stream()
                    .map(Holder::value)
                    .filter(block -> block != currentBlock)
                    .filter(block -> FilterUtil.shouldIncludeBlock(stack, block, entryList.get(), registry))
                    .collect(Collectors.toList());
                
                if (!variants.isEmpty()) {
                    // Pick a random variant
                    Block randomBlock = variants.get(level.random.nextInt(variants.size()));
                    
                    // Try to preserve block properties from the original state
                    BlockState newState = preserveBlockProperties(state, randomBlock.defaultBlockState());
                    
                    // Set the new block state
                    level.setBlock(pos, newState, 3);
                    
                    // Play sound and damage item slightly
                    level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, 
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                    
                    // Damage the tool
                    ItemStack toolStack = context.getItemInHand();
                    if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                        EquipmentSlot slot = context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND 
                            ? EquipmentSlot.MAINHAND 
                            : EquipmentSlot.OFFHAND;
                        toolStack.hurtAndBreak(1, context.getPlayer(), slot);
                    }
                    
                    return InteractionResult.SUCCESS;
                }
            }
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * Finds the Chipped tag associated with this block state.
     * Chipped blocks are tagged with tags like "chipped:oak_planks"
     */
    private Optional<TagKey<Block>> findChippedTag(Level level, BlockState state) {
        var registry = level.registryAccess().registry(Registries.BLOCK);
        if (registry.isEmpty()) {
            return Optional.empty();
        }
        
        // Get all tags for this block using the block's resource key
        var blockKey = registry.get().getResourceKey(state.getBlock());
        if (blockKey.isEmpty()) {
            return Optional.empty();
        }
        
        // Get the holder reference for this block
        var blockRef = registry.get().getHolder(blockKey.get());
        if (blockRef.isEmpty()) {
            return Optional.empty();
        }
        
        // Look for tags in the "chipped" namespace
        return blockRef.get().tags()
            .filter(tag -> tag.location().getNamespace().equals("chipped"))
            .findFirst();
    }
    
    /**
     * Attempts to preserve block properties from the old state to the new state.
     * This helps maintain things like rotation, waterlogged state, etc.
     */
    private BlockState preserveBlockProperties(BlockState oldState, BlockState newState) {
        BlockState result = newState;
        
        // Try to copy compatible properties
        for (Property<?> oldProperty : oldState.getProperties()) {
            Property<?> newProperty = newState.getBlock().getStateDefinition()
                .getProperty(oldProperty.getName());
            
            if (newProperty != null && oldProperty.getClass().equals(newProperty.getClass())) {
                result = copyProperty(oldState, result, oldProperty, newProperty);
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> BlockState copyProperty(
        BlockState oldState, BlockState newState, 
        Property<?> oldProperty, Property<?> newProperty) {
        Property<T> typedOldProperty = (Property<T>) oldProperty;
        Property<T> typedNewProperty = (Property<T>) newProperty;
        return newState.setValue(typedNewProperty, oldState.getValue(typedOldProperty));
    }
}
