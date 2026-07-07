package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.item.ModItems;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * 以太核心查询工具。
 *
 * <p>统一封装 Curios 查询，事件类和主动技能入口只关心“是否佩戴以太核心”。</p>
 */
public final class EtheriumCoreHelper {
    private static final String SPELLSTONE_SLOT = "spellstone";

    private EtheriumCoreHelper() {
    }

    public static boolean hasEtheriumCore(LivingEntity entity) {
        return findEtheriumCore(entity).isPresent();
    }

    public static Optional<ItemStack> findEtheriumCore(LivingEntity entity) {
        return findEtheriumCoreSlot(entity).map(SlotResult::stack);
    }

    public static Optional<SlotResult> findEtheriumCoreSlot(LivingEntity entity) {
        return CuriosLookupApi.findFirstSlot(entity, ModItems.ETHERIUM_CORE.get())
                .filter(result -> result.slotContext() != null)
                .filter(result -> SPELLSTONE_SLOT.equals(result.slotContext().identifier()));
    }
}
