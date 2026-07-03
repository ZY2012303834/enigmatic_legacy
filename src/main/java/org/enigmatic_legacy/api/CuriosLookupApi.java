package org.enigmatic_legacy.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Curios 查询统一入口。
 * <p>
 * 项目里大量物品只需要做“是否佩戴某个物品”或“查找第一个符合条件的饰品”。
 * 统一放在这里可以减少重复的 CuriosApi 调用，也方便以后兼容其他饰品栏实现。
 */
public final class CuriosLookupApi {
    private CuriosLookupApi() {
    }

    /**
     * 获取实体的 Curios 背包。
     */
    public static Optional<ICuriosItemHandler> getInventory(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity);
    }

    /**
     * 查找第一个指定物品的 Curios 槽位结果。
     */
    public static Optional<SlotResult> findFirstSlot(LivingEntity entity, ItemLike item) {
        return findFirstSlot(entity, stack -> stack.is(item.asItem()));
    }

    /**
     * 查找第一个满足条件的 Curios 槽位结果。
     */
    public static Optional<SlotResult> findFirstSlot(LivingEntity entity, Predicate<ItemStack> predicate) {
        return getInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(predicate));
    }

    /**
     * 查找第一个指定物品的 ItemStack。
     */
    public static Optional<ItemStack> findFirstStack(LivingEntity entity, ItemLike item) {
        return findFirstSlot(entity, item).map(SlotResult::stack);
    }

    /**
     * 查找第一个满足条件的 ItemStack。
     */
    public static Optional<ItemStack> findFirstStack(LivingEntity entity, Predicate<ItemStack> predicate) {
        return findFirstSlot(entity, predicate).map(SlotResult::stack);
    }

    /**
     * 判断实体是否佩戴指定物品。
     */
    public static boolean hasCurio(LivingEntity entity, ItemLike item) {
        return findFirstSlot(entity, item).isPresent();
    }

    /**
     * 获取指定 Curios 栏位的栈处理器。
     */
    public static Optional<ICurioStacksHandler> getStacksHandler(LivingEntity entity, String slotIdentifier) {
        return getInventory(entity)
                .flatMap(handler -> handler.getStacksHandler(slotIdentifier));
    }
}
