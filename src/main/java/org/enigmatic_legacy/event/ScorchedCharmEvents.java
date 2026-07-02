package org.enigmatic_legacy.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.util.BlazingCoreHelper;
import org.enigmatic_legacy.util.MagicQuartzRingHelper;
import org.enigmatic_legacy.util.ScorchedCharmHelper;

/**
 * 阳灼护符事件逻辑。
 * 功能：
 * 1. 免疫大多数火焰伤害；
 * 2. 可以在岩浆表面行走；
 * 3. 下蹲时允许潜入岩浆；
 * 4. 接触岩浆时每秒恢复 2 点生命；
 * 5. 攻击燃烧目标时获得 20% 生命汲取；
 * 6. 受到伤害时有 10% 概率抵御；
 * 7. 接触岩浆时抵御概率翻倍到 20%。
 */
public final class ScorchedCharmEvents {

    /**
     * 攻击燃烧目标时的生命汲取比例。
     * 0.20F = 造成伤害的 20%。
     */
    private static final float LIFESTEAL_MODIFIER = 0.20F;

    /**
     * 普通状态下抵御伤害概率。
     * 0.10F = 10%。
     */
    private static final float RESIST_DAMAGE_CHANCE = 0.10F;

    /**
     * 接触岩浆时抵御伤害概率。
     * 0.20F = 20%。
     */
    private static final float RESIST_DAMAGE_CHANCE_IN_LAVA = 0.20F;
    private static final float IRONS_SPELLBOOKS_FIRE_SPELL_RESISTANCE = 0.30F;
    private ScorchedCharmEvents() {
    }

    /**
     * 免疫火焰伤害，并处理概率抵御伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!ScorchedCharmHelper.hasScorchedCharm(target)) {
            return;
        }

        DamageSource source = event.getSource();

        /*
         * 免疫大多数火焰伤害：
         * - 着火；
         * - 火焰；
         * - 岩浆；
         * - 岩浆块；
         * - 其它带 minecraft:is_fire 标签的伤害。
         */
        if (source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.HOT_FLOOR)) {
            event.setCanceled(true);
            target.clearFire();
            return;
        }

        /*
         * 抵御下一次受伤：
         * - 默认 10%；
         * - 接触岩浆时翻倍为 20%。
         */
        float chance = isTouchingLava(target) ? RESIST_DAMAGE_CHANCE_IN_LAVA : RESIST_DAMAGE_CHANCE;

        if (target.getRandom().nextFloat() < chance) {
            event.setCanceled(true);

            target.level().playSound(
                    null,
                    target.blockPosition(),
                    SoundEvents.SHIELD_BLOCK,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.15F
            );
        }
    }

    /**
     * 攻击燃烧目标时触发生命汲取。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (!ScorchedCharmHelper.hasScorchedCharm(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();

        if (!target.isOnFire()) {
            return;
        }

        float damage = event.getNewDamage();

        if (damage <= 0.0F) {
            return;
        }

        /*
         * 生命汲取：
         * 攻击着火目标时，恢复造成伤害的 20%。
         */
        attacker.heal(damage * LIFESTEAL_MODIFIER);
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (!ScorchedCharmHelper.hasScorchedCharm(target) && !BlazingCoreHelper.hasBlazingCore(target)) {
            return;
        }

        if (!MagicQuartzRingHelper.isIronsSpellbooksFireDamage(event.getSource())) {
            return;
        }

        event.setNewDamage(event.getNewDamage() * (1.0F - IRONS_SPELLBOOKS_FIRE_SPELL_RESISTANCE));
    }

    private static boolean isTouchingLava(LivingEntity entity) {
        return entity.isInLava();
    }
}
