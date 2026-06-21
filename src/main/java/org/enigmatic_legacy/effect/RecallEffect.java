package org.enigmatic_legacy.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 召回效果。
 *
 * <p>这是一个瞬时药水效果。
 * 玩家获得该效果时，会被传送回重生点。
 */
public class RecallEffect extends InstantenousMobEffect {

    public RecallEffect() {
        // 药水颜色
        super(MobEffectCategory.BENEFICIAL, 0x2DA8FF);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            teleportPlayer(player);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    private static void teleportPlayer(ServerPlayer player) {
        ServerLevel currentLevel = player.serverLevel();

        // 在末地使用时，传送到末地黑曜石平台附近。
        if (currentLevel.dimension() == Level.END) {
            teleportToEndPlatform(player, currentLevel);
            return;
        }

        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos targetPos = player.getRespawnPosition();
        float yaw = player.getRespawnAngle();

        // 没有床/重生锚时，回主世界出生点。
        if (targetLevel == null || targetPos == null) {
            targetLevel = player.server.overworld();
            targetPos = targetLevel.getSharedSpawnPos();
            yaw = targetLevel.getSharedSpawnAngle();
        }

        spawnDepartureParticles(player);

        player.stopRiding();
        player.teleportTo(
                targetLevel,
                targetPos.getX() + 0.5D,
                targetPos.getY() + 0.1D,
                targetPos.getZ() + 0.5D,
                yaw,
                player.getXRot()
        );
        player.resetFallDistance();

        spawnArrivalParticlesDelayed(player);
    }

    private static void teleportToEndPlatform(ServerPlayer player, ServerLevel endLevel) {
        spawnDepartureParticles(player);

        player.stopRiding();
        player.teleportTo(
                endLevel,
                100.5D,
                50.1D,
                0.5D,
                player.getYRot(),
                player.getXRot()
        );
        player.resetFallDistance();

        spawnArrivalParticlesDelayed(player);
    }

    /**
     * 在玩家当前位置生成到达粒子。
     */
    private static void spawnArrivalParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        level.sendParticles(
                player,
                ParticleTypes.PORTAL,
                true,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                64,
                0.8D,
                1.0D,
                0.8D,
                0.18D
        );
    }

    /**
     * 延迟 1 tick 后在目的地生成末影粒子。
     */
    private static void spawnArrivalParticlesDelayed(ServerPlayer player) {
        player.server.tell(new TickTask(player.server.getTickCount() + 1, () -> {
            if (player.isRemoved()) {
                return;
            }

            spawnArrivalParticles(player);
        }));
    }

    /**
     * 传送前生成末影粒子。
     */
    private static void spawnDepartureParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        level.sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                48,
                0.7D,
                0.9D,
                0.7D,
                0.15D
        );
    }


}