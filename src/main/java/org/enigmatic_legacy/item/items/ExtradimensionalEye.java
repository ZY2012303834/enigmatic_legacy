package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.sound.ModSounds;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 超维之眼 / Extradimensional Eye。
 * 用法：
 * 1. 玩家站在目标位置，按住 Shift + 右键绑定当前位置；
 * 2. 手持已绑定的超维之眼，左键点击生物；
 * 3. 如果目标和绑定点在同一维度，则把目标绑定的超维之眼，左键点击生物；
 * 3. 如果目标和绑定点在同一维传送到绑定点；
 * 4. 非创造模式使用后消耗 1 个。
 * 注意：
 * 原项目逻辑是绑定后不能再次绑定。
 */
public class ExtradimensionalEye extends Item {
    private static final String BOUND_X_TAG = "BoundX";
    private static final String BOUND_Y_TAG = "BoundY";
    private static final String BOUND_Z_TAG = "BoundZ";
    private static final String BOUND_DIMENSION_TAG = "BoundDimension";

    public ExtradimensionalEye() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    /**
     * Shift + 右键绑定当前位置。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide()) {
            if (isBound(stack)) {
                player.displayClientMessage(
                        Component.translatable("message.enigmatic_legacy.extradimensional_eye.already_bound")
                                .withStyle(ChatFormatting.GOLD),
                        true
                );

                return InteractionResultHolder.fail(stack);
            }

            bindToPlayerPosition(stack, player);

            /*
             * 绑定成功时播放莫测之眼/休眠之眼激活音效。
             * 与 EnigmaticEye 唤醒时使用同一个音效：misc.hhon。
             */
            level.playSound(
                    null,
                    player.blockPosition(),
                    ModSounds.CHARGED_ON.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            player.displayClientMessage(
                    Component.translatable(
                                    "message.enigmatic_legacy.extradimensional_eye.bound",
                                    getBoundX(stack),
                                    getBoundY(stack),
                                    getBoundZ(stack)
                            )
                            .withStyle(ChatFormatting.DARK_PURPLE),
                    true
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static boolean isBound(ItemStack stack) {
        return getTag(stack).contains(BOUND_DIMENSION_TAG);
    }

    public static void bindToPlayerPosition(ItemStack stack, Player player) {
        CompoundTag tag = getTag(stack);

        tag.putDouble(BOUND_X_TAG, player.getX());
        tag.putDouble(BOUND_Y_TAG, player.getY());
        tag.putDouble(BOUND_Z_TAG, player.getZ());
        tag.putString(BOUND_DIMENSION_TAG, player.level().dimension().location().toString());

        setTag(stack, tag);
    }

    public static double getBoundX(ItemStack stack) {
        return getTag(stack).getDouble(BOUND_X_TAG);
    }

    public static double getBoundY(ItemStack stack) {
        return getTag(stack).getDouble(BOUND_Y_TAG);
    }

    public static double getBoundZ(ItemStack stack) {
        return getTag(stack).getDouble(BOUND_Z_TAG);
    }

    public static String getBoundDimension(ItemStack stack) {
        return getTag(stack).getString(BOUND_DIMENSION_TAG);
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.1")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.2")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.3")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.4")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        if (isBound(stack)) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.location")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.x", formatCoordinate(getBoundX(stack)))
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.y", formatCoordinate(getBoundY(stack)))
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.z", formatCoordinate(getBoundZ(stack)))
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.extradimensional_eye.dimension", getBoundDimension(stack))
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    private static int formatCoordinate(double value) {
        return (int) Math.floor(value);
    }
}