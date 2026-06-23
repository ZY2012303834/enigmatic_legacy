package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
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
 * 烈焰核心 / Blazing Core
 * 定位：术石 spellstone。
 * 原物品无主动技能，主要提供火焰与岩浆相关能力：
 * 1. 免疫普通火焰伤害；
 * 2. 自动熄灭自身燃烧；
 * 3. 临时免疫岩浆伤害，长时间泡岩浆会过热并开始受伤；
 * 4. 受到近战攻击时，点燃攻击者并造成火焰反馈伤害；
 * 5. 大多数状态效果持续时间减半，抗火类效果持续时间翻倍；
 * 6. 来自水生生物的伤害提高。
 */
public class BlazingCore extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    public BlazingCore() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
                .fireResistant());
    }

    /**
     * 只允许放入 Curios 的 spellstone 术石栏。
     */
    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();

        /*
         * 原版烈焰核心效果：
         * 佩戴时，如果实体正在燃烧，就立即熄灭。
         *
         * 注意：
         * 伤害免疫、岩浆过热、反伤等逻辑放在 BlazingCoreEvents 里处理。
         */
        if (entity.isOnFire()) {
            entity.clearFire();
        }
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.active")
                .withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.blazing_core.active")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.spellstone.cooldown",
                String.format("%.1f", ConfigCommon.BLAZING_CORE_COOLDOWN.get() / 20.0F)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.passive")
                .withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.blazing_core.passive.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.blazing_core.passive.2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.blazing_core.passive.3",
                String.format("%.1f", ConfigCommon.BLAZING_CORE_DAMAGE_FEEDBACK.get())
        ).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.blazing_core.passive.4",
                ConfigCommon.BLAZING_CORE_EFFECT_DURATION_MODIFIER.get() + "%"
        ).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.blazing_core.passive.5",
                String.format("%.1f", ConfigCommon.BLAZING_CORE_AQUATIC_DAMAGE_VULNERABILITY.get())
        ).withStyle(ChatFormatting.RED));
    }
}