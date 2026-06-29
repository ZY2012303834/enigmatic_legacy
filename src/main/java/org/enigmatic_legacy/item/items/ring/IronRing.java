package org.enigmatic_legacy.item.items.ring;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * 铁指环 / Iron Ring。
 *
 * <p>装备在 Curios 戒指栏时，提供 +1 护甲值。
 */
public class IronRing extends Item implements ICurioItem {

    public IronRing() {
        super(new Item.Properties()
                .stacksTo(1));
    }

    /**
     * 允许右键直接装备到 Curios 戒指栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 装备时提供 +1 护甲值。
     *
     * <p>这是原项目铁指环的核心功能。
     */
    @Override
    public @NotNull Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "iron_ring_armor"),
                        1.0D,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return builder.build();
    }
}
