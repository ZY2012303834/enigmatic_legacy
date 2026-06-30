package org.enigmatic_legacy.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.enigmatic_legacy.entity.PermanentItemEntity;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.SoulCrystal;
import org.enigmatic_legacy.util.CursedRingHelper;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 超维容器和灵魂水晶的实体/死亡事件逻辑。
 *
 * <p>原版中二者都是物品，但死亡后会被包进 PermanentItemEntity。
 * 因此这里同时处理“生成专用实体”和“维护灵魂损失属性”两部分。
 */
public final class SoulCrystalEvents {
    private static final Map<UUID, Integer> PENDING_LOST_CRYSTALS = new ConcurrentHashMap<>();

    private SoulCrystalEvents() {
    }

    /**
     * 玩家死亡掉落物生成后触发。
     *
     * <p>如果玩家有实际掉落物，则把所有掉落物收进超维容器，再生成一个不可摧毁的 PermanentItemEntity。
     * 如果没有掉落物但灵魂水晶机制仍然生效，则单独生成灵魂水晶实体。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

    // 超维容器和灵魂水晶现在都只由七咒之戒触发。
    // 没有佩戴七咒之戒时，完全保留原版死亡掉落流程。
        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        boolean canDropSoulCrystal = canDropSoulCrystal(player);
        ItemStack embeddedSoulCrystal = canDropSoulCrystal
                ? ModItems.SOUL_CRYSTAL.get().createCrystalFrom(player)
                : ItemStack.EMPTY;

        if (!embeddedSoulCrystal.isEmpty()) {
            PENDING_LOST_CRYSTALS.put(player.getUUID(), ModItems.SOUL_CRYSTAL.get().getLostCrystals(player));
        }

        Collection<ItemEntity> drops = event.getDrops();

        if (!drops.isEmpty()) {
            ItemStack vessel = ModItems.STORAGE_CRYSTAL.get()
                    .storeDropsOnCrystal(drops, player, embeddedSoulCrystal);
            spawnPermanentItem(player, vessel, 1.5D);
            drops.clear();
            return;
        }

        if (!embeddedSoulCrystal.isEmpty()) {
            spawnPermanentItem(player, embeddedSoulCrystal, 1.5D);
        }
    }

    /**
     * 玩家死亡后会克隆 Player 实例，持久化 NBT 不总是自动按模组预期迁移。
     * 这里显式复制已损失灵魂水晶数量。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        ModItems.SOUL_CRYSTAL.get().copyLostCrystals(event.getOriginal(), event.getEntity());
        applyPendingLostCrystals(event.getEntity(), true);
    }

    /**
     * 登录时重建灵魂损失带来的最大生命值 modifier。
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ModItems.SOUL_CRYSTAL.get().updatePlayerSoulMap(event.getEntity());
    }

    /**
     * 换维度/重生后属性容器可能被刷新，下一 tick 前补一次 modifier。
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        applyPendingLostCrystals(event.getEntity(), true);
        ModItems.SOUL_CRYSTAL.get().updatePlayerSoulMap(event.getEntity());
    }

    private static boolean canDropSoulCrystal(Player player) {
        SoulCrystal soulCrystal = ModItems.SOUL_CRYSTAL.get();

        return soulCrystal.getLostCrystals(player) < ConfigCommon.MAX_SOUL_CRYSTAL_LOSS.get();
    }

    private static void applyPendingLostCrystals(Player player, boolean clear) {
        Integer pendingLostCrystals = PENDING_LOST_CRYSTALS.get(player.getUUID());

        if (pendingLostCrystals == null) {
            return;
        }

        SoulCrystal soulCrystal = ModItems.SOUL_CRYSTAL.get();

        if (pendingLostCrystals > soulCrystal.getLostCrystals(player)) {
            soulCrystal.setLostCrystals(player, pendingLostCrystals);
        }

        if (clear) {
            PENDING_LOST_CRYSTALS.remove(player.getUUID());
        }
    }

    private static void spawnPermanentItem(Player player, ItemStack stack, double yOffset) {
        PermanentItemEntity entity = new PermanentItemEntity(
                player.level(),
                player.getX(),
                player.getY() + yOffset,
                player.getZ(),
                stack
        );
        entity.setOwnerId(player.getUUID());
        entity.setDefaultPickupDelay();
        player.level().addFreshEntity(entity);
    }
}
