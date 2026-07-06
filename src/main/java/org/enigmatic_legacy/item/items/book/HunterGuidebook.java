package org.enigmatic_legacy.item.items.book;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 野猎指南 / Guide to Feral Hunt。
 * <p>
 * 原项目类名 HunterGuidebook。物品栏中持有时，将附近宠物受到的伤害转移给主人。
 */
public class HunterGuidebook extends AbstractBookItem {

    public HunterGuidebook() {
        super(Rarity.RARE);
    }

    public static boolean hasGuidebook(Player player) {
        return hasInHotbarOrBookBag(player, ModItems.HUNTER_GUIDEBOOK.get());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hunter_guidebook.1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.hunter_guidebook.2",
                ConfigCommon.HUNTER_GUIDE_EFFECTIVE_DISTANCE.get()
        ).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hunter_guidebook.3")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hunter_guidebook.4")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.hunter_guidebook.5",
                ConfigCommon.HUNTER_GUIDE_SYNERGY_DAMAGE_REDUCTION.get() + "%"
        ).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
