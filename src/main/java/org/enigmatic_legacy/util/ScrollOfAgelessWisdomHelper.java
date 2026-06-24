package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

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
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.XP_SCROLL.get())
                ))
                .map(SlotResult::stack);
    }

    public static boolean hasScroll(LivingEntity entity) {
        return findScroll(entity).isPresent();
    }
}