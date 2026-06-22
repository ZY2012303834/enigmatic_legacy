package org.enigmatic_legacy.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.MonsterCharmHelper;

public final class MonsterCharmEvents {

    private MonsterCharmEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        if (!MonsterCharmHelper.hasMonsterCharm(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();

        if (target.getType().getCategory() != MobCategory.MONSTER) {
            return;
        }

        float bonusPercent = ConfigCommon.MONSTER_CHARM_HOSTILE_DAMAGE.get();

        if (target.isInvertedHealAndHarm()) {
            bonusPercent += ConfigCommon.MONSTER_CHARM_UNDEAD_DAMAGE.get();
        }

        if (bonusPercent <= 0.0F) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0F + bonusPercent / 100.0F));
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!ConfigCommon.MONSTER_CHARM_DOUBLE_XP_ENABLED.get()) {
            return;
        }

        Player attacker = event.getAttackingPlayer();

        if (attacker == null) {
            return;
        }

        if (!MonsterCharmHelper.hasMonsterCharm(attacker)) {
            return;
        }

        if (event.getEntity().getType().getCategory() != MobCategory.MONSTER) {
            return;
        }

        event.setDroppedExperience(event.getDroppedExperience() * 2);
    }
}
