package org.enigmatic_legacy.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 七咒受限 Curios 的兜底清理。
 */
public final class CursedRingRequirementEvents {
    private static final int FALLBACK_EJECT_DELAY_TICKS = 20;
    private static final Map<UUID, Integer> MISSING_CURSE_TICKS = new HashMap<>();

    private CursedRingRequirementEvents() {
    }

    /**
     * 玩家主动或被其他逻辑摘下七咒之戒时，立即弹出所有七咒受限饰品。
     */
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        if (!event.getFrom().is(ModItems.CURSED_RING.get()) || event.getTo().is(ModItems.CURSED_RING.get())) {
            return;
        }

        MISSING_CURSE_TICKS.remove(player.getUUID());
        CursedRingApi.ejectRestrictedCurios(player);
    }

    /**
     * 延迟兜底：如果受限饰品因为异常途径留在 Curios 中，但玩家确实没有七咒之戒，
     * 等待一小段时间后再弹出，避免登录/克隆时 Curios 槽位尚未完全同步导致误判。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        UUID playerId = player.getUUID();

        if (CursedRingApi.hasCursedRing(player) || !CursedRingApi.hasRestrictedCurio(player)) {
            MISSING_CURSE_TICKS.remove(playerId);
            return;
        }

        int missingTicks = MISSING_CURSE_TICKS.merge(playerId, 1, Integer::sum);

        if (missingTicks >= FALLBACK_EJECT_DELAY_TICKS) {
            CursedRingApi.ejectRestrictedCurios(player);
            MISSING_CURSE_TICKS.remove(playerId);
        }
    }
}
