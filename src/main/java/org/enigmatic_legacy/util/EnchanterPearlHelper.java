package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.enigmatic_legacy.item.items.charm.EnchanterPearl;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import net.minecraft.core.Holder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

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

        getCuriosInventory(entity)
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
        return getCuriosInventory(player)
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

    public static Optional<ICuriosItemHandler> getCuriosInventory(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(CuriosCapability.INVENTORY));
    }

    public static ItemStack mergeEnchantments(ItemStack input, ItemStack mergeFrom) {
        ItemStack result = input.copy();
        ItemEnchantments.Mutable resultEnchantments = new ItemEnchantments.Mutable(
                EnchantmentHelper.getEnchantmentsForCrafting(result)
        );
        ItemEnchantments extraEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(mergeFrom);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : extraEnchantments.entrySet()) {
            Holder<Enchantment> extraHolder = entry.getKey();
            Enchantment extraEnchantment = extraHolder.value();
            int existingLevel = resultEnchantments.getLevel(extraHolder);
            int extraLevel = entry.getIntValue();

            int mergedLevel = existingLevel == extraLevel
                    ? Math.min(extraLevel + 1, extraEnchantment.getMaxLevel())
                    : Math.max(existingLevel, extraLevel);

            boolean compatible = true;

            for (Holder<Enchantment> existingHolder : resultEnchantments.keySet()) {
                if (!existingHolder.equals(extraHolder)
                        && !Enchantment.areCompatible(extraHolder, existingHolder)) {
                    compatible = false;
                    break;
                }
            }

            if (compatible) {
                resultEnchantments.set(extraHolder, mergedLevel);
            }
        }

        EnchantmentHelper.setEnchantments(result, resultEnchantments.toImmutable());
        return result;
    }
}
