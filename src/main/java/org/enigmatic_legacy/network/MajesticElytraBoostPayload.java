package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.event.MajesticElytraEvents;
import org.jetbrains.annotations.NotNull;

public record MajesticElytraBoostPayload(boolean boosting) implements CustomPacketPayload {
    public static final Type<MajesticElytraBoostPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "majestic_elytra_boost")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MajesticElytraBoostPayload> STREAM_CODEC =
            CustomPacketPayload.codec(MajesticElytraBoostPayload::write, MajesticElytraBoostPayload::new);

    public MajesticElytraBoostPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(this.boosting);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MajesticElytraBoostPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                MajesticElytraEvents.setBoosting(player, payload.boosting);
            }
        });
    }
}
