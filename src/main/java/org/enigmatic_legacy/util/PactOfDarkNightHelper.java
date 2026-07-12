package org.enigmatic_legacy.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 暗夜契约卷轴工具类。
 *
 * <p>负责集中处理 Curios 检索、七咒资格判断和暗度倍率计算。
 * 这样物品类与事件类都能使用同一套规则，避免 tooltip 和实际效果不一致。</p>
 */
public final class PactOfDarkNightHelper {
    /**
     * 扩展项目中的最低暗度倍率。
     * 即使环境并不完全黑暗，暗夜契约也会提供少量基础效果。
     */
    public static final double MIN_DARK_MODIFIER = 0.4D;

    /**
     * 每降低 1 点亮度额外增加的倍率。
     */
    private static final double DARK_MODIFIER_PER_MISSING_LIGHT = 0.1D;

    /**
     * 原实现使用 raw brightness 参数 8，这里保留该值。
     * 它会让天空光等环境因素参与亮度计算，而不是只看方块光。
     */
    private static final int RAW_BRIGHTNESS_SKY_DARKEN = 8;

    private PactOfDarkNightHelper() {
    }

    public static Optional<ItemStack> findPact(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.NIGHT_SCROLL.get());
    }

    public static boolean hasPact(LivingEntity entity) {
        return entity instanceof Player player
                && CursedRingApi.canUseRestrictedCurio(player, ModItems.NIGHT_SCROLL.get());
    }

    /**
     * 判断暗夜契约是否可以按“暗夜状态”生效。
     *
     * <p>原扩展项目的 isDark 在通过七咒资格后实际上总是返回 true，
     * 具体强弱交给 getDarkModifier 计算。这里保留这一行为，避免明亮环境下完全失效。</p>
     */
    public static boolean isDark(Player player) {
        return hasPact(player);
    }

    /**
     * 计算暗度倍率。
     *
     * <p>有黑暗效果时直接按最高 2.0 处理；否则读取玩家所在位置的原始亮度，
     * 用 0.4 + 0.1 * (16 - rawBrightness) 计算，并夹在 0.4 到 2.0 之间。</p>
     */
    public static double getDarkModifier(Player player) {
        if (player == null || !hasPact(player)) {
            return MIN_DARK_MODIFIER;
        }

        if (player.hasEffect(MobEffects.DARKNESS)) {
            return 2.0D;
        }

        LevelLightEngine lightEngine = player.level().getLightEngine();
        BlockPos blockPos = player.blockPosition();
        int rawBrightness = lightEngine.getRawBrightness(blockPos, RAW_BRIGHTNESS_SKY_DARKEN);
        int missingLight = 16 - rawBrightness;

        return Math.clamp(
                MIN_DARK_MODIFIER + DARK_MODIFIER_PER_MISSING_LIGHT * missingLight,
                MIN_DARK_MODIFIER,
                2.0D
        );
    }
}
