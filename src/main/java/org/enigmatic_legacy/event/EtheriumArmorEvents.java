package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.item.ModItems;

/**
 * 以太套装事件。
 * 套装效果：
 * 生命值低于 40% 时生成强力护盾：
 * - 反弹大多数弹射物；
 * - 受到的伤害降低 50%；
 * - 攻击者被击退。
 */
public final class EtheriumArmorEvents {
    private static final float SHIELD_HEALTH_THRESHOLD = 0.40F;
    private static final float DAMAGE_RESISTANCE = 0.50F;
    private static final float KNOCKBACK_STRENGTH = 1.35F;
    private static final float SHIELD_ABSORPTION_AMOUNT = 8.0F;

    private EtheriumArmorEvents() {
    }

    /**
     * 每秒补一次护盾吸收值。
     * 这里用 absorption 表现“强力护盾”，不会无限叠加。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (player.tickCount % 20 != 0) {
            return;
        }

        if (!hasFullEtheriumArmor(player)) {
            return;
        }

        if (!isShieldActive(player)) {
            return;
        }

        if (player.getAbsorptionAmount() < SHIELD_ABSORPTION_AMOUNT) {
            player.setAbsorptionAmount(SHIELD_ABSORPTION_AMOUNT);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!hasFullEtheriumArmor(player)) {
            return;
        }

        if (!isShieldActive(player)) {
            return;
        }

        Entity directEntity = event.getSource().getDirectEntity();
        Entity attacker = event.getSource().getEntity();

        /*
         * 反弹大多数弹射物。
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
         * 攻击你的生物会被击退。
         */
        if (attacker instanceof LivingEntity livingAttacker && attacker != player) {
            knockBackAttacker(player, livingAttacker);
        }

        playShieldFeedback(player);
    }

    private static boolean hasFullEtheriumArmor(Player player) {
        return is(player, EquipmentSlot.HEAD, ModItems.ETHERIUM_HELMET.get().getDefaultInstance())
                && is(player, EquipmentSlot.CHEST, ModItems.ETHERIUM_CHESTPLATE.get().getDefaultInstance())
                && is(player, EquipmentSlot.LEGS, ModItems.ETHERIUM_LEGGINGS.get().getDefaultInstance())
                && is(player, EquipmentSlot.FEET, ModItems.ETHERIUM_BOOTS.get().getDefaultInstance());
    }

    private static boolean is(Player player, EquipmentSlot slot, ItemStack target) {
        return player.getItemBySlot(slot).is(target.getItem());
    }

    private static boolean isShieldActive(Player player) {
        return player.getHealth() / player.getMaxHealth() < SHIELD_HEALTH_THRESHOLD;
    }

    private static void reflectProjectile(Player player, Projectile projectile, Entity attacker) {
        if (!projectile.level().isClientSide()) {
            projectile.setOwner(player);

            if (attacker != null && attacker != player) {
                double dx = attacker.getX() - player.getX();
                double dy = attacker.getEyeY() - projectile.getY();
                double dz = attacker.getZ() - player.getZ();

                double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (length > 0.0001D) {
                    projectile.setDeltaMovement(
                            dx / length * 1.8D,
                            dy / length * 1.8D,
                            dz / length * 1.8D
                    );
                } else {
                    projectile.setDeltaMovement(projectile.getDeltaMovement().reverse());
                }
            } else {
                projectile.setDeltaMovement(projectile.getDeltaMovement().reverse());
            }

            projectile.hasImpulse = true;
        }
    }

    private static void knockBackAttacker(Player player, LivingEntity attacker) {
        attacker.knockback(
                KNOCKBACK_STRENGTH,
                player.getX() - attacker.getX(),
                player.getZ() - attacker.getZ()
        );
    }

    private static void playShieldFeedback(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.level().playSound(
                null,
                serverPlayer.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                1.0F,
                0.85F + serverPlayer.getRandom().nextFloat() * 0.25F
        );

        serverPlayer.hurtTime = Math.max(serverPlayer.hurtTime, 6);
        serverPlayer.hurtDuration = Math.max(serverPlayer.hurtDuration, 6);
        serverPlayer.level().broadcastEntityEvent(serverPlayer, (byte) 2);
    }
}