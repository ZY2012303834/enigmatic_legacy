package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 天堂之礼工具类。
 */
public final class GiftOfTheHeavenHelper {
    private GiftOfTheHeavenHelper() {
    }

    public static Optional<ItemStack> findGift(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.HEAVEN_SCROLL.get())
                ))
                .map(SlotResult::stack);
    }

    public static boolean hasGift(LivingEntity entity) {
        return findGift(entity).isPresent();
    }
}