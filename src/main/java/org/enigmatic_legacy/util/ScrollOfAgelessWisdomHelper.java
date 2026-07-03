package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 永恒智慧卷轴工具类。
 */
public final class ScrollOfAgelessWisdomHelper {
    private ScrollOfAgelessWisdomHelper() {
    }

    /**
     * 查找实体 Curios scroll 栏里的永恒智慧卷轴。
     */
    public static Optional<ItemStack> findScroll(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.XP_SCROLL.get());
    }

    public static boolean hasScroll(LivingEntity entity) {
        return findScroll(entity).isPresent();
    }
}
