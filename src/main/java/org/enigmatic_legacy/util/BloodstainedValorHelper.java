package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.items.charm.BloodstainedValorEmblem;

import java.util.Optional;

/**
 * 血战沙场之证工具类。
 */
public final class BloodstainedValorHelper {
    private BloodstainedValorHelper() {
    }

    /**
     * 查找当前佩戴的血战沙场之证。
     */
    public static Optional<ItemStack> findEquippedBloodstainedValor(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, stack -> stack.getItem() instanceof BloodstainedValorEmblem);
    }

    /**
     * 判断玩家是否佩戴血战沙场之证。
     */
    public static boolean hasBloodstainedValor(Player player) {
        return findEquippedBloodstainedValor(player).isPresent();
    }

    /**
     * 判断玩家是否能触发血战沙场之证效果。
     * 原项目要求该物品只能由七咒之戒佩戴者使用。
     */
    public static boolean canUseBloodstainedValor(Player player) {
        return hasBloodstainedValor(player) && CursedRingHelper.hasCursedRing(player);
    }

    /**
     * 获取玩家缺失生命比例。
     * 返回值范围：
     * 0.0 = 满血；
     * 1.0 = 生命几乎完全缺失。
     */
    public static double getMissingHealthRatio(Player player) {
        double maxHealth = player.getMaxHealth();

        if (maxHealth <= 0.0D) {
            return 0.0D;
        }

        double missing = maxHealth - player.getHealth();
        return Math.clamp(missing / maxHealth, 0.0D, 1.0D);
    }
}
