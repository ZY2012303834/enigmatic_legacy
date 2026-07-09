package org.enigmatic_legacy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 通知客户端播放恶意图腾触发动画。
 *
 * <p>拓展项目的恶意图腾不是只播放服务端粒子，而是在客户端显示图腾举起动画。</p>
 * <p>这个 payload 保存触发坐标，用于客户端在原位置播放图腾音效，同时显示恶意图腾物品激活动画。</p>
 */
public record TotemOfMaliceActivationPayload(double x, double y, double z) implements CustomPacketPayload {
    private static final String CLIENT_EFFECTS_CLASS = "org.enigmatic_legacy.client.TotemOfMaliceClientEffects";
    private static final String CLIENT_EFFECTS_METHOD = "playActivation";

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
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        context.enqueueWork(() -> playClientEffects(payload));
    }

    /**
     * 延迟调用客户端播放逻辑。
     *
     * <p>这个 payload 会在专用服务器启动时注册，因此此类不能直接引用 {@code net.minecraft.client.*}。</p>
     * <p>使用反射可以让专服完全不解析客户端类，同时客户端收到包时仍能调用真实播放逻辑。</p>
     *
     * @param payload 恶意图腾触发包
     */
    private static void playClientEffects(TotemOfMaliceActivationPayload payload) {
        try {
            Class<?> effectsClass = Class.forName(CLIENT_EFFECTS_CLASS);
            Method method = effectsClass.getMethod(CLIENT_EFFECTS_METHOD, double.class, double.class, double.class);
            method.invoke(null, payload.x, payload.y, payload.z);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            EnigmaticLegacy.LOGGER.error("Failed to play Totem of Malice activation effects", exception);
        }
    }
}
