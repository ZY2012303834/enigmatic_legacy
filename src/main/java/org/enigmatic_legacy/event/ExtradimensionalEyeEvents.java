package org.enigmatic_legacy.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.ExtradimensionalEye;

/**
 * 超维之眼事件。
 * 作用：
 * 玩家手持已绑定的超维之眼左键点击生物时，
 * 将目标传送到绑定位置。
 */
public final class ExtradimensionalEyeEvents {
    private ExtradimensionalEyeEvents() {
    }

    /**
     * 左键点击生物触发传送。
     * 条件：
     * 1. 玩家主手拿着超维之眼；
     * 2. 超维之眼已经绑定位置；
     * 3. 目标是 LivingEntity；
     * 4. 目标所在维度和绑定维度一致。
     */
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        ItemStack stack = player.getMainHandItem();

        if (!stack.is(ModItems.EXTRADIMENSIONAL_EYE.get())) {
            return;
        }

        Entity target = event.getTarget();

        if (!(target instanceof LivingEntity livingTarget)) {
            return;
        }

        /*
         * 原项目是通过左键攻击触发。
         * 这里取消原本攻击，避免造成伤害，只执行传送。
         */
        event.setCanceled(true);

        if (!ExtradimensionalEye.isBound(stack)) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.enigmatic_legacy.extradimensional_eye.not_bound"),
                    true
            );
            return;
        }

        String boundDimension = ExtradimensionalEye.getBoundDimension(stack);
        String targetDimension = livingTarget.level().dimension().location().toString();

        if (!boundDimension.equals(targetDimension)) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.enigmatic_legacy.extradimensional_eye.wrong_dimension"),
                    true
            );
            return;
        }

        teleportTarget(player, livingTarget, stack);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static void teleportTarget(Player player, LivingEntity target, ItemStack stack) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }

        double oldX = target.getX();
        double oldY = target.getY() + target.getBbHeight() * 0.5D;
        double oldZ = target.getZ();

        double x = ExtradimensionalEye.getBoundX(stack);
        double y = ExtradimensionalEye.getBoundY(stack);
        double z = ExtradimensionalEye.getBoundZ(stack);

        /*
         * 先解除载具和乘客。
         * 这样可以避免生物坐船、骑乘状态下传送失败。
         */
        target.stopRiding();
        target.ejectPassengers();

        level.sendParticles(
                ParticleTypes.PORTAL,
                oldX,
                oldY,
                oldZ,
                96,
                0.7D,
                0.9D,
                0.7D,
                0.08D
        );

        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.2F
        );

        target.teleportTo(x, y, z);
        target.setDeltaMovement(0.0D, 0.0D, 0.0D);

        level.sendParticles(
                ParticleTypes.PORTAL,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5D,
                target.getZ(),
                48,
                0.5D,
                0.7D,
                0.5D,
                0.05D
        );

        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.2F
        );
    }
}