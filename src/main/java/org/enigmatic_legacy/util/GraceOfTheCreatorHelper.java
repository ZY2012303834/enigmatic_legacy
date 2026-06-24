package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 创造者的恩赐工具类。
 */
public final class GraceOfTheCreatorHelper {
    private GraceOfTheCreatorHelper() {
    }

    public static Optional<ItemStack> findGrace(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.FABULOUS_SCROLL.get())
                ))
                .map(SlotResult::stack);
    }

    public static boolean hasGrace(LivingEntity entity) {
        return findGrace(entity).isPresent();
    }
}