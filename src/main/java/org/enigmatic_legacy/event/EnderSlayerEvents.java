package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.sword.EnderSlayer;
import org.enigmatic_legacy.util.CursedRingHelper;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 末影之屠事件。
 * 按原项目复刻：
 * 1. 未佩戴七咒之戒时，末影之屠无法造成伤害；
 * 2. 对末地生物造成 +150% 伤害；
 * 3. 对末地生物造成 +600% 击退；
 * 4. 命中玩家时，压制末影珍珠、召回药水、扭曲魔镜、星云之眼、非欧立方；
 * 5. 命中末影人 / 潜影贝时，短时间阻止其传送；
 * 6. 在末地用满蓄力攻击末影人时，额外强化伤害并清除其普通掉落，转为经验。
 */
public final class EnderSlayerEvents {

    /**
     * 被末影之屠压制传送的实体标记。
     */
    private static final String TELEPORT_BLOCK_TAG = "ELTeleportBlock";

    /**
     * 末影之屠击杀末地末影人的标记。
     * 用途：
     * - 原项目在末地击杀末影人时，会清除普通掉落并转为经验。
     */
    private static final String ENDER_SLAYER_VICTIM_TAG = "EnderSlayerVictim";

    /**
     * 临时击退倍率表。
     * 说明：
     * - LivingIncomingDamageEvent 阶段判断目标是否应该获得额外击退；
     * - LivingKnockBackEvent 阶段真正修改击退强度；
     * - 使用 WeakHashMap，实体销毁后不会长期占用内存。
     */
    private static final Map<LivingEntity, Float> KNOCKBACK_MULTIPLIERS = new WeakHashMap<>();

    private EnderSlayerEvents() {
    }

    /**
     * IncomingDamage 阶段。
     * 这里处理：
     * - 非七咒佩戴者不能使用末影之屠；
     * - 命中玩家时压制目标传送能力；
     * - 命中末影人 / 潜影贝时压制其传送；
     * - 对末地生物预登记击退倍率。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();

        if (!weapon.is(ModItems.ENDER_SLAYER.get())) {
            return;
        }

        /*
         * 原项目逻辑：
         * 末影之屠是七咒遗物。
         * 未佩戴七咒之戒者使用时，攻击直接无效。
         */
        if (!CursedRingHelper.hasCursedRing(attacker)) {
            event.setCanceled(true);
            return;
        }

        LivingEntity target = event.getEntity();

        // 压制目标的传送能力。
        suppressTeleportAbilities(target);

        /*
         * 对末地生物登记额外击退。
         *
         * 特殊处理：
         * - 末影龙仍然属于末地生物；
         * - 末影之屠仍然对末影龙造成额外伤害；
         * - 但末影龙不应该被末影之屠击飞。
         *
         * 所以这里排除 EnderDragon。
         */
        if (EnderSlayer.isEndDweller(target) && !(target instanceof EnderDragon)) {
            KNOCKBACK_MULTIPLIERS.put(
                    target,
                    1.0F + EnderSlayer.END_KNOCKBACK_BONUS
            );
        }
    }

    /**
     * Damage.Pre 阶段。
     * 这里处理真正的伤害加成。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();

        if (!weapon.is(ModItems.ENDER_SLAYER.get())) {
            return;
        }

        /*
         * 双保险：
         * Incoming 阶段已经取消过。
         * 这里再次置零，避免其它模组事件顺序导致伤害继续结算。
         */
        if (!CursedRingHelper.hasCursedRing(attacker)) {
            event.setNewDamage(0.0F);
            return;
        }

        LivingEntity target = event.getEntity();

        if (!EnderSlayer.isEndDweller(target)) {
            return;
        }

        float damage = event.getNewDamage();

        /*
         * 原项目特殊逻辑：
         * 在末地中，使用末影之屠满蓄力攻击末影人时，
         * 会造成极高伤害，方便快速清理被七咒吸引的末影人。
         */
        if (attacker.level().dimension() == Level.END) {
            target.getPersistentData().putBoolean(ENDER_SLAYER_VICTIM_TAG, true);

            if (target instanceof EnderMan && attacker.getAttackStrengthScale(0.5F) >= 1.0F) {
                damage = (damage + 100.0F) * 10.0F;
            }
        }

        /*
         * 对末地生物 +150% 伤害。
         *
         * 计算：
         * - +150% = 额外 1.5 倍；
         * - 最终 = 原伤害 * 2.5。
         */
        damage *= 1.0F + EnderSlayer.END_DAMAGE_BONUS;

        event.setNewDamage(damage);
    }

    /**
     * 修改击退强度。
     * 原项目对末地生物提供 +600% 击退。
     */
    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        Float multiplier = KNOCKBACK_MULTIPLIERS.remove(event.getEntity());

        if (multiplier == null) {
            return;
        }

        event.setStrength(event.getStrength() * multiplier);
    }

    /**
     * 阻止末影人 / 潜影贝传送。
     * 原项目命中末影人或潜影贝后，会给实体写入 ELTeleportBlock。
     * 该标记存在期间，传送事件会被取消。
     */
    @SubscribeEvent
    public static void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.getPersistentData().getInt(TELEPORT_BLOCK_TAG) > 0) {
            event.setCanceled(true);
        }
    }

    /**
     * 每 tick 递减传送压制时间。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!(entity instanceof EnderMan) && !(entity instanceof Shulker)) {
            return;
        }

        int cooldown = entity.getPersistentData().getInt(TELEPORT_BLOCK_TAG);

        if (cooldown <= 0) {
            return;
        }

        cooldown--;

        if (cooldown > 0) {
            entity.getPersistentData().putInt(TELEPORT_BLOCK_TAG, cooldown);
        } else {
            entity.getPersistentData().remove(TELEPORT_BLOCK_TAG);
        }
    }

    /**
     * 末地末影人特殊掉落处理。
     * 原项目逻辑：
     * - 在末地被末影之屠标记的末影人；
     * - 清除普通掉落；
     * - 如果掉落物里有末影珍珠 / 末影之眼，则转为经验球。
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity killed = event.getEntity();

        if (!(killed instanceof EnderMan)) {
            return;
        }

        if (killed.level().dimension() != Level.END) {
            return;
        }

        if (!killed.getPersistentData().getBoolean(ENDER_SLAYER_VICTIM_TAG)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        if (!player.getMainHandItem().is(ModItems.ENDER_SLAYER.get())) {
            return;
        }

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();

            if (stack.is(Items.ENDER_PEARL)) {
                dropExperience(killed, 10 * stack.getCount());
            } else if (stack.is(Items.ENDER_EYE)) {
                dropExperience(killed, 20 * stack.getCount());
            }
        }

        event.getDrops().clear();
        event.setCanceled(true);
    }

    /**
     * 命中目标后压制目标的传送能力。
     */
    private static void suppressTeleportAbilities(LivingEntity target) {
        /*
         * 命中玩家：
         * - 末影珍珠冷却 20 秒；
         * - 召回药水冷却 20 秒；
         * - 扭曲魔镜冷却 20 秒；
         * - 星云之眼冷却 20 秒；
         * - 非欧立方冷却 20 秒。
         */
        if (target instanceof ServerPlayer player) {
            player.getCooldowns().addCooldown(Items.ENDER_PEARL, EnderSlayer.TELEPORT_BLOCK_TICKS);
            player.getCooldowns().addCooldown(ModItems.RECALL_POTION.get(), EnderSlayer.TELEPORT_BLOCK_TICKS);
            player.getCooldowns().addCooldown(ModItems.TWISTED_MIRROR.get(), EnderSlayer.TELEPORT_BLOCK_TICKS);
            player.getCooldowns().addCooldown(ModItems.EYE_OF_NEBULA.get(), EnderSlayer.TELEPORT_BLOCK_TICKS);
            player.getCooldowns().addCooldown(ModItems.THE_CUBE.get(), EnderSlayer.TELEPORT_BLOCK_TICKS);
        }

        /*
         * 命中末影人 / 潜影贝：
         * 写入传送封锁标记。
         */
        if (target instanceof EnderMan || target instanceof Shulker) {
            target.getPersistentData().putInt(
                    TELEPORT_BLOCK_TAG,
                    EnderSlayer.TELEPORT_BLOCK_TICKS
            );
        }
    }

    /**
     * 掉落经验球。
     */
    private static void dropExperience(LivingEntity entity, int amount) {
        if (amount <= 0) {
            return;
        }

        entity.level().addFreshEntity(new ExperienceOrb(
                entity.level(),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                amount
        ));
    }
}