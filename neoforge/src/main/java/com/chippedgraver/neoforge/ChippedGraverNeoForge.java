package com.chippedgraver.neoforge;

import com.chippedgraver.common.ChippedGraver;
import com.chippedgraver.common.items.RandomizerTool;
import com.chippedgraver.neoforge.network.SyncFilterPacket;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(ChippedGraver.MOD_ID)
public class ChippedGraverNeoForge {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChippedGraver.MOD_ID);
    
    public static final DeferredItem<Item> RANDOMIZER_TOOL = ITEMS.register("randomizer_tool",
        () -> new RandomizerTool(new Item.Properties().stacksTo(1)));
    
    public ChippedGraverNeoForge(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.register(this);
    }
    
    @SubscribeEvent
    public void onRegister(RegisterEvent event) {
        // Set the common reference after registration
        if (event.getRegistryKey() == net.minecraft.core.registries.Registries.ITEM) {
            ChippedGraver.RANDOMIZER_TOOL = RANDOMIZER_TOOL.get();
        }
    }
    
    @SubscribeEvent
    public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ChippedGraver.MOD_ID);
        registrar.playToServer(
            SyncFilterPacket.TYPE,
            SyncFilterPacket.STREAM_CODEC,
            SyncFilterPacket::handle
        );
    }
}
