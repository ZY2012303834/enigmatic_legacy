package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.item.items.scroll.GraceOfTheCreator;
import org.enigmatic_legacy.util.GraceOfTheCreatorHelper;

/**
 * 创造者的恩赐事件。
 */
public final class GraceOfTheCreatorEvents {
    private static final float FLYING_BREAK_SPEED_MULTIPLIER = 5.0F;

    private GraceOfTheCreatorEvents() {
    }

    /**
     * 每 tick 处理：
     * 1. 飞行授予；
     * 2. 非信标范围飞行经验消耗；
     * 3. 无经验时失去飞行能力。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = GraceOfTheCreatorHelper.findGrace(player)
                .orElse(ItemStack.EMPTY);

        if (stack.getItem() instanceof GraceOfTheCreator) {
            GraceOfTheCreator.serverTick(player, stack);
        } else {
            GraceOfTheCreator.revokeWhenMissing(player);
        }
    }

    /**
     * 补偿飞行时挖掘速度损失。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (player.getAbilities().flying && GraceOfTheCreatorHelper.hasGrace(player)) {
            event.setNewSpeed(event.getNewSpeed() * FLYING_BREAK_SPEED_MULTIPLIER);
        }
    }

    /**
     * 在未丧失飞行能力时免疫摔落伤害。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!event.getSource().is(DamageTypes.FALL)) {
            return;
        }

        if (GraceOfTheCreatorHelper.hasGrace(player) && GraceOfTheCreator.hasGraceFlight(player)) {
            event.setCanceled(true);
        }
    }
}
