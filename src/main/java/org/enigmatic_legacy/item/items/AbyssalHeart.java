package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.jetbrains.annotations.NotNull;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * 深渊之心 / Heart of the Abyss。
 * 原项目物品 ID：
 * enigmaticlegacy:abyssal_heart
 * 当前项目 ID：
 * enigmatic_legacy:abyssal_heart
 * 获取方式：
 * 佩戴七咒之戒击败末影龙后，在末影龙死亡位置生成并悬浮。
 * 使用限制：
 * 玩家必须在七咒之戒折磨下度过总游戏时间的 99.5%，
 * 否则无法捡起，也无法使用。
 */
public class AbyssalHeart extends Item {
    public AbyssalHeart() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 右键使用限制。
     * 深渊之心本身主要作为后续高阶物品的合成材料，
     * 这里保留“未达资格无法使用”的限制。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && !AbyssalHeartHelper.isWorthy(player)) {
            AbyssalHeartHelper.sendUnworthyMessage(player);
            return InteractionResultHolder.fail(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    /**
     * 深渊之心提示文本。
     * 修复内容：
     * - 按住 Shift 时显示当前玩家的七咒佩戴时间；
     * - 显示当前七咒折磨比例；
     * - 显示要求比例 99.5%；
     * - 如果已经达标，显示“资格已满足”；
     * - 如果未达标，显示“资格不足”。
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
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.abyssal_heart.1")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.abyssal_heart.2")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.abyssal_heart.3")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.abyssal_heart.4")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            Player localPlayer = Minecraft.getInstance().player;

            if (localPlayer != null) {
                String currentPercent = AbyssalHeartHelper.getSufferingPercentage(localPlayer);

                tooltip.add(Component.translatable(
                        "tooltip.enigmatic_legacy.abyssal_heart.current_cursed_ratio",
                        currentPercent
                ).withStyle(ChatFormatting.GOLD));
            }

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.abyssal_heart.requirement")
                    .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.abyssal_heart.short")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
