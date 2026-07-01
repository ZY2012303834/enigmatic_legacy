package org.enigmatic_legacy.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * 玩家背包扫描工具。
 *
 * <p>不要直接遍历 {@code player.getInventory().items}：
 * 那只代表玩家物品容器的一部分实现细节，容易漏掉副手、护甲等仍属于玩家背包容器的槽位。
 * 这里统一通过 {@code getContainerSize()/getItem(slot)} 读取完整玩家物品容器。</p>
 */
public final class PlayerInventoryHelper {
    private PlayerInventoryHelper() {
    }

    /**
     * 查找玩家完整物品容器中的第一个匹配物品。
     */
    public static Optional<ItemStack> find(Player player, Predicate<ItemStack> predicate) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    /**
     * 判断玩家完整物品容器中是否存在匹配物品。
     */
    public static boolean contains(Player player, Predicate<ItemStack> predicate) {
        return find(player, predicate).isPresent();
    }
}
