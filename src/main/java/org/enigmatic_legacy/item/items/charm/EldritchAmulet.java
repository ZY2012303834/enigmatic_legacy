package org.enigmatic_legacy.item.items.charm;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.util.CursedSufferingTooltip;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Testament of Contempt / 轻蔑之约。
 * 原项目类名 EldritchAmulet，注册名 {@code eldritch_amulet}。
 * 它是飞升护符的深渊强化版：
 * - 继承七色神秘护符效果；
 * - 额外拥有凝视压制；
 * - 额外拥有生命偷取；
 * - 当前项目中，灵魂水晶 / 超维容器死亡保存逻辑已经绑定到七咒之戒，
 *   所以本物品不再处理该逻辑。
 */
public class EldritchAmulet extends AscensionAmulet {

    public static final double ATTACK_DAMAGE = 3.0D;
    public static final float EXTRA_LIFESTEAL = 0.15F;
    public static final double GAZE_RANGE = 128.0D;
    public static final double GAZE_RADIUS = 3.0D;

    /**
     * 轻蔑之约 tooltip。
     * 统一标准：
     * 1. 普通介绍文字：紫色；
     * 2. 数字 / 百分比：金色；
     * 3. 负面效果名称：红色；
     * 4. 限制 / 惩罚说明：红色；
     * 5. 佩戴属性中不重复显示 +2 攻击伤害 和 +3 攻击伤害；
     * 6. 攻击伤害合并显示为 +5 攻击伤害；
     * 7. 七咒折磨 99.5% 要求放在最底部。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.short"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        // 基础介绍
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.intro.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.intro.2"));

        tooltip.add(SpellstoneTooltip.empty());

        // 凝视压制效果
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.gaze.title"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.2",
                SpellstoneTooltip.effect("effect.minecraft.slowness")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.3",
                SpellstoneTooltip.effect("effect.minecraft.weakness")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.6",
                SpellstoneTooltip.effect("effect.minecraft.mining_fatigue")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.7",
                SpellstoneTooltip.number((int) GAZE_RANGE),
                SpellstoneTooltip.number((int) GAZE_RADIUS)
        ));

        tooltip.add(SpellstoneTooltip.empty());

        // 死亡保护
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.death.title"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.4"));

        // 消失诅咒属于负面限制，整句红色。
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.eldritch_amulet.5"));

        tooltip.add(SpellstoneTooltip.empty());

        // 佩戴在护符栏时
        tooltip.add(SpellstoneTooltip.text("curios.modifiers.charm"));

        /*
         * 轻蔑之约继承飞升护符的七色效果。
         *
         * 原本红色神秘护符会显示 +2 攻击伤害；
         * 轻蔑之约自身又额外显示 +3 攻击伤害；
         * 为避免 tooltip 出现两行攻击伤害，这里合并显示为 +5 攻击伤害。
         */
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.attack_total",
                SpellstoneTooltip.number("+5")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.aqua",
                SpellstoneTooltip.percent("+15%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.violet",
                SpellstoneTooltip.percent("15%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.magenta",
                SpellstoneTooltip.percent("-20%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.green",
                SpellstoneTooltip.number("+2")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.blue",
                SpellstoneTooltip.percent("+25%")
        ));

        /*
         * 黑色神秘护符提供 10% 生命偷取；
         * 轻蔑之约自身额外提供 +15% 生命偷取；
         * 为避免 tooltip 出现两行生命偷取，这里合并显示为 25% 生命偷取。
         */
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.modifier.lifesteal_total",
                SpellstoneTooltip.percent("25%")
        ));

        // 最底部统一显示七咒折磨 99.5% 要求和当前百分比。
        CursedSufferingTooltip.appendTooltip(tooltip);
    }
}