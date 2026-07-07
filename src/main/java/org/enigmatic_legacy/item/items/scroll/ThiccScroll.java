package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

// 空卷轴
public class ThiccScroll extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    public ThiccScroll() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null && SCROLL_SLOT.equals(context.identifier());
    }
}
