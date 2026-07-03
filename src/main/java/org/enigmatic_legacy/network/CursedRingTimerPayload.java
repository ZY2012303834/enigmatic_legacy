package org.enigmatic_legacy.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

/**
 * 同步七咒之戒佩戴时间给客户端 tooltip 使用。
 */
public record CursedRingTimerPayload(long cursedPlayTime, long totalPlayTime) implements CustomPacketPayload {
    public static final String CLIENT_TOTAL_PLAY_TIME_TAG = "enigmatic_legacy_client_total_play_time";

    public static final Type<CursedRingTimerPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "cursed_ring_timer")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CursedRingTimerPayload> STREAM_CODEC =
            CustomPacketPayload.codec(CursedRingTimerPayload::write, CursedRingTimerPayload::new);

    public CursedRingTimerPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readLong(), buf.readLong());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeLong(this.cursedPlayTime);
        buf.writeLong(this.totalPlayTime);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CursedRingTimerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player == null) {
                return;
            }

            CompoundTag data = player.getPersistentData();
            data.putLong("enigmatic_legacy_cursed_play_time", payload.cursedPlayTime);
            data.putLong(CLIENT_TOTAL_PLAY_TIME_TAG, payload.totalPlayTime);
        });
    }
}
