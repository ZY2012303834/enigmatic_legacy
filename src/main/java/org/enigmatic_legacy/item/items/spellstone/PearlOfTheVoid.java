package org.enigmatic_legacy.item.items.spellstone;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 虚空珍珠 / Pearl of the Void。

 * 类型：术石 spellstone。

 * 说明：
 * 这个物品没有主动技能，所以不需要接入 SpellstoneUsePayload。
 * 所有被动效果都放在 PearlOfTheVoidEvents 里处理。
 */
public class PearlOfTheVoid extends Item implements ICurioItem {
    /**
     * Curios 术石槽位 ID。
     * 你的项目里术石槽位统一叫 spellstone。
     */
    private static final String SPELLSTONE_SLOT = "spellstone";

    /**
     * 黑暗光环扫描间隔。
     * 10 tick = 0.5 秒。
     */
    public static final int DARKNESS_INTERVAL_TICKS = 10;

    /**
     * 黑暗光环默认范围。
     */
    public static final double DARKNESS_RANGE = 16.0D;

    /**
     * 黑暗光环每次造成的虚空伤害。
     * Minecraft 里 2 点伤害 = 1 颗心。
     * 这里 4 点 = 2 颗心。
     */
    public static final float DARKNESS_DAMAGE = 4.0F;

    /**
     * 35% 概率抵挡致命伤害。
     */
    public static final int DEATH_PROTECTION_CHANCE = 35;

    /**
     * 佩戴者攻击目标时附加凋零。
     */
    public static final int ATTACK_WITHER_DURATION = 100; // 5 秒
    public static final int ATTACK_WITHER_AMPLIFIER = 0; // 凋零 I

    /**
     * 黑暗光环附加效果。
     */
    public static final int AURA_WITHER_DURATION = 80; // 4 秒
    public static final int AURA_WITHER_AMPLIFIER = 1; // 凋零 II

    public static final int AURA_SLOWNESS_DURATION = 100; // 5 秒
    public static final int AURA_SLOWNESS_AMPLIFIER = 2; // 缓慢 III

    public static final int AURA_BLINDNESS_DURATION = 100; // 5 秒
    public static final int AURA_BLINDNESS_AMPLIFIER = 0; // 失明 I

    public static final int AURA_HUNGER_DURATION = 160; // 8 秒
    public static final int AURA_HUNGER_AMPLIFIER = 2; // 饥饿 III

    public static final int AURA_MINING_FATIGUE_DURATION = 100; // 5 秒
    public static final int AURA_MINING_FATIGUE_AMPLIFIER = 3; // 挖掘疲劳 IV

    public PearlOfTheVoid() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许右键从手中直接装备到 Curios 槽。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 限制只能放进 spellstone 术石槽。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 判断当前 Curios 槽位是不是 spellstone。
     */
    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * Tooltip 显示。
     *
     * 风格保持和你已有术石一致：
     * 不按 Shift 只显示“按住 Shift 查看详情”；
     * 按住 Shift 后显示完整被动效果。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.passive"));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void_pearl.passive.1"));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void_pearl.passive.2"));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void_pearl.passive.3"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.void_pearl.passive.4",
                SpellstoneTooltip.number(String.format("%.1f", DARKNESS_RANGE)),
                SpellstoneTooltip.number(String.format("%.1f", DARKNESS_DAMAGE))
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.void_pearl.passive.5",
                SpellstoneTooltip.number(DEATH_PROTECTION_CHANCE + "%")
        ));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void_pearl.passive.6"));
    }
}
