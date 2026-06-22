package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.items.EnchanterPearl;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 附魔师的珍珠工具类。
 */
public final class EnchanterPearlHelper {
    private EnchanterPearlHelper() {
    }

    /**
     * 查找当前佩戴的附魔师的珍珠。
     */
    public static Optional<ItemStack> findEquippedEnchanterPearl(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof EnchanterPearl))
                .ifPresent(slotResult -> result.set(slotResult.stack()));

        ItemStack stack = result.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    /**
     * 判断玩家是否佩戴附魔师的珍珠。
     */
    public static boolean hasEnchanterPearl(Player player) {
        return findEquippedEnchanterPearl(player).isPresent();
    }

    public static boolean hasOtherEnchanterPearl(Player player, SlotContext currentSlot) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findCurios(stack ->
                                stack.getItem() instanceof EnchanterPearl)
                        .stream()
                        .anyMatch(slotResult -> !isSameSlot(slotResult.slotContext(), currentSlot)))
                .orElse(false);
    }

    private static boolean isSameSlot(SlotContext first, SlotContext second) {
        return first.index() == second.index()
                && first.identifier().equals(second.identifier());
    }

    /**
     * 判断玩家是否能触发附魔师的珍珠效果。
     */
    public static boolean canUseEnchanterPearl(Player player) {
        return hasEnchanterPearl(player) && CursedRingHelper.hasCursedRing(player);
    }
}
