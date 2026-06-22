package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.items.MonsterCharm;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof MonsterCharm))
                .ifPresent(slotResult -> result.set(slotResult.stack()));

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断玩家是否佩戴怪物猎人勋章。
     */
    public static boolean hasMonsterCharm(Player player) {
        return findEquippedMonsterCharm(player).isEmpty();
    }
}