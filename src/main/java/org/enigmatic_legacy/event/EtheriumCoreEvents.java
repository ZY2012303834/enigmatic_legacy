package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.spellstone.EtheriumCore;
import org.enigmatic_legacy.network.HeartOfCreationGuardPayload;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.util.EtheriumCoreHelper;

/**
 * 以太核心事件逻辑。
 *
 * <p>物品类负责 Curios 佩戴、主动能力、属性和 tooltip。</p>
 *
 * <p>这里处理伤害免疫、主动以太护盾、伤害记录与下一次攻击释放。</p>
 */
public final class EtheriumCoreEvents {
    private static final double PROJECTILE_REFLECT_SPEED = 1.8D;

    private EtheriumCoreEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!(target instanceof Player player) || !EtheriumCoreHelper.hasEtheriumCore(player)) {
            return;
        }

        DamageSource source = event.getSource();

        if (isImmuneDamage(source)) {
            event.setCanceled(true);
            playShieldFeedback(player);
            return;
        }

        if (!EtheriumCore.isShieldActive(player)) {
            return;
        }

        Entity directEntity = source.getDirectEntity();
        Entity attacker = source.getEntity();

        if (directEntity instanceof Projectile projectile) {
            reflectProjectile(player, projectile, attacker);
            event.setCanceled(true);
            playShieldFeedback(player);
            return;
        }

        event.setAmount(event.getAmount() * (1.0F - ConfigCommon.ETHERIUM_CORE_SHIELD_DAMAGE_RESISTANCE.get() / 100.0F));

        if (attacker instanceof LivingEntity livingAttacker && attacker != player) {
            livingAttacker.knockback(
                    ConfigCommon.ETHERIUM_CORE_SHIELD_KNOCKBACK_STRENGTH.get(),
                    player.getX() - livingAttacker.getX(),
                    player.getZ() - livingAttacker.getZ()
            );
        }

        playShieldFeedback(player);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        if (target instanceof Player player && EtheriumCoreHelper.hasEtheriumCore(player) && damage > 0.0F) {
            double converted = damage * ConfigCommon.ETHERIUM_CORE_DAMAGE_CONVERSION.get() / 100.0D;
            EtheriumCore.setStoredDamage(player, EtheriumCore.getStoredDamage(player) + converted);
        }

        if (source.getEntity() instanceof Player attacker && EtheriumCoreHelper.hasEtheriumCore(attacker)) {
            double storedDamage = EtheriumCore.getStoredDamage(attacker);

            if (storedDamage > 0.0D) {
                event.setNewDamage((float) (damage + storedDamage));
                EtheriumCore.setStoredDamage(attacker, 0.0D);
                playShieldFeedback(attacker);
            }
        }
    }

    private static boolean isImmuneDamage(DamageSource source) {
        return source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.THORNS)
                || source.is(DamageTypeTags.IS_EXPLOSION);
    }

    private static void reflectProjectile(Player player, Projectile projectile, Entity attacker) {
        if (projectile.level().isClientSide()) {
            return;
        }

        projectile.setOwner(player);

        if (attacker != null && attacker != player) {
            double dx = attacker.getX() - player.getX();
            double dy = attacker.getEyeY() - projectile.getY();
            double dz = attacker.getZ() - player.getZ();
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (length > 0.0001D) {
                projectile.setDeltaMovement(
                        dx / length * PROJECTILE_REFLECT_SPEED,
                        dy / length * PROJECTILE_REFLECT_SPEED,
                        dz / length * PROJECTILE_REFLECT_SPEED
                );
            } else {
                projectile.setDeltaMovement(projectile.getDeltaMovement().reverse());
            }
        } else {
            projectile.setDeltaMovement(projectile.getDeltaMovement().reverse());
        }

        projectile.hasImpulse = true;

        if (projectile instanceof AbstractArrow arrow) {
            arrow.setNoPhysics(false);
            arrow.shakeTime = 0;
        }
    }

    private static void playShieldFeedback(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                ModSounds.SHIELD_TRIGGER.get(),
                SoundSource.PLAYERS,
                1.35F,
                0.85F + player.getRandom().nextFloat() * 0.2F
        );

        player.hurtTime = Math.max(player.hurtTime, 10);
        player.hurtDuration = Math.max(player.hurtDuration, 10);
        player.invulnerableTime = Math.max(player.invulnerableTime, 10);
        player.level().broadcastEntityEvent(player, (byte) 2);

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new HeartOfCreationGuardPayload());
        }
    }
}
