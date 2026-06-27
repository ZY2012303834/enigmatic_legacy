package org.enigmatic_legacy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.effect.GrowingBloodlustEffect;
import org.enigmatic_legacy.effect.GrowingHungerEffect;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.ForbiddenFruit;
import org.enigmatic_legacy.item.items.VoraciousPan;
import org.enigmatic_legacy.potion.ModEffects;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 饕餮之锅事件。
 * 完善原项目机制：
 * 1. 未佩戴七咒之戒时，饕餮之锅攻击无效；
 * 2. 主手持有饕餮之锅会逐渐获得 Growing Hunger；
 * 3. 如果吃过禁忌之果，则改为 Growing Bloodlust；
 * 4. Growing Hunger 增加攻击伤害，并增加饥饿消耗；
 * 5. Growing Bloodlust 增加攻击伤害和吸血，但持续扣血；
 * 6. 命中造成伤害时吸血；
 * 7. 命中造成伤害时夺取饥饿值；
 * 8. 击杀新种类生物时，记录该生物并强化锅。
 */
public final class VoraciousPanEvents {

    private VoraciousPanEvents() {
    }

    /**
     * 玩家 tick。
     * 原项目逻辑：
     * - 只检测主手；
     * - 主手持有饕餮之锅时累计持有时间；
     * - 正常玩家获得 Growing Hunger；
     * - 不会饥饿的玩家获得 Growing Bloodlust；
     * - 不再持有时清空计时，并移除两个效果。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();

        if (!mainHand.is(ModItems.VORACIOUS_PAN.get())) {
            VoraciousPan.HOLDING_DURATIONS.put(player, 0);
            player.removeEffect(ModEffects.GROWING_HUNGER);
            player.removeEffect(ModEffects.GROWING_BLOODLUST);
            return;
        }

        /*
         * 原项目是“持有饕餮之锅”就开始增长。
         * 这里保留七咒限制：没有七咒之戒时不触发成长效果。
         */
        if (!CursedRingHelper.hasCursedRing(player)) {
            VoraciousPan.HOLDING_DURATIONS.put(player, 0);
            player.removeEffect(ModEffects.GROWING_HUNGER);
            player.removeEffect(ModEffects.GROWING_BLOODLUST);
            return;
        }

        int currentTicks = VoraciousPan.HOLDING_DURATIONS.getOrDefault(player, 0);

        if (ForbiddenFruit.hasConsumedFruit(player)) {
            int amplifier = Math.min(
                    9,
                    currentTicks / GrowingBloodlustEffect.TICKS_PER_LEVEL
            );

            player.addEffect(new MobEffectInstance(
                    ModEffects.GROWING_BLOODLUST,
                    MobEffectInstance.INFINITE_DURATION,
                    amplifier,
                    true,
                    true
            ));

            player.removeEffect(ModEffects.GROWING_HUNGER);
        } else {
            int amplifier = Math.min(
                    9,
                    currentTicks / GrowingHungerEffect.TICKS_PER_LEVEL
            );

            player.addEffect(new MobEffectInstance(
                    ModEffects.GROWING_HUNGER,
                    MobEffectInstance.INFINITE_DURATION,
                    amplifier,
                    true,
                    true
            ));

            player.removeEffect(ModEffects.GROWING_BLOODLUST);
        }

        VoraciousPan.HOLDING_DURATIONS.put(player, currentTicks + 1);
    }

    /**
     * IncomingDamage 阶段。
     * 未佩戴七咒之戒时，饕餮之锅攻击无效。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();

        if (!weapon.is(ModItems.VORACIOUS_PAN.get())) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(attacker)) {
            event.setCanceled(true);
        }
    }

    /**
     * Damage.Pre 阶段。
     * 处理：
     * - Growing Hunger 伤害加成；
     * - Growing Bloodlust 伤害加成；
     * - 饕餮之锅基础吸血；
     * - Growing Bloodlust 额外吸血；
     * - 饕餮之锅夺饥。
     */
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();

        if (!weapon.is(ModItems.VORACIOUS_PAN.get())) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(attacker)) {
            event.setNewDamage(0.0F);
            return;
        }

        LivingEntity target = event.getEntity();

        float damage = event.getNewDamage();

        if (damage <= 0.0F) {
            return;
        }

        /*
         * Growing Hunger 伤害加成。
         *
         * 原项目每级 +10% 攻击伤害。
         */
        if (attacker.hasEffect(ModEffects.GROWING_HUNGER)) {
            int level = 1 + attacker.getEffect(ModEffects.GROWING_HUNGER).getAmplifier();
            damage *= 1.0F + GrowingHungerEffect.DAMAGE_BOOST_PER_LEVEL * level;
        }

        /*
         * Growing Bloodlust 伤害加成。
         *
         * 原项目每级 +5% 攻击伤害。
         */
        if (attacker.hasEffect(ModEffects.GROWING_BLOODLUST)) {
            int level = 1 + attacker.getEffect(ModEffects.GROWING_BLOODLUST).getAmplifier();
            damage *= 1.0F + GrowingBloodlustEffect.DAMAGE_BOOST_PER_LEVEL * level;
        }

        event.setNewDamage(damage);

        /*
         * 饕餮之锅基础吸血。
         */
        VoraciousPan.applyLifeSteal(attacker, damage);

        /*
         * Growing Bloodlust 额外吸血。
         *
         * 原项目每级 +2.5% 吸血。
         */
        if (attacker.hasEffect(ModEffects.GROWING_BLOODLUST)) {
            int level = 1 + attacker.getEffect(ModEffects.GROWING_BLOODLUST).getAmplifier();
            attacker.heal(damage * GrowingBloodlustEffect.LIFESTEAL_BOOST_PER_LEVEL * level);
        }

        /*
         * 饕餮之锅夺饥。
         */
        VoraciousPan.applyHungerSteal(attacker, target);
    }

    /**
     * 生物死亡事件。
     * 使用饕餮之锅击杀新的生物类型时：
     * - 记录该实体类型；
     * - 刷新锅的属性；
     * - 发送强化提示。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();

        if (!weapon.is(ModItems.VORACIOUS_PAN.get())) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(attacker)) {
            return;
        }

        ResourceLocation killedType = VoraciousPan.getEntityTypeId(event.getEntity());

        if (VoraciousPan.addKillIfNotPresent(weapon, killedType)) {
            attacker.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.voracious_pan_buff")
                            .withStyle(ChatFormatting.GOLD),
                    false
            );
        }
    }
}