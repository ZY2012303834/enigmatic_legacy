package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.1")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.2")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.variant." + this.variant.id())
                .withStyle(this.variant.color()));

        String owner = getOwner(stack);

        if (!owner.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.owner", owner)
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.add(Component.translatable("curios.modifiers.charm")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.enigmatic_amulet.modifier." + this.variant.id())
                .withStyle(ChatFormatting.GOLD));
    }
}