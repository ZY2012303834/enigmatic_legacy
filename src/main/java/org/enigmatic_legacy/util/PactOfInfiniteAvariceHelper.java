package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 无尽贪婪契约工具类。
 */
public final class PactOfInfiniteAvariceHelper {
    private PactOfInfiniteAvariceHelper() {
    }

    public static Optional<ItemStack> findPact(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.AVARICE_SCROLL.get())
                ))
                .map(SlotResult::stack);
    }

    public static boolean hasPact(LivingEntity entity) {
        return findPact(entity).isPresent();
    }
}