package org.enigmatic_legacy.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

// 大地之心
public class EarthHeart extends Item {
    public EarthHeart() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }
}