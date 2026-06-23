package org.enigmatic_legacy.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.GolemHeart;
import org.enigmatic_legacy.util.GolemHeartHelper;

/**
 * 魔像之心伤害事件。
 */
public class GolemHeartEvents {

    /**
     * 免疫挤压、墙内窒息、仙人掌、钟乳石刺伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!GolemHeartHelper.hasGolemHeart(event.getEntity())) {
            return;
        }

        DamageSource source = event.getSource();

        if (isGolemHeartImmuneDamage(source)) {
            event.setCanceled(true);
        }
    }

    /**
     * Plus 版伤害调整：
     * 1. 无护甲时爆炸减伤；
     * 2. 魔法伤害易伤；
     * 3. 近战减伤。
     */
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        if (!GolemHeartHelper.hasGolemHeart(entity)) {
            return;
        }

        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        if (GolemHeart.hasNoArmor(entity) && source.is(DamageTypeTags.IS_EXPLOSION)) {
            damage *= 1.0F - ConfigCommon.GOLEM_HEART_EXPLOSION_RESISTANCE.get() / 100.0F;
        } else if (source.is(Tags.DamageTypes.IS_MAGIC)) {
            damage *= ConfigCommon.GOLEM_HEART_MAGIC_VULNERABILITY.get().floatValue();
        } else if (isMeleeDamage(source)) {
            damage *= 1.0F - ConfigCommon.GOLEM_HEART_MELEE_RESISTANCE.get() / 100.0F;
        }

        event.setNewDamage(damage);
    }

    private static boolean isGolemHeartImmuneDamage(DamageSource source) {
        return source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.STALAGMITE);
    }

    private static boolean isMeleeDamage(DamageSource source) {
        return source.getEntity() instanceof LivingEntity
                && source.getDirectEntity() == source.getEntity();
    }
}