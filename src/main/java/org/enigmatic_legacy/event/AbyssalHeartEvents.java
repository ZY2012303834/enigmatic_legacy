package org.enigmatic_legacy.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 深渊之心事件。
 * 复刻逻辑：
 * 1. 佩戴七咒之戒击败末影龙后，在末影龙死亡位置生成深渊之心；
 * 2. 深渊之心不受重力影响，悬浮在死亡位置；
 * 3. 玩家没有达到 99.5% 七咒折磨时间比例时，无法捡起；
 * 4. 玩家死亡重生时保留七咒折磨时间统计。
 */
public final class AbyssalHeartEvents {
    private static final String FLOATING_ABYSSAL_HEART_TAG = "enigmatic_legacy_floating_abyssal_heart";
    private static final String BOUND_X_TAG = "enigmatic_legacy_bound_x";
    private static final String BOUND_Y_TAG = "enigmatic_legacy_bound_y";
    private static final String BOUND_Z_TAG = "enigmatic_legacy_bound_z";

    private AbyssalHeartEvents() {
    }

    /**
     * 每 tick 记录玩家总游戏时间和佩戴七咒之戒的时间。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer) {
            AbyssalHeartHelper.tickPlaytime(player);
        }
    }

    /**
     * 玩家死亡后复制深渊之心资格统计。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        AbyssalHeartHelper.copyPersistentData(event.getOriginal(), event.getEntity());
    }

    /**
     * 佩戴七咒之戒击败末影龙后生成深渊之心。
     * 按你当前需求：
     * 只要佩戴七咒之戒击败末影龙，就会生成。
     * 但如果没有达到 99.5% 七咒折磨比例，
     * 玩家无法捡起，也无法使用。
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof EnderDragon dragon)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return;
        }

        if (!AbyssalHeartHelper.canGainAnotherAbyssalHeart(player)) {
            return;
        }

        ItemEntity heartEntity = createFloatingAbyssalHeart(
                dragon,
                new ItemStack(ModItems.ABYSSAL_HEART.get())
        );

        event.getDrops().add(heartEntity);
        AbyssalHeartHelper.incrementAbyssalHeartsGained(player);
    }

    /**
     * 未达到 99.5% 七咒折磨比例时，阻止拾取深渊之心。
     * NeoForge 的 ItemEntityPickupEvent.Pre 可以通过 TriState.FALSE
     * 明确拒绝某个物品被玩家捡起。
     */
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack stack = itemEntity.getItem();
        Player player = event.getPlayer();

        if (!stack.is(ModItems.ABYSSAL_HEART.get())) {
            return;
        }

        if (AbyssalHeartHelper.isWorthy(player)) {
            event.setCanPickup(TriState.TRUE);
            return;
        }

        event.setCanPickup(TriState.FALSE);

        /*
         * 避免玩家贴着物品时每 tick 刷屏。
         */
        if (player.tickCount % 40 == 0) {
            AbyssalHeartHelper.sendUnworthyMessage(player);
        }
    }

    /**
     * 维持深渊之心悬浮状态。
     * 使用普通 ItemEntity，而不是自定义实体：
     * - 设置无重力；
     * - 设置发光；
     * - 设置无限寿命；
     * - 绑定到首次生成的位置；
     * - 每 tick 把速度清零并拉回绑定位置。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        if (!itemEntity.getItem().is(ModItems.ABYSSAL_HEART.get())) {
            return;
        }

        markAndFreezeAbyssalHeart(itemEntity);
    }

    private static ItemEntity createFloatingAbyssalHeart(Entity source, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(
                source.level(),
                source.getX(),
                source.getY() + source.getBbHeight() * 0.5D,
                source.getZ(),
                stack
        );

        itemEntity.setDeltaMovement(Vec3.ZERO);
        itemEntity.setNoGravity(true);
        itemEntity.setGlowingTag(true);
        itemEntity.setUnlimitedLifetime();
        itemEntity.setPickUpDelay(20);

        itemEntity.getPersistentData().putBoolean(FLOATING_ABYSSAL_HEART_TAG, true);
        itemEntity.getPersistentData().putDouble(BOUND_X_TAG, itemEntity.getX());
        itemEntity.getPersistentData().putDouble(BOUND_Y_TAG, itemEntity.getY());
        itemEntity.getPersistentData().putDouble(BOUND_Z_TAG, itemEntity.getZ());

        return itemEntity;
    }

    private static void markAndFreezeAbyssalHeart(ItemEntity itemEntity) {
        if (!itemEntity.getPersistentData().getBoolean(FLOATING_ABYSSAL_HEART_TAG)) {
            itemEntity.getPersistentData().putBoolean(FLOATING_ABYSSAL_HEART_TAG, true);
            itemEntity.getPersistentData().putDouble(BOUND_X_TAG, itemEntity.getX());
            itemEntity.getPersistentData().putDouble(BOUND_Y_TAG, itemEntity.getY());
            itemEntity.getPersistentData().putDouble(BOUND_Z_TAG, itemEntity.getZ());
        }

        double x = itemEntity.getPersistentData().getDouble(BOUND_X_TAG);
        double y = itemEntity.getPersistentData().getDouble(BOUND_Y_TAG);
        double z = itemEntity.getPersistentData().getDouble(BOUND_Z_TAG);

        itemEntity.setNoGravity(true);
        itemEntity.setGlowingTag(true);
        itemEntity.setUnlimitedLifetime();
        itemEntity.setDeltaMovement(Vec3.ZERO);

        /*
         * 防止被水流、爆炸、实体碰撞或其它模组移动。
         */
        if (itemEntity.distanceToSqr(x, y, z) > 0.01D) {
            itemEntity.teleportTo(x, y, z);
        }
    }
}