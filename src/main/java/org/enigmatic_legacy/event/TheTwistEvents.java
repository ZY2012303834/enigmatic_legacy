package org.enigmatic_legacy.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.book.TheTwist;
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
     * - 让其他 Incoming 阶段的基础伤害修正先执行；
     * - 倒转之启再追加 Boss / 玩家伤害加成。
     *
     * 第四诅咒不在这里除回去。
     * 当前七咒之戒已经在 LivingDamageEvent.Pre 中识别倒转之启，
     * 并且直接跳过“佩戴者造成伤害降低”这条诅咒。
     * 这样能避免旧写法在最终伤害阶段迁移后变成额外增伤。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();
        boolean attackingWithTheTwist = weapon.is(ModItems.THE_TWIST.get());

        /*
         * 倒转之启属于可由快捷栏 / 古旧书袋提供效果的书类物品。
         * 旧逻辑只检查主手，导致玩家把它放在快捷栏其它格子或书袋中时，
         * 完全进不到后续的 Boss / 玩家增伤与击退增强逻辑。
         */
        if (!attackingWithTheTwist && !TheTwist.hasTheTwist(attacker)) {
            return;
        }

        LivingEntity target = event.getEntity();

        /*
         * 未佩戴七咒之戒时：
         * - 如果玩家实际用主手倒转之启攻击，仍按原项目表现把伤害归零；
         * - 如果倒转之启只是放在快捷栏或书袋中，则只是不提供被动加成，
         *   不应该让玩家手里的其它武器也变成 0 伤害。
         *
         * 这样可以同时保留“七咒专属武器”的限制，
         * 又不会让被动携带判定误伤普通攻击。
         */
        if (!CursedRingHelper.hasCursedRing(attacker)) {
            if (attackingWithTheTwist) {
                event.setAmount(0.0F);
            }

            return;
        }

        float damage = event.getAmount();

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
                || ConfigCommon.isBoss(entity);
    }
}
