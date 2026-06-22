package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.config.ConfigCommon;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 怪物猎人勋章 / Emblem of Monster Slayer。
 * 对齐原项目的核心能力：
 * 1. 提高对亡灵生物的伤害；
 * 2. 提高对敌对生物的伤害；
 * 3. 提供 +1 Looting（可配置关闭）；
 * 4. 怪物经验掉落翻倍（可配置关闭）。
 * 注意：
 * 这个物品放在 Curios 的 charm 栏位。
 * 由于你当前项目的 charm 栏位数量是 1，
 * 所以它会与神秘护身符共用同一个槽位，不能同时佩戴。
 */
public class MonsterCharm extends Item implements ICurioItem {

    public MonsterCharm() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    /**
     * 允许右键直接装备到 Curios 的 charm 栏位。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * Tooltip。
     * 对齐原项目：
     * - 默认只提示“按住 Shift 查看详情”
     * - 按住 Shift 时显示完整效果说明
     */
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
                    "tooltip.enigmatic_legacy.monster_charm.1",
                    ConfigCommon.MONSTER_CHARM_UNDEAD_DAMAGE.get() + "%"
            ).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.monster_charm.2",
                    ConfigCommon.MONSTER_CHARM_HOSTILE_DAMAGE.get() + "%"
            ).withStyle(ChatFormatting.GOLD));

            if (ConfigCommon.MONSTER_CHARM_BONUS_LOOTING_ENABLED.get()) {
                tooltip.add(Component.translatable(
                        "tooltip.enigmatic_legacy.monster_charm.3"
                ).withStyle(ChatFormatting.GOLD));
            }

            if (ConfigCommon.MONSTER_CHARM_DOUBLE_XP_ENABLED.get()) {
                tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
                tooltip.add(Component.translatable(
                        "tooltip.enigmatic_legacy.monster_charm.4"
                ).withStyle(ChatFormatting.GOLD));
            }
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.enigmatic_legacy.holdShift"
            ).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
