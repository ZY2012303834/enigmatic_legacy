package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.network.HeartOfCreationGuardPayload;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.util.HeartOfCreationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 创造之心事件逻辑。

 * 被动效果：
 * 1. 免疫窒息、坠落、碰撞、挤压、饥饿、虚空、荆棘、火焰和岩浆伤害；
 * 2. 免疫大部分负面状态效果；
 * 3. 免疫击退；
 * 4. 给予飞行能力；
 * 5. 补偿飞行时挖掘速度损失；
 * 6. 装备或放在物品栏中时不朽。
 */
public final class HeartOfCreationEvents {
    /**
     * 用于记录创造之心是否曾经给玩家开启过飞行。

     * 这样玩家取下创造之心后，我们只撤销由创造之心授予的飞行能力，
     * 不影响创造模式 / 旁观模式本身的飞行。
     */
    private static final String GRANTED_FLIGHT_TAG =
            "enigmatic_legacy_heart_of_creation_granted_flight";

    private HeartOfCreationEvents() {
    }

    /**
     * 每 tick 处理飞行、氧气和状态免疫。

     * 注意：
     * 这里只处理 ServerPlayer，避免对所有生物每 tick 查询 Curios 导致掉帧。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        boolean equipped = HeartOfCreationHelper.hasHeartOfCreationEquipped(player);

        if (equipped) {
            grantFlight(player);
            player.setAirSupply(player.getMaxAirSupply());
            removeNegativeEffects(player);
        } else {
            revokeFlightIfGranted(player);
        }
    }

    /**
     * 补偿飞行时挖掘速度损失。

     * 原版飞行时挖掘会明显变慢，这里只在玩家真正飞行时补偿。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (HeartOfCreationHelper.hasHeartOfCreationEquipped(player)
                && player.getAbilities().flying) {
            event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }

    /**
     * 免疫击退。

     * LivingKnockBackEvent 在 NeoForge 中是可取消事件；
     * 取消后实体不会被击退。:contentReference[oaicite:2]{index=2}
     */
    @SubscribeEvent
    public static void onKnockBack(LivingKnockBackEvent event) {
        if (HeartOfCreationHelper.hasHeartOfCreationEquipped(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    /**
     * 免疫指定伤害类型。

     * 这些免疫只在“佩戴创造之心”时生效。
     * 如果只是放在物品栏中，只获得不朽，不获得完整术石被动。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (!HeartOfCreationHelper.hasHeartOfCreationEquipped(target)) {
            return;
        }

        if (isImmuneDamage(event.getSource())) {
            event.setCanceled(true);
        }
    }

    /**
     * 不朽效果：
     * 装备创造之心，或物品栏内有创造之心时，
     * 任何伤害最多只会把生命降到 1 点。

     * 这里直接取消致命伤害并设置 1 点血，
     * 同时发送客户端包触发血条 GUI 保护特效。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();

        if (!HeartOfCreationHelper.hasCreationImmortality(target)) {
            return;
        }

        float damage = event.getNewDamage();

        if (damage <= 0.0F) {
            return;
        }

        float health = target.getHealth();

        // 如果这次伤害不会把生命压到 1 点以下，正常结算。
        if (health - damage > 1.0F) {
            return;
        }

        // 不朽触发：直接取消本次致命伤害，并把血量设为 1。
        event.setNewDamage(0.0F);
        target.setHealth(1.0F);
        target.invulnerableTime = Math.max(target.invulnerableTime, 10);

        playImmortalFeedback(target);
    }

    /**
     * 兜底死亡保护。

     * 某些伤害或其他模组可能绕过普通伤害阶段，
     * 所以这里再拦截一次死亡事件。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();

        if (!HeartOfCreationHelper.hasCreationImmortality(target)) {
            return;
        }

        event.setCanceled(true);
        target.setHealth(1.0F);
        target.invulnerableTime = Math.max(target.invulnerableTime, 20);
        playImmortalFeedback(target);
    }

    /**
     * 给玩家开启飞行能力。
     */
    private static void grantFlight(ServerPlayer player) {
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        player.getPersistentData().putBoolean(GRANTED_FLIGHT_TAG, true);
    }

    /**
     * 如果飞行能力是创造之心授予的，则在取下后撤销。
     */
    private static void revokeFlightIfGranted(ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(GRANTED_FLIGHT_TAG)) {
            return;
        }

        player.getPersistentData().remove(GRANTED_FLIGHT_TAG);

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    /**
     * 清除负面效果。
     *
     * 创造之心保留正面效果，只清除 debuff。
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
     * 创造之心免疫的伤害类型。
     */
    private static boolean isImmuneDamage(DamageSource source) {
        return source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.FLY_INTO_WALL)
                || source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.STARVE)
                || source.is(DamageTypes.FELL_OUT_OF_WORLD)
                || source.is(DamageTypes.THORNS)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypeTags.IS_FIRE);
    }

    /**
     * 创造之心不朽触发反馈。

     * 这里同时处理：
     * 1. 原作者 shield_trigger 音效；
     * 2. 实体受击动画；
     * 3. 玩家血量同步；
     * 4. 客户端 HUD 血量条保护特效。
     */
    private static void playImmortalFeedback(LivingEntity entity) {
        entity.level().playSound(
                null,
                entity.blockPosition(),
                ModSounds.SHIELD_TRIGGER.get(),
                SoundSource.PLAYERS,
                1.35F,
                0.85F + entity.getRandom().nextFloat() * 0.2F
        );

        entity.hurtTime = Math.max(entity.hurtTime, 10);
        entity.hurtDuration = Math.max(entity.hurtDuration, 10);
        entity.invulnerableTime = Math.max(entity.invulnerableTime, 10);

        // 播放实体受击动画。
        entity.level().broadcastEntityEvent(entity, (byte) 2);

        if (entity instanceof ServerPlayer player) {
            // 立即同步血量到 1 点。
            player.connection.send(new ClientboundSetHealthPacket(
                    1.0F,
                    player.getFoodData().getFoodLevel(),
                    player.getFoodData().getSaturationLevel()
            ));

            // 关键：通知客户端绘制血条 GUI 保护特效。
            PacketDistributor.sendToPlayer(player, new HeartOfCreationGuardPayload());
        }
    }
}