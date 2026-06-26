package org.enigmatic_legacy.item.items.armor.material;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.enigmatic_legacy.item.ModItems;

import java.util.EnumMap;
import java.util.List;

/**
 * 以太套装材料。
 * 重点：
 * layers 使用空列表，穿戴后不渲染护甲模型。
 * 物品本身仍然有图标、护甲值、耐久和套装效果。
 */
public final class EtheriumArmorMaterial {
    public static final Holder<ArmorMaterial> HOLDER = Holder.direct(new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 4);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.of(ModItems.ETHERIUM_INGOT.get()),
            List.of(),
            4.0F,
            0.15F
    ));

    private EtheriumArmorMaterial() {
    }
}