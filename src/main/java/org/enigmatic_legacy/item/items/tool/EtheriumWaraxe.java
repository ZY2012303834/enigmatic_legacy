package org.enigmatic_legacy.item.items.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.item.items.material.EtheriumToolMaterial;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 以太斧 / Etherium Waraxe。
 * 当前要求：
 * - 取消特殊效果；
 * - 攻击速度改为 1.0；
 * - 保留原版斧头基础行为，例如右键去皮。
 * 物品 ID：
 * enigmatic_legacy:etherium_axe
 */
public class EtheriumWaraxe extends AxeItem {
    public EtheriumWaraxe() {
        super(
                EtheriumToolMaterial.INSTANCE,
                new Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .rarity(Rarity.EPIC)
                        .attributes(AxeItem.createAttributes(
                                EtheriumToolMaterial.INSTANCE,
                                6.0F,
                                -3.0F
                        ))
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
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.etherium_axe.1"));
    }
}