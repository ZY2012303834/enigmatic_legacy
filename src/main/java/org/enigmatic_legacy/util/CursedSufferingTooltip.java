package org.enigmatic_legacy.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 七咒折磨时间提示工具。
 * 统一规则：
 * 1. 普通介绍文字为紫色；
 * 2. 99.5% 和当前百分比为金色；
 * 3. 所有需要 99.5% 七咒折磨时间的物品共用这里；
 * 4. 使用 List<Component>，避免原始 List 警告。
 */
public final class CursedSufferingTooltip {

    private CursedSufferingTooltip() {
    }

    /**
     * 在 tooltip 最底部追加七咒折磨要求。
     */
    public static void appendTooltip(List<Component> tooltip) {
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_suffering.requirement",
                SpellstoneTooltip.percent("99.5%")
        ));

        /*
         * 所有需要“深渊之心资格 / 99.5% 七咒折磨时间”的物品都走这个统一入口。
         * 物品类属于 common 代码，专用服务器也可能加载这些类；
         * 因此这里通过 ClientTooltipState 的反射安全入口获取客户端玩家，
         * 避免直接静态引用 Minecraft / LocalPlayer。
         */
        Player player = ClientTooltipState.getClientPlayer();
        String currentPercentage = player != null
                ? AbyssalHeartHelper.getSufferingPercentage(player)
                : "0.0%";

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.cursed_suffering.current_percentage",
                SpellstoneTooltip.percent(currentPercentage)
        ));
    }
}
