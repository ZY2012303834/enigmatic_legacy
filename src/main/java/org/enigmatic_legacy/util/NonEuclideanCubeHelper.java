package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 非欧立方工具类。

 * 统一封装 Curios 查询逻辑，避免事件类里重复写 CuriosApi。
 */
public final class NonEuclideanCubeHelper {
    private NonEuclideanCubeHelper() {
    }

    public static boolean hasNonEuclideanCube(LivingEntity entity) {
        return findNonEuclideanCube(entity).isPresent();
    }

    public static Optional<ItemStack> findNonEuclideanCube(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.THE_CUBE.get());
    }
}
