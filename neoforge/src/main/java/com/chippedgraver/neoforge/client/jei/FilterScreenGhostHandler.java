package com.chippedgraver.neoforge.client.jei;

import com.chippedgraver.neoforge.client.FilterScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class FilterScreenGhostHandler implements IGhostIngredientHandler<FilterScreen> {
    
    @Override
    public <I> List<Target<I>> getTargetsTyped(FilterScreen gui, ITypedIngredient<I> typedIngredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        
        I ingredient = typedIngredient.getIngredient();
        
        // Only handle ItemStack ingredients (blocks as items)
        if (ingredient instanceof ItemStack stack && !stack.isEmpty()) {
            // Get the block from the item
            Block block = Block.byItem(stack.getItem());
            if (block != null) {
                // Create targets for all empty filter slots and existing slots
                // Allow dropping anywhere in the filter area
                // Use the ContainerScreen's leftPos and topPos
                int slotStartX = gui.getGuiLeft() + 8;
                int slotStartY = gui.getGuiTop() + 30;
                
                // Create a target area for the entire filter grid
                Rect2i targetArea = new Rect2i(
                    slotStartX - 1,
                    slotStartY - 1,
                    9 * 18 + 2,  // 9 slots per row * 18 pixels + border
                    6 * 18 + 2   // Allow up to 6 rows
                );
                
                targets.add(new Target<I>() {
                    @Override
                    public Rect2i getArea() {
                        return targetArea;
                    }
                    
                    @Override
                    public void accept(I ingredient) {
                        if (ingredient instanceof ItemStack itemStack) {
                            Block targetBlock = Block.byItem(itemStack.getItem());
                            if (targetBlock != null) {
                                ResourceLocation targetBlockId = BuiltInRegistries.BLOCK.getKey(targetBlock);
                                gui.addFilter(targetBlockId);
                            }
                        }
                    }
                });
            }
        }
        
        return targets;
    }
    
    @Override
    public void onComplete() {
        // Called when drag operation completes
    }
}
