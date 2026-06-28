package org.enigmatic_legacy.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.MagicQuartzRingHelper;

/**
 * 魔法石英戒指事件。
 * 负责实际处理魔法伤害减免。
 */
@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public final class MagicQuartzRingEvents {

    private MagicQuartzRingEvents() {
    }

    /**
     * 玩家受到魔法类伤害前，若佩戴魔法石英戒指，则减少 30% 伤害。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!MagicQuartzRingHelper.hasMagicQuartzRing(player)) {
            return;
        }

        if (!MagicQuartzRingHelper.isMagicQuartzRingDamage(event.getSource())) {
            return;
        }

        event.setNewDamage(MagicQuartzRingHelper.reduceMagicDamage(event.getNewDamage()));
    }
}