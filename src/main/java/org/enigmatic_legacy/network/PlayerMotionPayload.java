package org.enigmatic_legacy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 服务端强制同步玩家运动。
 */
public record PlayerMotionPayload(double x, double y, double z) implements CustomPacketPayload {
    public static final Type<PlayerMotionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "player_motion")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerMotionPayload> STREAM_CODEC =
            CustomPacketPayload.codec(PlayerMotionPayload::write, PlayerMotionPayload::new);

    public PlayerMotionPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public Type<PlayerMotionPayload> type() {
        return TYPE;
    }

    public static void handle(PlayerMotionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.setDeltaMovement(
                        new Vec3(payload.x, payload.y, payload.z)
                );
            }
        });
    }
}