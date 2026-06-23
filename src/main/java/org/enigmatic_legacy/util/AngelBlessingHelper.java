package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;

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
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.is(ModItems.ANGEL_BLESSING.get())
                ))
                .map(slotResult -> slotResult.stack());
    }
}