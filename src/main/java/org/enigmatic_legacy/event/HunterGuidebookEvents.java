package org.enigmatic_legacy.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.AnimalGuidebook;
import org.enigmatic_legacy.item.items.HunterGuidebook;

/**
 * 野猎指南服务端事件。
 */
public final class HunterGuidebookEvents {

    private HunterGuidebookEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof OwnableEntity ownable)) {
            return;
        }

        Entity owner = ownable.getOwner();
        if (!(owner instanceof Player player)) {
            return;
        }

        if (!HunterGuidebook.hasGuidebook(player)) {
            return;
        }

        double range = ConfigCommon.HUNTER_GUIDE_EFFECTIVE_DISTANCE.get();
        if (range <= 0.0D || player.distanceToSqr(event.getEntity()) > range * range) {
            return;
        }

        float redirectedDamage = event.getAmount();
        if (AnimalGuidebook.hasGuidebook(player)) {
            float reduction = ConfigCommon.HUNTER_GUIDE_SYNERGY_DAMAGE_REDUCTION.get() / 100.0F;
            redirectedDamage *= Math.max(0.0F, 1.0F - reduction);
        }

        event.setCanceled(true);

        if (redirectedDamage > 0.0F) {
            player.hurt(event.getSource(), redirectedDamage);
        }
    }
}
