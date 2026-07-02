package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.BlazingCoreHelper;

/**
 * 烈焰核心事件逻辑。
 * 说明：
 * 1. 物品类 BlazingCore 只负责 Curios 佩戴和 tooltip；
 * 2. 伤害免疫、岩浆过热、火焰反馈、药水时长调整都放在事件里；
 * 3. 这样玩家和其他可佩戴 Curios 的 LivingEntity 都能生效。
 */
public final class BlazingCoreEvents {
    /**
     * 存在实体 PersistentData 里的岩浆热量。
     * 进入岩浆时增加，离开岩浆后下降。
     */
    private static final String LAVA_HEAT_TAG = "enigmatic_legacy.blazing_core_lava_heat";

    /**
     * 防止调整药水时间时 remove + add 重新触发 MobEffectEvent.Added 导致递归。
     */
    private static final String EFFECT_ADJUSTING_TAG = "enigmatic_legacy.blazing_core_adjusting_effect";
    private BlazingCoreEvents() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player entity)) {
            return;
        }

        if (entity.level().isClientSide()) {
            return;
        }

        CompoundTag data = entity.getPersistentData();

        if (isCreativePlayer(entity)) {
            /*
             * 创造模式不触发烈焰之核功能。
             * 创造模式切换进去后，热力值直接清掉，避免 GUI 残留。
             */
            data.remove(LAVA_HEAT_TAG);
            return;
        }

        boolean hasBlazingCore = BlazingCoreHelper.hasBlazingCore(entity);

        /*
         * 自动灭火只在佩戴烈焰之核时生效。
         * 脱下后不应该继续自动灭火。
         */
        if (hasBlazingCore && entity.isOnFire()) {
            entity.clearFire();
        }

        /*
         * 佩戴时：
         * - 在岩浆中增加热力；
         * - 离开岩浆后降低热力。
         *
         * 脱下后：
         * - 不再增加热力；
         * - 但已有热力继续按原速度降低；
         * - 降到 0 后 GUI 自然消失。
         */
        handleLavaHeat(entity, data, hasBlazingCore);
    }

    /**
     * 处理岩浆临时免疫的“热量”。
     * 修复点：
     * 1. 不再只依赖 entity.isInLava()；
     * 2. 岩浆游泳、岩浆表面、脚下/身体方块为岩浆时，都算作正在接触岩浆；
     * 3. 这样热量会稳定增长，达到上限后烈焰之核不再取消岩浆伤害。
     * 设计：
     * - 接触岩浆时每 tick +1；
     * - 热量未满时免疫岩浆伤害；
     * - 热量满后不再拦截岩浆伤害；
     * - 离开岩浆后逐渐冷却。
     */
    private static void handleLavaHeat(LivingEntity entity, CompoundTag data, boolean hasBlazingCore) {
        int heat = data.getInt(LAVA_HEAT_TAG);
        int maxHeat = ConfigCommon.BLAZING_CORE_LAVA_IMMUNITY_TICKS.get();

        /*
         * 只有佩戴烈焰之核时，接触岩浆才会增加热力。
         *
         * 这里必须使用 isTouchingLava(entity)，不能只用 entity.isInLava()。
         * 否则新增“岩浆中游泳 / 岩浆表面移动”后，某些状态下热量不会增长，
         * 导致 heat 永远小于 maxHeat，岩浆伤害被永久取消。
         */
        if (hasBlazingCore && isTouchingLava(entity)) {
            data.putInt(LAVA_HEAT_TAG, Math.min(maxHeat, heat + 1));
            return;
        }

        /*
         * 未佩戴、离开岩浆，或者脱下烈焰之核后：
         * 只要热力值还大于 0，就继续按配置速度冷却。
         */
        if (heat > 0) {
            int cooldown = Math.max(1, ConfigCommon.BLAZING_CORE_LAVA_COOLDOWN_PER_TICK.get());
            int newHeat = Math.max(0, heat - cooldown);

            if (newHeat <= 0) {
                data.remove(LAVA_HEAT_TAG);
            } else {
                data.putInt(LAVA_HEAT_TAG, newHeat);
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!BlazingCoreHelper.hasBlazingCore(target)) {
            return;
        }

        DamageSource source = event.getSource();

        if (isCreativePlayer(target) && source.is(DamageTypes.LAVA)) {
            target.getPersistentData().remove(LAVA_HEAT_TAG);
            return;
        }

        /*
         * 岩浆伤害：
         *
         * 必须最先单独处理，并且无论是否取消，都直接 return。
         * 这样可以避免岩浆伤害继续落入 DamageTypeTags.IS_FIRE 分支，
         * 否则岩浆会被当成普通火焰伤害永久免疫。
         */
        if (isLavaDamage(source)) {
            int heat = target.getPersistentData().getInt(LAVA_HEAT_TAG);
            int maxHeat = ConfigCommon.BLAZING_CORE_LAVA_IMMUNITY_TICKS.get();

            /*
             * 热量未满：取消岩浆伤害。
             * 热量满：不取消，让玩家开始受到岩浆伤害。
             */
            if (heat < maxHeat) {
                event.setCanceled(true);
            }

            return;
        }

        /*
         * 普通火焰伤害：
         * 包括着火、火焰、岩浆块等火焰标签伤害。
         * 注意：LAVA 已经在上面单独处理，避免把岩浆永久免疫掉。
         */
        if (source.is(DamageTypeTags.IS_FIRE)) {
            event.setCanceled(true);
        }
    }

    /**
     * 判断伤害是否为岩浆伤害。
     * 说明：
     * 正常情况下 source.is(DamageTypes.LAVA) 就够了。
     * 额外判断 msgId 是为了避免不同映射 / 兼容环境下岩浆伤害没被 ResourceKey 命中。
     */
    private static boolean isLavaDamage(DamageSource source) {
        return source.is(DamageTypes.LAVA) || "lava".equals(source.getMsgId());
    }

    /**
     * 判断实体是否正在接触岩浆。
     * 修复烈焰之核永久免疫的关键：
     * 新增岩浆游泳后，部分状态下 entity.isInLava() 不一定足够稳定。
     * 所以同时检查身体位置、眼睛位置、脚下位置的流体状态。
     */
    private static boolean isTouchingLava(LivingEntity entity) {
        if (entity.isInLava()) {
            return true;
        }

        BlockPos bodyPos = entity.blockPosition();
        BlockPos eyePos = BlockPos.containing(entity.getEyePosition());

        return entity.level().getFluidState(bodyPos).is(FluidTags.LAVA)
                || entity.level().getFluidState(bodyPos.below()).is(FluidTags.LAVA)
                || entity.level().getFluidState(eyePos).is(FluidTags.LAVA);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (!BlazingCoreHelper.hasBlazingCore(target)) {
            return;
        }

        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        /*
         * 元素弱点：
         * 烈焰核心佩戴者受到水生生物攻击时，受到更多伤害。
         */
        if (isAquaticAttacker(source)) {
            damage *= ConfigCommon.BLAZING_CORE_AQUATIC_DAMAGE_VULNERABILITY.get().floatValue();
        }

        event.setNewDamage(damage);

        /*
         * 火焰反馈：
         * 原逻辑只对 MOB_ATTACK、PLAYER_ATTACK、GENERIC 这类近战/通用伤害反馈。
         * 弓箭、火球等远程伤害不在这里反馈。
         */
        if (damage > 0.0F && isFeedbackDamageSource(source) && source.getEntity() instanceof LivingEntity attacker) {
            applyFireFeedback(target, attacker);
        }
    }

    /**
     * 对攻击者造成火焰反馈伤害，并点燃攻击者。
     */
    private static void applyFireFeedback(LivingEntity bearer, LivingEntity attacker) {
        if (attacker.fireImmune()) {
            return;
        }

        float feedbackDamage = ConfigCommon.BLAZING_CORE_DAMAGE_FEEDBACK.get().floatValue();
        int ignitionSeconds = ConfigCommon.BLAZING_CORE_IGNITION_FEEDBACK.get();

        if (feedbackDamage > 0.0F) {
            attacker.hurt(
                    bearer.damageSources().source(DamageTypes.ON_FIRE, bearer),
                    feedbackDamage
            );
        }

        if (ignitionSeconds > 0) {
            attacker.igniteForSeconds(ignitionSeconds);
        }
    }

    /**
     * 哪些伤害类型会触发烈焰核心的火焰反馈。
     */
    private static boolean isFeedbackDamageSource(DamageSource source) {
        return source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.PLAYER_ATTACK)
                || source.is(DamageTypes.GENERIC);
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || !BlazingCoreHelper.hasBlazingCore(entity)) {
            return;
        }

        MobEffectInstance original = event.getEffectInstance();

        if (original == null || original.isInfiniteDuration()) {
            return;
        }

        CompoundTag data = entity.getPersistentData();

        // 防止 removeEffect + addEffect 再次触发本事件形成无限递归。
        if (data.getBoolean(EFFECT_ADJUSTING_TAG)) {
            return;
        }

        /*
         * 原玩法：
         * - 大多数状态效果持续时间减半；
         * - 熔火之心 / 抗火类效果持续时间翻倍。
         *
         * 你当前项目还没有独立的 Molten Heart 效果，
         * 所以先把原版 FIRE_RESISTANCE 作为“抗火类效果”处理。
         */
        double modifier = isMoltenHeartLike(original)
                ? ConfigCommon.BLAZING_CORE_MOLTEN_EFFECT_DURATION_MODIFIER.get() / 100.0D
                : ConfigCommon.BLAZING_CORE_EFFECT_DURATION_MODIFIER.get() / 100.0D;

        int oldDuration = original.getDuration();
        int newDuration = Math.max(1, Mth.floor(oldDuration * modifier));

        if (newDuration == oldDuration) {
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

    /**
     * 当前项目暂时没有“熔火之心 / Molten Heart”自定义效果。
     * 先把原版 FIRE_RESISTANCE 当成抗火类效果。
     * 后续如果你新增了 ModEffects.MOLTEN_HEART，可以把这里扩展为：
     * return instance.is(ModEffects.MOLTEN_HEART) || instance.is(MobEffects.FIRE_RESISTANCE);
     */
    private static boolean isMoltenHeartLike(MobEffectInstance instance) {
        return instance.is(MobEffects.FIRE_RESISTANCE);
    }

    private static boolean isAquaticAttacker(DamageSource source) {
        return isAquaticEntity(source.getEntity()) || isAquaticEntity(source.getDirectEntity());
    }

    private static boolean isAquaticEntity(Entity entity) {
        if (entity == null) {
            return false;
        }

        return entity instanceof Drowned
                || entity instanceof Guardian
                || entity instanceof ElderGuardian
                || entity.getType().is(EntityTypeTags.AQUATIC);
    }

    /**
     * 判断实体是否为创造模式玩家。
     * 只用于烈焰之核的岩浆过热功能过滤；
     * 避免创造模式玩家显示 GUI 或积累热量。
     */
    private static boolean isCreativePlayer(LivingEntity entity) {
        return entity instanceof Player player && player.getAbilities().instabuild;
    }
}
