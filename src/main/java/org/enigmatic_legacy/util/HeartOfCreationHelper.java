package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 创造之心工具类。

 * 作用：
 * 1. 判断是否装备创造之心；
 * 2. 判断玩家物品栏内是否存在创造之心；
 * 3. 判断是否应该获得“不朽”效果。
 */
public final class HeartOfCreationHelper {
    private HeartOfCreationHelper() {
    }

    /**
     * 判断实体是否佩戴创造之心。
     */
    public static boolean hasHeartOfCreationEquipped(LivingEntity entity) {
        return findHeartOfCreation(entity).isPresent();
    }

    /**
     * 查找 Curios 栏位里的创造之心。
     */
    public static Optional<ItemStack> findHeartOfCreation(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.HEART_OF_CREATION.get());
    }

    /**
     * 判断玩家物品栏、护甲栏、副手里是否有创造之心。

     * 用于“不朽”效果：
     * 装备它或把它放在物品栏中，玩家都不会真正死亡。
     */
    public static boolean hasHeartOfCreationInInventory(Player player) {
        return PlayerInventoryHelper.hasInInventory(player, stack -> stack.is(ModItems.HEART_OF_CREATION.get()));
    }

    /**
     * 判断实体是否应该拥有不朽效果。

     * 规则：
     * 1. 佩戴创造之心：有效；
     * 2. 玩家物品栏内有创造之心：有效；
     * 3. 非玩家实体只能通过 Curios 佩戴生效。
     */
    public static boolean hasCreationImmortality(LivingEntity entity) {
        if (hasHeartOfCreationEquipped(entity)) {
            return true;
        }

        return entity instanceof Player player && hasHeartOfCreationInInventory(player);
    }
}
