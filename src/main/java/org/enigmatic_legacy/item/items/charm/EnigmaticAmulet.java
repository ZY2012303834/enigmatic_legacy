package org.enigmatic_legacy.item.items.charm;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnigmaticAmulet extends Item {
    public static final String OWNER_TAG = "Owner";

    private final AmuletVariant variant;

    public EnigmaticAmulet(AmuletVariant variant) {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());

        this.variant = variant;
    }

    public AmuletVariant variant() {
        return this.variant;
    }

    public static ItemStack createRandom(RandomSource random) {
        return switch (random.nextInt(7)) {
            case 0 -> new ItemStack(ModItems.ENIGMATIC_AMULET_RED.get());
            case 1 -> new ItemStack(ModItems.ENIGMATIC_AMULET_AQUA.get());
            case 2 -> new ItemStack(ModItems.ENIGMATIC_AMULET_VIOLET.get());
            case 3 -> new ItemStack(ModItems.ENIGMATIC_AMULET_MAGENTA.get());
            case 4 -> new ItemStack(ModItems.ENIGMATIC_AMULET_GREEN.get());
            case 5 -> new ItemStack(ModItems.ENIGMATIC_AMULET_BLACK.get());
            default -> new ItemStack(ModItems.ENIGMATIC_AMULET_BLUE.get());
        };
    }

    public static void setOwner(ItemStack stack, String ownerName) {
        CompoundTag tag = new CompoundTag();
        tag.putString(OWNER_TAG, ownerName);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static String getOwner(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return "";
        }

        CompoundTag tag = data.copyTag();
        return tag.contains(OWNER_TAG) ? tag.getString(OWNER_TAG) : "";
    }

    /**
     * 神秘护身符 tooltip。
     * 统一规则：
     * 1. 介绍文字为紫色；
     * 2. 见证者文字不再作为负面效果，不再红色；
     * 3. 属性说明文字统一紫色；
     * 4. 如果语言文件中有写死的数字，后续需要把语言改成 %s 参数，数字才能单独金色。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.enigmatic_amulet.variant." + this.variant.id()));

        String owner = getOwner(stack);
        if (!owner.isEmpty()) {
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.owner",
                    SpellstoneTooltip.number(owner)
            ));
        }

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("curios.modifiers.charm"));
        tooltip.add(this.modifierTooltip());
    }

    private Component modifierTooltip() {
        return switch (this.variant) {
            case RED -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.red",
                    SpellstoneTooltip.number("+2")
            );
            case AQUA -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.aqua",
                    SpellstoneTooltip.percent("+15%")
            );
            case VIOLET -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.violet",
                    SpellstoneTooltip.percent("15%")
            );
            case MAGENTA -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.magenta",
                    SpellstoneTooltip.percent("-20%")
            );
            case GREEN -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.green",
                    SpellstoneTooltip.number("+2")
            );
            case BLACK -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.black",
                    SpellstoneTooltip.percent("10%")
            );
            case BLUE -> SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.enigmatic_amulet.modifier.blue",
                    SpellstoneTooltip.percent("+25%")
            );
        };
    }
}
