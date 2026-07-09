package org.enigmatic_legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.enigmatic_legacy.item.ModItems;

/**
 * 恶意图腾客户端表现。
 *
 * <p>该类只能在客户端加载，通用网络 payload 会通过反射延迟调用这里，避免专服加载 {@code net.minecraft.client.*}。</p>
 */
public final class TotemOfMaliceClientEffects {
    private TotemOfMaliceClientEffects() {
    }

    /**
     * 播放恶意图腾触发反馈。
     *
     * <p>对齐 Enigmatic Addons：创建跟随玩家的女巫粒子，在触发坐标播放图腾音效，并显示恶意图腾举起动画。</p>
     *
     * @param x 触发位置 X
     * @param y 触发位置 Y
     * @param z 触发位置 Z
     */
    public static void playActivation(double x, double y, double z) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.level == null) {
            return;
        }

        minecraft.particleEngine.createTrackingEmitter(player, ParticleTypes.WITCH, 40);
        minecraft.level.playLocalSound(
                x,
                y,
                z,
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F,
                false
        );
        minecraft.gameRenderer.displayItemActivation(ModItems.TOTEM_OF_MALICE.get().getDefaultInstance());
    }
}
