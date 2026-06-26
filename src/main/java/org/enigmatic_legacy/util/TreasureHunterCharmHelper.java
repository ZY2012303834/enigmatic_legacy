package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.items.charm.TreasureHunterCharm;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 猎宝者护符工具类。
 */
public final class TreasureHunterCharmHelper {
    private TreasureHunterCharmHelper() {
    }

    /**
     * 查找当前佩戴的猎宝者护符。
     */
    public static Optional<ItemStack> findEquippedTreasureHunterCharm(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof TreasureHunterCharm))
                .ifPresent(slotResult -> result.set(slotResult.stack()));

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断玩家是否佩戴猎宝者护符。
     */
    public static boolean hasTreasureHunterCharm(Player player) {
        return findEquippedTreasureHunterCharm(player).isPresent();
    }
}
