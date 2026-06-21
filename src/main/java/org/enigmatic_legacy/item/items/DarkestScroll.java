package org.enigmatic_legacy.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class DarkestScroll extends Item {

    public DarkestScroll() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }
}
