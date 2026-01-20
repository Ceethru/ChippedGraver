package com.chippedgraver.neoforge.client.jei;

import com.chippedgraver.common.ChippedGraver;
import com.chippedgraver.neoforge.client.FilterScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ChippedGraverJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(ChippedGraver.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Register ghost ingredient handler for dragging blocks from JEI
        registration.addGhostIngredientHandler(FilterScreen.class, new FilterScreenGhostHandler());
        
        // Register GUI container handler to define exclusion zones for JEI
        // This ensures JEI doesn't overlap with our filter panel
        registration.addGuiContainerHandler(FilterScreen.class, new IGuiContainerHandler<FilterScreen>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(FilterScreen containerScreen) {
                List<Rect2i> areas = new ArrayList<>();
                // Define the filter panel area so JEI doesn't overlap it
                areas.add(new Rect2i(
                    containerScreen.getGuiLeft(),
                    containerScreen.getGuiTop(),
                    FilterScreen.SCREEN_WIDTH,
                    FilterScreen.SCREEN_HEIGHT
                ));
                return areas;
            }
        });
    }
    
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        // Store the JEI runtime so we can access it later
        JeiHelper.setJeiRuntime(jeiRuntime);
    }
}
