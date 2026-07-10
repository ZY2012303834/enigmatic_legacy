package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.spellstone.NonEuclideanCube;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.NonEuclideanCubeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 非欧立方被动事件。
 */
public final class NonEuclideanCubeEvents {
    private NonEuclideanCubeEvents() {
    }

    private static final List<Holder<MobEffect>> DEFAULT_NEGATIVE_EFFECTS = List.of(
            MobEffects.WEAKNESS,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.BLINDNESS,
            MobEffects.HUNGER,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.CONFUSION
    );

    /**
     * 正面效果池。

     * 按你的要求：排除缓降 SLOW_FALLING。
     */
    private static final List<Holder<MobEffect>> DEFAULT_POSITIVE_EFFECTS = List.of(
            MobEffects.REGENERATION,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DIG_SPEED,
            MobEffects.DAMAGE_BOOST,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.NIGHT_VISION,
            MobEffects.ABSORPTION,
            MobEffects.HEALTH_BOOST,
            MobEffects.JUMP,
            MobEffects.LUCK,
            MobEffects.SATURATION
    );

    /**
     * 每 tick 常驻被动：
     * 1. 补满氧气；
     * 2. 清除负面效果。

     * 只处理 ServerPlayer，避免像虚空珍珠之前那样对所有生物查 Curios 导致掉帧。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!NonEuclideanCubeHelper.hasNonEuclideanCube(player)) {
            return;
        }

        player.setAirSupply(player.getMaxAirSupply());
        removeNegativeEffects(player);
    }

    /**
     * 挖掘速度 +60%。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (NonEuclideanCubeHelper.hasNonEuclideanCube(player)) {
            event.setNewSpeed(event.getNewSpeed() * (1.0F + NonEuclideanCube.DIG_SPEED_BONUS));
        }
    }

    /**
     * Incoming 阶段处理：
     * 1. 免疫指定伤害；
     * 2. 35% 反弹投射物；
     * 3. 35% 返还普通伤害；
     * 4. 非投射物伤害给攻击者负面效果。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!NonEuclideanCubeHelper.hasNonEuclideanCube(target)) {
            return;
        }

        DamageSource source = event.getSource();

        /*
         * /kill 指令伤害必须穿透非欧立方。
         * 非欧立方不能免疫、反弹或取消 generic_kill 类型伤害。
         */
        if (isKillDamage(source)) {
            return;
        }

        if (isImmuneDamage(source)) {
            event.setCanceled(true);
            return;
        }

        boolean projectile = source.getDirectEntity() instanceof Projectile;

        if (!projectile && source.getEntity() instanceof LivingEntity attacker && attacker != target) {
            addProgressiveNegativeEffect(attacker, target);
        }

        if (target.getRandom().nextInt(100) >= NonEuclideanCube.REFLECT_CHANCE) {
            return;
        }

        if (projectile && source.getDirectEntity() instanceof Projectile projectileEntity) {
            reflectProjectile(projectileEntity, target);
            playReflectSound(target);
            event.setCanceled(true);
            return;
        }

        if (source.getEntity() instanceof LivingEntity attacker && attacker != target) {
            attacker.hurt(target.damageSources().thorns(target), event.getAmount());
            playReflectSound(target);
            event.setCanceled(true);
        }
    }

    /**
     * Damage.Pre 阶段处理：
     * 1. 高额伤害阈值免疫；
     * 2. 致命伤害保护；
     * 3. 濒死时触发主动效果并回复 30%。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (!NonEuclideanCubeHelper.hasNonEuclideanCube(target)) {
            return;
        }

        float damage = event.getNewDamage();

        /*
         * /kill 指令伤害必须可以杀死非欧立方佩戴者。
         * 这里放在高额伤害阈值保护之前，否则 /kill 会被当作超高伤害清零。
         */
        if (isKillDamage(event.getSource())) {
            return;
        }

        // 高于阈值的伤害直接无视。
        if (damage > getHighDamageLimit(target)) {
            event.setNewDamage(0.0F);
            return;
        }

        if (damage < target.getHealth()) {
            return;
        }

        // 血量大于 1 时，致命伤害只会把血量压到 1。
        if (target.getHealth() > 1.0F) {
            event.setNewDamage(Math.max(0.0F, target.getHealth() - 1.0F));
            return;
        }

        if (!ConfigCommon.CUBE_AUTO_SKILL_TRIGGERING.get()) {
            return;
        }

        /*
         * 血量小于等于 1 时，触发主动效果。
         * 这里只对玩家处理，因为主动技能需要 ServerPlayer。
         */
        if (target instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
            ItemStack stack = NonEuclideanCubeHelper.findNonEuclideanCube(player).orElse(ItemStack.EMPTY);

            if (stack.getItem() instanceof NonEuclideanCube cube) {
                cube.triggerActiveAbility(level, player, stack, true);
            }

            event.setNewDamage(0.0F);

            float healTo = player.getMaxHealth() * 0.30F;
            player.setHealth(Math.max(player.getHealth(), healTo));

            giveEmergencyBuffs(player);
            player.invulnerableTime = Math.max(player.invulnerableTime, 40);
        }
    }

    /**
     * 击败生物后获得随机正面效果。

     * 排除缓降。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!NonEuclideanCubeHelper.hasNonEuclideanCube(player)) {
            return;
        }

        List<Holder<MobEffect>> positiveEffects = getConfiguredEffects(
                ConfigCommon.THE_CUBE_RANDOM_BUFFS.get(),
                DEFAULT_POSITIVE_EFFECTS
        );
        Holder<MobEffect> effect = positiveEffects.get(
                player.getRandom().nextInt(positiveEffects.size())
        );

        player.addEffect(new MobEffectInstance(
                effect,
                20 * (20 + player.getRandom().nextInt(21)),
                0
        ));
    }

    /**
     * 移除负面效果。
     */
    private static void removeNegativeEffects(LivingEntity entity) {
        List<Holder<MobEffect>> toRemove = new ArrayList<>();

        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (!instance.getEffect().value().isBeneficial()) {
                toRemove.add(instance.getEffect());
            }
        }

        for (Holder<MobEffect> effect : toRemove) {
            entity.removeEffect(effect);
        }
    }

    /**
     * 高额伤害阈值。

     * 佩戴七咒之戒：150。
     * 未佩戴七咒之戒：100。
     */
    private static float getHighDamageLimit(LivingEntity entity) {
        int damageLimit = ConfigCommon.CUBE_DAMAGE_LIMIT.get();

        if (entity instanceof Player player && CursedRingHelper.hasCursedRing(player)) {
            return damageLimit * 1.5F;
        }

        return damageLimit;
    }

    /**
     * 免疫伤害类型。
     */
    private static boolean isImmuneDamage(DamageSource source) {
        return source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.FLY_INTO_WALL)
                || source.is(DamageTypes.THORNS)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypeTags.IS_FIRE)
                || isTeleportDamage(source);
    }

    /**
     * 判断是否为 /kill 指令使用的强制击杀伤害。
     * 这个伤害必须穿透非欧立方。
     * 创造之心不在这里处理，保持它自己的不朽逻辑不变。
     */
    private static boolean isKillDamage(DamageSource source) {
        return source.typeHolder()
                .unwrapKey()
                .map(key -> {
                    String path = key.location().getPath();
                    return path.equals("generic_kill") || path.equals("kill");
                })
                .orElse(false);
    }

    /**
     * 传送类伤害兼容判断。
     */
    private static boolean isTeleportDamage(DamageSource source) {
        return source.typeHolder()
                .unwrapKey()
                .map(key -> {
                    String path = key.location().getPath();
                    return path.equals("ender_pearl") || path.contains("teleport");
                })
                .orElse(false);
    }

    /**
     * 反弹投射物。
     */
    private static void reflectProjectile(Projectile projectile, LivingEntity reflector) {
        projectile.setOwner(reflector);
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.25D));
        projectile.hurtMarked = true;
    }

    /**
     * 反弹 / 返伤音效。

     * 使用当前项目已有的原项目音效 misc.hhon。
     */
    private static void playReflectSound(LivingEntity entity) {
        entity.level().playSound(
                null,
                entity.blockPosition(),
                ModSounds.CHARGED_ON.get(),
                entity.getSoundSource(),
                2.5F,
                0.75F + entity.getRandom().nextFloat() * 0.25F
        );
    }

    /**
     * 受到非投射物伤害时，给攻击者逐步叠加负面效果。

     * 规则：
     * 1. 每次增加一个攻击者身上还没有的负面效果；
     * 2. 如果所有效果都已经有了，就延长所有效果持续时间。
     */
    private static void addProgressiveNegativeEffect(LivingEntity attacker, LivingEntity source) {
        List<Holder<MobEffect>> negativeEffects = getConfiguredEffects(
                ConfigCommon.THE_CUBE_RANDOM_DEBUFFS.get(),
                DEFAULT_NEGATIVE_EFFECTS
        );

        for (Holder<MobEffect> effect : negativeEffects) {
            if (!attacker.hasEffect(effect)) {
                attacker.addEffect(new MobEffectInstance(effect, 20 * 8, 0), source);
                return;
            }
        }

        for (Holder<MobEffect> effect : negativeEffects) {
            MobEffectInstance old = attacker.getEffect(effect);

            if (old == null) {
                continue;
            }

            attacker.addEffect(new MobEffectInstance(
                    effect,
                    old.getDuration() + 20 * 5,
                    old.getAmplifier()
            ), source);
        }
    }

    private static List<Holder<MobEffect>> getConfiguredEffects(
            List<? extends String> effectIds,
            List<Holder<MobEffect>> fallback
    ) {
        List<Holder<MobEffect>> effects = new ArrayList<>();

        for (String rawId : effectIds) {
            ResourceLocation id = ResourceLocation.tryParse(rawId.trim());

            if (id == null) {
                continue;
            }

            BuiltInRegistries.MOB_EFFECT.getHolder(id).ifPresent(effects::add);
        }

        return effects.isEmpty() ? fallback : effects;
    }

    /**
     * 濒死主动触发后的正面效果。
     */
    private static void giveEmergencyBuffs(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 8, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 8, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 20, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 10, 1));
    }
}
