package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class SpellstoneTooltip {
    private SpellstoneTooltip() {
    }

    // 普通介绍文字：紫色
    public static Component text(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.DARK_PURPLE);
    }

    // 负面效果 / 限制 / 消耗：红色
    public static Component negative(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.RED);
    }

    // 数字 / 百分比：金色
    public static Component number(Object value) {
        return Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD);
    }

    // 百分比，本质也是金色数字
    public static Component percent(Object value) {
        return number(value);
    }

    // 空行
    public static Component empty() {
        return Component.translatable("tooltip.enigmatic_legacy.void");
    }

    // 按住 Shift
    public static Component holdShift() {
        return Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                .withStyle(ChatFormatting.DARK_GRAY);
    }
}