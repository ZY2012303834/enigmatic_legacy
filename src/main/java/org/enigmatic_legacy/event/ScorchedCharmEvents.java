package org.enigmatic_legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
     * 岩浆中每秒恢复生命值。
     * 2.0F = 1 颗心。
     */
    private static final float LAVA_HEAL_AMOUNT = 2.0F;

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

    private ScorchedCharmEvents() {
    }

    /**
     * 每 tick 处理：
     * 1. 自动灭火；
     * 2. 岩浆自然回复；
     * 3. 岩浆表面行走。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (!ScorchedCharmHelper.hasScorchedCharm(player)) {
            return;
        }

        /*
         * 阳灼护符佩戴者不会持续燃烧。
         */
        if (player.isOnFire()) {
            player.clearFire();
        }

        /*
         * 接触岩浆时每秒恢复 2 点生命。
         */
        if (isTouchingLava(player) && player.tickCount % 20 == 0) {
            player.heal(LAVA_HEAL_AMOUNT);
        }

        /*
         * 岩浆行走：
         * - 没有下蹲时，尝试停留在岩浆表面；
         * - 下蹲时不处理，让玩家可以潜入岩浆。
         */
        handleLavaWalking(player);
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
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
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

    /**
     * 处理岩浆行走。
     * 说明：
     * 原拓展项目逻辑大意：
     * - 在岩浆里时，如果玩家没有下蹲，会给向上的运动或保持地面状态；
     * - 下蹲时允许玩家向下潜入岩浆。
     */
    private static void handleLavaWalking(Player player) {
        if (!player.isInLava()) {
            return;
        }

        /*
         * 下蹲时不托起玩家，允许潜入岩浆。
         */
        if (player.isShiftKeyDown()) {
            return;
        }

        BlockPos feetPos = player.blockPosition();

        if (!player.level().getFluidState(feetPos).is(FluidTags.LAVA)) {
            return;
        }

        boolean lavaAbove = player.level().getFluidState(feetPos.above()).is(FluidTags.LAVA);
        Vec3 motion = player.getDeltaMovement();

        if (lavaAbove) {
            /*
             * 如果头顶仍然是岩浆，说明玩家在较深岩浆中。
             * 非下蹲状态下轻微上浮，避免一直下沉。
             */
            player.setDeltaMovement(motion.x, motion.y + 0.07D, motion.z);
            player.fallDistance = 0.0F;
            return;
        }

        /*
         * 如果只在岩浆表面，托到当前岩浆方块顶部附近。
         */
        double surfaceY = feetPos.getY() + 1.0D;

        if (player.getY() < surfaceY + 0.02D) {
            player.setPos(player.getX(), surfaceY + 0.02D, player.getZ());
        }

        player.setDeltaMovement(motion.x, Math.max(0.0D, motion.y), motion.z);
        player.setOnGround(true);
        player.fallDistance = 0.0F;
    }

    /**
     * 判断实体是否正在接触岩浆。
     */
    private static boolean isTouchingLava(LivingEntity entity) {
        if (entity.isInLava()) {
            return true;
        }

        BlockPos pos = entity.blockPosition();

        return entity.level().getFluidState(pos).is(FluidTags.LAVA)
                || entity.level().getFluidState(pos.below()).is(FluidTags.LAVA);
    }
}