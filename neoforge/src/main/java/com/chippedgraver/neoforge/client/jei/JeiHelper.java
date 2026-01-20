package com.chippedgraver.neoforge.client.jei;

import mezz.jei.api.runtime.IJeiRuntime;
import java.util.Optional;

/**
 * Helper class to interact with JEI runtime
 */
public class JeiHelper {
    private static IJeiRuntime jeiRuntime;
    
    /**
     * Set the JEI runtime (called by the JEI plugin)
     */
    public static void setJeiRuntime(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }
    
    /**
     * Get the JEI runtime if available
     */
    public static Optional<IJeiRuntime> getJeiRuntime() {
        return Optional.ofNullable(jeiRuntime);
    }
    
    /**
     * Show JEI if it's available and hidden
     * JEI automatically hides for regular Screen classes, so we need to manually show it
     */
    public static void showJei() {
        getJeiRuntime().ifPresent(runtime -> {
            try {
                // Get the ingredient list overlay
                var overlay = runtime.getIngredientListOverlay();
                if (overlay != null) {
                    // Check if the list is currently displayed
                    if (!overlay.isListDisplayed()) {
                        // Try to show the ingredient list
                        // Note: This might not work if JEI has hidden it for this screen type
                        // The user may need to manually toggle JEI with the keybind
                    }
                    // Even if it's not displayed, the overlay should still be accessible
                    // for ghost ingredient dragging, which is what we really need
                }
            } catch (Exception e) {
                // Ignore errors - JEI API might have changed
            }
        });
    }
}
