package org.enigmatic_legacy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * 通知客户端播放恶意图腾触发动画。
 *
 * <p>拓展项目的恶意图腾不是只播放服务端粒子，而是在客户端显示图腾举起动画。</p>
 * <p>这个 payload 保存触发坐标，用于客户端在原位置播放图腾音效，同时显示恶意图腾物品激活动画。</p>
 */
public record TotemOfMaliceActivationPayload(double x, double y, double z) implements CustomPacketPayload {
    public static final Type<TotemOfMaliceActivationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "totem_of_malice_activation")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TotemOfMaliceActivationPayload> STREAM_CODEC =
            CustomPacketPayload.codec(TotemOfMaliceActivationPayload::write, TotemOfMaliceActivationPayload::new);

    /**
     * 从网络缓冲区读取恶意图腾触发位置。
     *
     * @param buf 网络缓冲区
     */
    public TotemOfMaliceActivationPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    /**
     * 将恶意图腾触发位置写入网络缓冲区。
     *
     * @param buf 网络缓冲区
     */
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 在客户端播放与拓展项目一致的恶意图腾反馈。
     *
     * <p>反馈包括：跟随玩家的女巫粒子、触发位置的图腾音效、屏幕中央的恶意图腾激活动画。</p>
     *
     * @param payload 恶意图腾触发包
     * @param context 网络上下文
     */
    public static void handle(TotemOfMaliceActivationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null || minecraft.level == null) {
                return;
            }

            minecraft.particleEngine.createTrackingEmitter(player, ParticleTypes.WITCH, 40);
            minecraft.level.playLocalSound(
                    payload.x,
                    payload.y,
                    payload.z,
                    SoundEvents.TOTEM_USE,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F,
                    false
            );
            minecraft.gameRenderer.displayItemActivation(ModItems.TOTEM_OF_MALICE.get().getDefaultInstance());
        });
    }
}
