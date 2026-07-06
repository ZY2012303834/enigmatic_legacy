package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 求知之书 / Tome of Hungering Knowledge。
 *
 * <p>这是附魔转移配方的功能类物品。虽然名称和外观属于“书”，
 * 但它不是古旧书袋可收纳的随身书类，也不应该继承书袋书籍基类。</p>
 *
 * <p>原项目 ID：{@code enigmaticlegacy:enchantment_transposer}；
 * 当前项目 ID：{@code enigmatic_legacy:enchantment_transposer}。</p>
 *
 * <p>用途：与任意附魔物品一起合成时，将该物品上的全部附魔转移到一本附魔书上。</p>
 */
public class TomeOfHungeringKnowledge extends Item {
    public TomeOfHungeringKnowledge() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enchantment_transposer.1")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enchantment_transposer.2")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enchantment_transposer.3")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
