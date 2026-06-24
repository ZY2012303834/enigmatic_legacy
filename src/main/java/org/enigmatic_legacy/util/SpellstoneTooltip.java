package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class SpellstoneTooltip {
    private SpellstoneTooltip() {
    }

    public static Component text(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.DARK_PURPLE);
    }

    public static Component negative(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.RED);
    }

    public static Component number(Object value) {
        return Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD);
    }
}
