package org.enigmatic_legacy.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.TheInfinitum;
import org.enigmatic_legacy.util.AbyssalHeartHelper;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 无止之言事件。
 */
public final class TheInfinitumEvents {
    private static final float BOSS_DAMAGE_BONUS_MULTIPLIER =
            TheInfinitum.BOSS_DAMAGE_BONUS_PERCENT / 100.0F;
    private static final float KNOCKBACK_MULTIPLIER =
            1.0F + TheInfinitum.KNOCKBACK_BONUS_PERCENT / 100.0F;
    private static final float LIFESTEAL_MULTIPLIER =
            TheInfinitum.LIFESTEAL_PERCENT / 100.0F;

    private static final Map<LivingEntity, Float> KNOCKBACK_TARGETS = new WeakHashMap<>();

    private TheInfinitumEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();
        if (!weapon.is(ModItems.THE_INFINITUM.get())) {
            return;
        }

        LivingEntity target = event.getEntity();

        if (!AbyssalHeartHelper.isWorthy(attacker)) {
            event.setAmount(0.0F);
            return;
        }

        applyDebuffs(target);

        float damage = event.getAmount();

        if (target instanceof Enemy) {
            damage = restoreFourthCurseDamage(damage);
        }

        if (isBossOrPlayer(target)) {
            damage += damage * BOSS_DAMAGE_BONUS_MULTIPLIER;
        }

        event.setAmount(damage);

        float knockback = KNOCKBACK_MULTIPLIER;
        if (target instanceof Phantom) {
            knockback *= 1.5F;
        }

        KNOCKBACK_TARGETS.put(target, knockback);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        if (!attacker.getMainHandItem().is(ModItems.THE_INFINITUM.get())) {
            return;
        }

        if (!AbyssalHeartHelper.isWorthy(attacker)) {
            return;
        }

        float damage = event.getNewDamage();
        if (damage > 0.0F) {
            attacker.heal(damage * LIFESTEAL_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onHolderIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!TheInfinitum.isHeld(player) || !AbyssalHeartHelper.isWorthy(player)) {
            return;
        }

        if (event.getAmount() < player.getHealth()) {
            return;
        }

        if (player.getRandom().nextInt(100) >= TheInfinitum.UNDEAD_PROBABILITY_PERCENT) {
            return;
        }

        event.setCanceled(true);
        player.setHealth(Math.max(player.getHealth(), 1.0F));
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        Float multiplier = KNOCKBACK_TARGETS.remove(event.getEntity());

        if (multiplier == null) {
            return;
        }

        event.setStrength(event.getStrength() * multiplier);
    }

    private static void applyDebuffs(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 500, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 3, false, true));
    }

    private static float restoreFourthCurseDamage(float damage) {
        float debuff = ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() / 100.0F;
        float multiplier = Math.max(0.0F, 1.0F - debuff);

        if (multiplier <= 0.0001F) {
            return damage;
        }

        return damage / multiplier;
    }

    private static boolean isBossOrPlayer(LivingEntity entity) {
        return entity instanceof Player
                || entity instanceof EnderDragon
                || entity instanceof WitherBoss
                || entity instanceof ElderGuardian;
    }
}
