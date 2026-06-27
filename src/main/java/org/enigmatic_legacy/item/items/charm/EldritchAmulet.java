package org.enigmatic_legacy.item.items.charm;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Testament of Contempt / 轻蔑之约。
 *
 * <p>原项目类名 EldritchAmulet，注册名 {@code eldritch_amulet}。
 * 它是飞升护符的深渊强化版：继承七色神秘护符效果，并额外拥有凝视压制、
 * 生命偷取和死亡保存背包逻辑。
 */
public class EldritchAmulet extends AscensionAmulet {
    public static final double ATTACK_DAMAGE = 3.0D;
    public static final float EXTRA_LIFESTEAL = 0.15F;
    public static final double GAZE_RANGE = 128.0D;
    public static final double GAZE_RADIUS = 3.0D;

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
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
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.worthy_ones_only")
                .withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
        tooltip.add(Component.translatable("curios.modifiers.charm")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.stat.1")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.eldritch_amulet.stat.2")
                .withStyle(ChatFormatting.GOLD));
    }
}
