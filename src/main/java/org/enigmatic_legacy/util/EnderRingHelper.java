package org.enigmatic_legacy.util;

import net.minecraft.world.entity.player.Player;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 末影之戒工具类。
 */
public final class EnderRingHelper {

    private EnderRingHelper() {
    }

    /**
     * 是否佩戴了末影之戒。
     */
    public static boolean hasEnderRing(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findFirstCurio(ModItems.ENDER_RING.get()).isPresent())
                .orElse(false);
    }

    /**
     * 是否拥有打开末影箱的权限。
     *
     * <p>原项目中七咒之戒也给予 Ring of Ender 的功能，
     * 所以这里允许七咒之戒佩戴者使用。
     */
    public static boolean hasEnderChestAccess(Player player) {
        return hasEnderRing(player) || CursedRingHelper.hasCursedRing(player);
    }
}