package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public final class EarthPromiseHelper {
    private EarthPromiseHelper() {
    }

    public static Optional<ItemStack> findEquippedEarthPromise(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(ModItems.EARTH_PROMISE.get()))
                .map(slotResult -> slotResult.stack());
    }

    public static boolean hasEarthPromise(LivingEntity entity) {
        return findEquippedEarthPromise(entity).isPresent();
    }

    public static boolean canUseEarthPromise(Player player) {
        return CursedRingHelper.hasCursedRing(player);
    }
}
