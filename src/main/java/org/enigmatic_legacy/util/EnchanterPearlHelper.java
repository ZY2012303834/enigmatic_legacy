package org.enigmatic_legacy.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.enchantment.ModEnchantments;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.EnchanterPearl;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.Optional;

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
        return CuriosLookupApi.findFirstStack(entity, stack -> stack.getItem() instanceof EnchanterPearl);
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
        return CursedRingApi.canUseRestrictedCurio(player, ModItems.ENCHANTER_PEARL.get());
    }

    public static Optional<ICuriosItemHandler> getCuriosInventory(LivingEntity entity) {
        return CuriosLookupApi.getInventory(entity);
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

    public static ItemStack maybeApplyEternalBinding(Player player, ItemStack stack) {
        if (player.getRandom().nextFloat() >= 0.5F) {
            return stack;
        }

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup =
                player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> bindingCurse = enchantmentLookup.getOrThrow(Enchantments.BINDING_CURSE);
        Holder<Enchantment> eternalBinding = enchantmentLookup.getOrThrow(ModEnchantments.ETERNAL_BINDING);
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        if (enchantments.getLevel(bindingCurse) <= 0) {
            return stack;
        }

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        int level = mutable.getLevel(bindingCurse);
        mutable.removeIf(enchantment -> enchantment.is(Enchantments.BINDING_CURSE)
                || enchantment.is(Enchantments.VANISHING_CURSE));
        mutable.set(eternalBinding, level);
        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());

        return stack;
    }
}
