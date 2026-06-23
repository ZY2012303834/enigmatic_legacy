package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.client.HeartOfCreationClientEffects;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端通知客户端播放创造之心血条保护特效。
 */
public record HeartOfCreationGuardPayload() implements CustomPacketPayload {
    public static final Type<HeartOfCreationGuardPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "heart_of_creation_guard")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HeartOfCreationGuardPayload> STREAM_CODEC =
            StreamCodec.unit(new HeartOfCreationGuardPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HeartOfCreationGuardPayload payload, IPayloadContext context) {
        context.enqueueWork(HeartOfCreationClientEffects::triggerGuardFlash);
    }
}