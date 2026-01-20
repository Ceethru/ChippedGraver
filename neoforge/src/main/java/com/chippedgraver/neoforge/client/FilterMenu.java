package com.chippedgraver.neoforge.client;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * A simple client-side only menu for the filter screen.
 * This allows us to use ContainerScreen which makes JEI automatically show.
 */
public class FilterMenu extends AbstractContainerMenu {
    public FilterMenu(int containerId, Inventory playerInventory) {
        // Use a simple menu type - we don't need actual slots since this is client-side only
        super(null, containerId);
    }
    
    @Override
    public boolean stillValid(Player player) {
        // Always valid since this is client-side only
        return true;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No slots to move items, so return empty
        return ItemStack.EMPTY;
    }
}
