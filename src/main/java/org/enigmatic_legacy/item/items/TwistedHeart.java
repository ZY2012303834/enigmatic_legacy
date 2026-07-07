package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TwistedHeart extends Item {

    public static final String ACTIVE_TAG = "enigmatic_legacy_twisted_heart_active";
    public static final int CHECK_INTERVAL_TICKS = 40; // 2 秒检测一次

    public TwistedHeart() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack,
                              @NotNull Level level,
                              @NotNull Entity entity,
                              int slotId,
                              boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide()) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        // 每 2 秒检测一次，减少 Curios 查询次数和服务器负担。
        if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        boolean active = CursedRingHelper.hasCursedRing(player);
        setActivated(stack, active);
    }


    public static boolean isActivated(@NotNull ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getBoolean(ACTIVE_TAG);
    }

    public static void setActivated(@NotNull ItemStack stack, boolean activated) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        if (tag.getBoolean(ACTIVE_TAG) == activated) {
            return;
        }

        tag.putBoolean(ACTIVE_TAG, activated);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.DARK_PURPLE));

        if (isActivated(stack)) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.twisted_heart.active")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.twisted_heart.inactive")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}