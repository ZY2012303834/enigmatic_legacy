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
     * 深渊之心拾取限制。
     * 修复内容：
     * - 达到资格的玩家不再使用 TriState.TRUE 强制拾取；
     * - 改为 TriState.DEFAULT，让原版拾取逻辑继续处理；
     * - 这样玩家主动丢出深渊之心后，会保留正常 pickup delay，
     *   不会立刻回到背包。
     * 逻辑：
     * - 未达资格：禁止拾取任何深渊之心；
     * - 已达资格：不强制拾取，交给原版逻辑处理。
     */
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack stack = itemEntity.getItem();
        Player player = event.getPlayer();

        if (!stack.is(ModItems.ABYSSAL_HEART.get())) {
            return;
        }

        /*
         * 已达资格：
         * 不要设置 TriState.TRUE。
         *
         * 原因：
         * - TriState.TRUE 会强制允许拾取；
         * - 可能绕过玩家刚丢出物品时的拾取延迟；
         * - 导致“刚扔出去就立刻回背包”。
         *
         * 使用 DEFAULT：
         * - 允许原版正常判断 pickup delay；
         * - 玩家丢出后不会立刻捡回；
         * - 延迟结束后仍可正常捡起。
         */
        if (AbyssalHeartHelper.isWorthy(player)) {
            event.setCanPickup(TriState.DEFAULT);
            return;
        }

        // 未达资格：禁止拾取。
        event.setCanPickup(TriState.FALSE);

        /*
         * 避免玩家贴着物品时每 tick 刷屏。
         */
        if (player.tickCount % 40 == 0) {
            AbyssalHeartHelper.sendUnworthyMessage(player);
        }
    }

    /**
     * 维持“龙死亡生成的深渊之心”悬浮状态。
     * 修复内容：
     * - 只处理带有 FLOATING_ABYSSAL_HEART_TAG 标记的深渊之心；
     * - 玩家从背包中主动丢出的深渊之心没有这个标记；
     * - 所以玩家丢出的深渊之心会像普通掉落物一样落地、旋转、可重新捡起。
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

        /*
         * 关键修复：
         * 只有“末影龙死亡位置生成的悬浮深渊之心”才会带这个标记。
         *
         * 玩家主动丢出的深渊之心不应该被重新标记为悬浮物。
         */
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
     * 维持深渊之心悬浮状态。
     * 注意：
     * - 这个方法只应该处理已经带有 FLOATING_ABYSSAL_HEART_TAG 的实体；
     * - 不要在这里给普通丢出的深渊之心补标记；
     * - 否则玩家从背包丢出的深渊之心也会被固定在空中。
     * 同时：
     * - 不要每 tick 调用 setUnlimitedLifetime()；
     * - 反复调用会影响 ItemEntity 的 age，
     *   可能导致掉落物旋转显示异常。
     */
    private static void markAndFreezeAbyssalHeart(ItemEntity itemEntity) {
        /*
         * 保险判断：
         * 如果没有悬浮标记，直接返回。
         */
        if (!itemEntity.getPersistentData().getBoolean(FLOATING_ABYSSAL_HEART_TAG)) {
            return;
        }

        double x = itemEntity.getPersistentData().getDouble(BOUND_X_TAG);
        double y = itemEntity.getPersistentData().getDouble(BOUND_Y_TAG);
        double z = itemEntity.getPersistentData().getDouble(BOUND_Z_TAG);

        itemEntity.setNoGravity(true);
        itemEntity.setGlowingTag(true);
        itemEntity.setDeltaMovement(Vec3.ZERO);

        /*
         * 防止被水流、爆炸、实体碰撞或其它模组移动。
         */
        if (itemEntity.distanceToSqr(x, y, z) > 0.01D) {
            itemEntity.teleportTo(x, y, z);
        }
    }
}
