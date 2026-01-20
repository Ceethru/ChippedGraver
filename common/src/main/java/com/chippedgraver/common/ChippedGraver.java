package com.chippedgraver.common;

import com.chippedgraver.common.items.RandomizerTool;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ChippedGraver {
    public static final String MOD_ID = "chippedgraver";
    
    public static Item RANDOMIZER_TOOL;
    
    public static void init() {
        RANDOMIZER_TOOL = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "randomizer_tool"),
            new RandomizerTool(new Item.Properties().stacksTo(1).durability(256))
        );
    }
}
