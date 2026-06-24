package org.enigmatic_legacy.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.enigmatic_legacy.util.ScrollOfThousandCursesHelper;

/**
 * 千咒卷轴事件。
 * 用事件实现动态加成，避免 Attribute Modifier 重复叠加和刷新问题。
 */
public final class ScrollOfThousandCursesEvents {
    private ScrollOfThousandCursesEvents() {
    }

    /**
     * +4% 攻击伤害 × 诅咒因子。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        double bonus = ScrollOfThousandCursesHelper.getAttackDamageBonus(player);

        if (bonus <= 0.0D) {
            return;
        }

        event.setNewDamage((float) (event.getNewDamage() * (1.0D + bonus)));
    }

    /**
     * +7% 挖掘速度 × 诅咒因子。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        double bonus = ScrollOfThousandCursesHelper.getMiningSpeedBonus(player);

        if (bonus <= 0.0D) {
            return;
        }

        event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + bonus)));
    }

    /**
     * +4% 生命恢复 × 诅咒因子。
     * 这里增强所有治疗来源：
     * 自然回血、药水、食物、其他模组治疗等。
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        double bonus = ScrollOfThousandCursesHelper.getHealingBonus(player);

        if (bonus <= 0.0D) {
            return;
        }

        event.setAmount((float) (event.getAmount() * (1.0D + bonus)));
    }
}