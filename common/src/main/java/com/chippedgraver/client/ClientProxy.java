package com.chippedgraver.client;

import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;

/**
 * Client-side proxy for opening the filter screen.
 * Implementation is provided by loader-specific client code.
 */
public class ClientProxy {
    public static Consumer<ItemStack> openFilterScreen = (stack) -> {
        // Default implementation - overridden in client-side code
    };
    
    public static void openFilterScreen(ItemStack stack) {
        openFilterScreen.accept(stack);
    }
}
