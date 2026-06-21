package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 守卫者之心 / Heart of the Guardian。
 *
 * <p>复刻原项目核心功能：
 * <ul>
 *     <li>七咒玩家将其放在快捷栏时，可以通过注视怪物触发仇恨混乱；</li>
 *     <li>被注视的怪物会攻击附近怪物，附近怪物也会反过来攻击它；</li>
 *     <li>普通守卫者会被动攻击附近的非守卫者怪物；</li>
 *     <li>冷却 10 秒。</li>
 * </ul>
 */
public class GuardianHeart extends Item {

    public static final int ABILITY_RANGE = 24;
    public static final int ENRAGE_RANGE = 12;
    public static final int ACTIVE_ABILITY_COOLDOWN = 200;

    public GuardianHeart() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack,
                              @NotNull Level level,
                              @NotNull Entity entity,
                              int slotId,
                              boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide()) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        List<Monster> nearbyMonsters = level.getEntitiesOfClass(
                Monster.class,
                boxAround(player, ABILITY_RANGE),
                LivingEntity::isAlive
        );

        boolean cursed = CursedRingHelper.hasCursedRing(player);

        // 主动能力：七咒玩家 + 放在快捷栏 + 不在冷却中 + 正在注视有效怪物。
        if (cursed
                && isHotbarSlot(slotId)
                && !player.getCooldowns().isOnCooldown(this)) {
            tryTriggerWatchedMonster(player, nearbyMonsters);
        }

        // 被动能力：普通守卫者主动攻击附近非守卫者怪物。
        redirectGuardiansToMonsters(player, nearbyMonsters);

        // 防御能力：七咒玩家携带守卫者之心时，普通守卫者不会主动锁定该玩家。
        // 如果玩家刚刚攻击过守卫者，则允许它反击。
        if (cursed) {
            preventGuardiansTargetingBearer(player, nearbyMonsters);
        }
    }

    /**
     * 七咒玩家携带守卫者之心时，普通守卫者不会主动攻击该玩家。
     *
     * <p>不用 LivingChangeTargetEvent，是因为不同 NeoForge 1.21.1 版本里
     * LivingChangeTargetEvent 的 getter 方法不稳定。
     *
     * <p>这里直接在物品 tick 中检查附近守卫者目标：
     * <ul>
     *     <li>如果普通守卫者正在锁定持有者；</li>
     *     <li>并且持有者没有刚刚攻击过该守卫者；</li>
     *     <li>则清除守卫者目标。</li>
     * </ul>
     */
    private static void preventGuardiansTargetingBearer(Player player, List<Monster> nearbyMonsters) {
        for (Monster monster : nearbyMonsters) {
            if (!(monster instanceof Guardian guardian)) {
                continue;
            }

            if (guardian instanceof ElderGuardian) {
                continue;
            }

            if (guardian.getTarget() != player) {
                continue;
            }

            // 玩家刚刚攻击过它时，允许普通守卫者反击。
            if (guardian.getLastHurtByMob() == player) {
                continue;
            }

            guardian.setTarget(null);
        }
    }

    /**
     * 尝试触发“注视怪物”能力。
     */
    private void tryTriggerWatchedMonster(Player player, List<Monster> nearbyMonsters) {
        Monster watchedMonster = nearbyMonsters.stream().filter(monster -> !isExcludedWatchedTarget(monster)).filter(monster -> isObservedByPlayer(player, monster)).findFirst().orElse(null);

        if (watchedMonster == null || !watchedMonster.isAlive()) {
            return;
        }

        List<Monster> surroundingMonsters = player.level().getEntitiesOfClass(
                Monster.class,
                boxAround(watchedMonster, ENRAGE_RANGE),
                monster -> monster.isAlive() && watchedMonster.hasLineOfSight(monster)
        );

        Optional<Monster> closestMonster = surroundingMonsters.stream()
                .filter(monster -> monster != watchedMonster)
                .min(Comparator.comparingDouble(monster -> monster.distanceToSqr(watchedMonster)));

        if (closestMonster.isEmpty()) {
            return;
        }

        Monster firstTarget = closestMonster.get();

        setAttackTarget(watchedMonster, firstTarget);

        watchedMonster.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0, false, true));
        watchedMonster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 1, false, false));
        watchedMonster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false));

        for (Monster otherMonster : surroundingMonsters) {
            if (otherMonster == watchedMonster) {
                continue;
            }

            if (isExcludedWatchedTarget(otherMonster)) {
                continue;
            }

            setAttackTarget(otherMonster, watchedMonster);
        }

        Level level = player.level();

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ELDER_GUARDIAN_CURSE,
                SoundSource.HOSTILE,
                1.0F,
                1.0F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.ELDER_GUARDIAN,
                    watchedMonster.getX(),
                    watchedMonster.getEyeY(),
                    watchedMonster.getZ(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        player.getCooldowns().addCooldown(this, ACTIVE_ABILITY_COOLDOWN);
    }

    /**
     * 普通守卫者被动攻击附近非守卫者怪物。
     */
    private void redirectGuardiansToMonsters(Player player, List<Monster> nearbyMonsters) {
        for (Monster monster : nearbyMonsters) {
            if (!(monster instanceof Guardian guardian)) {
                continue;
            }

            if (guardian instanceof ElderGuardian) {
                continue;
            }

            if (guardian.getTarget() != null) {
                continue;
            }

            List<Monster> surroundingMonsters = player.level().getEntitiesOfClass(
                    Monster.class,
                    boxAround(guardian, ENRAGE_RANGE),
                    checked -> checked.isAlive()
                            && !(checked instanceof Guardian)
                            && guardian.hasLineOfSight(checked)
            );

            surroundingMonsters.stream()
                    .min(Comparator.comparingDouble(checked -> checked.distanceToSqr(guardian)))
                    .ifPresent(target -> setAttackTarget(guardian, target));
        }
    }

    /**
     * 玩家是否正在注视该怪物。
     *
     * <p>0.95 太严格，实际游戏里很容易看着怪却无法触发。
     */
    private static boolean isObservedByPlayer(Player player, LivingEntity target) {
        if (!player.hasLineOfSight(target)) {
            return false;
        }

        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 eye = new Vec3(player.getX(), player.getEyeY(), player.getZ());
        Vec3 targetEye = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        Vec3 directionToTarget = targetEye.subtract(eye).normalize();

        return look.dot(directionToTarget) > 0.90D;
    }

    /**
     * 原项目排除猪灵类和守卫者类，不让它们作为被注视的核心怪物。
     */
    private static boolean isExcludedWatchedTarget(LivingEntity entity) {
        return entity instanceof AbstractPiglin || entity instanceof Guardian;
    }

    /**
     * 强制切换怪物攻击目标。
     *
     * <p>不能只用 monster.setTarget(...)。
     * 猪灵类和部分中立怪物有自己的 AI / Brain 目标记忆，
     * 只设置普通 target 很容易被下一轮 AI tick 覆盖。
     */
    private static void setAttackTarget(Monster monster, LivingEntity target) {
        if (monster == null || target == null || monster == target) {
            return;
        }

        // 猪灵类使用 Brain AI，需要写入 ATTACK_TARGET 记忆。
        if (monster instanceof AbstractPiglin piglin) {
            piglin.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        }

        // 中立怪物需要走 NeutralMob 自己的 setTarget。
        if (monster instanceof NeutralMob neutralMob) {
            neutralMob.setTarget(target);
        }

        monster.setTarget(target);
        monster.setLastHurtByMob(target);
        monster.setAggressive(true);
    }

    private static boolean isHotbarSlot(int slotId) {
        return slotId >= 0 && slotId < 9;
    }

    private static AABB boxAround(Entity entity, double range) {
        return new AABB(
                entity.getX() - range,
                entity.getY() - range,
                entity.getZ() - range,
                entity.getX() + range,
                entity.getY() + range,
                entity.getZ() + range
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.guardian_heart1", ABILITY_RANGE)
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.guardian_heart2", ENRAGE_RANGE)
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.guardian_heart3", ACTIVE_ABILITY_COOLDOWN / 20.0D)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}