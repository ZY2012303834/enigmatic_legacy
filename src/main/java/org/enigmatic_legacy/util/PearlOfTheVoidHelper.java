package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 虚空珍珠工具类。
 *
 * 作用：
 * 统一封装 Curios 查询逻辑。
 *
 * 好处：
 * 事件类、其他物品、未来兼容逻辑都不需要重复写 CuriosApi 查询代码。
 */
public final class PearlOfTheVoidHelper {
    private PearlOfTheVoidHelper() {
    }

    /**
     * 判断实体是否佩戴虚空珍珠。
     */
    public static boolean hasPearlOfTheVoid(LivingEntity entity) {
        return findPearlOfTheVoid(entity).isPresent();
    }

    /**
     * 查找实体 Curios 栏里的虚空珍珠。
     */
    public static Optional<ItemStack> findPearlOfTheVoid(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        stack -> stack.is(ModItems.VOID_PEARL.get())
                ))
                .map(SlotResult::stack);
    }
}