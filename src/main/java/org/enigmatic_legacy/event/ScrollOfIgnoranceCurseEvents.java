package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import org.enigmatic_legacy.item.items.scroll.ScrollOfIgnoranceCurse;
import org.enigmatic_legacy.util.ScrollOfIgnoranceCurseHelper;

/**
 * 无知诅咒卷轴的全局事件逻辑。
 *
 * <p>卷轴本体负责 Curios 槽位、经验储存和击退抗性动态属性；
 * 这里负责必须通过事件修改的效果：
 * 攻击伤害倍率、治疗倍率，以及死亡清空储存经验。</p>
 */
public final class ScrollOfIgnoranceCurseEvents {
    private ScrollOfIgnoranceCurseEvents() {
    }

    /**
     * 攻击伤害加成。
     *
     * <p>只有伤害来源实体是玩家，并且该玩家当前真正装备并可使用无知诅咒卷轴时才生效。
     * 这里先走 hasScroll 校验七咒资格，再读取 Curios 中的卷轴 ItemStack 计算当前储存等级带来的倍率。</p>
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (!ScrollOfIgnoranceCurseHelper.hasScroll(player)) {
            return;
        }

        ItemStack scroll = ScrollOfIgnoranceCurseHelper.findScroll(player)
                .orElse(ItemStack.EMPTY);

        if (scroll.isEmpty()) {
            return;
        }

        double bonus = ScrollOfIgnoranceCurse.getAttackDamageBonus(scroll);

        if (bonus <= 0.0D) {
            return;
        }

        event.setNewDamage((float) (event.getNewDamage() * (1.0D + bonus)));
    }

    /**
     * 治疗效果加成。
     *
     * <p>LivingHealEvent 覆盖自然回血、食物、药水和其它模组触发的治疗。
     * 加成只作用于被治疗的玩家本人，不影响玩家治疗其它实体的行为。</p>
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!ScrollOfIgnoranceCurseHelper.hasScroll(player)) {
            return;
        }

        ItemStack scroll = ScrollOfIgnoranceCurseHelper.findScroll(player)
                .orElse(ItemStack.EMPTY);

        if (scroll.isEmpty()) {
            return;
        }

        double bonus = ScrollOfIgnoranceCurse.getHealingBonus(scroll);

        if (bonus <= 0.0D) {
            return;
        }

        event.setAmount((float) (event.getAmount() * (1.0D + bonus)));
    }

    /**
     * 死亡惩罚。
     *
     * <p>复刻扩展项目行为：玩家死亡时，如果 Curios 槽位中存在该卷轴，
     * 直接清空卷轴中储存的全部经验。这里使用 ServerPlayer 限定服务端执行，
     * 避免客户端侧重复处理物品数据。</p>
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ScrollOfIgnoranceCurseHelper.findScroll(player)
                .ifPresent(ScrollOfIgnoranceCurse::clearStoredExperience);
    }
}
