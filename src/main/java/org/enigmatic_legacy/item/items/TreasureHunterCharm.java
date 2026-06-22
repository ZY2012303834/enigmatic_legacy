package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.config.ConfigCommon;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 猎宝者护符 / Charm of Treasure Hunter。
 * 功能：
 * 1. 提供夜视；
 * 2. 提供 +1 时运；
 * 3. 提高挖掘速度；
 * 4. 可右键切换夜视开关。
 */
public class TreasureHunterCharm extends Item implements ICurioItem {
    private static final String NIGHT_VISION_ENABLED_TAG = "NightVisionEnabled";

    public TreasureHunterCharm() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    /**
     * 允许右键直接装备到 Curios 护符栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 右键切换夜视开关。
     * 注意：
     * 这个开关只控制猎宝者护符提供的夜视。
     * 时运和挖掘速度加成不受影响。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            boolean next = !isNightVisionEnabled(stack);
            setNightVisionEnabled(stack, next);

            player.displayClientMessage(
                    Component.translatable(next
                            ? "message.enigmatic_legacy.treasure_hunter_charm.night_vision.enabled"
                            : "message.enigmatic_legacy.treasure_hunter_charm.night_vision.disabled"),
                    true
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 判断夜视是否开启。
     * 没有数据时默认开启，兼容旧物品。
     */
    public static boolean isNightVisionEnabled(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return true;
        }

        CompoundTag tag = data.copyTag();

        if (!tag.contains(NIGHT_VISION_ENABLED_TAG)) {
            return true;
        }

        return tag.getBoolean(NIGHT_VISION_ENABLED_TAG);
    }

    /**
     * 设置夜视开关。
     */
    public static void setNightVisionEnabled(ItemStack stack, boolean enabled) {
        CompoundTag tag = new CompoundTag();

        CustomData oldData = stack.get(DataComponents.CUSTOM_DATA);

        if (oldData != null) {
            tag = oldData.copyTag();
        }

        tag.putBoolean(NIGHT_VISION_ENABLED_TAG, enabled);
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

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.treasure_hunter_charm.1"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.treasure_hunter_charm.2"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.treasure_hunter_charm.3",
                ConfigCommon.TREASURE_HUNTER_CHARM_MINING_SPEED_BONUS.get() + "%"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                isNightVisionEnabled(stack)
                        ? "tooltip.enigmatic_legacy.treasure_hunter_charm.night_vision.enabled"
                        : "tooltip.enigmatic_legacy.treasure_hunter_charm.night_vision.disabled"
        ).withStyle(isNightVisionEnabled(stack)
                ? ChatFormatting.GREEN
                : ChatFormatting.RED));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.treasure_hunter_charm.4"
        ).withStyle(ChatFormatting.GRAY));
    }
}