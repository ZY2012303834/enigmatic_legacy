package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 血战沙场之证 / Emblem of Bloodstained Valor。
 * 效果：
 * 玩家损失的生命比例越高，获得的战斗加成越强。
 * 原项目中它只能由七咒之戒佩戴者使用。
 */
public class BloodstainedValorEmblem extends Item implements ICurioItem {

    public BloodstainedValorEmblem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    /**
     * 允许右键尝试装备到 Curios 护符栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 只有七咒之戒佩戴者可以装备。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        return CursedRingHelper.hasCursedRing(player);
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
            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.1",
                    format(ConfigCommon.BLOODSTAINED_VALOR_ATTACK_DAMAGE.get())
            ).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.2",
                    format(ConfigCommon.BLOODSTAINED_VALOR_ATTACK_SPEED.get())
            ).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.3",
                    format(ConfigCommon.BLOODSTAINED_VALOR_MOVEMENT_SPEED.get())
            ).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.4",
                    format(ConfigCommon.BLOODSTAINED_VALOR_DAMAGE_RESISTANCE.get())
            ).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.5"
            ).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.6"
            ).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.cursed_only"
            ).withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.hold_shift"
            ).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static String format(double value) {
        if (value == (long) value) {
            return Long.toString((long) value) + "%";
        }

        return Double.toString(value) + "%";
    }
}