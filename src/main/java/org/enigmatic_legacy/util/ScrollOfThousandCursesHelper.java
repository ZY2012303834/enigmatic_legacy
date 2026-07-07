package org.enigmatic_legacy.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 千咒卷轴工具类。
 * 负责：
 * 1. 判断玩家是否装备千咒卷轴；
 * 2. 统计装备、主副手、Curios 饰品里的诅咒附魔项数；
 * 3. 计算最终加成。
 */
public final class ScrollOfThousandCursesHelper {
    private static final int SEVEN_CURSES_RING_BONUS = 7;

    private static final double ATTACK_DAMAGE_PER_CURSE = 0.04D;
    private static final double MINING_SPEED_PER_CURSE = 0.07D;
    private static final double HEALING_PER_CURSE = 0.04D;

    private ScrollOfThousandCursesHelper() {
    }

    public static Optional<ItemStack> findScroll(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.CURSED_SCROLL.get());
    }

    public static boolean hasScroll(LivingEntity entity) {
        return entity instanceof Player player
                && CursedRingApi.canUseRestrictedCurio(player, ModItems.CURSED_SCROLL.get());
    }

    /**
     * 计算千咒卷轴倍率基数。
     * 公式：
     * 所有装备物品中的诅咒附魔项数 + 7
     * 其中 +7 代表七咒之戒本身的七项诅咒。
     */
    public static int getCurseFactor(Player player) {
        if (!hasScroll(player)) {
            return 0;
        }

        return countEquippedCurseEnchantments(player) + SEVEN_CURSES_RING_BONUS;
    }

    public static double getAttackDamageBonus(Player player) {
        return getCurseFactor(player) * ATTACK_DAMAGE_PER_CURSE;
    }

    public static double getMiningSpeedBonus(Player player) {
        return getCurseFactor(player) * MINING_SPEED_PER_CURSE;
    }

    public static double getHealingBonus(Player player) {
        return getCurseFactor(player) * HEALING_PER_CURSE;
    }

    /**
     * 统计玩家所有“装备中”的物品上的诅咒附魔项数。
     * 包括：
     * 1. 主手；
     * 2. 副手；
     * 3. 身上盔甲；
     * 4. Curios 的全部栏位。
     * 不统计背包普通物品栏。
     */
    private static int countEquippedCurseEnchantments(Player player) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup =
                player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        int curses = 0;

        curses += countStackCurses(player.getMainHandItem(), enchantmentLookup);
        curses += countStackCurses(player.getOffhandItem(), enchantmentLookup);

        for (ItemStack armorStack : player.getArmorSlots()) {
            curses += countStackCurses(armorStack, enchantmentLookup);
        }

        curses += countAllCurioCurses(player, enchantmentLookup);

        return curses;
    }

    private static int countAllCurioCurses(
            Player player,
            HolderLookup.RegistryLookup<Enchantment> enchantmentLookup
    ) {
        return CuriosLookupApi.getInventory(player)
                .map(handler -> {
                    int curses = 0;

                    for (var stacksHandler : handler.getCurios().values()) {
                        var stacks = stacksHandler.getStacks();

                        for (int slot = 0; slot < stacks.getSlots(); slot++) {
                            curses += countStackCurses(stacks.getStackInSlot(slot), enchantmentLookup);
                        }
                    }

                    return curses;
                })
                .orElse(0);
    }

    /**
     * 统计单个 ItemStack 上的诅咒附魔项数。
     * 注意：
     * 只按“项数”计算，不按等级计算。
     * 例如某个模组添加了 Curse X，也只算 1 项。
     */
    private static int countStackCurses(
            ItemStack stack,
            HolderLookup.RegistryLookup<Enchantment> enchantmentLookup
    ) {
        if (stack.isEmpty()) {
            return 0;
        }

        Set<ResourceLocation> countedEnchantments = new HashSet<>();

        int curses = 0;

        /*
         * 统计实际作用在物品上的附魔。
         */
        curses += countCursesFromEnchantments(
                stack.getAllEnchantments(enchantmentLookup),
                countedEnchantments
        );

        /*
         * 额外统计附魔书里的 stored enchantments。
         * 这样主手 / 副手拿着带诅咒的附魔书时，也能按装备物品统计。
         */
        curses += countCursesFromEnchantments(
                stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY),
                countedEnchantments
        );

        return curses;
    }

    private static int countCursesFromEnchantments(
            ItemEnchantments enchantments,
            Set<ResourceLocation> countedEnchantments
    ) {
        if (enchantments.isEmpty()) {
            return 0;
        }

        int curses = 0;

        for (var entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();

            if (!enchantment.is(EnchantmentTags.CURSE)) {
                continue;
            }

            ResourceLocation enchantmentId = enchantment.unwrapKey()
                    .map(key -> key.location())
                    .orElse(null);

            /*
             * 同一件物品里同一个诅咒只算 1 项。
             */
            if (enchantmentId == null || countedEnchantments.add(enchantmentId)) {
                curses++;
            }
        }

        return curses;
    }
}
