package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 同步被天使之祝加速或反射的弹射物。
 */
public record ForceProjectileRotationPayload(
        int entityId,
        float yRot,
        float xRot,
        double motionX,
        double motionY,
        double motionZ,
        double posX,
        double posY,
        double posZ
) implements CustomPacketPayload {
    public static final Type<ForceProjectileRotationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "force_projectile_rotation")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ForceProjectileRotationPayload> STREAM_CODEC =
            CustomPacketPayload.codec(ForceProjectileRotationPayload::write, ForceProjectileRotationPayload::new);

    public ForceProjectileRotationPayload(RegistryFriendlyByteBuf buf) {
        this(
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.yRot);
        buf.writeFloat(this.xRot);
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
    }

    @Override
    public Type<ForceProjectileRotationPayload> type() {
        return TYPE;
    }

    public static void handle(ForceProjectileRotationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player == null || player.level() == null) {
                return;
            }

            Entity entity = player.level().getEntity(payload.entityId);

            if (entity == null) {
                return;
            }

            entity.moveTo(payload.posX, payload.posY, payload.posZ);
            entity.setDeltaMovement(payload.motionX, payload.motionY, payload.motionZ);
            entity.setYRot(payload.yRot);
            entity.yRotO = payload.yRot;
            entity.setXRot(payload.xRot);
            entity.xRotO = payload.xRot;
        });
    }
}
