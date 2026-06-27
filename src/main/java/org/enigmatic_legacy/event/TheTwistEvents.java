package org.enigmatic_legacy.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.TheTwist;
import org.enigmatic_legacy.util.CursedRingHelper;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 倒转之启事件。
 * 原项目机制：
 * 1. 未佩戴七咒之戒时，倒转之启造成 0 伤害；
 * 2. 佩戴七咒之戒时，倒转之启修正第四诅咒，始终造成全额伤害；
 * 3. 对 Boss 和玩家造成 +300% 额外伤害；
 * 4. 造成 +300% 击退；
 * 5. 对幻翼的击退额外乘 1.5。
 */
public final class TheTwistEvents {

    /**
     * Boss / 玩家伤害加成。
     * +300% 表示最终额外增加 3 倍当前伤害，
     * 即对 Boss 和玩家总计约为 4 倍伤害。
     */
    private static final float BOSS_DAMAGE_BONUS_MULTIPLIER =
            TheTwist.BOSS_DAMAGE_BONUS_PERCENT / 100.0F;

    /**
     * 击退加成。
     * +300% 表示最终击退强度约为 4 倍。
     */
    private static final float KNOCKBACK_MULTIPLIER =
            1.0F + TheTwist.KNOCKBACK_BONUS_PERCENT / 100.0F;

    /**
     * 临时记录需要增强击退的目标。
     * LivingIncomingDamageEvent 中能拿到攻击者；
     * LivingKnockBackEvent 中主要处理目标击退。
     * 所以这里用 WeakHashMap 过渡。
     */
    private static final Map<LivingEntity, Float> KNOCKBACK_TARGETS = new WeakHashMap<>();

    private TheTwistEvents() {
    }

    /**
     * 伤害阶段。
     * 使用 LOWEST：
     * - 让七咒之戒的怪物伤害削弱先执行；
     * - 然后倒转之启再把第四诅咒修正回来；
     * - 最后再追加 Boss / 玩家伤害加成。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();

        if (!weapon.is(ModItems.THE_TWIST.get())) {
            return;
        }

        LivingEntity target = event.getEntity();

        /*
         * 未佩戴七咒之戒：
         * - 不直接 cancel；
         * - 只把伤害归零；
         * - 这样物品本身 hurtEnemy 的点燃效果仍然可以保留。
         */
        if (!CursedRingHelper.hasCursedRing(attacker)) {
            event.setAmount(0.0F);
            return;
        }

        float damage = event.getAmount();

        /*
         * 修正第四诅咒。
         *
         * 你当前 CursedRingEvents 中，七咒佩戴者攻击 Enemy 会被削弱。
         * 原项目倒转之启的机制是：
         * - Alteration of the Fourth Curse
         * - Always deals its full damage.
         *
         * 所以这里在七咒削弱之后，把伤害除回去。
         */
        if (target instanceof Enemy) {
            damage = restoreFourthCurseDamage(damage);
        }

        /*
         * 对 Boss 和玩家 +300% 伤害。
         */
        if (isBossOrPlayer(target)) {
            damage += damage * BOSS_DAMAGE_BONUS_MULTIPLIER;
        }

        event.setAmount(damage);

        /*
         * 注册额外击退。
         *
         * 原项目：
         * - 普通目标：+300% 击退，即约 4 倍击退；
         * - 幻翼：再乘 1.5。
         */
        float knockback = KNOCKBACK_MULTIPLIER;

        if (target instanceof Phantom) {
            knockback *= 1.5F;
        }

        KNOCKBACK_TARGETS.put(target, knockback);
    }

    /**
     * 击退阶段。
     */
    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        Float multiplier = KNOCKBACK_TARGETS.remove(event.getEntity());

        if (multiplier == null) {
            return;
        }

        event.setStrength(event.getStrength() * multiplier);
    }

    /**
     * 修正第四诅咒伤害。
     * 当前项目七咒之戒对怪物伤害削弱来自：
     * ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF
     * 例如默认 50%：
     * - 七咒先把伤害乘 0.5；
     * - 倒转之启再除以 0.5；
     * - 最终恢复为全额伤害。
     */
    private static float restoreFourthCurseDamage(float damage) {
        float debuff = ConfigCommon.CURSED_RING_MONSTER_DAMAGE_DEBUFF.get() / 100.0F;
        float multiplier = Math.max(0.0F, 1.0F - debuff);

        if (multiplier <= 0.0001F) {
            return damage;
        }

        return damage / multiplier;
    }

    /**
     * 判断 Boss / 玩家。
     * 原项目使用配置列表判断 boss/player。
     * 这里先覆盖原版核心 Boss：
     * - 玩家
     * - 末影龙
     * - 凋灵
     * - 远古守卫者
     */
    private static boolean isBossOrPlayer(LivingEntity entity) {
        return entity instanceof Player
                || entity instanceof EnderDragon
                || entity instanceof WitherBoss
                || entity instanceof ElderGuardian;
    }
}