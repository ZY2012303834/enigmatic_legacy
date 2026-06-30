package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 阳灼护符查询工具。
 * 用途：
 * 统一封装 Curios 查询，避免事件类和客户端视觉事件里重复写 CuriosApi。
 */
public final class ScorchedCharmHelper {

    private ScorchedCharmHelper() {
    }

    /**
     * 判断实体是否佩戴阳灼护符。
     */
    public static boolean hasScorchedCharm(LivingEntity entity) {
        return findScorchedCharm(entity).isPresent();
    }

    /**
     * 在实体 Curios 栏中寻找阳灼护符。
     */
    public static Optional<? extends SlotResult> findScorchedCharm(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.is(ModItems.SCORCHED_CHARM.get())));
    }
}
