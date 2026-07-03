package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

public final class EarthPromiseHelper {
    private EarthPromiseHelper() {
    }

    public static Optional<ItemStack> findEquippedEarthPromise(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.EARTH_PROMISE.get());
    }

    public static boolean hasEarthPromise(LivingEntity entity) {
        return findEquippedEarthPromise(entity).isPresent();
    }

    public static boolean canUseEarthPromise(Player player) {
        return CursedRingHelper.hasCursedRing(player);
    }
}
