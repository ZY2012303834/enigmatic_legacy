package org.enigmatic_legacy.item.items.charm;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 飞升护符 / Amulet of Ascension
 * 按原项目复刻：
 * 1. 品质：EPIC；
 * 2. 防火；
 * 3. 最大堆叠 1；
 * 4. 栏位：curios:charm；
 * 5. 效果：同时拥有七种神秘护身符颜色效果。
 */
public class AscensionAmulet extends Item {

    public AscensionAmulet() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
        );
    }

    /**
     * 获取见证者名称。
     * 如果后续通过合成继承普通神秘护身符 Owner，
     * 这里会自动显示原来的见证者。
     */
    public static String getOwner(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return "";
        }

        CompoundTag tag = data.copyTag();
        return tag.contains(EnigmaticAmulet.OWNER_TAG) ? tag.getString(EnigmaticAmulet.OWNER_TAG) : "";
    }

    /**
     * 飞升护符 tooltip。
     * 统一规则：
     * 1. 普通介绍文字：紫色；
     * 2. 数字 / 百分比：金色；
     * 3. 使用独立 ascension_amulet.modifier.* 语言 key；
     * 4. 不再使用 enigmatic_amulet.modifier.*，避免数字被写死成紫色；
     * 5. 使用 List<Component>，避免原始 List 警告。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ascension_amulet.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ascension_amulet.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ascension_amulet.3"));

        String owner = getOwner(stack);
        if (!owner.isEmpty()) {
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.owner",
                    SpellstoneTooltip.number(owner)
            ));
        }

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("curios.modifiers.charm"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.red",
                SpellstoneTooltip.number("+2")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.aqua",
                SpellstoneTooltip.percent("+15%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.violet",
                SpellstoneTooltip.percent("15%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.magenta",
                SpellstoneTooltip.percent("-20%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.green",
                SpellstoneTooltip.number("+2")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.black",
                SpellstoneTooltip.percent("10%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.ascension_amulet.modifier.blue",
                SpellstoneTooltip.percent("+25%")
        ));

        tooltip.add(SpellstoneTooltip.empty());
    }
}
