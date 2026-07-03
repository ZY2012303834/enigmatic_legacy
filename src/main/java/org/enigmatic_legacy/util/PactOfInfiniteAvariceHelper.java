package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 无尽贪婪契约工具类。
 */
public final class PactOfInfiniteAvariceHelper {
    private PactOfInfiniteAvariceHelper() {
    }

    public static Optional<ItemStack> findPact(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.AVARICE_SCROLL.get());
    }

    public static boolean hasPact(LivingEntity entity) {
        return findPact(entity).isPresent();
    }
}
