package com.chippedgraver.common;

import com.chippedgraver.common.items.RandomizerTool;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChippedGraver {
    public static final String MOD_ID = "chippedgraver";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static Item RANDOMIZER_TOOL;
    
    public static void init() {
        RANDOMIZER_TOOL = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "randomizer_tool"),
            new RandomizerTool(new Item.Properties().stacksTo(1))
        );
    }
}
