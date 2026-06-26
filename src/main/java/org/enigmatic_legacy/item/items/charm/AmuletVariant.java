package org.enigmatic_legacy.item.items.charm;

import net.minecraft.ChatFormatting;

public enum AmuletVariant {
    RED("red", ChatFormatting.RED),
    AQUA("aqua", ChatFormatting.AQUA),
    VIOLET("violet", ChatFormatting.DARK_PURPLE),
    MAGENTA("magenta", ChatFormatting.LIGHT_PURPLE),
    GREEN("green", ChatFormatting.GREEN),
    BLACK("black", ChatFormatting.DARK_GRAY),
    BLUE("blue", ChatFormatting.BLUE);

    private final String id;
    private final ChatFormatting color;

    AmuletVariant(String id, ChatFormatting color) {
        this.id = id;
        this.color = color;
    }

    public String id() {
        return id;
    }

    public ChatFormatting color() {
        return color;
    }
}
