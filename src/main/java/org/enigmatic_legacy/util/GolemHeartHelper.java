package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 魔像之心工具类。
 */
public final class GolemHeartHelper {
    private GolemHeartHelper() {
    }

    public static boolean hasGolemHeart(LivingEntity entity) {
        return findEquippedGolemHeart(entity).isPresent();
    }

    public static Optional<ItemStack> findEquippedGolemHeart(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack ->
                        stack.is(ModItems.GOLEM_HEART.get())
                ))
                .map(SlotResult::stack);
    }
}