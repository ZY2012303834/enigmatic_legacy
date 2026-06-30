package org.enigmatic_legacy.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.enigmatic_legacy.effect.BlazingMightEffect;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 烈焰之傲壁垒 / Bulwark of Blazing Pride 的战斗逻辑。
 */
public final class BulwarkOfBlazingPrideEvents {
    private static final float BACKSTAB_DAMAGE_MULTIPLIER = 1.5F;
    private static final int ATTACKER_IGNITION_SECONDS = 5;
    private static final int BLAZING_MIGHT_DURATION = 20 * 12;
    private static final int BLAZING_MIGHT_AMPLIFIER = 0;

    private BulwarkOfBlazingPrideEvents() {
    }

    /**
     * 烈焰之傲壁垒举盾时免疫击退。
     * 说明：
     * - LivingKnockBackEvent 不携带 DamageSource；
     * - 所以这里按你的需求做成“举盾期间免疫所有击退”；
     * - 正面攻击本身已经会在 onIncomingDamage 中被取消伤害；
     * - 这个事件用于兜底处理其他来源或其他模组触发的击退。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBulwarkKnockBack(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!isActivelyUsingBulwark(player)) {
            return;
        }

        event.setCanceled(true);
    }

    /**
     * 烈焰之傲壁垒正面特殊格挡。
     * 功能：
     * 1. 正面伤害直接消失；
     * 2. 可以立即格挡；
     * 3. 可以挡穿透箭；
     * 4. 成功格挡后扣盾牌耐久；
     * 5. 成功格挡后给予烈焰巨力；
     * 6. 成功格挡后点燃攻击者；
     * 7. 远程攻击的发射者也会被点燃。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!isActivelyUsingBulwark(player)) {
            return;
        }

        DamageSource source = event.getSource();

        if (!isFrontDamage(player, source)) {
            return;
        }

        // 取消正面伤害。
        // 配合 onBulwarkKnockBack 可以实现正面免伤并免疫击退。
        event.setCanceled(true);

        damageUsedBulwark(player, event.getAmount());
        applyBlockReward(player, source);
    }

    /**
     * 烈焰巨力增伤，以及受到真实伤害后移除烈焰巨力。
     */
    @SubscribeEvent
    public static void onLivingDamagePreForBlazingMight(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        // 受到真实伤害后立刻失去烈焰巨力。
        if (target.hasEffect(ModEffects.BLAZING_MIGHT)) {
            target.removeEffect(ModEffects.BLAZING_MIGHT);
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        MobEffectInstance blazingMight = attacker.getEffect(ModEffects.BLAZING_MIGHT);

        if (blazingMight == null) {
            return;
        }

        float multiplier = 1.0F + BlazingMightEffect.DAMAGE_BOOST_PER_LEVEL * (blazingMight.getAmplifier() + 1);
        event.setNewDamage(event.getNewDamage() * multiplier);
    }

    /**
     * 举起烈焰之傲壁垒时，背后或侧后方受到的伤害提高 50%。
     */
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!isActivelyUsingBulwark(player)) {
            return;
        }

        DamageSource source = event.getSource();

        if (source.getEntity() == null || isFrontDamage(player, source)) {
            return;
        }

        event.setNewDamage(event.getNewDamage() * BACKSTAB_DAMAGE_MULTIPLIER);
    }

    /**
     * 判断玩家是否正在使用烈焰之傲壁垒。
     */
    private static boolean isActivelyUsingBulwark(Player player) {
        return CursedRingHelper.hasCursedRing(player)
                && player.isUsingItem()
                && player.getUseItem().is(ModItems.BULWARK_OF_BLAZING_PRIDE.get());
    }

    /**
     * 判断攻击是否来自玩家正面。
     */
    private static boolean isFrontDamage(Player player, DamageSource source) {
        Vec3 sourcePos = source.getSourcePosition();

        if (sourcePos == null) {
            return true;
        }

        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 sourceToSelf = sourcePos.vectorTo(player.position());
        sourceToSelf = new Vec3(sourceToSelf.x, 0.0D, sourceToSelf.z);

        if (sourceToSelf.lengthSqr() <= 1.0E-6D) {
            return true;
        }

        return sourceToSelf.normalize().dot(lookVec) < 0.0D;
    }

    /**
     * 根据被格挡的伤害消耗盾牌耐久。
     */
    private static void damageUsedBulwark(Player player, float blockedDamage) {
        ItemStack stack = player.getUseItem();

        if (!stack.is(ModItems.BULWARK_OF_BLAZING_PRIDE.get()) || player.getAbilities().instabuild) {
            return;
        }

        int durabilityDamage = Math.max(1, 1 + (int) Math.floor(blockedDamage));
        InteractionHand hand = player.getUsedItemHand();

        stack.hurtAndBreak(durabilityDamage, player, LivingEntity.getSlotForHand(hand));
    }

    /**
     * 成功格挡后的奖励效果。
     */
    private static void applyBlockReward(Player player, DamageSource source) {
        player.addEffect(new MobEffectInstance(
                ModEffects.BLAZING_MIGHT,
                BLAZING_MIGHT_DURATION,
                BLAZING_MIGHT_AMPLIFIER,
                false,
                true,
                true
        ));

        // 保留你的需求：
        // 远程攻击也能点燃发射者。
        LivingEntity attacker = getLivingAttacker(source);

        if (attacker != null && !attacker.fireImmune()) {
            attacker.igniteForSeconds(ATTACKER_IGNITION_SECONDS);
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                1.0F,
                0.85F + player.getRandom().nextFloat() * 0.3F
        );
    }

    /**
     * 获取攻击者。
     * 近战：
     * - source.getEntity() 通常就是攻击者。
     * 远程：
     * - source.getDirectEntity() 是箭、三叉戟等投射物；
     * - projectile.getOwner() 是发射者；
     * - 所以远程攻击被挡后，发射者也会被点燃。
     */
    private static LivingEntity getLivingAttacker(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        if (source.getDirectEntity() instanceof Projectile projectile
                && projectile.getOwner() instanceof LivingEntity owner) {
            return owner;
        }

        return null;
    }
}