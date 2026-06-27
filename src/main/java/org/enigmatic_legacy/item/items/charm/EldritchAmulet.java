package org.enigmatic_legacy.item.items.charm;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.CursedSufferingTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Testament of Contempt / 轻蔑之约。
 *
 * 原项目类名 EldritchAmulet，注册名 {@code eldritch_amulet}。
 *
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
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.1")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.3")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.4")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.5")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("curios.modifiers.charm")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.stat.1")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.stat.2")
                .withStyle(ChatFormatting.GOLD));

        // 最底部统一显示七咒折磨 99.5% 要求和当前百分比。
        CursedSufferingTooltip.appendTooltip(tooltip);
    }

    /**
     * 在 Shift tooltip 底部追加七咒佩戴时间。
     * 注意：
     * - tooltip 在客户端渲染；
     * - 客户端总游戏时间和七咒佩戴时间已经由 CursedRingTimerPayload 同步；
     * - 所以这里可以直接读取 AbyssalHeartHelper。
     */
    private static void appendCursedRingTimeTooltip(List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        long totalPlayTime = AbyssalHeartHelper.getTotalPlayTime(player);
        long currentCursedTime = AbyssalHeartHelper.getCursedPlayTime(player);

        /*
         * 需要的七咒佩戴时间：
         * 总游戏时间 × 99.5%。
         *
         * 使用 ceil，避免 99.5% 边界显示时出现少 1 tick 的误差。
         */
        long requiredCursedTime = (long) Math.ceil(
                totalPlayTime * AbyssalHeartHelper.REQUIRED_SUFFERING_FRACTION
        );

        String requiredTimeText = AbyssalHeartHelper.formatPlayTime(requiredCursedTime);
        String currentTimeText = AbyssalHeartHelper.formatPlayTime(currentCursedTime);

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.eldritch_amulet.required_cursed_time",
                requiredTimeText
        ).withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.eldritch_amulet.current_cursed_time",
                currentTimeText
        ).withStyle(ChatFormatting.DARK_PURPLE));
    }
}