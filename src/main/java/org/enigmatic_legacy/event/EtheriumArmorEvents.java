package org.enigmatic_legacy.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.item.ModItems;

/**
 * 以太套装事件。
 * 按原项目逻辑：
 * 生命值低于 40% 且穿满以太套装时，护盾激活。
 * 护盾效果：
 * - 反弹/格挡大多数弹射物；
 * - 受到的伤害降低 50%；
 * - 近战攻击者会被击退；
 * - 播放护盾格挡音效。
 */
public final class EtheriumArmorEvents {
    private static final float SHIELD_HEALTH_THRESHOLD = 0.40F;
    private static final float DAMAGE_RESISTANCE = 0.50F;
    private static final float KNOCKBACK_STRENGTH = 0.75F;
    private static final double PROJECTILE_REFLECT_SPEED = 1.8D;

    private EtheriumArmorEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!hasShield(player)) {
            return;
        }

        Entity directEntity = event.getSource().getDirectEntity();
        Entity attacker = event.getSource().getEntity();

        /*
         * 原项目逻辑：
         * AbstractArrow / AbstractHurtingProjectile 这类弹射物在护盾激活时被取消。
         *
         * 这里额外把弹射物速度反向，表现为“反弹”。
         */
        if (directEntity instanceof AbstractArrow || directEntity instanceof AbstractHurtingProjectile) {
            if (directEntity instanceof Projectile projectile) {
                reflectProjectile(player, projectile, attacker);
            }

            event.setCanceled(true);
            playShieldSound(player);
            return;
        }

        /*
         * 原项目逻辑：
         * 近战攻击者被击退。
         */
        if (attacker instanceof LivingEntity livingAttacker && attacker != player) {
            knockBackAttacker(player, livingAttacker);
            playShieldSound(player);
        }

        /*
         * 原项目逻辑：
         * 护盾激活时降低受到的伤害。
         * 这里为 50% 伤害抗性。
         */
        event.setAmount(event.getAmount() * (1.0F - DAMAGE_RESISTANCE));
    }

    private static boolean hasShield(Player player) {
        return hasFullEtheriumArmor(player) && isShieldActive(player);
    }

    private static boolean hasFullEtheriumArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.ETHERIUM_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.ETHERIUM_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.ETHERIUM_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.ETHERIUM_BOOTS.get());
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
    }

    private static void playShieldSound(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                1.0F,
                0.9F + player.getRandom().nextFloat() * 0.1F
        );
    }
}