package org.enigmatic_legacy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 七咒折磨时间提示工具。
 * 显示规则：
 * 1. 普通介绍文字为紫色；
 * 2. 99.5% 和当前百分比为金色；
 * 3. 所有需要 99.5% 七咒折磨时间的物品共用这里；
 * 4. 不使用原始 List，避免 unchecked 警告。
 */
public final class CursedSufferingTooltip {

    private CursedSufferingTooltip() {
    }

    /**
     * 在 Shift 介绍最底部追加七咒折磨要求。
     */
    public static void appendTooltip(List<Component> tooltip) {
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_suffering.requirement",
                SpellstoneTooltip.percent("99.5%")
        ));

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.cursed_suffering.current_percentage",
                    SpellstoneTooltip.percent(AbyssalHeartHelper.getSufferingPercentage(player))
            ));
        }
    }
}