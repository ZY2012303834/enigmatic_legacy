package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 魔像之心工具类。
 * 修复点：
 * 1. 不能只判断 Curios 背包里是否存在魔像之心；
 * 2. 必须确认魔像之心真的装备在 spellstone 槽；
 * 3. 否则脱下后，旧 Curios 数据、非目标槽位或异常残留都会继续触发魔法易伤。
 */
public final class GolemHeartHelper {

    /**
     * 魔像之心只允许生效的 Curios 槽位。
     */
    private static final String SPELLSTONE_SLOT = "spellstone";

    private GolemHeartHelper() {
    }

    /**
     * 判断实体是否真正佩戴魔像之心。
     * 注意：
     * 这里只认 spellstone 槽。
     * 背包、其它 Curios 槽、旧数据残留都不应该让魔像之心生效。
     */
    public static boolean hasGolemHeart(LivingEntity entity) {
        return findEquippedGolemHeart(entity).isPresent();
    }

    /**
     * 查找真正装备在 spellstone 槽中的魔像之心。
     */
    public static Optional<ItemStack> findEquippedGolemHeart(LivingEntity entity) {
        return findEquippedGolemHeartSlot(entity)
                .map(SlotResult::stack);
    }

    /**
     * 查找魔像之心所在的 Curios 槽位结果。
     * 重点：
     * result.slotContext().identifier() 必须等于 spellstone。
     */
    public static Optional<SlotResult> findEquippedGolemHeartSlot(LivingEntity entity) {
        return CuriosLookupApi.findFirstSlot(entity, ModItems.GOLEM_HEART.get())
                .filter(result -> result.slotContext() != null)
                .filter(result -> SPELLSTONE_SLOT.equals(result.slotContext().identifier()));
    }
}
