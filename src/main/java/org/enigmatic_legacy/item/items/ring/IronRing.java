package org.enigmatic_legacy.item.items.ring;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 铁指环 / Iron Ring。
 * 装备在 Curios 戒指栏时，提供 +1 护甲值。
 */
public class IronRing extends Item implements ICurioItem {
    private static final double ARMOR_BONUS = 1.0D;

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
     * 直接显示铁指环效果，不需要按 Shift。
     * 显示效果：
     * 护甲值+1
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(
                Component.literal("护甲值")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                        .append(Component.literal("+1").withStyle(ChatFormatting.GOLD))
        );
    }

    /**
     * 装备时提供 +1 护甲值。
     * 注意：
     * - 不再使用固定的 enigmatic_legacy:iron_ring_armor；
     * - 改为根据 Curios 槽位 identifier + index 生成唯一 modifier id；
     * - 这样多个铁指环装备在不同戒指栏时，护甲值可以正常叠加。
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
                        getArmorModifierId(slotContext),
                        ARMOR_BONUS,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return builder.build();
    }

    /**
     * 为每个 Curios 戒指槽生成独立属性 ID。
     * 例如：
     * - enigmatic_legacy:iron_ring_armor_ring_0
     * - enigmatic_legacy:iron_ring_armor_ring_1
     * 这样不同槽位的铁指环不会互相覆盖属性修饰符。
     */
    private static ResourceLocation getArmorModifierId(SlotContext slotContext) {
        String slotId = slotContext.identifier()
                .toLowerCase()
                .replace(':', '_')
                .replace('/', '_');

        return ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                "iron_ring_armor_" + slotId + "_" + slotContext.index()
        );
    }
}