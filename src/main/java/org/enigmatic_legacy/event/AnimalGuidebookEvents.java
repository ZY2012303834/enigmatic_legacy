package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.AnimalGuidebook;
import org.enigmatic_legacy.item.items.OdeToLiving;

import java.util.List;

/**
 * 兽友指南服务端事件。
 */
public final class AnimalGuidebookEvents {

    private AnimalGuidebookEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof Player player
                && AnimalGuidebook.hasGuidebook(player)
                && !OdeToLiving.isHeldOde(player.getMainHandItem())
                && AnimalGuidebook.isProtectedAnimal(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (event.getEntity() instanceof Player player
                && attacker instanceof Hoglin
                && AnimalGuidebook.hasGuidebook(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 20 != 0 || !AnimalGuidebook.hasGuidebook(player)) {
            return;
        }

        double range = ConfigCommon.CURSED_RING_NEUTRAL_ANGER_RANGE.get();
        AABB box = player.getBoundingBox().inflate(range);
        List<Hoglin> hoglins = player.level().getEntitiesOfClass(Hoglin.class, box, Hoglin::isAlive);

        for (Hoglin hoglin : hoglins) {
            if (!AnimalGuidebook.isTargetingGuidebookHolder(hoglin, player)) {
                continue;
            }

            hoglin.setTarget(null);
            hoglin.setAggressive(false);
        }
    }
}
