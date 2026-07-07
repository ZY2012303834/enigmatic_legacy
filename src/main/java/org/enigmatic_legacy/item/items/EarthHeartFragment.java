package org.enigmatic_legacy.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

// 大地之心碎片
public class EarthHeartFragment extends Item {
    public EarthHeartFragment() {
        super(new Properties()
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON));
    }
}