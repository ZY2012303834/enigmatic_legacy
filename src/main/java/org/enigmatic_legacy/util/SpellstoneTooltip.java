package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Tooltip 统一样式工具。
 * 统一规则：
 * 1. 普通介绍文字：紫色；
 * 2. 数字 / 百分比：金色；
 * 3. 负面效果 / 限制 / 惩罚：红色；
 * 4. 按住 Shift：深灰色；
 * 5. 空行统一走 tooltip.enigmatic_legacy.void。
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
     * 负面效果、限制、惩罚：红色。
     */
    public static Component negative(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.RED);
    }

    /**
     * 数字 / 数值：金色。
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
     * 药水效果 / 负面状态效果名称：红色。
     * 示例：
     * SpellstoneTooltip.effect("effect.minecraft.weakness")
     */
    public static Component effect(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.RED);
    }

    /**
     * 通用负面词条：红色。
     * 用于“负面效果”“严重负面效果”这类不是具体药水效果名的文本。
     * 示例：
     * SpellstoneTooltip.negativeTerm("tooltip.enigmatic_legacy.term.negative_effects")
     * SpellstoneTooltip.negativeTerm("tooltip.enigmatic_legacy.term.severe_negative_effects")
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
     * 项目统一规则：
     * Shift 提示文字使用金色。
     */
    public static Component holdShift() {
        return Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                .withStyle(ChatFormatting.GOLD);
    }
}