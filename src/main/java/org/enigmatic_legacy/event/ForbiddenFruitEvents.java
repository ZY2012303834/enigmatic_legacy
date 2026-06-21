package org.enigmatic_legacy.event;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.ForbiddenFruit;

/**
 * 禁忌之果的玩家常驻效果。
 *
 * <p>原版把“已食用禁忌之果”作为玩家永久数据保存。这里同样把标记写入
 * Player persistent data，并在 tick、治疗和玩家克隆事件里复刻它的后续效果。
 */
public final class ForbiddenFruitEvents {

    private ForbiddenFruitEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (!ForbiddenFruit.hasConsumedFruit(player)) {
            return;
        }

        ForbiddenFruit.applySyncMarker(player);

        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(20);
        foodData.setSaturation(0.0F);
        player.removeEffect(MobEffects.HUNGER);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!ForbiddenFruit.hasConsumedFruit(player)) {
            return;
        }

        // 原版只削弱小额治疗，主要对应自然恢复和再生类逐跳回血。
        if (event.getAmount() > 1.0F) {
            return;
        }

        float subtraction = ConfigCommon.FORBIDDEN_FRUIT_REGENERATION_SUBTRACTION.get() / 100.0F;
        event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - subtraction));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        if (ForbiddenFruit.hasConsumedFruit(event.getOriginal())) {
            ForbiddenFruit.setConsumedFruit(event.getEntity(), true);
        }
    }
}
