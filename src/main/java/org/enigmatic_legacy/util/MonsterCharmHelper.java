package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.items.charm.MonsterCharm;

import java.util.Optional;

/**
 * 怪物猎人勋章辅助类。
 * 用于统一查询玩家是否佩戴了怪物猎人勋章，
 * 避免在多个事件里重复写 Curios 查询逻辑。
 */
public final class MonsterCharmHelper {

    private MonsterCharmHelper() {
    }

    /**
     * 查找当前实体佩戴的怪物猎人勋章。
     */
    public static Optional<ItemStack> findEquippedMonsterCharm(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, stack -> stack.getItem() instanceof MonsterCharm);
    }

    /**
     * 判断玩家是否佩戴怪物猎人勋章。
     */
    public static boolean hasMonsterCharm(Player player) {
        return findEquippedMonsterCharm(player).isPresent();
    }
}
