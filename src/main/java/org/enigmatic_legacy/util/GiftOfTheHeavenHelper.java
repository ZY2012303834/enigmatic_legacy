package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 天堂之礼工具类。
 */
public final class GiftOfTheHeavenHelper {
    private GiftOfTheHeavenHelper() {
    }

    public static Optional<ItemStack> findGift(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.HEAVEN_SCROLL.get());
    }

    public static boolean hasGift(LivingEntity entity) {
        return findGift(entity).isPresent();
    }
}
