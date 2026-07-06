package org.enigmatic_legacy.item.items.book;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.enigmatic_legacy.util.AntiqueBookBagHelper;
import org.enigmatic_legacy.util.PlayerInventoryHelper;

import java.util.Optional;

/**
 * 古旧书袋可收纳书籍的基础类。
 *
 * <p>这个基类只用于“放在玩家快捷栏或古旧书袋中都能提供效果”的书类物品，
 * 例如兽友指南、野猎指南、生灵颂词、启示之证及其进阶形态。
 * 它不代表所有名字里带“书”的物品。</p>
 *
 * <p>求知之书和噬咒之书属于合成功能物品，
 * 不能放入古旧书袋，因此不应该继承这个基类。</p>
 */
public abstract class AbstractBookItem extends Item {
    /**
     * 创建一个默认只能堆叠 1 个的书袋书籍。
     */
    protected AbstractBookItem(Rarity rarity) {
        this(new Properties().rarity(rarity));
    }

    /**
     * 允许子类附加属性、抗火等额外物品属性，
     * 但统一保证这些书类只能堆叠 1 个。
     */
    protected AbstractBookItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /**
     * 判断玩家是否在快捷栏或古旧书袋中拥有指定书籍。
     *
     * <p>这里刻意只检查快捷栏和书袋，保持与这些书原本的生效范围一致；
     * 普通背包中的书不会自动提供效果。</p>
     */
    protected static boolean hasInHotbarOrBookBag(Player player, Item book) {
        return PlayerInventoryHelper.hasInHotbar(player, stack -> stack.is(book))
                || AntiqueBookBagHelper.hasBook(player, book);
    }

    /**
     * 查找玩家快捷栏或古旧书袋中的指定书籍实例。
     *
     * <p>需要读取冷却、NBT 或实际 ItemStack 的书类可以使用这个方法，
     * 例如生灵颂词需要对找到的物品应用冷却。</p>
     */
    protected static Optional<ItemStack> findInHotbarOrBookBag(Player player, Item book) {
        return PlayerInventoryHelper.findInHotbar(player, stack -> stack.is(book))
                .or(() -> AntiqueBookBagHelper.findBook(player, book));
    }

}
