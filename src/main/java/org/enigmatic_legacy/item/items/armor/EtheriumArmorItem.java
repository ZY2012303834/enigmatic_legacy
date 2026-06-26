package org.enigmatic_legacy.item.items.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.item.items.armor.material.EtheriumArmorMaterial;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 以太套装。
 * 单件效果：
 * 穿戴时不显示。
 * 套装效果：
 * 生命值低于 40% 时生成强力护盾：
 * - 反弹大多数弹射物；
 * - 50% 伤害抗性；
 * - 攻击你的生物会被击退。
 */
public class EtheriumArmorItem extends ArmorItem {
    public EtheriumArmorItem(Type type) {
        super(
                EtheriumArmorMaterial.HOLDER,
                type,
                new Item.Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .rarity(Rarity.EPIC)
                        .durability(type.getDurability(74))
        );
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_armor.single"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_armor.set.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_armor.set.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_armor.set.3"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_armor.set.4"));
    }
}