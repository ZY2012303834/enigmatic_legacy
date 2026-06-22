package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.MonsterCharmHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 怪物猎人勋章事件处理。
 * 已实现：
 * 1. 对亡灵生物增伤；
 * 2. 对敌对生物增伤；
 * 3. 怪物经验翻倍；
 * 4. 临时 +1 抢夺。
 */
public class MonsterCharmEvents {
    /**
     * 等待还原的临时抢夺数据。
     * key：玩家 UUID。
     * value：玩家击杀时主手物品原本的附魔数据。
     */
    private static final Map<UUID, PendingLootingRestore> PENDING_LOOTING_RESTORES = new HashMap<>();

    /**
     * 提高对亡灵和敌对生物的伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        if (!MonsterCharmHelper.hasMonsterCharm(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();

        float bonusPercent = 0.0F;

        // 亡灵生物增伤。
        // 1.21.1 没有旧版 MobType.UNDEAD；
        // isInvertedHealAndHarm() 可判断亡灵治疗/伤害反转特性。
        if (target.getType().getCategory() == MobCategory.MONSTER && target.isInvertedHealAndHarm()) {
            bonusPercent += ConfigCommon.MONSTER_CHARM_UNDEAD_DAMAGE.get();
        }

        // 敌对生物增伤。
        if (target instanceof Enemy) {
            bonusPercent += ConfigCommon.MONSTER_CHARM_HOSTILE_DAMAGE.get();
        }

        if (bonusPercent <= 0.0F) {
            return;
        }

        float multiplier = 1.0F + bonusPercent / 100.0F;
        event.setAmount(event.getAmount() * multiplier);
    }

    /**
     * 在实体死亡时临时给玩家主手物品 +1 抢夺。
     * 原理：
     * 1. LivingDeathEvent 在实体死亡时触发；
     * 2. 此时掉落还没最终生成；
     * 3. 临时把玩家主手物品的 Looting 等级 +1；
     * 4. 掉落生成后，在 LivingDropsEvent 里还原原始附魔。
     * 这样可以让原版战利品表正常读取到额外抢夺等级。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!ConfigCommon.MONSTER_CHARM_BONUS_LOOTING_ENABLED.get()) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (!MonsterCharmHelper.hasMonsterCharm(player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();

        // 空手没有 ItemStack 可以临时写入附魔。
        if (weapon.isEmpty()) {
            return;
        }

        UUID playerId = player.getUUID();

        // 防止极端情况下重复写入。
        if (PENDING_LOOTING_RESTORES.containsKey(playerId)) {
            return;
        }

        Holder<Enchantment> looting = player.level()
                .registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.LOOTING);

        ItemEnchantments originalEnchantments = weapon.getOrDefault(
                DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY
        );

        int originalLootingLevel = weapon.getEnchantmentLevel(looting);

        EnchantmentHelper.updateEnchantments(weapon, mutable -> {
            mutable.set(looting, originalLootingLevel + 1);
        });

        PENDING_LOOTING_RESTORES.put(
                playerId,
                new PendingLootingRestore(weapon, originalEnchantments)
        );
    }

    /**
     * 掉落生成后还原玩家主手物品原本的附魔。
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            restoreTemporaryLooting(player);
        }
    }

    /**
     * 怪物经验翻倍。
     */
    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!ConfigCommon.MONSTER_CHARM_DOUBLE_XP_ENABLED.get()) {
            return;
        }

        Player attacker = event.getAttackingPlayer();

        if (attacker == null) {
            return;
        }

        if (!MonsterCharmHelper.hasMonsterCharm(attacker)) {
            return;
        }

        if (!(event.getEntity() instanceof Enemy)) {
            return;
        }

        event.setDroppedExperience(event.getDroppedExperience() * 2);
    }

    /**
     * 兜底还原。
     * 正常情况下，LivingDropsEvent 会立刻还原。
     * 这里是防止某些实体没有正常触发 drops 事件时，临时抢夺残留在武器上。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        restoreTemporaryLooting(event.getEntity());
    }

    /**
     * 还原临时抢夺。
     */
    private static void restoreTemporaryLooting(Player player) {
        PendingLootingRestore pending = PENDING_LOOTING_RESTORES.remove(player.getUUID());

        if (pending == null) {
            return;
        }

        if (pending.weapon().isEmpty()) {
            return;
        }

        EnchantmentHelper.setEnchantments(pending.weapon(), pending.originalEnchantments());
    }

    /**
     * 临时抢夺还原数据。
     */
    private record PendingLootingRestore(
            ItemStack weapon,
            ItemEnchantments originalEnchantments
    ) {
    }
}
