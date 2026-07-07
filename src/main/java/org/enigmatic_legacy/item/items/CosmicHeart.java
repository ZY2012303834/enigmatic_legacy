package org.enigmatic_legacy.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

// 寰宇之心
public class CosmicHeart extends Item {

    public CosmicHeart() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    }

}
