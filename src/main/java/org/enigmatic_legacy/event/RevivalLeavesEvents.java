package org.enigmatic_legacy.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.RevivalLeavesHelper;

/**
 * 复苏之叶事件逻辑。
 *
 * <p>物品类负责 Curios 佩戴、主动能力、自然恢复、作物和飞行；
 * 这里负责需要监听战斗/治疗/状态效果事件的部分。</p>
 */
public final class RevivalLeavesEvents {
    private static final String REVIVING_POISONED_TAG = "enigmatic_legacy.revival_leaves_poisoned";
    private static final String EFFECT_ADJUSTING_TAG = "enigmatic_legacy.revival_leaves_adjusting_effect";

    private RevivalLeavesEvents() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.getPersistentData().getBoolean(REVIVING_POISONED_TAG) && !entity.hasEffect(MobEffects.POISON)) {
            entity.getPersistentData().remove(REVIVING_POISONED_TAG);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!RevivalLeavesHelper.hasRevivalLeaves(target)) {
            return;
        }

        DamageSource source = event.getSource();

        // 原项目把 Wither 伤害列入免疫列表。
        if (source.is(DamageTypes.WITHER)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();

        if (RevivalLeavesHelper.hasRevivalLeaves(target)) {
            float damage = event.getNewDamage();

            if (source.is(DamageTypeTags.IS_FIRE)) {
                damage *= ConfigCommon.REVIVAL_LEAVES_FIRE_DAMAGE_VULNERABILITY.get().floatValue();
            }

            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                damage *= ConfigCommon.REVIVAL_LEAVES_PROJECTILE_DAMAGE_VULNERABILITY.get().floatValue();
            }

            event.setNewDamage(damage);
        }

        if (source.getEntity() instanceof LivingEntity attacker && RevivalLeavesHelper.hasRevivalLeaves(attacker)) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.POISON,
                    ConfigCommon.REVIVAL_LEAVES_POISON_TIME.get(),
                    ConfigCommon.REVIVAL_LEAVES_POISON_LEVEL.get(),
                    false,
                    true
            ), attacker);

            target.getPersistentData().putBoolean(REVIVING_POISONED_TAG, true);
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntity().getPersistentData().getBoolean(REVIVING_POISONED_TAG)) {
            event.setAmount(event.getAmount() * 0.25F);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || !RevivalLeavesHelper.hasRevivalLeaves(entity)) {
            return;
        }

        MobEffectInstance original = event.getEffectInstance();
        if (original == null || original.isInfiniteDuration()) {
            return;
        }

        CompoundTag data = entity.getPersistentData();
        if (data.getBoolean(EFFECT_ADJUSTING_TAG)) {
            return;
        }

        double modifier = ConfigCommon.REVIVAL_LEAVES_EFFECT_DURATION_MODIFIER.get() / 100.0D;
        int newDuration = Math.max(1, Mth.floor(original.getDuration() * modifier));

        if (newDuration == original.getDuration()) {
            return;
        }

        MobEffectInstance adjusted = new MobEffectInstance(
                original.getEffect(),
                newDuration,
                original.getAmplifier(),
                original.isAmbient(),
                original.isVisible(),
                original.showIcon()
        );

        data.putBoolean(EFFECT_ADJUSTING_TAG, true);
        entity.removeEffect(original.getEffect());
        entity.addEffect(adjusted, event.getEffectSource());
        data.remove(EFFECT_ADJUSTING_TAG);
    }
}
