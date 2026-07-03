package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 烈焰核心查询工具。
 * 统一封装 Curios 查询，避免事件类里反复写 CuriosApi。
 */
public final class BlazingCoreHelper {
    private BlazingCoreHelper() {
    }

    /**
     * 判断实体是否佩戴烈焰核心。
     */
    public static boolean hasBlazingCore(LivingEntity entity) {
        return findBlazingCore(entity).isPresent();
    }

    /**
     * 在实体 Curios 栏里寻找烈焰核心。
     */
    public static Optional<ItemStack> findBlazingCore(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.BLAZING_CORE.get());
    }
}
