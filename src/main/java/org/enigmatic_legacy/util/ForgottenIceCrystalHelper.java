package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 忘却冰晶查询工具。
 *
 * <p>统一封装 Curios 查询，事件类和主动技能入口只关心“是否佩戴忘却冰晶”。</p>
 */
public final class ForgottenIceCrystalHelper {
    private ForgottenIceCrystalHelper() {
    }

    public static boolean hasForgottenIceCrystal(LivingEntity entity) {
        return findForgottenIceCrystal(entity).isPresent();
    }

    public static Optional<ItemStack> findForgottenIceCrystal(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.FORGOTTEN_ICE_CRYSTAL.get());
    }
}
