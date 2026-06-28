package org.enigmatic_legacy.item.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.MagicQuartzRingHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 魔法石英戒指 / Magic Quartz Ring。
 * 参考 Enigmatic Addons 的 QuartzRing 复刻并适配到 NeoForge 1.21.1。
 * 效果：
 * 1. 戒指栏佩戴；
 * 2. +2 护甲；
 * 3. +1.5 幸运；
 * 4. 受到魔法、间接魔法、凋零、龙息伤害时减少 30%；
 * 5. 同一玩家同时只能佩戴一个魔法石英戒指。
 */
public class MagicQuartzRing extends Item implements ICurioItem {

    public static final double ARMOR_BONUS = 2.0D;
    public static final double LUCK_BONUS = 1.5D;

    /**
     * 魔法伤害抗性。
     * 0.30D = 减少 30% 对应伤害。
     */
    public static final double MAGIC_RESISTANCE = 0.30D;

    public MagicQuartzRing() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
        );
    }

    /**
     * 允许右键直接装备到 Curios 戒指栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 限制同一玩家同时只能佩戴一个魔法石英戒指。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        return MagicQuartzRingHelper.canEquipMagicQuartzRing(entity, context.identifier(), context.index());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.magic_quartz_ring.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.magic_quartz_ring.2"));
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(Component.translatable("curios.modifiers.ring")
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.magic_quartz_ring.modifier.magic_resistance",
                SpellstoneTooltip.percent("+30%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.magic_quartz_ring.modifier.armor",
                SpellstoneTooltip.number("+2")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.magic_quartz_ring.modifier.luck",
                SpellstoneTooltip.number("+1.5")
        ));

        tooltip.add(SpellstoneTooltip.negative(
                "tooltip.enigmatic_legacy.magic_quartz_ring.limit"
        ));
    }

    /**
     * 佩戴属性：
     * - +2 护甲；
     * - +1.5 幸运。
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
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "magic_quartz_ring_armor"),
                        ARMOR_BONUS,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        builder.put(
                Attributes.LUCK,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "magic_quartz_ring_luck"),
                        LUCK_BONUS,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return builder.build();
    }
}
