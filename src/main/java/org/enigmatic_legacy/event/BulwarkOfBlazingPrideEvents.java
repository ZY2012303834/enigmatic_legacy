package org.enigmatic_legacy.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.effect.BlazingMightEffect;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * Bulwark of Blazing Pride combat handling.
 */
public final class BulwarkOfBlazingPrideEvents {

    private static final float BACKSTAB_DAMAGE_MULTIPLIER = 1.5F;
    private static final int ATTACKER_IGNITION_SECONDS = 5;
    private static final int BLAZING_MIGHT_DURATION = 20 * 12;
    private static final int BLAZING_MIGHT_AMPLIFIER = 0;

    private BulwarkOfBlazingPrideEvents() {
    }

    /**
     * Special frontal block.
     *
     * <p>This handles the Bulwark-specific behavior that vanilla shield blocking cannot cover:
     * immediate blocking, piercing arrows, and damage types that bypass normal shields.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!isActivelyUsingBulwark(player)) {
            return;
        }

        DamageSource source = event.getSource();

        if (!isFrontDamage(player, source)) {
            return;
        }

        event.setCanceled(true);
        damageUsedBulwark(player, event.getAmount());
        applyBlockReward(player, source);
    }

    /**
     * Blazing Might damage bonus, and removal after the bearer takes real damage.
     */
    @SubscribeEvent
    public static void onLivingDamagePreForBlazingMight(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (target.hasEffect(ModEffects.BLAZING_MIGHT)) {
            target.removeEffect(ModEffects.BLAZING_MIGHT);
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        MobEffectInstance blazingMight = attacker.getEffect(ModEffects.BLAZING_MIGHT);

        if (blazingMight == null) {
            return;
        }

        float multiplier = 1.0F + BlazingMightEffect.DAMAGE_BOOST_PER_LEVEL * (blazingMight.getAmplifier() + 1);
        event.setNewDamage(event.getNewDamage() * multiplier);
    }

    /**
     * While blocking with the Bulwark, attacks from the back or rear side deal 50% more damage.
     */
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!isActivelyUsingBulwark(player)) {
            return;
        }

        DamageSource source = event.getSource();

        if (source.getEntity() == null || isFrontDamage(player, source)) {
            return;
        }

        event.setNewDamage(event.getNewDamage() * BACKSTAB_DAMAGE_MULTIPLIER);
    }

    private static boolean isActivelyUsingBulwark(Player player) {
        return CursedRingHelper.hasCursedRing(player)
                && player.isUsingItem()
                && player.getUseItem().is(ModItems.BULWARK_OF_BLAZING_PRIDE.get());
    }

    private static boolean isFrontDamage(Player player, DamageSource source) {
        Vec3 sourcePos = source.getSourcePosition();

        if (sourcePos == null) {
            return true;
        }

        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 sourceToSelf = sourcePos.vectorTo(player.position());
        sourceToSelf = new Vec3(sourceToSelf.x, 0.0D, sourceToSelf.z);

        if (sourceToSelf.lengthSqr() <= 1.0E-6D) {
            return true;
        }

        return sourceToSelf.normalize().dot(lookVec) < 0.0D;
    }

    private static void damageUsedBulwark(Player player, float blockedDamage) {
        ItemStack stack = player.getUseItem();

        if (!stack.is(ModItems.BULWARK_OF_BLAZING_PRIDE.get()) || player.getAbilities().instabuild) {
            return;
        }

        int durabilityDamage = Math.max(1, 1 + (int) Math.floor(blockedDamage));
        InteractionHand hand = player.getUsedItemHand();
        stack.hurtAndBreak(durabilityDamage, player, LivingEntity.getSlotForHand(hand));
    }

    private static void applyBlockReward(Player player, DamageSource source) {
        player.addEffect(new MobEffectInstance(
                ModEffects.BLAZING_MIGHT,
                BLAZING_MIGHT_DURATION,
                BLAZING_MIGHT_AMPLIFIER,
                false,
                true,
                true
        ));

        LivingEntity attacker = getLivingAttacker(source);

        if (attacker != null && !attacker.fireImmune()) {
            attacker.igniteForSeconds(ATTACKER_IGNITION_SECONDS);
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                1.0F,
                0.85F + player.getRandom().nextFloat() * 0.3F
        );
    }

    private static LivingEntity getLivingAttacker(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        if (source.getDirectEntity() instanceof Projectile projectile
                && projectile.getOwner() instanceof LivingEntity owner) {
            return owner;
        }

        return null;
    }
}
