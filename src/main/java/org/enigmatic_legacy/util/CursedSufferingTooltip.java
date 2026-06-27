package org.enigmatic_legacy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 七咒折磨时间提示工具。
 * 作用：
 * 1. 统一显示“需要在七咒之戒折磨下度过总游戏时间 99.5%”的提示；
 * 2. 统一显示当前玩家受七咒折磨的时间百分比；
 * 3. 避免每个物品都单独写重复语言 key。
 */
public final class CursedSufferingTooltip {

    private CursedSufferingTooltip() {
    }

    /**
     * 在 tooltip 最底部追加七咒折磨要求。
     */
    public static void appendTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_suffering.requirement")
                .withStyle(ChatFormatting.DARK_RED));

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            tooltip.add(Component.translatable(
                            "tooltip.enigmatic_legacy.cursed_suffering.current_percentage",
                            Component.literal(AbyssalHeartHelper.getSufferingPercentage(player))
                                    .withStyle(ChatFormatting.GOLD)
                    )
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}