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
     * 1. 普通介绍文字为紫色；
     * 2. 数字 / 百分比为金色；
     * 3. 负面效果名称为红色；
     * 4. 限制 / 惩罚说明为红色；
     * 5. 非 Shift 显示简短介绍 + 按住 Shift；
     * 6. Shift 显示完整介绍、凝视效果、死亡保留、佩戴属性、七咒折磨要求。
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

        // 介绍
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

        // 死亡保留
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.death.title"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.4"));

        // 消失诅咒是负面限制，整句红色。
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.eldritch_amulet.5"));

        tooltip.add(SpellstoneTooltip.empty());

        // 佩戴在护符栏时
        tooltip.add(SpellstoneTooltip.text("curios.modifiers.charm"));

        // 继承飞升护符 / 七色神秘护身符的完整效果
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.red"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.aqua"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.violet"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.magenta"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.green"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.black"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.modifier.blue"));

        // 轻蔑之约额外佩戴属性
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.stat.1",
                SpellstoneTooltip.number("+3")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.eldritch_amulet.stat.2",
                SpellstoneTooltip.percent("+15%")
        ));

        // 最底部统一显示七咒折磨 99.5% 要求和当前百分比。
        CursedSufferingTooltip.appendTooltip(tooltip);
    }

}