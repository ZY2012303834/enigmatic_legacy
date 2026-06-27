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
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.CursedRingHelper;

/**
 * 深渊之心事件。
 * 复刻逻辑：
 * 1. 佩戴七咒之戒击败末影龙后，在末影龙死亡位置生成深渊之心；
 * 2. 龙生成的深渊之心不受重力影响，悬浮在死亡位置；
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
     * 只维持龙生成的悬浮深渊之心状态。
     * 玩家拾取后再次丢出的深渊之心没有这个实体标记，
     * 因此会像普通掉落物一样运动和旋转。
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

        if (!itemEntity.getPersistentData().getBoolean(FLOATING_ABYSSAL_HEART_TAG)) {
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

    /**
     * 维持已标记的悬浮深渊之心状态。
     */
    private static void markAndFreezeAbyssalHeart(ItemEntity itemEntity) {
        if (!hasBoundPosition(itemEntity)) {
            itemEntity.getPersistentData().putDouble(BOUND_X_TAG, itemEntity.getX());
            itemEntity.getPersistentData().putDouble(BOUND_Y_TAG, itemEntity.getY());
            itemEntity.getPersistentData().putDouble(BOUND_Z_TAG, itemEntity.getZ());
            itemEntity.setUnlimitedLifetime();
        }

        double x = itemEntity.getPersistentData().getDouble(BOUND_X_TAG);
        double y = itemEntity.getPersistentData().getDouble(BOUND_Y_TAG);
        double z = itemEntity.getPersistentData().getDouble(BOUND_Z_TAG);

        /*
         * 这些状态可以每 tick 保持：
         * - 无重力；
         * - 发光；
         * - 停止物理速度。
         */
        itemEntity.setNoGravity(true);
        itemEntity.setGlowingTag(true);
        if (itemEntity.getDeltaMovement().lengthSqr() > 1.0E-7D) {
            itemEntity.setDeltaMovement(Vec3.ZERO);
        }

        /*
         * 防止被水流、爆炸、实体碰撞或其它模组移动。
         *
         * 注意：
         * teleportTo 只在位置偏移明显时调用，
         * 避免每 tick 强制传送影响客户端渲染插值。
         */
        if (itemEntity.distanceToSqr(x, y, z) > 0.01D) {
            itemEntity.teleportTo(x, y, z);
        }
    }

    private static boolean hasBoundPosition(ItemEntity itemEntity) {
        return itemEntity.getPersistentData().contains(BOUND_X_TAG)
                && itemEntity.getPersistentData().contains(BOUND_Y_TAG)
                && itemEntity.getPersistentData().contains(BOUND_Z_TAG);
    }
}
