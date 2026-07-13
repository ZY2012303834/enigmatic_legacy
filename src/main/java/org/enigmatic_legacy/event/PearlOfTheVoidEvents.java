package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.spellstone.PearlOfTheVoid;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.util.OwnedEntityHelper;
import org.enigmatic_legacy.util.PearlOfTheVoidHelper;
import org.enigmatic_legacy.util.TreasureHunterCharmHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 虚空珍珠事件逻辑。

 * 这里处理所有被动效果：
 * 1. 不再需要呼吸空气；
 * 2. 免疫溺水伤害；
 * 3. 免疫墙内窒息伤害；
 * 4. 每 tick 熄灭身上的火；
 * 5. 下一 tick 清除绝大多数状态效果；
 * 6. 攻击附加凋零；
 * 7. 黑暗光环；
 * 8. 35% 概率抵挡致命伤害。
 */
public final class PearlOfTheVoidEvents {
    /*
     * 灾变 Cataclysm 咒魂胸甲复活后附加的“幽灵病”效果。
     *
     * 该效果用于阻止咒魂胸甲在短时间内反复触发复活。
     * 虚空珍珠的常驻净化如果把它清掉，会导致咒魂胸甲复活冷却失效，形成无限复活漏洞。
     * 这里直接硬编码保留该效果，不把它暴露为配置项，避免服务器配置误删后重新引入漏洞。
     */
    private static final ResourceLocation CATACLYSM_GHOST_SICKNESS = ResourceLocation.fromNamespaceAndPath(
            "cataclysm",
            "ghost_sickness"
    );

    private PearlOfTheVoidEvents() {
    }

    /*
     * 每 tick 处理常驻被动：
     * - 氧气补满；
     * - 清火；
     * - 清除状态效果；
     * - 每 10 tick 扫描黑暗中的附近生物。
     */
    /**
     * 每 tick 处理虚空珍珠佩戴者的常驻被动。

     * 重要优化：
     * 这里只检查 ServerPlayer，不再检查所有 LivingEntity。

     * 原因：
     * 如果对所有生物每 tick 都调用 CuriosApi 查询，会非常掉帧，
     * 尤其是附近怪物多的时候，还可能导致退出保存时主线程迟迟停不下来。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // 玩家每 tick 处理完整被动；女仆等 OwnableEntity 只在黑暗光环间隔检查，避免对所有生物高频查询 Curios。
        if (!(event.getEntity() instanceof LivingEntity bearer)
                || (!(bearer instanceof ServerPlayer)
                && (!(bearer instanceof OwnableEntity)
                || bearer.tickCount % PearlOfTheVoid.DARKNESS_INTERVAL_TICKS != 0))) {
            return;
        }

        // 只对真正佩戴虚空珍珠的玩家生效。
        if (!PearlOfTheVoidHelper.hasPearlOfTheVoid(bearer)) {
            return;
        }

        // 不再需要呼吸空气：每 tick 补满氧气。
        bearer.setAirSupply(bearer.getMaxAirSupply());

        // 未写明效果：每 tick 熄灭身上的火。
        if (bearer.isOnFire()) {
            bearer.clearFire();
        }

        // 免疫状态效果：下一 tick 清除。
        // 这里只对佩戴者执行，不再对所有生物执行。
        removeForbiddenEffects(bearer);

        // 黑暗光环每 10 tick，也就是 0.5 秒，触发一次。
        if (bearer.tickCount % PearlOfTheVoid.DARKNESS_INTERVAL_TICKS == 0) {
            applyDarknessAura(bearer);
        }
    }

    /**
     * IncomingDamage 阶段可以直接取消伤害。

     * 用于：
     * 1. 免疫溺水伤害；
     * 2. 免疫墙内窒息伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!PearlOfTheVoidHelper.hasPearlOfTheVoid(target)) {
            return;
        }

        DamageSource source = event.getSource();

        // 不再需要呼吸空气：同时取消原版溺水伤害。
        if (source.is(DamageTypes.DROWN)) {
            event.setCanceled(true);
            return;
        }

        // 未写明效果：免疫墙内窒息伤害。
        if (source.is(DamageTypes.IN_WALL)) {
            event.setCanceled(true);
        }
    }

    /**
     * LivingDamageEvent.Pre 可以修改最终伤害。

     * 用于：
     * 1. 攻击附加凋零；
     * 2. 35% 概率抵挡致命伤害。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        /*
         * 佩戴者攻击会造成凋零。
         *
         * 注意：
         * 这里不直接增加伤害，只给目标附加凋零效果。
         * 所以力量、暴击、锋利等普通伤害系统不会影响这个附加效果。
         */
        if (source.getEntity() instanceof LivingEntity attacker
                && attacker != target
                && PearlOfTheVoidHelper.hasPearlOfTheVoid(attacker)
                && !(attacker instanceof Player player && OwnedEntityHelper.isProtectedPlayerOwnedAlly(player, target))
                && !OwnedEntityHelper.isOwnerProtectedFromOwnedAlly(attacker, target)
                && damage > 0.0F) {

            target.addEffect(new MobEffectInstance(
                    MobEffects.WITHER,
                    PearlOfTheVoid.ATTACK_WITHER_DURATION,
                    PearlOfTheVoid.ATTACK_WITHER_AMPLIFIER
            ), attacker);
        }

        /*
         * 35% 概率抵挡致命伤害。
         *
         * 这里在最终伤害阶段判断：
         * 如果本次伤害会让佩戴者死亡，就有 35% 概率把伤害改成 0。
         */
        if (PearlOfTheVoidHelper.hasPearlOfTheVoid(target)
                && isFatalDamage(target, damage)
                && canBlockFatalDamage(source)
                && target.getRandom().nextInt(100) < PearlOfTheVoid.DEATH_PROTECTION_CHANCE) {

            event.setNewDamage(0.0F);

            // 保险：确保血量至少为 1，避免某些模组同时处理伤害导致死亡。
            if (target.getHealth() < 1.0F) {
                target.setHealth(1.0F);
            }

            // 给一点无敌帧，避免同一瞬间连续伤害立刻击杀。
            target.invulnerableTime = Math.max(target.invulnerableTime, 20);
        }
    }

    /**
     * 判断本次伤害是否足以致命。
     */
    private static boolean isFatalDamage(LivingEntity target, float damage) {
        return damage >= target.getHealth();
    }

    /**
     * 判断致命伤害是否可以被虚空珍珠抵挡。

     * 这里不抵挡掉出世界伤害。
     * 否则虚空、kill 指令、某些强制死亡逻辑会变得很怪。
     */
    private static boolean canBlockFatalDamage(DamageSource source) {
        return !source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    /**
     * 清除佩戴者身上的状态效果。

     * 保留例外：
     * 1. 灾变咒魂胸甲用于防止反复复活的幽灵病；
     * 2. 配置白名单中显式保留的效果；
     * 3. 禁忌之果的隐藏同步效果；
     * 4. 猎宝者护符提供的夜视。
     */
    private static void removeForbiddenEffects(LivingEntity entity) {
        List<Holder<MobEffect>> effectsToRemove = new ArrayList<>();

        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (!shouldKeepEffect(entity, instance)) {
                effectsToRemove.add(instance.getEffect());
            }
        }

        for (Holder<MobEffect> effect : effectsToRemove) {
            entity.removeEffect(effect);
        }
    }

    /**
     * 判断某个效果是否不应该被虚空珍珠清理。
     */
    private static boolean shouldKeepEffect(LivingEntity entity, MobEffectInstance instance) {
        /*
         * 灾变 Cataclysm 的 cataclysm:ghost_sickness 不能清。
         * 咒魂胸甲复活后会附加这个“幽灵病”效果，用它阻止胸甲在短时间内再次复活。
         * 如果虚空珍珠把它清掉，咒魂胸甲的复活冷却就会失效，从而出现无限复活漏洞。
         *
         * 这里使用 MobEffect 的注册 ID 做判断，而不是使用显示名称或类名。
         * 这样不受语言文件影响，也不需要在编译期直接依赖灾变的效果类。
         */
        if (isCataclysmGhostSickness(instance)) {
            return true;
        }

        /*
         * 服务器配置白名单用于追加其他模组的关键状态效果。
         *
         * 这和上面的灾变硬编码保护是并列关系：
         * - cataclysm:ghost_sickness 永远保留，避免配置误删后重新出现无限复活漏洞；
         * - EffectWhitelist 只负责让整合包作者额外指定“不能被虚空珍珠净化”的效果。
         */
        if (isConfiguredWhitelistedEffect(instance)) {
            return true;
        }

        // 禁忌之果的永久标记效果不能清，否则禁忌之果逻辑会被破坏。
        if (instance.is(ModEffects.FORBIDDEN_FRUIT)) {
            return true;
        }

        /*
         * 猎宝者护符的夜视不清。
         *
         * 这里保持和你的 TreasureHunterCharmEvents 兼容：
         * 玩家佩戴猎宝者护符时，夜视效果可以保留。
         */
        return entity instanceof Player player
                && instance.is(MobEffects.NIGHT_VISION)
                && TreasureHunterCharmHelper.hasTreasureHunterCharm(player);
    }

    /**
     * 判断效果是否是灾变咒魂胸甲用于防止反复复活的幽灵病。
     */
    private static boolean isCataclysmGhostSickness(MobEffectInstance instance) {
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());

        return CATACLYSM_GHOST_SICKNESS.equals(effectId);
    }

    /**
     * 判断效果是否位于虚空珍珠配置白名单中。
     *
     * <p>配置项只接受精确的 {@code namespace:path} 效果注册名。
     * 这里每次按注册表 ID 比较，不依赖效果显示名称，因此不会受语言文件影响。</p>
     */
    private static boolean isConfiguredWhitelistedEffect(MobEffectInstance instance) {
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());

        if (effectId == null) {
            return false;
        }

        for (String rawId : ConfigCommon.VOID_PEARL_EFFECT_WHITELIST.get()) {
            ResourceLocation configuredId = ResourceLocation.tryParse(rawId.trim());

            if (effectId.equals(configuredId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 黑暗光环：
     * 每 0.5 秒扫描 16 格内生物。

     * 条件：
     * 1. 目标不是佩戴者自己；
     * 2. 目标存活；
     * 3. 目标不是同样佩戴虚空珍珠的玩家；
     * 4. 目标所在位置亮度 < 3，或者目标是未着火的幻翼。
     */
    private static void applyDarknessAura(LivingEntity bearer) {
        if (!(bearer.level() instanceof ServerLevel level)) {
            return;
        }

        AABB area = bearer.getBoundingBox().inflate(ConfigCommon.VOID_PEARL_DARKNESS_RANGE.get());

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> canDarknessAuraAffect(bearer, target)
        );

        for (LivingEntity target : targets) {
            if (!isExposedToDarkness(level, target)) {
                continue;
            }

            // 使用原版“掉出世界”伤害源。
            // 这样无需为虚空珍珠额外维护自定义 DamageType、死亡消息和伤害标签。
            target.hurt(
                    bearer.damageSources().source(DamageTypes.FELL_OUT_OF_WORLD, bearer),
                    PearlOfTheVoid.DARKNESS_DAMAGE
            );

            // 附加严重负面效果。
            applyDarknessEffects(target, bearer);
        }
    }

    /**
     * 判断黑暗光环是否可以影响目标。
     */
    private static boolean canDarknessAuraAffect(LivingEntity bearer, LivingEntity target) {
        if (target == bearer) {
            return false;
        }

        if (!target.isAlive()) {
            return false;
        }

        /*
         * 同样佩戴虚空珍珠的玩家除外。
         * 这里按你的描述只排除玩家，避免其他可穿戴实体出现奇怪兼容问题。
         */
        /*
         * 黑暗光环后续会同时执行虚空伤害和 addEffect。
         * 在筛选阶段排除玩家友方实体，可以一次性阻止宠物、女仆、玩家傀儡
         * 被持续扣血或反复附加凋零、缓慢、失明、饥饿、挖掘疲劳。
         */
        if (bearer instanceof Player player && OwnedEntityHelper.isProtectedPlayerOwnedAlly(player, target)) {
            return false;
        }

        /*
         * 女仆、宠物、召唤物自己佩戴虚空珍珠时，黑暗光环的来源不是 Player。
         * 这种情况下需要反向判断“光环来源是否属于目标玩家”，从而保护它的主人。
         */
        if (OwnedEntityHelper.isOwnerProtectedFromOwnedAlly(bearer, target)) {
            return false;
        }

        return !(target instanceof Player) || !PearlOfTheVoidHelper.hasPearlOfTheVoid(target);
    }

    /**
     * 判断目标是否暴露在黑暗中。

     * 普通生物：
     * - 所在位置综合亮度 < 3。

     * 幻翼：
     * - 如果没有着火，忽略天空光照等级，直接受到影响。
     */
    private static boolean isExposedToDarkness(ServerLevel level, LivingEntity target) {
        // 文本提到的“飞行生物”实测按幻翼处理。
        if (isConfiguredFlyingCreature(target) && !target.isOnFire()) {
            return true;
        }

        /*
         * 触发暗度改为服务器配置项。
         * 这里保持原语义：综合亮度必须“小于阈值”才算暴露在黑暗中。
         * 默认阈值为 3，因此亮度 0、1、2 会触发，和旧逻辑完全一致。
         */
        return level.getMaxLocalRawBrightness(target.blockPosition())
                < ConfigCommon.VOID_PEARL_DARKNESS_BRIGHTNESS_THRESHOLD.get();
    }

    /**
     * 判断目标是否属于虚空珍珠配置中的“飞行生物”。
     *
     * <p>这些实体在没有着火时会被黑暗光环直接视为暴露于黑暗中，
     * 不再检查当前位置亮度。默认列表只有 {@code minecraft:phantom}，
     * 因此不修改配置时仍保持原来的幻翼特殊规则。</p>
     */
    private static boolean isConfiguredFlyingCreature(LivingEntity target) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());

        for (String rawId : ConfigCommon.VOID_PEARL_FLYING_CREATURES.get()) {
            ResourceLocation configuredId = ResourceLocation.tryParse(rawId.trim());

            if (entityId.equals(configuredId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 给黑暗光环目标附加负面效果。

     * 保留药水粒子：
     * 这里使用普通 MobEffectInstance 构造，不关闭 visible。
     */
    private static void applyDarknessEffects(LivingEntity target, LivingEntity source) {
        target.addEffect(new MobEffectInstance(
                MobEffects.WITHER,
                PearlOfTheVoid.AURA_WITHER_DURATION,
                PearlOfTheVoid.AURA_WITHER_AMPLIFIER
        ), source);

        target.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                PearlOfTheVoid.AURA_SLOWNESS_DURATION,
                PearlOfTheVoid.AURA_SLOWNESS_AMPLIFIER
        ), source);

        target.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                PearlOfTheVoid.AURA_BLINDNESS_DURATION,
                PearlOfTheVoid.AURA_BLINDNESS_AMPLIFIER
        ), source);

        target.addEffect(new MobEffectInstance(
                MobEffects.HUNGER,
                PearlOfTheVoid.AURA_HUNGER_DURATION,
                PearlOfTheVoid.AURA_HUNGER_AMPLIFIER
        ), source);

        target.addEffect(new MobEffectInstance(
                MobEffects.DIG_SLOWDOWN,
                PearlOfTheVoid.AURA_MINING_FATIGUE_DURATION,
                PearlOfTheVoid.AURA_MINING_FATIGUE_AMPLIFIER
        ), source);
    }
}
