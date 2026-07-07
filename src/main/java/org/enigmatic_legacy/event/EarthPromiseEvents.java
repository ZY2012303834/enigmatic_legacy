package org.enigmatic_legacy.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.util.EarthPromiseHelper;

public final class EarthPromiseEvents {
    private static final int PURE_RESISTANCE_DURATION = 20 * 5;
    private static final int PURE_RESISTANCE_AMPLIFIER = 4;
    private static final int EARTH_PROMISE_REGENERATION_DURATION = 20 * 5;
    private static final int EARTH_PROMISE_BUFF_AMPLIFIER = 3;
    private static final String COOLDOWN_UNTIL_TAG = "enigmatic_legacy_earth_promise_cooldown_until";

    private EarthPromiseEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        restorePersistentCooldown(player);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        CompoundTag originalData = event.getOriginal().getPersistentData();

        if (originalData.contains(COOLDOWN_UNTIL_TAG)) {
            event.getEntity().getPersistentData().putLong(COOLDOWN_UNTIL_TAG, originalData.getLong(COOLDOWN_UNTIL_TAG));
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (!EarthPromiseHelper.hasEarthPromise(player) || !EarthPromiseHelper.canUseEarthPromise(player)) {
            return;
        }

        double bonus = ConfigCommon.EARTH_PROMISE_BREAK_SPEED_BONUS.get() / 100.0D;

        if (bonus <= 0.0D) {
            return;
        }

        event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + bonus)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        applyPureResistance(event, target);

        if (!(target instanceof Player player)) {
            return;
        }

        if (!EarthPromiseHelper.hasEarthPromise(player) || !EarthPromiseHelper.canUseEarthPromise(player)) {
            return;
        }

        float damage = event.getNewDamage();

        damage *= Math.max(0.0F, 1.0F - ConfigCommon.EARTH_PROMISE_TOTAL_RESISTANCE.get() / 100.0F);
        event.setNewDamage(damage);

        if (isEarthPromiseOnCooldown(player)) {
            return;
        }

        if (!player.isAlive() || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        float threshold = player.getHealth() * ConfigCommon.EARTH_PROMISE_ABILITY_TRIGGER_PERCENT.get() / 100.0F;

        if (damage < threshold) {
            return;
        }

        startEarthPromiseCooldown(player, ConfigCommon.EARTH_PROMISE_COOLDOWN.get());

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY(), player.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY(0.5D), player.getZ(), 36, 0.1D, 0.1D, 0.1D, 0.2D);
            player.level().playSound(null, player, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        player.addEffect(new MobEffectInstance(
                ModEffects.PURE_RESISTANCE,
                PURE_RESISTANCE_DURATION,
                PURE_RESISTANCE_AMPLIFIER,
                false,
                true,
                true
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                EARTH_PROMISE_REGENERATION_DURATION,
                EARTH_PROMISE_BUFF_AMPLIFIER,
                false,
                true,
                true
        ));

        event.setNewDamage(0.0F);
    }

    private static boolean isEarthPromiseOnCooldown(Player player) {
        if (player.getCooldowns().isOnCooldown(ModItems.EARTH_PROMISE.get())) {
            return true;
        }

        long cooldownUntil = player.getPersistentData().getLong(COOLDOWN_UNTIL_TAG);
        return cooldownUntil > player.level().getGameTime();
    }

    private static void startEarthPromiseCooldown(Player player, int ticks) {
        if (ticks <= 0) {
            player.getPersistentData().remove(COOLDOWN_UNTIL_TAG);
            return;
        }

        player.getPersistentData().putLong(COOLDOWN_UNTIL_TAG, player.level().getGameTime() + ticks);
        player.getCooldowns().addCooldown(ModItems.EARTH_PROMISE.get(), ticks);
    }

    private static void restorePersistentCooldown(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        long cooldownUntil = persistentData.getLong(COOLDOWN_UNTIL_TAG);

        if (cooldownUntil <= 0L) {
            return;
        }

        long remaining = cooldownUntil - player.level().getGameTime();

        if (remaining <= 0L) {
            persistentData.remove(COOLDOWN_UNTIL_TAG);
            return;
        }

        if (!player.getCooldowns().isOnCooldown(ModItems.EARTH_PROMISE.get())) {
            player.getCooldowns().addCooldown(ModItems.EARTH_PROMISE.get(), (int) Math.min(Integer.MAX_VALUE, remaining));
        }
    }

    private static void applyPureResistance(LivingDamageEvent.Pre event, LivingEntity target) {
        MobEffectInstance resistance = target.getEffect(ModEffects.PURE_RESISTANCE);

        if (resistance == null) {
            return;
        }

        int amplifier = resistance.getAmplifier();

        if (target.getRandom().nextInt(5) <= amplifier) {
            event.setNewDamage(0.0F);
            return;
        }

        event.setNewDamage(event.getNewDamage() * 0.2F * Math.max(0, 4 - amplifier));
    }
}
