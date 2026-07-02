package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.OdeToLiving;

import java.util.List;

/**
 * Server-side behavior for Ode to Living Beings.
 */
public final class OdeToLivingEvents {
    private OdeToLivingEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (event.getEntity() instanceof Player player
                && attacker instanceof Hoglin
                && OdeToLiving.hasOde(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onHoglinChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Hoglin)) {
            return;
        }

        if (!(event.getNewAboutToBeSetTarget() instanceof Player player)) {
            return;
        }

        if (OdeToLiving.hasOde(player)) {
            event.setCanceled(true);
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 20 != 0 || !OdeToLiving.hasOde(player)) {
            return;
        }

        calmProtectedAnimals(player);
    }

    private static void calmProtectedAnimals(ServerPlayer player) {
        double range = ConfigCommon.CURSED_RING_NEUTRAL_ANGER_RANGE.get();
        AABB box = player.getBoundingBox().inflate(range);

        List<Animal> animals = player.level().getEntitiesOfClass(
                Animal.class,
                box,
                animal -> animal.isAlive() && OdeToLiving.isProtectedByOde(player, animal)
        );

        for (Animal animal : animals) {
            if (!(animal instanceof Mob mob) || mob.getTarget() != player) {
                continue;
            }

            mob.setTarget(null);

            if (animal instanceof NeutralMob neutralMob) {
                neutralMob.stopBeingAngry();
            }
        }

        List<Hoglin> hoglins = player.level().getEntitiesOfClass(
                Hoglin.class,
                box,
                hoglin -> hoglin.isAlive() && hoglin.getTarget() == player
        );

        for (Hoglin hoglin : hoglins) {
            hoglin.setTarget(null);
            hoglin.setAggressive(false);
        }
    }
}
