package com.chippedgraver.neoforge.client;

import com.chippedgraver.client.ClientProxy;
import com.chippedgraver.common.ChippedGraver;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ChippedGraver.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Override the client proxy to open our screen
        ClientProxy.openFilterScreen = ClientEvents::openFilterScreenImpl;
    }
    
    private static void openFilterScreenImpl(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // Create a client-side only menu
            FilterMenu menu = new FilterMenu(0, mc.player.getInventory());
            mc.setScreen(new FilterScreen(menu, mc.player.getInventory(), stack));
        }
    }
}
