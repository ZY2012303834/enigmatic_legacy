package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 复苏之叶查询工具。
 *
 * <p>统一封装 Curios 查询，事件类只关心“是否佩戴复苏之叶”。</p>
 */
public final class RevivalLeavesHelper {
    private RevivalLeavesHelper() {
    }

    public static boolean hasRevivalLeaves(LivingEntity entity) {
        return findRevivalLeaves(entity).isPresent();
    }

    public static Optional<ItemStack> findRevivalLeaves(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.REVIVAL_LEAVES.get());
    }
}
