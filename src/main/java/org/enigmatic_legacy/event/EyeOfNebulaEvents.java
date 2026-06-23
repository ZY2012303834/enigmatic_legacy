package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.enigmatic_legacy.item.items.EyeOfNebula;
import org.enigmatic_legacy.util.EyeOfNebulaHelper;

/**
 * 星云之眼事件。

 * 这里集中处理所有被动效果：
 * 1. 魔法伤害加成；
 * 2. 魔法抗性；
 * 3. 被攻击时概率闪避；
 * 4. 主动技能后的下一击强化；
 * 5. 传送保护；
 * 6. 水中受伤翻倍。
 */
public final class EyeOfNebulaEvents {
    private EyeOfNebulaEvents() {
    }

    /**
     * IncomingDamage 阶段可以取消伤害。

     * 用途：
     * 1. 传送保护期间取消摔落型伤害；
     * 2. 15% 概率闪避并取消本次伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!EyeOfNebulaHelper.hasEyeOfNebula(target)) {
            return;
        }

        DamageSource source = event.getSource();

        // 星云之眼传送后短时间免疫摔落型传送伤害。
        // 注意：这里不直接永久免疫所有 FALL，否则会让普通摔落也失效，太强。
        if (source.is(DamageTypes.FALL) && EyeOfNebula.hasTeleportProtection(target)) {
            event.setCanceled(true);
            return;
        }

        // 15% 概率受到攻击时闪现到别处。
        // 成功传送后，取消这次伤害。
        if (!target.level().isClientSide()
                && target.getRandom().nextInt(100) < EyeOfNebula.DODGE_CHANCE
                && randomTeleport(target)) {
            event.setCanceled(true);
        }
    }

    /**
     * LivingDamageEvent.Pre 阶段可以修改最终伤害数值。

     * 你的项目里 GolemHeart / OceanStone 也是用这个事件处理减伤和易伤。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        /*
         * 攻击者佩戴星云之眼：
         * 1. 如果这次伤害是魔法伤害，则 +40%；
         * 2. 如果刚用过主动技能，则下一次攻击 +150%，然后清除标记。
         */
        if (source.getEntity() instanceof LivingEntity attacker
                && EyeOfNebulaHelper.hasEyeOfNebula(attacker)) {

            if (isMagicDamage(source)) {
                damage *= 1.0F + EyeOfNebula.MAGIC_DAMAGE_BONUS;
            }

            if (attacker.getPersistentData().getBoolean(EyeOfNebula.EMPOWERED_ATTACK_TAG)) {
                damage *= 1.0F + EyeOfNebula.EMPOWERED_ATTACK_BONUS;
                attacker.getPersistentData().remove(EyeOfNebula.EMPOWERED_ATTACK_TAG);
            }
        }

        LivingEntity target = event.getEntity();

        /*
         * 受击者佩戴星云之眼：
         * 1. 魔法伤害减少 65%；
         * 2. 如果在水中，所有伤害翻倍。
         */
        if (EyeOfNebulaHelper.hasEyeOfNebula(target)) {
            if (isMagicDamage(source)) {
                damage *= 1.0F - EyeOfNebula.MAGIC_RESISTANCE;
            }

            if (target.isInWaterOrBubble()) {
                damage *= 2.0F;
            }
        }

        event.setNewDamage(damage);
    }

    /**
     * 判断伤害是否属于魔法类。

     * 保持和你项目 GolemHeart 的魔法伤害判断风格一致：
     * 包含 NeoForge 的 magic 标签，以及原版魔法、间接魔法、凋零、龙息等。
     */
    private static boolean isMagicDamage(DamageSource source) {
        return source.is(Tags.DamageTypes.IS_MAGIC)
                || source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.DRAGON_BREATH);
    }

    /**
     * 被攻击时的随机闪现。

     * 成功找到安全位置后：
     * 1. 播放粒子；
     * 2. 传送；
     * 3. 清空摔落距离；
     * 4. 添加短暂传送保护；
     * 5. 播放末影人传送音效。
     */
    private static boolean randomTeleport(LivingEntity entity) {
        Level level = entity.level();

        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        for (int attempt = 0; attempt < 16; attempt++) {
            double x = entity.getX()
                    + (entity.getRandom().nextDouble() - 0.5D) * 2.0D * EyeOfNebula.DODGE_RANGE;

            double y = Mth.clamp(
                    entity.getY() + entity.getRandom().nextInt(17) - 8,
                    serverLevel.getMinBuildHeight() + 1,
                    serverLevel.getMaxBuildHeight() - 2
            );

            double z = entity.getZ()
                    + (entity.getRandom().nextDouble() - 0.5D) * 2.0D * EyeOfNebula.DODGE_RANGE;

            BlockPos pos = BlockPos.containing(x, y, z);

            // 往下找地面。
            while (pos.getY() > serverLevel.getMinBuildHeight()
                    && serverLevel.getBlockState(pos).getCollisionShape(serverLevel, pos).isEmpty()) {
                pos = pos.below();
            }

            BlockPos destination = pos.above();

            if (!isSafeTeleportPosition(serverLevel, destination)) {
                continue;
            }

            // 原地离开粒子。
            serverLevel.sendParticles(
                    ParticleTypes.PORTAL,
                    entity.getX(),
                    entity.getY() + 1.0D,
                    entity.getZ(),
                    32,
                    0.5D,
                    0.75D,
                    0.5D,
                    0.15D
            );

            entity.stopRiding();
            entity.teleportTo(
                    destination.getX() + 0.5D,
                    destination.getY(),
                    destination.getZ() + 0.5D
            );
            entity.resetFallDistance();

            // 闪避传送也给短暂保护。
            EyeOfNebula.markTeleportProtected(entity, 20);

            // 到达粒子。
            serverLevel.sendParticles(
                    ParticleTypes.PORTAL,
                    entity.getX(),
                    entity.getY() + 1.0D,
                    entity.getZ(),
                    32,
                    0.5D,
                    0.75D,
                    0.5D,
                    0.15D
            );

            serverLevel.playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            return true;
        }

        return false;
    }

    /**
     * 判断位置是否适合传送。
     */
    private static boolean isSafeTeleportPosition(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty();
    }
}