package org.enigmatic_legacy.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.MajesticElytraHelper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MajesticElytraEvents {
    private static final int FALL_DAMAGE_GRACE_TICKS = 20;
    private static final ConcurrentMap<UUID, Boolean> BOOSTING_PLAYERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, Long> LAST_MAJESTIC_FLIGHT_TICKS = new ConcurrentHashMap<>();

    private MajesticElytraEvents() {
    }

    public static void setBoosting(ServerPlayer player, boolean boosting) {
        if (boosting) {
            BOOSTING_PLAYERS.put(player.getUUID(), true);
        } else {
            BOOSTING_PLAYERS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = MajesticElytraHelper.getEquippedStack(player);

        /*
         * 只要壮丽鞘翅正在承担滑翔，就记录最近一次滑翔 tick 并清除摔落距离。
         * 后续 LivingFallEvent 会用这个时间戳判断本次落地是否来自壮丽鞘翅滑翔，
         * 避免把普通下落也错误免疫掉。
         */
        if (!stack.isEmpty() && player.isFallFlying()) {
            LAST_MAJESTIC_FLIGHT_TICKS.put(player.getUUID(), player.level().getGameTime());
            player.resetFallDistance();
        } else {
            removeExpiredFlightRecord(player);
        }

        /*
         * 混沌之傲的俯冲落地在原扩展中同时监听普通下落事件和 Caelus 的 flyable fall 事件。
         * 当前项目没有接入 Caelus，且背饰栏鞘翅的落地路径有时不会产生 LivingFallEvent，
         * 因此这里在服务端 tick 里补一个兜底：玩家刚结束滑翔并且已经触地时，
         * 仍然尝试触发一次混沌之傲落地伤害。
         */
        Long lastFlightTick = LAST_MAJESTIC_FLIGHT_TICKS.get(player.getUUID());
        if (!stack.isEmpty()
                && player.onGround()
                && lastFlightTick != null
                && player.level().getGameTime() - lastFlightTick <= FALL_DAMAGE_GRACE_TICKS
                && TheArroganceOfChaosEvents.tryTriggerDescending(player, stack)) {
            player.resetFallDistance();
            BOOSTING_PLAYERS.remove(player.getUUID());
            LAST_MAJESTIC_FLIGHT_TICKS.remove(player.getUUID(), lastFlightTick);
            return;
        }

        if (!BOOSTING_PLAYERS.containsKey(player.getUUID())) {
            return;
        }

        if (stack.isEmpty() || !player.isFallFlying()) {
            BOOSTING_PLAYERS.remove(player.getUUID());
            return;
        }

        /*
         * 助推会直接改写玩家速度；如果其它逻辑在同一 tick 里提前累计了 fallDistance，
         * 落地时可能被当作普通下落结算。助推期间再次清零，确保壮丽鞘翅飞行不会留下摔落伤害债。
         */
        player.resetFallDistance();

        boostPlayer(player, stack);
        spawnBoostParticles(player);

        int nextFlightTick = player.getFallFlyingTicks() + 1;
        int damageInterval = stack.is(ModItems.CHAOS_ELYTRA.get()) ? 6 : 5;

        if (nextFlightTick % damageInterval == 0) {
            stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Long lastFlightTick = LAST_MAJESTIC_FLIGHT_TICKS.get(player.getUUID());

        if (lastFlightTick == null) {
            return;
        }

        long elapsedTicks = player.level().getGameTime() - lastFlightTick;

        if (elapsedTicks > FALL_DAMAGE_GRACE_TICKS) {
            LAST_MAJESTIC_FLIGHT_TICKS.remove(player.getUUID(), lastFlightTick);
            return;
        }

        ItemStack stack = MajesticElytraHelper.getEquippedStack(player);

        /*
         * 混沌之傲是壮丽鞘翅的七咒高阶变体，也复用本事件记录“刚刚仍在滑翔”的落地窗口。
         * 如果玩家正在助推并垂直俯冲，优先触发混沌之傲的落地范围伤害；
         * 之后同样取消本次 fall 事件，避免技能触发后又吃到普通摔落伤害。
         */
        if (TheArroganceOfChaosEvents.tryTriggerDescending(player, stack)) {
            event.setCanceled(true);
            event.setDistance(0.0F);
            player.resetFallDistance();
            BOOSTING_PLAYERS.remove(player.getUUID());
            LAST_MAJESTIC_FLIGHT_TICKS.remove(player.getUUID(), lastFlightTick);
            return;
        }

        /*
         * 背饰栏壮丽鞘翅通过 mixin 接入原版滑翔后，某些落地路径仍可能保留飞行期间累计的 fallDistance。
         * 如果玩家刚刚确实在使用壮丽鞘翅滑翔，取消这次落地伤害并清理记录。
         */
        event.setCanceled(true);
        event.setDistance(0.0F);
        player.resetFallDistance();
        LAST_MAJESTIC_FLIGHT_TICKS.remove(player.getUUID(), lastFlightTick);
    }

    private static void removeExpiredFlightRecord(ServerPlayer player) {
        Long lastFlightTick = LAST_MAJESTIC_FLIGHT_TICKS.get(player.getUUID());

        if (lastFlightTick != null
                && player.level().getGameTime() - lastFlightTick > FALL_DAMAGE_GRACE_TICKS) {
            LAST_MAJESTIC_FLIGHT_TICKS.remove(player.getUUID(), lastFlightTick);
        }
    }

    private static void boostPlayer(ServerPlayer player, ItemStack stack) {
        double speedModifier = stack.is(ModItems.CHAOS_ELYTRA.get())
                ? ConfigCommon.CHAOS_ELYTRA_FLYING_SPEED_MODIFIER.get()
                : 1.0D;
        Vec3 look = player.getLookAngle().scale(speedModifier);
        Vec3 movement = player.getDeltaMovement();

        player.setDeltaMovement(movement.add(
                look.x * 0.1D + (look.x * 1.5D - movement.x) * 0.5D,
                look.y * 0.1D + (look.y * 1.5D - movement.y) * 0.5D,
                look.z * 0.1D + (look.z * 1.5D - movement.z) * 0.5D
        ));
        player.hurtMarked = true;
    }

    private static void spawnBoostParticles(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            double x = player.getX() + (player.getRandom().nextDouble() - 0.5D);
            double y = player.getY() + player.getBbHeight() * 0.35D + (player.getRandom().nextDouble() - 0.5D);
            double z = player.getZ() + (player.getRandom().nextDouble() - 0.5D);
            double dx = (player.getRandom().nextDouble() - 0.5D) * 0.2D;
            double dy = (player.getRandom().nextDouble() - 0.5D) * 0.2D;
            double dz = (player.getRandom().nextDouble() - 0.5D) * 0.2D;

            level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 1, dx, dy, dz, 0.0D);
        }
    }
}
