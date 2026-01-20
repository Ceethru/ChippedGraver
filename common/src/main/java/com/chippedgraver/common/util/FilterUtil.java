package com.chippedgraver.common.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for managing block filters stored in ItemStack NBT
 */
public class FilterUtil {
    private static final String FILTER_TAG = "ChippedGraverFilters";
    
    /**
     * Gets the list of filtered block IDs from the ItemStack
     */
    public static Set<ResourceLocation> getFilters(ItemStack stack) {
        Set<ResourceLocation> filters = new HashSet<>();
        
        // Get custom data component (1.21+)
        var customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return filters;
        }
        
        CompoundTag tag = customData.copyTag();
        if (tag == null || !tag.contains(FILTER_TAG)) {
            return filters;
        }
        
        ListTag filterList = tag.getList(FILTER_TAG, 8); // 8 = String tag type
        for (int i = 0; i < filterList.size(); i++) {
            String blockId = filterList.getString(i);
            try {
                filters.add(ResourceLocation.parse(blockId));
            } catch (Exception e) {
                // Invalid resource location, skip it
            }
        }
        
        return filters;
    }
    
    /**
     * Sets the filter list for the ItemStack
     */
    public static void setFilters(ItemStack stack, Set<ResourceLocation> filters) {
        CompoundTag tag = new CompoundTag();
        ListTag filterList = new ListTag();
        
        for (ResourceLocation blockId : filters) {
            filterList.add(StringTag.valueOf(blockId.toString()));
        }
        
        tag.put(FILTER_TAG, filterList);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    
    /**
     * Adds a block to the filter list
     */
    public static void addFilter(ItemStack stack, ResourceLocation blockId) {
        Set<ResourceLocation> filters = getFilters(stack);
        filters.add(blockId);
        setFilters(stack, filters);
    }
    
    /**
     * Removes a block from the filter list
     */
    public static void removeFilter(ItemStack stack, ResourceLocation blockId) {
        Set<ResourceLocation> filters = getFilters(stack);
        filters.remove(blockId);
        setFilters(stack, filters);
    }
    
    /**
     * Clears all filters
     */
    public static void clearFilters(ItemStack stack) {
        // Remove the filter tag from custom data
        var customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            tag.remove(FILTER_TAG);
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
    }
    
    /**
     * Checks if a block is in the filter list
     */
    public static boolean isFiltered(ItemStack stack, Block block, Registry<Block> registry) {
        Set<ResourceLocation> filters = getFilters(stack);
        
        // If no filters, allow all blocks
        if (filters.isEmpty()) {
            return true;
        }
        
        // Check if this block is in the filter list
        ResourceKey<Block> blockKey = registry.getResourceKey(block).orElse(null);
        if (blockKey == null) {
            return false;
        }
        
        return filters.contains(blockKey.location());
    }
    
    /**
     * Checks if any blocks in the given tag group are in the filter list
     * This is used to determine if we should filter a specific block type
     */
    public static boolean hasFiltersForTag(ItemStack stack, net.minecraft.core.HolderSet<Block> tagBlocks, Registry<Block> registry) {
        Set<ResourceLocation> filters = getFilters(stack);
        
        if (filters.isEmpty()) {
            return false;
        }
        
        // Check if any block in this tag group is in the filter
        for (net.minecraft.core.Holder<Block> holder : tagBlocks) {
            ResourceKey<Block> blockKey = holder.unwrapKey().orElse(null);
            if (blockKey != null && filters.contains(blockKey.location())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a block should be included when randomizing.
     * If the tag group has filters, only include blocks that are in the filter.
     * If the tag group has no filters, include all blocks.
     */
    public static boolean shouldIncludeBlock(ItemStack stack, Block block, net.minecraft.core.HolderSet<Block> tagBlocks, Registry<Block> registry) {
        Set<ResourceLocation> filters = getFilters(stack);
        
        // If no filters at all, allow all blocks
        if (filters.isEmpty()) {
            return true;
        }
        
        // Check if this tag group has any filters
        boolean tagHasFilters = hasFiltersForTag(stack, tagBlocks, registry);
        
        if (!tagHasFilters) {
            // This tag group has no filters, so allow all blocks in this group
            return true;
        }
        
        // This tag group has filters, so only allow blocks that are in the filter
        ResourceKey<Block> blockKey = registry.getResourceKey(block).orElse(null);
        if (blockKey == null) {
            return false;
        }
        
        return filters.contains(blockKey.location());
    }
}
