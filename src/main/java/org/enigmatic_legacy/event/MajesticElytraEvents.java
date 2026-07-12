package org.enigmatic_legacy.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.util.MajesticElytraHelper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MajesticElytraEvents {
    private static final ConcurrentMap<UUID, Boolean> BOOSTING_PLAYERS = new ConcurrentHashMap<>();

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

        if (!BOOSTING_PLAYERS.containsKey(player.getUUID())) {
            return;
        }

        ItemStack stack = MajesticElytraHelper.getEquippedStack(player);

        if (stack.isEmpty() || !player.isFallFlying()) {
            BOOSTING_PLAYERS.remove(player.getUUID());
            return;
        }

        boostPlayer(player);
        spawnBoostParticles(player);

        int nextFlightTick = player.getFallFlyingTicks() + 1;
        if (nextFlightTick % 5 == 0) {
            stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
        }
    }

    private static void boostPlayer(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
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
