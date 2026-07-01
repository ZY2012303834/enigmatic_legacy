package org.enigmatic_legacy.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.AntiqueBookBag;

import java.util.List;
import java.util.Optional;

public final class AntiqueBookBagHelper {
    private AntiqueBookBagHelper() {
    }

    /**
     * 查询玩家身上或末影箱中的书袋是否收纳了指定书本。
     *
     * <p>同一本书只需要出现一次即可生效；调用方不要用数量叠加效果。</p>
     */
    public static boolean hasBook(Player player, Item book) {
        return findBook(player, book).isPresent();
    }

    public static Optional<ItemStack> findBook(Player player, Item book) {
        if (!hasAccessibleBag(player)) {
            return Optional.empty();
        }

        List<ItemStack> books = AntiqueBookBag.getStoredBooks(player);
        java.util.Set<Item> seenBooks = new java.util.HashSet<>();
        for (ItemStack stored : books) {
            if (stored.isEmpty() || !seenBooks.add(stored.getItem())) {
                continue;
            }

            if (stored.is(book)) {
                return Optional.of(stored);
            }
        }

        return Optional.empty();
    }

    private static boolean hasAccessibleBag(Player player) {
        return containsBag(player.getInventory()) || containsBag(player.getEnderChestInventory());
    }

    private static boolean containsBag(Container container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).is(ModItems.ANTIQUE_BOOK_BAG.get())) {
                return true;
            }
        }

        return false;
    }
}
