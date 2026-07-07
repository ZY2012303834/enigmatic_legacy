package org.enigmatic_legacy.item.items.ring;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * 末影之戒 / Ring of Ender。
 *
 * <p>装备在 Curios 戒指栏后，可以通过按键或背包 UI 按钮打开末影箱。
 */
public class EnderRing extends Item implements ICurioItem {

    public EnderRing() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    /**
     * 允许右键直接装备到 Curios 戒指栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

}
