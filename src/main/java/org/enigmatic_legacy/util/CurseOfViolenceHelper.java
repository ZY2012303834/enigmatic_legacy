package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 暴戾之咒查询工具。
 *
 * <p>事件类需要频繁判断攻击者是否装备暴戾之咒。
 * 统一放在这里可以避免每个事件方法都直接调用 Curios API，
 * 也能把“必须达到深渊之心资格才生效”的规则集中到一个入口。</p>
 */
public final class CurseOfViolenceHelper {
    private CurseOfViolenceHelper() {
    }

    public static Optional<ItemStack> findScroll(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.VIOLENCE_SCROLL.get());
    }

    public static boolean hasUsableScroll(LivingEntity entity) {
        return entity instanceof Player player
                && AbyssalHeartHelper.isWorthy(player)
                && findScroll(player).isPresent();
    }
}
