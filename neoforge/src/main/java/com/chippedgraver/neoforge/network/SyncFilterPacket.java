package com.chippedgraver.neoforge.network;

import com.chippedgraver.common.ChippedGraver;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFilterPacket(int slotId, CompoundTag filterData) implements CustomPacketPayload {
    public static final Type<SyncFilterPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(ChippedGraver.MOD_ID, "sync_filter")
    );
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SyncFilterPacket::slotId,
        ByteBufCodecs.COMPOUND_TAG, SyncFilterPacket::filterData,
        SyncFilterPacket::new
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(SyncFilterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // Validate slot index
                if (packet.slotId() < 0 || packet.slotId() >= player.getInventory().getContainerSize()) {
                    ChippedGraver.LOGGER.warn("[SyncFilterPacket] Invalid slot index: {}", packet.slotId());
                    return;
                }
                
                ItemStack stack = player.getInventory().getItem(packet.slotId());
                
                // Verify it's our tool
                if (stack.getItem() == ChippedGraver.RANDOMIZER_TOOL) {
                    // Apply the filter data
                    if (packet.filterData() != null && !packet.filterData().isEmpty()) {
                        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(packet.filterData()));
                        ChippedGraver.LOGGER.info("[SyncFilterPacket] Synced filter data to server inventory slot {}", packet.slotId());
                    } else {
                        stack.remove(DataComponents.CUSTOM_DATA);
                        ChippedGraver.LOGGER.info("[SyncFilterPacket] Removed custom data from server inventory slot {}", packet.slotId());
                    }
                } else {
                    ChippedGraver.LOGGER.warn("[SyncFilterPacket] Item in slot {} is not the randomizer tool", packet.slotId());
                }
            }
        });
    }
}
