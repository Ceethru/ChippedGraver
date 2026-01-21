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
            // Find the actual stack in the player's inventory (not a copy) and its slot
            ItemStack actualStack = null;
            int slot = -1;
            for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
                ItemStack slotStack = mc.player.getInventory().getItem(i);
                if (slotStack == stack || (slotStack.getItem() == stack.getItem() && ItemStack.isSameItem(slotStack, stack))) {
                    actualStack = slotStack;
                    slot = i;
                    break;
                }
            }
            
            // If we couldn't find it, use the provided stack (fallback)
            if (actualStack == null) {
                actualStack = stack;
                // Try to find the slot by item type
                for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
                    ItemStack slotStack = mc.player.getInventory().getItem(i);
                    if (slotStack.getItem() == stack.getItem()) {
                        slot = i;
                        break;
                    }
                }
            }
            
            // Create a client-side only menu
            FilterMenu menu = new FilterMenu(0, mc.player.getInventory());
            mc.setScreen(new FilterScreen(menu, mc.player.getInventory(), actualStack, slot));
        }
    }
}
