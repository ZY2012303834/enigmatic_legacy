package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 纯净之心。
 * 复刻自 Enigmatic Addons 的 Pure Heart。
 * 定位：
 * 1. 七咒净化路线核心材料；
 * 2. 本身不是可装备遗物；
 * 3. 后续可用于制作净化、祝福、救赎路线相关物品。
 */
public class PureHeart extends Item {

    public PureHeart() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.empty());

        /*
         * 原拓展项目中 Pure Heart 同时属于 cursed/blessed 路线物品。
         * 这里先以物品介绍方式标明它属于七咒净化路线，
         * 实际功能留给后续“祝福 / 救赎”路线物品使用。
         */
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.pure_heart.1")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.pure_heart.2")
                .withStyle(ChatFormatting.RED));
    }
}