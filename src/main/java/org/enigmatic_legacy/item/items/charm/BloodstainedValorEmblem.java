package org.enigmatic_legacy.item.items.charm;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
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
        return canEquip(context, stack);
    }

    /**
     * 只有七咒之戒佩戴者可以装备。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        return CursedRingHelper.hasCursedRing(player) || CuriosLookupApi.isStackInSlot(player, context, stack);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.1",
                    SpellstoneTooltip.percent(format(ConfigCommon.BLOODSTAINED_VALOR_ATTACK_DAMAGE.get()))
            ));

            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.2",
                    SpellstoneTooltip.percent(format(ConfigCommon.BLOODSTAINED_VALOR_ATTACK_SPEED.get()))
            ));

            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.3",
                    SpellstoneTooltip.percent(format(ConfigCommon.BLOODSTAINED_VALOR_MOVEMENT_SPEED.get()))
            ));

            tooltip.add(SpellstoneTooltip.empty());

            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.5"
            ));

            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.6"
            ));

            tooltip.add(SpellstoneTooltip.empty());

            tooltip.add(SpellstoneTooltip.negative(
                    "tooltip.enigmatic_legacy.bloodstained_valor_emblem.cursed_only"
            ));
        } else {
            tooltip.add(SpellstoneTooltip.holdShift());
        }
    }

    private static String format(double value) {
        if (value == (long) value) {
            return Long.toString((long) value) + "%";
        }

        return Double.toString(value) + "%";
    }
}
