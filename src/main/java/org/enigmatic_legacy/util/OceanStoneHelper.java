package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public final class OceanStoneHelper {
    private OceanStoneHelper() {
    }

    public static boolean hasOceanStone(LivingEntity entity) {
        return findOceanStone(entity).isPresent();
    }

    public static Optional<ItemStack> findOceanStone(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.is(ModItems.OCEAN_STONE.get())))
                .map(SlotResult::stack);
    }
}