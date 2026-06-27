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
     * 轻蔑之约提示文本。
     * 修改内容：
     * 1. 非 Shift 时只显示简短介绍和按住 Shift；
     * 2. Shift 介绍显示完整效果；
     * 3. Shift 介绍最底部统一显示七咒折磨 99.5% 要求和当前百分比。
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
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.1"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.3"));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.4"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.5"));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("curios.modifiers.charm"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.stat.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eldritch_amulet.stat.2"));

        // 最底部统一显示七咒折磨 99.5% 要求和当前百分比。
        CursedSufferingTooltip.appendTooltip(tooltip);
    }

}