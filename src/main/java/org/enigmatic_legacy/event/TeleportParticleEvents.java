package org.enigmatic_legacy.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 传送粒子事件。
 *
 * <p>用于处理扭曲魔镜、召回药水等传送后的到达粒子。
 *
 * <p>不要在 teleportTo(...) 后立刻刷到达粒子。
 * 跨维度或远距离传送时，客户端可能还没有完成位置同步，
 * 立刻发粒子包容易看不到。
 *
 * <p>这里改为延迟数 tick 后，从服务器玩家列表重新获取玩家，
 * 再在玩家当前所在维度和当前位置生成粒子，稳定性更高。
 */
public final class TeleportParticleEvents {

    private static final List<ScheduledArrivalParticles> SCHEDULED_PARTICLES = new ArrayList<>();

    private TeleportParticleEvents() {
    }

    /**
     * 传送前粒子。
     *
     * <p>这个可以立刻播放，因为玩家还在原地。
     */
    public static void spawnDepartureParticles(ServerPlayer player) {
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

    /**
     * 安排传送到达粒子。
     *
     * @param player     被传送的玩家
     * @param delayTicks 延迟 tick 数，建议 3 到 5
     */
    public static void scheduleArrivalParticles(ServerPlayer player, int delayTicks) {
        SCHEDULED_PARTICLES.add(new ScheduledArrivalParticles(
                player.getUUID(),
                Math.max(1, delayTicks)
        ));
    }

    /**
     * 服务端 tick 后处理延迟粒子。
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (SCHEDULED_PARTICLES.isEmpty()) {
            return;
        }

        MinecraftServer server = event.getServer();
        Iterator<ScheduledArrivalParticles> iterator = SCHEDULED_PARTICLES.iterator();

        while (iterator.hasNext()) {
            ScheduledArrivalParticles scheduled = iterator.next();
            scheduled.ticks--;

            if (scheduled.ticks > 0) {
                continue;
            }

            ServerPlayer player = server.getPlayerList().getPlayer(scheduled.playerId);

            if (player != null && !player.isRemoved()) {
                spawnArrivalParticles(player);
            }

            iterator.remove();
        }
    }

    /**
     * 传送到目的地后的粒子。
     *
     * <p>这里同时做两件事：
     * <ul>
     *     <li>指定发给被传送玩家，保证本人能看到；</li>
     *     <li>广播给附近玩家，保证其他人也能看到。</li>
     * </ul>
     */
    private static void spawnArrivalParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        // 发给被传送玩家本人，最稳。
        level.sendParticles(
                player,
                ParticleTypes.PORTAL,
                true,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                80,
                0.9D,
                1.1D,
                0.9D,
                0.2D
        );

        // 广播给目标点附近的其他玩家。
        level.sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                80,
                0.9D,
                1.1D,
                0.9D,
                0.2D
        );
    }

    private static final class ScheduledArrivalParticles {
        private final UUID playerId;
        private int ticks;

        private ScheduledArrivalParticles(UUID playerId, int ticks) {
            this.playerId = playerId;
            this.ticks = ticks;
        }
    }
}