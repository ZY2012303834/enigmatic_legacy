package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 星云之眼工具类。

 * 作用：
 * 统一检查玩家或生物是否佩戴了星云之眼。

 * 这样事件类、网络包、其他物品逻辑都不用重复写 Curios 查询代码。
 */
public final class EyeOfNebulaHelper {
    private EyeOfNebulaHelper() {
    }

    /**
     * 判断实体是否佩戴星云之眼。
     */
    public static boolean hasEyeOfNebula(LivingEntity entity) {
        return findEyeOfNebula(entity).isPresent();
    }

    /**
     * 查找实体 Curios 栏位里的星云之眼 ItemStack。
     */
    public static Optional<ItemStack> findEyeOfNebula(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.EYE_OF_NEBULA.get())
                ))
                .map(SlotResult::stack);
    }
}