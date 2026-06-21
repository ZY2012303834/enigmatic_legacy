package org.enigmatic_legacy.effect;

import net.minecraft.core.BlockPos;
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
    }

    private static void teleportToEndPlatform(ServerPlayer player, ServerLevel endLevel) {
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
    }
}