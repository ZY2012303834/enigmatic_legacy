package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 七咒之戒事件处理类。
 *
 * 说明：
 * 原项目部分逻辑写在 CursedRing 类和 SuperpositionHandler 中。
 * 当前项目拆分为事件类，方便适配 NeoForge 1.21.1。
 */
public class CursedRingEvents {

    /**
     * 玩家每秒处理一次七咒之戒的仇恨逻辑。
     *
     * PlayerTickEvent.Post 会在逻辑客户端和逻辑服务端都触发，
     * 所以具体逻辑中必须检查 level().isClientSide()。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.tickCount % 20 != 0) {
            return;
        }

        CursedRingHelper.tickCurses(player);
    }

    /**
     * 七咒之戒调整伤害：
     * 1. 佩戴者受到更多伤害。
     * 2. 佩戴者攻击怪物时，伤害降低。
     * 3. 佩戴者的护甲减伤效果降低。
     *
     * NeoForge 1.21.1 使用 LivingIncomingDamageEvent 替代旧版 LivingHurtEvent。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (target instanceof Player player && CursedRingHelper.hasCursedRing(player)) {
            float multiplier = ConfigCommon.CURSED_RING_PAIN_MODIFIER.get() / 100.0F;
            event.setAmount(event.getAmount() * multiplier);

            float armorDebuff = ConfigCommon.CURSED_RING_ARMOR_DEBUFF.get() / 100.0F;
            event.addReductionModifier(
                    DamageContainer.Reduction.ARMOR,
                    (container, baseReduction) -> baseReduction * Math.max(0.0F, 1.0F - armorDebuff)
            );
        }

        if (event.getSource().getEntity() instanceof Player attacker
                && CursedRingHelper.hasCursedRing(attacker)
                && target instanceof Enemy) {
            float debuff = ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() / 100.0F;
            event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - debuff));
        }
    }

    /**
     * 七咒之戒佩戴者受到更多击退。
     */
    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        float multiplier = ConfigCommon.CURSED_RING_KNOCKBACK_DEBUFF.get() / 100.0F;
        event.setStrength(event.getStrength() * multiplier);
    }

    /**
     * 七咒之戒佩戴者击杀生物时获得更多经验。
     */
    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        Player attacker = event.getAttackingPlayer();

        if (attacker == null) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(attacker)) {
            return;
        }

        int original = event.getDroppedExperience();
        double multiplier = ConfigCommon.CURSED_RING_EXPERIENCE_BONUS.get() / 100.0D;

        event.setDroppedExperience((int) Math.round(original * multiplier));
    }

    /**
     * Ultra Hardcore 模式：
     * 玩家进入世界时给予七咒之戒。
     *
     * 当前阶段先放入背包，不直接写入 Curios 槽位。
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!ConfigCommon.CURSED_RING_ENABLED.get()) {
            return;
        }

        if (!ConfigCommon.CURSED_RING_ULTRA_HARDCORE.get()) {
            return;
        }

        boolean alreadyHasRing = player.getInventory().contains(new ItemStack(ModItems.CURSED_RING.get()))
                || CursedRingHelper.hasCursedRing(player);

        if (alreadyHasRing) {
            return;
        }

        player.getInventory().add(new ItemStack(ModItems.CURSED_RING.get()));
    }
}
