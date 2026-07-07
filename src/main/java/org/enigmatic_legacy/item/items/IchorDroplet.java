package org.enigmatic_legacy.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * 灵液滴。
 * 来源：
 * 1. 下界大多数战利品箱；
 * 2. 佩戴七咒之戒击杀恶魂的特殊掉落。
 * 说明：
 * 这是 Enigmatic Addons 中 Ichor Droplet 的复刻材料。
 */
public class IchorDroplet extends Item {

    public IchorDroplet() {
        super(new Properties()
                .stacksTo(64)
                .rarity(Rarity.UNCOMMON)
        );
    }
}