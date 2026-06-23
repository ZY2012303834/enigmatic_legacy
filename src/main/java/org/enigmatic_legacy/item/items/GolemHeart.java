package org.enigmatic_legacy.item.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
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
import org.enigmatic_legacy.config.ConfigCommon;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 魔像之心 / Heart of the Golem。
 * Plus 版术石效果：
 * 1. 默认提供护甲与击退抗性；
 * 2. 没穿任何护甲时，提供更高护甲、护甲韧性与爆炸减伤；
 * 3. 近战减伤；
 * 4. 免疫挤压 / 墙内窒息 / 仙人掌 / 钟乳石伤害；
 * 5. 受到更多魔法伤害。
 */
public class GolemHeart extends Item implements ICurioItem {
    public static final ResourceLocation DEFAULT_ARMOR_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "golem_heart_default_armor"
    );

    public static final ResourceLocation DEFAULT_KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "golem_heart_default_knockback"
    );

    public static final ResourceLocation NO_ARMOR_ARMOR_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "golem_heart_without_armor"
    );

    public static final ResourceLocation NO_ARMOR_TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "golem_heart_without_armor_toughness"
    );

    public static final ResourceLocation NO_ARMOR_KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "golem_heart_without_armor_knockback"
    );

    public GolemHeart() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        // 每 tick 先清理两组属性，避免切换穿甲状态时残留。
        entity.getAttributes().removeAttributeModifiers(getDefaultModifiers());
        entity.getAttributes().removeAttributeModifiers(getNoArmorModifiers());

        if (hasNoArmor(entity)) {
            entity.getAttributes().addTransientAttributeModifiers(getNoArmorModifiers());
        } else {
            entity.getAttributes().addTransientAttributeModifiers(getDefaultModifiers());
        }
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();

        entity.getAttributes().removeAttributeModifiers(getDefaultModifiers());
        entity.getAttributes().removeAttributeModifiers(getNoArmorModifiers());

        ICurioItem.super.onUnequip(context, newStack, stack);
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getDefaultModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        DEFAULT_ARMOR_ID,
                        ConfigCommon.GOLEM_HEART_DEFAULT_ARMOR_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        modifiers.put(
                Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(
                        DEFAULT_KNOCKBACK_ID,
                        ConfigCommon.GOLEM_HEART_KNOCKBACK_RESISTANCE.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return modifiers;
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getNoArmorModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        NO_ARMOR_ARMOR_ID,
                        ConfigCommon.GOLEM_HEART_SUPER_ARMOR_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        modifiers.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(
                        NO_ARMOR_TOUGHNESS_ID,
                        ConfigCommon.GOLEM_HEART_SUPER_ARMOR_TOUGHNESS_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        modifiers.put(
                Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(
                        NO_ARMOR_KNOCKBACK_ID,
                        ConfigCommon.GOLEM_HEART_KNOCKBACK_RESISTANCE.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return modifiers;
    }

    public static boolean hasNoArmor(LivingEntity entity) {
        for (ItemStack armorStack : entity.getArmorSlots()) {
            if (!armorStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.passive")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.golem_heart.1",
                String.format("%.1f", ConfigCommon.GOLEM_HEART_DEFAULT_ARMOR_BONUS.get())
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.golem_heart.2")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.golem_heart.3",
                String.format("%.1f", ConfigCommon.GOLEM_HEART_SUPER_ARMOR_BONUS.get()),
                String.format("%.1f", ConfigCommon.GOLEM_HEART_SUPER_ARMOR_TOUGHNESS_BONUS.get())
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.golem_heart.4",
                ConfigCommon.GOLEM_HEART_EXPLOSION_RESISTANCE.get() + "%"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.golem_heart.5",
                ConfigCommon.GOLEM_HEART_MELEE_RESISTANCE.get() + "%"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.golem_heart.6",
                String.format("%.0f%%", ConfigCommon.GOLEM_HEART_KNOCKBACK_RESISTANCE.get() * 100.0D)
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.golem_heart.7")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.golem_heart.8",
                String.format("%.1f", ConfigCommon.GOLEM_HEART_MAGIC_VULNERABILITY.get())
        ).withStyle(ChatFormatting.RED));
    }
}