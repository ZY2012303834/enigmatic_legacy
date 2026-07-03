package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

public final class OceanStoneHelper {
    private OceanStoneHelper() {
    }

    public static boolean hasOceanStone(LivingEntity entity) {
        return findOceanStone(entity).isPresent();
    }

    public static Optional<ItemStack> findOceanStone(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.OCEAN_STONE.get());
    }
}
