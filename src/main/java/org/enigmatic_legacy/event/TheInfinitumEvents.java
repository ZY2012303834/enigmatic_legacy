package org.enigmatic_legacy.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.book.TheInfinitum;
import org.enigmatic_legacy.util.AbyssalHeartHelper;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 无止之言事件。
 */
public final class TheInfinitumEvents {
    private static final float BOSS_DAMAGE_BONUS_MULTIPLIER =
            TheInfinitum.BOSS_DAMAGE_BONUS_PERCENT / 100.0F;
    private static final float KNOCKBACK_MULTIPLIER =
            1.0F + TheInfinitum.KNOCKBACK_BONUS_PERCENT / 100.0F;
    private static final float LIFESTEAL_MULTIPLIER =
            TheInfinitum.LIFESTEAL_PERCENT / 100.0F;

    private static final Map<LivingEntity, Float> KNOCKBACK_TARGETS = new WeakHashMap<>();

    private TheInfinitumEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();
        boolean attackingWithTheInfinitum = weapon.is(ModItems.THE_INFINITUM.get());

        /*
         * 无止之言是启示之证路线的书类物品，基础被动应当和其它可收纳书一样，
         * 在快捷栏或古旧书袋中都可以生效。这里保留主手判定，只用于区分
         * “实际拿无止之言攻击”与“携带无止之言给其它武器提供被动”。
         */
        if (!attackingWithTheInfinitum && !TheInfinitum.hasTheInfinitum(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();

        /*
         * 未满足深渊之心资格时：
         * - 主手直接用无止之言攻击仍然造成 0 伤害；
         * - 如果只是把无止之言放在快捷栏或书袋中，则不提供被动，
         *   但也不能把其它武器的伤害错误归零。
         */
        if (!AbyssalHeartHelper.isWorthy(attacker)) {
            if (attackingWithTheInfinitum) {
                event.setAmount(0.0F);
            }

            return;
        }

        applyDebuffs(target);

        float damage = event.getAmount();

        /*
         * 无尽之书同样属于会修正第四诅咒的特殊武器。
         * 当前七咒之戒在 LivingDamageEvent.Pre 中识别主手无尽之书，
         * 并直接跳过“造成伤害降低”这条诅咒。
         * 因此这里不再把伤害按第四诅咒倍率除回去，避免新旧逻辑叠加后变成额外增伤。
         */
        if (isBossOrPlayer(target)) {
            damage += damage * BOSS_DAMAGE_BONUS_MULTIPLIER;
        }

        event.setAmount(damage);

        float knockback = KNOCKBACK_MULTIPLIER;
        if (target instanceof Phantom) {
            knockback *= 1.5F;
        }

        KNOCKBACK_TARGETS.put(target, knockback);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        /*
         * 吸血属于无止之言的被动效果，和增伤/击退一样需要支持快捷栏与古旧书袋。
         * 主手属性和附魔仍然只来自实际拿在手里的物品；这里只处理战斗后回血。
         */
        if (!TheInfinitum.hasTheInfinitum(attacker)) {
            return;
        }

        if (!AbyssalHeartHelper.isWorthy(attacker)) {
            return;
        }

        float damage = event.getNewDamage();
        if (damage > 0.0F) {
            attacker.heal(damage * LIFESTEAL_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onHolderIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        /*
         * 濒死保护同样按书类携带规则生效。
         * 旧的 isHeld 只检查主副手，会漏掉快捷栏其它格子和古旧书袋。
         */
        if (!TheInfinitum.hasTheInfinitum(player) || !AbyssalHeartHelper.isWorthy(player)) {
            return;
        }

        if (event.getAmount() < player.getHealth()) {
            return;
        }

        if (player.getRandom().nextInt(100) >= TheInfinitum.UNDEAD_PROBABILITY_PERCENT) {
            return;
        }

        event.setCanceled(true);
        player.setHealth(Math.max(player.getHealth(), 1.0F));
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        Float multiplier = KNOCKBACK_TARGETS.remove(event.getEntity());

        if (multiplier == null) {
            return;
        }

        event.setStrength(event.getStrength() * multiplier);
    }

    private static void applyDebuffs(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 500, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 3, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 3, false, true));
    }

    private static boolean isBossOrPlayer(LivingEntity entity) {
        return entity instanceof Player
                || ConfigCommon.isBoss(entity);
    }
}
