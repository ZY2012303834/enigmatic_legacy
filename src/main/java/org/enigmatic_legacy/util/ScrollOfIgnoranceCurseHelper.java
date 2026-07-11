package org.enigmatic_legacy.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 无知诅咒卷轴查询工具。
 *
 * <p>把“从 Curios 里查找卷轴”和“判断卷轴是否真正可用”集中在这里，
 * 事件、网络包和物品逻辑都调用同一套入口，避免不同位置写出略有差异的资格判断。</p>
 */
public final class ScrollOfIgnoranceCurseHelper {
    private ScrollOfIgnoranceCurseHelper() {
    }

    /**
     * 在实体的 Curios 槽位中查找第一个无知诅咒卷轴。
     *
     * <p>这里只负责查找 ItemStack，不判断七咒资格。
     * 某些场景，例如死亡清空经验，只需要找到卷轴本身即可。</p>
     */
    public static Optional<ItemStack> findScroll(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.CURSED_XP_SCROLL.get());
    }

    /**
     * 判断实体是否真正拥有可生效的无知诅咒卷轴。
     *
     * <p>该方法要求实体是玩家、玩家佩戴七咒之戒，并且 Curios 中装备了无知诅咒卷轴。
     * 这比单纯 findScroll 更严格，适合攻击、治疗、快捷键等实际效果入口。</p>
     */
    public static boolean hasScroll(LivingEntity entity) {
        return entity instanceof Player player
                && CursedRingApi.canUseRestrictedCurio(player, ModItems.CURSED_XP_SCROLL.get());
    }
}
