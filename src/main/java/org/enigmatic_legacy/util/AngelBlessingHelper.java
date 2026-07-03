package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 天使之祝工具类。
 */
public final class AngelBlessingHelper {
    private AngelBlessingHelper() {
    }

    public static boolean hasAngelBlessing(LivingEntity entity) {
        return findAngelBlessing(entity).isPresent();
    }

    public static Optional<ItemStack> findAngelBlessing(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.ANGEL_BLESSING.get());
    }
}
