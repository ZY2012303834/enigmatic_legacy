package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 创造者的恩赐工具类。
 */
public final class GraceOfTheCreatorHelper {
    private GraceOfTheCreatorHelper() {
    }

    public static Optional<ItemStack> findGrace(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.FABULOUS_SCROLL.get());
    }

    public static boolean hasGrace(LivingEntity entity) {
        return findGrace(entity).isPresent();
    }
}
