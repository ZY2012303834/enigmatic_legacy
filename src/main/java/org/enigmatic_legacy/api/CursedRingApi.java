package org.enigmatic_legacy.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.SlotContext;

/**
 * 七咒之戒资格统一入口。
 *
 * <p>这里集中处理“玩家是否承受七咒”和“哪些 Curios 物品必须承受七咒才能装备/生效”。
 * 物品类只负责调用这个 API，避免每个物品各写一套略有差异的检测逻辑。
 */
public final class CursedRingApi {
    /**
     * Curios 在玩家刚进入世界时会分批恢复各个饰品槽。
     * 受限饰品可能先于七咒之戒被校验，所以登录初期只允许“保留装备”，实际效果仍由 hasCursedRing 判断。
     */
    public static final int LOAD_GRACE_TICKS = 100;

    private CursedRingApi() {
    }

    /**
     * 判断玩家是否真正佩戴并启用了七咒之戒。
     */
    public static boolean hasCursedRing(Player player) {
        return player != null
                && ConfigCommon.CURSED_RING_ENABLED.get()
                && CuriosLookupApi.hasCurio(player, ModItems.CURSED_RING.get());
    }

    /**
     * 判断物品是否属于“必须承受七咒才能装备/生效”的 Curios 物品。
     */
    public static boolean requiresCursedRing(ItemStack stack) {
        return !stack.isEmpty()
                && (stack.is(ModItems.ENCHANTER_PEARL.get())
                || stack.is(ModItems.CURSED_SCROLL.get())
                || stack.is(ModItems.CURSED_XP_SCROLL.get())
                || stack.is(ModItems.THUNDER_SCROLL.get())
                || stack.is(ModItems.NIGHT_SCROLL.get())
                || stack.is(ModItems.VIOLENCE_SCROLL.get())
                || stack.is(ModItems.AVARICE_SCROLL.get())
                || stack.is(ModItems.ULTIMATE_LUXURY_RING.get())
                || stack.is(ModItems.BLOODSTAINED_VALOR_EMBLEM.get())
                || stack.is(ModItems.TOTEM_OF_MALICE.get())
                || stack.is(ModItems.EARTH_PROMISE.get()));
    }

    /**
     * 受限 Curios 的装备资格。
     *
     * <p>没有七咒时不能新装备；但如果 Curios 正在刷新当前槽位中已经存在的物品，
     * 仍然放行，让后续兜底事件处理失效装备，避免登录或槽位刷新时误卸下。
     */
    public static boolean canEquipRestrictedCurio(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        return hasCursedRing(player)
                || CuriosLookupApi.isStackInSlot(player, context, stack)
                || isInLoadGrace(player);
    }

    /**
     * 玩家刚进入世界时的 Curios 恢复宽限。
     * 只用于避免装备被误卸下；所有实际效果仍必须走 canUseRestrictedCurio。
     */
    public static boolean isInLoadGrace(Player player) {
        return player != null && player.tickCount < LOAD_GRACE_TICKS;
    }

    /**
     * 判断某个受限物品的实际效果是否可以生效。
     */
    public static boolean canUseRestrictedCurio(Player player, ItemLike item) {
        return hasCursedRing(player) && CuriosLookupApi.hasCurio(player, item);
    }

    /**
     * 判断玩家当前是否装备了任何七咒受限 Curios。
     */
    public static boolean hasRestrictedCurio(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, CursedRingApi::requiresCursedRing).isPresent();
    }

    /**
     * 移除没有七咒资格时仍留在 Curios 栏中的受限物品。
     *
     * <p>优先放回玩家背包；背包满时掉落到玩家脚下。这个方法只负责兜底清理，
     * 不决定物品效果，所有效果入口仍必须单独调用本 API 做资格判断。
     */
    public static boolean ejectRestrictedCurios(Player player) {
        if (hasCursedRing(player)) {
            return false;
        }

        return CuriosLookupApi.getInventory(player)
                .map(handler -> {
                    boolean ejectedAny = false;

                    for (var stacksHandler : handler.getCurios().values()) {
                        var stacks = stacksHandler.getStacks();
                        boolean handlerChanged = false;

                        for (int slot = 0; slot < stacks.getSlots(); slot++) {
                            ItemStack equippedStack = stacks.getStackInSlot(slot);

                            if (!requiresCursedRing(equippedStack)) {
                                continue;
                            }

                            ItemStack ejectedStack = equippedStack.copy();
                            stacks.setStackInSlot(slot, ItemStack.EMPTY);
                            giveOrDrop(player, ejectedStack);
                            handlerChanged = true;
                            ejectedAny = true;
                        }

                        if (handlerChanged) {
                            stacksHandler.update();
                        }
                    }

                    return ejectedAny;
                })
                .orElse(false);
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            player.drop(stack, false);
        }
    }
}
