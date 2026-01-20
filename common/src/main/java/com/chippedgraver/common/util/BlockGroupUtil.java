package com.chippedgraver.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;

import java.util.*;

/**
 * Utility class for finding source blocks and grouping Chipped blocks
 */
public class BlockGroupUtil {
    
    /**
     * Finds the source Minecraft block for a given block by looking at its Chipped tag.
     * The source block is typically the one from the "minecraft" namespace in the tag.
     */
    public static Optional<Block> findSourceBlock(Level level, Block block) {
        var registry = level.registryAccess().registry(Registries.BLOCK);
        if (registry.isEmpty()) {
            return Optional.empty();
        }
        
        // Get the block's resource key
        var blockKey = registry.get().getResourceKey(block);
        if (blockKey.isEmpty()) {
            return Optional.empty();
        }
        
        // Get the holder reference
        var blockRef = registry.get().getHolder(blockKey.get());
        if (blockRef.isEmpty()) {
            return Optional.empty();
        }
        
        // Find the Chipped tag
        Optional<TagKey<Block>> chippedTag = blockRef.get().tags()
            .filter(tag -> tag.location().getNamespace().equals("chipped"))
            .findFirst();
        
        if (chippedTag.isEmpty()) {
            return Optional.empty();
        }
        
        // Get all blocks in the tag
        Optional<HolderSet.Named<Block>> tagSet = registry.get().getTag(chippedTag.get());
        if (tagSet.isEmpty()) {
            return Optional.empty();
        }
        
        // Find the source block (from "minecraft" namespace)
        for (Holder<Block> holder : tagSet.get()) {
            ResourceKey<Block> key = holder.unwrapKey().orElse(null);
            if (key != null && key.location().getNamespace().equals("minecraft")) {
                return Optional.of(holder.value());
            }
        }
        
        // If no minecraft block found, return the first block in the tag (fallback)
        if (tagSet.get().size() > 0) {
            return Optional.of(tagSet.get().iterator().next().value());
        }
        
        return Optional.empty();
    }
    
    /**
     * Groups block IDs by their source block type.
     * Returns a map where the key is the source block ID and the value is a list of variant block IDs.
     */
    public static Map<ResourceLocation, List<ResourceLocation>> groupBySourceBlock(
            Level level, Set<ResourceLocation> blockIds) {
        Map<ResourceLocation, List<ResourceLocation>> groups = new LinkedHashMap<>();
        var registry = level.registryAccess().registry(Registries.BLOCK);
        
        if (registry.isEmpty()) {
            return groups;
        }
        
        for (ResourceLocation blockId : blockIds) {
            Block block = registry.get().get(blockId);
            if (block == null) {
                continue;
            }
            
            Optional<Block> sourceBlock = findSourceBlock(level, block);
            ResourceLocation sourceId;
            
            if (sourceBlock.isPresent()) {
                ResourceKey<Block> sourceKey = registry.get().getResourceKey(sourceBlock.get()).orElse(null);
                if (sourceKey != null) {
                    sourceId = sourceKey.location();
                } else {
                    // Fallback: use the block itself as source
                    sourceId = blockId;
                }
            } else {
                // No source block found, check if this is already a minecraft block
                if (blockId.getNamespace().equals("minecraft")) {
                    sourceId = blockId;
                } else {
                    // Not a minecraft block and no source found, use the block itself
                    sourceId = blockId;
                }
            }
            
            // Add to group (only add variants, not the source itself if it's already the source)
            if (!blockId.equals(sourceId)) {
                groups.computeIfAbsent(sourceId, k -> new ArrayList<>()).add(blockId);
            } else {
                // This is the source block itself, create group if it doesn't exist
                groups.computeIfAbsent(sourceId, k -> new ArrayList<>());
            }
        }
        
        return groups;
    }
}
