package org.enigmatic_legacy.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.spellstone.ForgottenIceCrystal;
import org.enigmatic_legacy.util.ForgottenIceCrystalHelper;

/**
 * 忘却冰晶事件逻辑。
 *
 * <p>物品类负责 Curios 佩戴、主动能力、冻结时间累计和 tooltip。</p>
 *
 * <p>这里负责需要监听伤害与实体 tick 的部分：伤害倍率、冻结反制、近战追加冻结、硬冻结移动限制。</p>
 */
public final class ForgottenIceCrystalEvents {
    private ForgottenIceCrystalEvents() {
    }

    /**
     * 处理硬冻结目标。
     *
     * <p>硬冻结目标会被锁住水平移动，并临时附加极强的移动速度降低属性。</p>
     *
     * <p>目标不再完全冻结时，移除硬冻结标记和属性修正。</p>
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.level().isClientSide()) {
            return;
        }

        boolean hardFrozen = entity.getPersistentData().getBoolean(ForgottenIceCrystal.HARD_FROZEN_TAG);

        if (!hardFrozen) {
            return;
        }

        entity.getAttributes().removeAttributeModifiers(ForgottenIceCrystal.getHardFrozenModifiers());

        if (!entity.isFullyFrozen()) {
            entity.getPersistentData().remove(ForgottenIceCrystal.HARD_FROZEN_TAG);
            entity.getPersistentData().remove(ForgottenIceCrystal.FROZEN_TICK_TAG);
            return;
        }

        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(0.0D, motion.y, 0.0D);
        entity.hasImpulse = true;
        entity.getAttributes().addTransientAttributeModifiers(ForgottenIceCrystal.getHardFrozenModifiers());
    }

    /**
     * 免疫冻结伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!ForgottenIceCrystalHelper.hasForgottenIceCrystal(target)) {
            return;
        }

        if (event.getSource().is(DamageTypes.FREEZE)) {
            event.setCanceled(true);
        }
    }

    /**
     * 处理忘却冰晶的伤害相关效果。
     *
     * <p>佩戴者自身：降低弹射物和音波伤害，放大火焰和摔落伤害，并在受到近战类攻击时冻结攻击者。</p>
     *
     * <p>佩戴者攻击目标：近战类攻击追加冻结时间；目标完全冻结时，额外提高最终伤害。</p>
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        if (ForgottenIceCrystalHelper.hasForgottenIceCrystal(target)) {
            damage = modifyDamageTaken(target, source, damage);
        }

        if (source.getEntity() instanceof LivingEntity attacker
                && ForgottenIceCrystalHelper.hasForgottenIceCrystal(attacker)
                && isNemesisDamage(source)) {
            if (target.canFreeze()) {
                target.setTicksFrozen(target.getTicksFrozen() + ConfigCommon.FORGOTTEN_ICE_CRYSTAL_MELEE_FREEZE_TICKS.get());
            }

            if (target.isFullyFrozen()) {
                damage *= 1.0F + ConfigCommon.FORGOTTEN_ICE_CRYSTAL_FROST_DAMAGE_BOOST.get() / 100.0F;
            }
        }

        event.setNewDamage(damage);
    }

    private static float modifyDamageTaken(LivingEntity target, DamageSource source, float damage) {
        float modified = damage;

        if (source.is(DamageTypeTags.IS_PROJECTILE) || source.is(DamageTypes.SONIC_BOOM)) {
            modified *= 1.0F - ConfigCommon.FORGOTTEN_ICE_CRYSTAL_PROJECTILE_AND_SONIC_RESISTANCE.get() / 100.0F;
        }

        if (source.is(DamageTypeTags.IS_FIRE)) {
            modified *= ConfigCommon.FORGOTTEN_ICE_CRYSTAL_FIRE_DAMAGE_VULNERABILITY.get().floatValue();
        }

        if (source.is(DamageTypes.FALL)) {
            modified *= ConfigCommon.FORGOTTEN_ICE_CRYSTAL_FALL_DAMAGE_VULNERABILITY.get().floatValue();
        }

        if (source.getEntity() instanceof LivingEntity attacker && isNemesisDamage(source) && attacker.canFreeze()) {
            attacker.hurt(target.damageSources().source(DamageTypes.FREEZE, target),
                    ConfigCommon.FORGOTTEN_ICE_CRYSTAL_RETALIATION_FREEZE_DAMAGE.get().floatValue());
            attacker.setTicksFrozen(Math.max(attacker.getTicksFrozen(), attacker.getTicksRequiredToFreeze()));
        }

        return modified;
    }

    private static boolean isNemesisDamage(DamageSource source) {
        return source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.PLAYER_ATTACK)
                || source.is(DamageTypes.GENERIC);
    }
}
