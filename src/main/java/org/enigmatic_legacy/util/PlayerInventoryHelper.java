package org.enigmatic_legacy.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * 玩家物品位置扫描工具。
 *
 * <p>本项目里“物品栏”和“背包栏”不是同一个概念：
 * 物品栏指屏幕底部 0-8 的快捷栏，背包栏指打开背包后上方的普通储物槽。
 * 不同遗物的生效范围必须分清，避免放进背包栏后错误触发。</p>
 */
public final class PlayerInventoryHelper {
    private static final int HOTBAR_SIZE = 9;

    private PlayerInventoryHelper() {
    }

    /**
     * 查找主手或副手中的第一个匹配物品。
     *
     * <p>用于“持有有效”的物品，例如倒转之启、无止之言。</p>
     */
    public static Optional<ItemStack> findHeld(Player player, Predicate<ItemStack> predicate) {
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && predicate.test(mainHand)) {
            return Optional.of(mainHand);
        }

        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty() && predicate.test(offHand)) {
            return Optional.of(offHand);
        }

        return Optional.empty();
    }

    /**
     * 判断主手或副手中是否存在匹配物品。
     */
    public static boolean hasHeld(Player player, Predicate<ItemStack> predicate) {
        return findHeld(player, predicate).isPresent();
    }

    /**
     * 查找快捷栏中的第一个匹配物品。
     *
     * <p>用于“仅物品快捷栏有效”的书类物品。
     */
    public static Optional<ItemStack> findInHotbar(Player player, Predicate<ItemStack> predicate) {
        for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    /**
     * 判断快捷栏中是否存在匹配物品。
     */
    public static boolean hasInHotbar(Player player, Predicate<ItemStack> predicate) {
        return findInHotbar(player, predicate).isPresent();
    }

    /**
     * 查找完整玩家物品容器中的第一个匹配物品。
     *
     * <p>只有真正设计为“整个玩家背包都有效”的物品才应该使用这个方法。</p>
     */
    public static Optional<ItemStack> findInInventory(Player player, Predicate<ItemStack> predicate) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    /**
     * 判断完整玩家物品容器中是否存在匹配物品。
     */
    public static boolean hasInInventory(Player player, Predicate<ItemStack> predicate) {
        return findInInventory(player, predicate).isPresent();
    }
}
