package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.items.charm.TreasureHunterCharm;

import java.util.Optional;

/**
 * 猎宝者护符工具类。
 */
public final class TreasureHunterCharmHelper {
    private TreasureHunterCharmHelper() {
    }

    /**
     * 查找当前佩戴的猎宝者护符。
     */
    public static Optional<ItemStack> findEquippedTreasureHunterCharm(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, stack -> stack.getItem() instanceof TreasureHunterCharm);
    }

    /**
     * 判断玩家是否佩戴猎宝者护符。
     */
    public static boolean hasTreasureHunterCharm(LivingEntity player) {
        return findEquippedTreasureHunterCharm(player).isPresent();
    }
}
