package org.enigmatic_legacy.item.items.ring;

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 精美戒指 / Exquisite Ring。
 *
 * <p>注意：原项目内部注册名是 golden_ring，
 * 显示名才是 Exquisite Ring。
 *
 * <p>功能：
 * <ul>
 *     <li>装备在 Curios 戒指栏时提供 +1 幸运；</li>
 *     <li>让猪灵中立；</li>
 *     <li>如果玩家佩戴七咒之戒，猪灵中立效果不生效。</li>
 * </ul>
 */
public class ExquisiteRing extends Item implements ICurioItem {

    public ExquisiteRing() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    /**
     * 允许右键直接装备到 Curios 戒指栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    /**
     * 装备时提供 +1 幸运。
     *
     * <p>使用固定 modifier id，避免多个精美戒指重复叠加。
     * 这更接近原项目：同类戒指效果不应该无限堆叠。
     */
    @Override
    public @NotNull Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
                Attributes.LUCK,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "exquisite_ring_luck"),
                        1.0D,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return builder.build();
    }

    /**
     * 让猪灵把该物品视为金制物品。
     */
    public boolean isPiglinCurrency(@NotNull ItemStack stack) {
        return true;
    }

    /**
     * 佩戴时让猪灵中立。
     *
     * <p>七咒之戒佩戴者除外，因为原项目提示中明确说明：
     * 七咒者不会获得这个猪灵中立效果。
     */
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return !(wearer instanceof Player player) || !CursedRingHelper.hasCursedRing(player);
    }

    /**
     * Curios 版本的猪灵中立判断。
     */
    public boolean makesPiglinsNeutral(SlotContext context, ItemStack stack) {
        LivingEntity wearer = context.entity();

        return !(wearer instanceof Player player) || !CursedRingHelper.hasCursedRing(player);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.exquisite_ring.luck")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.exquisite_ring.piglin")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.exquisite_ring.cursed_warning")
                .withStyle(ChatFormatting.RED));
    }
}
