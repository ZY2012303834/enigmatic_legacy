package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 万钧之护卷轴工具类。
 *
 * <p>所有“玩家是否真正拥有并能使用万钧之护卷轴”的判断都集中在这里。
 * 这样事件、网络包和物品类不会各自写一套略有差异的 Curios 检索逻辑。</p>
 */
public final class ScrollOfThunderEmbraceHelper {
    private ScrollOfThunderEmbraceHelper() {
    }

    /**
     * 查找实体 Curios 栏中的第一个万钧之护卷轴。
     * 返回 Optional 是为了让调用方显式处理“没有装备”的情况。
     */
    public static Optional<ItemStack> findScroll(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.THUNDER_SCROLL.get());
    }

    /**
     * 判断玩家当前是否真正可使用万钧之护卷轴。
     *
     * <p>这里不仅检查 Curios 中是否存在卷轴，也会检查七咒之戒是否仍然有效。
     * 受限物品即使因为登录宽限暂时留在槽位里，也不能绕过这个实际生效校验。</p>
     */
    public static boolean hasScroll(LivingEntity entity) {
        return entity instanceof Player player
                && CursedRingApi.canUseRestrictedCurio(player, ModItems.THUNDER_SCROLL.get());
    }
}
