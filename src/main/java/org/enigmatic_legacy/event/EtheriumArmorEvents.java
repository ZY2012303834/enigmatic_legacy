package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.item.ModItems;

/**
 * 以太套装护盾事件。
 * 按原项目逻辑：
 * 穿满以太套装，并且生命值低于阈值时，护盾生效。
 * 当前阈值：
 * 40%
 * 当前效果：
 * - 反弹/格挡大多数弹射物；
 * - 受到伤害降低 50%；
 * - 攻击者被击退；
 * - 播放盾牌格挡音效；
 * - 不使用黄心吸收值。
 */
public final class EtheriumArmorEvents {
    private static final float SHIELD_HEALTH_THRESHOLD = 0.40F;
    private static final float DAMAGE_RESISTANCE = 0.50F;
    private static final float KNOCKBACK_STRENGTH = 0.85F;
    private static final double PROJECTILE_REFLECT_SPEED = 1.8D;

    private EtheriumArmorEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!hasEtheriumShield(player)) {
            return;
        }

        Entity directEntity = event.getSource().getDirectEntity();
        Entity attacker = event.getSource().getEntity();

        /*
         * 不处理虚空、饥饿、指令等无实体来源伤害。
         * 这样更接近“受到攻击时生成护盾”的语义。
         */
        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)
                || event.getSource().is(DamageTypes.STARVE)
                || event.getSource().is(DamageTypes.GENERIC_KILL)) {
            return;
        }

        /*
         * 原项目护盾会挡住弹射物。
         * 这里对 Arrow / HurtingProjectile 做反弹表现；
         * 其他 Projectile 直接取消并尝试反向。
         */
        if (directEntity instanceof Projectile projectile) {
            reflectProjectile(player, projectile, attacker);
            event.setCanceled(true);
            playShieldFeedback(player);
            return;
        }

        /*
         * 50% 伤害抗性。
         */
        event.setAmount(event.getAmount() * (1.0F - DAMAGE_RESISTANCE));

        /*
         * 攻击者被击退。
         */
        if (attacker instanceof LivingEntity livingAttacker && attacker != player) {
            knockBackAttacker(player, livingAttacker);
            playShieldFeedback(player);
        } else {
            playShieldFeedback(player);
        }
    }

    private static boolean hasEtheriumShield(Player player) {
        return hasFullEtheriumArmor(player) && isShieldActive(player);
    }

    private static boolean hasFullEtheriumArmor(Player player) {
        return isArmor(player, EquipmentSlot.HEAD, ModItems.ETHERIUM_HELMET.get())
                && isArmor(player, EquipmentSlot.CHEST, ModItems.ETHERIUM_CHESTPLATE.get())
                && isArmor(player, EquipmentSlot.LEGS, ModItems.ETHERIUM_LEGGINGS.get())
                && isArmor(player, EquipmentSlot.FEET, ModItems.ETHERIUM_BOOTS.get());
    }

    private static boolean isArmor(Player player, EquipmentSlot slot, Item item) {
        return player.getItemBySlot(slot).is(item);
    }

    private static boolean isShieldActive(Player player) {
        return player.getHealth() / player.getMaxHealth() <= SHIELD_HEALTH_THRESHOLD;
    }

    private static void knockBackAttacker(Player player, LivingEntity attacker) {
        attacker.knockback(
                KNOCKBACK_STRENGTH,
                player.getX() - attacker.getX(),
                player.getZ() - attacker.getZ()
        );
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

        /*
         * 箭类弹射物反弹后重新变成可命中状态。
         */
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
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                1.0F,
                0.85F + player.getRandom().nextFloat() * 0.25F
        );

        /*
         * 给玩家一个受击闪烁反馈，表示护盾触发。
         */
        player.hurtTime = Math.max(player.hurtTime, 6);
        player.hurtDuration = Math.max(player.hurtDuration, 6);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.broadcastEntityEvent(player, (byte) 2);
        }
    }
}