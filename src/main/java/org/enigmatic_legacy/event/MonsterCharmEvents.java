package org.enigmatic_legacy.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.MonsterCharmHelper;

/**
 * 怪物猎人勋章事件处理。
 */
public class MonsterCharmEvents {

    /**
     * 提高对亡灵和敌对生物的伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        if (!MonsterCharmHelper.hasMonsterCharm(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();

        float bonusPercent = 0.0F;

        // 亡灵生物增伤。
        // 1.21.1 没有旧版 MobType.UNDEAD；
        // isInvertedHealAndHarm() 可判断亡灵治疗/伤害反转特性。
        if (target.getType().getCategory() == MobCategory.MONSTER && target.isInvertedHealAndHarm()) {
            bonusPercent += ConfigCommon.MONSTER_CHARM_UNDEAD_DAMAGE.get();
        }

        // 敌对生物增伤。
        if (target instanceof Enemy) {
            bonusPercent += ConfigCommon.MONSTER_CHARM_HOSTILE_DAMAGE.get();
        }

        if (bonusPercent <= 0.0F) {
            return;
        }

        float multiplier = 1.0F + bonusPercent / 100.0F;
        event.setAmount(event.getAmount() * multiplier);
    }

    /**
     * 怪物经验翻倍。
     */
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

        if (!(event.getEntity() instanceof Enemy)) {
            return;
        }

        event.setDroppedExperience(event.getDroppedExperience() * 2);
    }
}