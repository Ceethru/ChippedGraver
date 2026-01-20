package com.chippedgraver.neoforge.client;

import com.chippedgraver.common.util.BlockGroupUtil;
import com.chippedgraver.common.util.FilterUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterScreen extends AbstractContainerScreen<FilterMenu> {
    private final ItemStack toolStack;
    private final List<FilterSlot> filterSlots = new ArrayList<>();
    public static final int SLOT_SIZE = 18;
    public static final int SLOTS_PER_ROW = 9;
    private static final int SLOT_START_X = 8;
    private static final int SLOT_START_Y = 30;
    public static final int SCREEN_WIDTH = 176;
    public static final int SCREEN_HEIGHT = 166;
    
    public FilterScreen(FilterMenu menu, Inventory playerInventory, ItemStack stack) {
        super(menu, playerInventory, Component.translatable("gui.chippedgraver.filter.title"));
        this.toolStack = stack;
        this.imageWidth = SCREEN_WIDTH;
        this.imageHeight = SCREEN_HEIGHT;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // leftPos and topPos are now provided by AbstractContainerScreen
        // Add clear button
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.chippedgraver.filter.clear"),
            button -> {
                FilterUtil.clearFilters(toolStack);
                refreshSlots();
            }
        ).bounds(this.leftPos + SCREEN_WIDTH - 60, this.topPos + 5, 50, 20).build());
        
        refreshSlots();
    }
    
    private void refreshSlots() {
        filterSlots.clear();
        Set<ResourceLocation> filters = FilterUtil.getFilters(toolStack);
        
        if (filters.isEmpty()) {
            return;
        }
        
        // Group filters by source block type
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        
        Map<ResourceLocation, List<ResourceLocation>> groups = BlockGroupUtil.groupBySourceBlock(level, filters);
        
        int slotIndex = 0;
        int currentRow = 0;
        
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> group : groups.entrySet()) {
            ResourceLocation sourceId = group.getKey();
            List<ResourceLocation> variants = group.getValue();
            
            // Start a new row for each group (if not at the start)
            if (slotIndex > 0) {
                // Move to next row
                currentRow++;
                slotIndex = currentRow * SLOTS_PER_ROW;
            }
            
            // Add source block first (always show it, even if not explicitly filtered)
            Block sourceBlock = BuiltInRegistries.BLOCK.get(sourceId);
            if (sourceBlock != null) {
                int x = this.leftPos + SLOT_START_X + (slotIndex % SLOTS_PER_ROW) * SLOT_SIZE;
                int y = this.topPos + SLOT_START_Y + (slotIndex / SLOTS_PER_ROW) * SLOT_SIZE;
                filterSlots.add(new FilterSlot(x, y, sourceBlock, sourceId, true)); // true = is source block
                slotIndex++;
            }
            
            // Add spacer (empty slot with divider)
            if (!variants.isEmpty()) {
                int spacerX = this.leftPos + SLOT_START_X + (slotIndex % SLOTS_PER_ROW) * SLOT_SIZE;
                int spacerY = this.topPos + SLOT_START_Y + (slotIndex / SLOTS_PER_ROW) * SLOT_SIZE;
                filterSlots.add(new FilterSlot(spacerX, spacerY, null, null, false, true)); // true = is spacer
                slotIndex++;
            }
            
            // Add variant blocks (excluding source if it's in the list)
            for (ResourceLocation variantId : variants) {
                // Skip if it's the same as source (don't show duplicates)
                if (variantId.equals(sourceId)) {
                    continue;
                }
                
                Block variantBlock = BuiltInRegistries.BLOCK.get(variantId);
                if (variantBlock != null) {
                    int x = this.leftPos + SLOT_START_X + (slotIndex % SLOTS_PER_ROW) * SLOT_SIZE;
                    int y = this.topPos + SLOT_START_Y + (slotIndex / SLOTS_PER_ROW) * SLOT_SIZE;
                    filterSlots.add(new FilterSlot(x, y, variantBlock, variantId, false)); // false = is variant
                    slotIndex++;
                }
            }
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Draw container background panel (this is rendered on top of the blurred background)
        // Outer border (light gray)
        guiGraphics.fill(this.leftPos - 1, this.topPos - 1, this.leftPos + SCREEN_WIDTH + 1, this.topPos + SCREEN_HEIGHT + 1, 0xFFC6C6C6);
        // Inner panel (dark gray/black)
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + SCREEN_WIDTH, this.topPos + SCREEN_HEIGHT, 0xFF000000);
        // Inner border (slightly lighter)
        guiGraphics.fill(this.leftPos + 1, this.topPos + 1, this.leftPos + SCREEN_WIDTH - 1, this.topPos + SCREEN_HEIGHT - 1, 0xFF2C2C2C);
        
        // Draw filter slots
        for (FilterSlot slot : filterSlots) {
            slot.render(guiGraphics, mouseX, mouseY);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw title with shadow (white text with shadow for visibility)
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF, true);
        
        // Draw instruction text with word wrapping and centering
        Component instruction = Component.translatable("gui.chippedgraver.filter.instruction");
        int textWidth = SCREEN_WIDTH - 16; // Leave 8 pixels margin on each side
        int textY = SCREEN_HEIGHT - 30; // Start a bit higher to leave room for wrapping
        
        // Word wrap the text
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(instruction, textWidth);
        
        // Center the text vertically in the remaining space
        int totalTextHeight = lines.size() * this.font.lineHeight;
        int startY = SCREEN_HEIGHT - 20 - totalTextHeight;
        
        // Draw each line, centered horizontally
        int lineIndex = 0;
        for (net.minecraft.util.FormattedCharSequence line : lines) {
            int lineWidth = this.font.width(line);
            int x = (SCREEN_WIDTH - lineWidth) / 2; // Center horizontally
            int y = startY + (lineIndex * this.font.lineHeight);
            guiGraphics.drawString(this.font, line, x, y, 0xC0C0C0, false);
            lineIndex++;
        }
    }
    
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Use the standard menu background (same dimming and blur as other menus)
        // This will blur the world behind the UI if blur is enabled, but our UI panel (rendered in renderBg) will be on top and not blurred
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the blurred background (this will blur the world behind)
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        // Render the container (bg and labels) - this renders on top, so it won't be blurred
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Check if mouse is over a filter slot and render tooltip
        for (FilterSlot slot : filterSlots) {
            if (slot.isMouseOver(mouseX, mouseY) && !slot.isSpacer && slot.block != null) {
                ItemStack stack = new ItemStack(slot.block.asItem());
                guiGraphics.renderTooltip(this.font, stack, mouseX, mouseY);
                break;
            }
        }
        
        // Render other tooltips (buttons, etc.)
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        // Allow clicking outside to close
        return mouseX < guiLeft || mouseX > guiLeft + SCREEN_WIDTH || 
               mouseY < guiTop || mouseY > guiTop + SCREEN_HEIGHT;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) { // Right click to remove
            for (FilterSlot slot : filterSlots) {
                if (slot.isMouseOver(mouseX, mouseY) && !slot.isSpacer && slot.blockId != null) {
                    FilterUtil.removeFilter(toolStack, slot.blockId);
                    refreshSlots();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public boolean addFilter(ResourceLocation blockId) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        if (block == null) {
            return false;
        }
        
        // Add the variant block to filter
        FilterUtil.addFilter(toolStack, blockId);
        
        // Automatically add the source block if it's a chipped variant
        var sourceBlock = BlockGroupUtil.findSourceBlock(level, block);
        if (sourceBlock.isPresent()) {
            var registry = level.registryAccess().registry(net.minecraft.core.registries.Registries.BLOCK).orElseThrow();
            var sourceKey = registry.getResourceKey(sourceBlock.get());
            if (sourceKey.isPresent() && !sourceKey.get().location().equals(blockId)) {
                // Add source block if it's different from the variant
                FilterUtil.addFilter(toolStack, sourceKey.get().location());
            }
        }
        
        refreshSlots();
        return true;
    }
    
    private class FilterSlot {
        final int x, y;
        final Block block;
        final ResourceLocation blockId;
        final boolean isSourceBlock;
        final boolean isSpacer;
        
        FilterSlot(int x, int y, Block block, ResourceLocation blockId) {
            this(x, y, block, blockId, false, false);
        }
        
        FilterSlot(int x, int y, Block block, ResourceLocation blockId, boolean isSourceBlock) {
            this(x, y, block, blockId, isSourceBlock, false);
        }
        
        FilterSlot(int x, int y, Block block, ResourceLocation blockId, boolean isSourceBlock, boolean isSpacer) {
            this.x = x;
            this.y = y;
            this.block = block;
            this.blockId = blockId;
            this.isSourceBlock = isSourceBlock;
            this.isSpacer = isSpacer;
        }
        
        void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            // Draw slot background
            guiGraphics.fill(x - 1, y - 1, x + SLOT_SIZE + 1, y + SLOT_SIZE + 1, 0xFF8B8B8B);
            guiGraphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF373737);
            
            if (isSpacer) {
                // Draw spacer - just an empty slot with a divider line
                guiGraphics.fill(x + SLOT_SIZE / 2 - 1, y + 2, x + SLOT_SIZE / 2 + 1, y + SLOT_SIZE - 2, 0xFF555555);
            } else if (block != null) {
                // Draw block icon
                ItemStack stack = new ItemStack(block.asItem());
                guiGraphics.renderItem(stack, x + 1, y + 1);
                guiGraphics.renderItemDecorations(FilterScreen.this.font, stack, x + 1, y + 1);
                
                // Highlight source blocks slightly
                if (isSourceBlock) {
                    guiGraphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x40FFFFFF); // Slight white overlay
                }
            }
        }
        
        boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
        }
    }
}
