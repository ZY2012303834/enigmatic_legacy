package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

// 至暗卷轴
public class DarkestScroll extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    public DarkestScroll() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
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
