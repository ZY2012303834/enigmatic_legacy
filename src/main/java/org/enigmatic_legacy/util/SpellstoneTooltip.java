package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Tooltip 统一样式工具。
 * 全项目 tooltip 文案统一规则：
 * 1. 普通介绍文字：紫色；
 * 2. 数字、百分比：金色；
 * 3. 负面效果、负面词条、限制说明：红色；
 * 4. 按住 Shift 提示：深灰色；
 * 5. 空行统一使用 tooltip.enigmatic_legacy.void。
 */
public final class SpellstoneTooltip {

    private SpellstoneTooltip() {
    }

    /**
     * 普通介绍文字：紫色。
     */
    public static Component text(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.DARK_PURPLE);
    }

    /**
     * 负面效果 / 负面词条 / 限制说明：红色。
     */
    public static Component negative(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.RED);
    }

    /**
     * 数字：金色。
     */
    public static Component number(Object value) {
        return Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD);
    }

    /**
     * 百分比：金色。
     */
    public static Component percent(Object value) {
        return number(value);
    }

    /**
     * 负面状态效果名称：红色。
     * 示例：
     * SpellstoneTooltip.effect("effect.minecraft.wither")
     */
    public static Component effect(String translationKey) {
        return Component.translatable(translationKey).withStyle(ChatFormatting.RED);
    }

    /**
     * 通用负面词条：红色。
     * 用于“负面效果”“严重负面效果”这类不是原版药水效果名的文本。
     */
    public static Component negativeTerm(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.RED);
    }

    /**
     * 空行。
     */
    public static Component empty() {
        return Component.translatable("tooltip.enigmatic_legacy.void");
    }

    /**
     * 按住 Shift 查看详情。
     */
    public static Component holdShift() {
        return Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                .withStyle(ChatFormatting.DARK_GRAY);
    }
}