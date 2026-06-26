package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.item.items.scroll.GiftOfTheHeaven;
import org.enigmatic_legacy.util.GiftOfTheHeavenHelper;

/**
 * 天堂之礼事件。
 */
public final class GiftOfTheHeavenEvents {
    private static final float FLYING_BREAK_SPEED_MULTIPLIER = 5.0F;

    private GiftOfTheHeavenEvents() {
    }

    /**
     * 每 tick 处理：
     * 1. 信标范围检测；
     * 2. 飞行授予 / 移除；
     * 3. 飞行经验消耗；
     * 4. 离开信标范围后的缓降。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = GiftOfTheHeavenHelper.findGift(player)
                .orElse(ItemStack.EMPTY);

        if (stack.getItem() instanceof GiftOfTheHeaven) {
            GiftOfTheHeaven.serverTick(player, stack);
        } else {
            GiftOfTheHeaven.revokeWhenMissing(player);
        }
    }

    /**
     * 飞行时增加挖掘速度。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (player.getAbilities().flying && GiftOfTheHeavenHelper.hasGift(player)) {
            event.setNewSpeed(event.getNewSpeed() * FLYING_BREAK_SPEED_MULTIPLIER);
        }
    }

    /**
     * 在信标范围内且未丧失飞行能力时，免疫摔落伤害。
     * 离开范围后只靠 8 秒缓降保护；
     * 缓降结束还没落地，就会正常摔落。
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!event.getSource().is(DamageTypes.FALL)) {
            return;
        }

        if (GiftOfTheHeavenHelper.hasGift(player) && GiftOfTheHeaven.hasSafeBeaconFlight(player)) {
            event.setCanceled(true);
        }
    }
}
