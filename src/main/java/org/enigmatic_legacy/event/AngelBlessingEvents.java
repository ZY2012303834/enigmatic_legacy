package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.network.ForceProjectileRotationPayload;
import org.enigmatic_legacy.tag.ModDamageTags;
import org.enigmatic_legacy.util.AngelBlessingHelper;

import java.util.List;

/**
 * 天使之祝服务端事件。
 */
public final class AngelBlessingEvents {
    private static final String ACCELERATED_TAG = "AngelBlessingAccelerated";
    private static final String DEFLECT_CHECKED_TAG = "AngelBlessingDeflectChecked";
    private static final String DEFLECTED_TAG = "AngelBlessingDeflected";

    private AngelBlessingEvents() {
    }

    /**
     * 免疫摔落和撞墙伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!AngelBlessingHelper.hasAngelBlessing(event.getEntity())) {
            return;
        }

        DamageSource source = event.getSource();

        if (source.is(ModDamageTags.ANGEL_BLESSING_IMMUNE_TO)) {
            event.setCanceled(true);
        }
    }

    /**
     * 凋零和虚空伤害增加。
     */
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (!AngelBlessingHelper.hasAngelBlessing(event.getEntity())) {
            return;
        }

        DamageSource source = event.getSource();

        if (source.is(ModDamageTags.ANGEL_BLESSING_VULNERABLE_TO)) {
            event.setNewDamage((float) (
                    event.getNewDamage() * ConfigCommon.ANGEL_BLESSING_VULNERABILITY_MODIFIER.get()
            ));
        }
    }

    /**
     * 被动弹射物逻辑：
     * 1. 自己射出的弹射物加速；
     * 2. 反射接近的敌对弹射物。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (!AngelBlessingHelper.hasAngelBlessing(player)) {
            return;
        }

        AABB box = player.getBoundingBox().inflate(4.0D);
        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, box);

        for (Projectile projectile : projectiles) {
            if (!projectile.isAlive()) {
                continue;
            }

            Entity owner = projectile.getOwner();

            if (owner == player) {
                accelerateOwnProjectile(level, player, projectile);
            } else {
                tryDeflectIncomingProjectile(level, player, projectile);
            }
        }
    }

    private static void accelerateOwnProjectile(ServerLevel level, ServerPlayer player, Projectile projectile) {
        if (projectile.getTags().contains(ACCELERATED_TAG)) {
            return;
        }

        if (projectile instanceof ThrownTrident trident && trident.clientSideReturnTridentTickCount > 0) {
            return;
        }

        if (!projectile.addTag(ACCELERATED_TAG)) {
            return;
        }

        Vec3 movement = projectile.getDeltaMovement().scale(1.75D);
        projectile.setDeltaMovement(movement);
        syncProjectile(level, projectile);
    }

    private static void tryDeflectIncomingProjectile(ServerLevel level, ServerPlayer player, Projectile projectile) {
        if (projectile.getTags().contains(DEFLECT_CHECKED_TAG)) {
            return;
        }

        if (projectile.getTags().contains(DEFLECTED_TAG)) {
            return;
        }

        Vec3 projectilePos = projectile.position();
        Vec3 playerCenter = player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D);
        Vec3 toPlayer = playerCenter.subtract(projectilePos);

        if (toPlayer.lengthSqr() <= 0.0001D) {
            return;
        }

        Vec3 movement = projectile.getDeltaMovement();

        // 只有正在接近玩家的弹射物才尝试反射。
        if (movement.normalize().dot(toPlayer.normalize()) <= 0.0D) {
            return;
        }

        projectile.addTag(DEFLECT_CHECKED_TAG);

        if (player.getRandom().nextInt(100) >= ConfigCommon.ANGEL_BLESSING_DEFLECT_CHANCE.get()) {
            return;
        }

        Vec3 away = projectilePos.subtract(playerCenter).normalize();
        double speed = Math.max(0.35D, movement.length());

        projectile.setOwner(player);
        projectile.setDeltaMovement(away.scale(speed * 1.25D));
        projectile.addTag(DEFLECTED_TAG);

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                0.7F,
                1.2F
        );

        syncProjectile(level, projectile);
    }

    private static void syncProjectile(ServerLevel level, Projectile projectile) {
        Vec3 movement = projectile.getDeltaMovement();

        ForceProjectileRotationPayload payload = new ForceProjectileRotationPayload(
                projectile.getId(),
                projectile.getYRot(),
                projectile.getXRot(),
                movement.x,
                movement.y,
                movement.z,
                projectile.getX(),
                projectile.getY(),
                projectile.getZ()
        );

        for (ServerPlayer player : level.getPlayers(serverPlayer ->
                serverPlayer.distanceToSqr(projectile) < 256.0D
        )) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }
}