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
import org.enigmatic_legacy.util.SpellstoneTooltip;
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

    private static final String SPELLSTONE_SLOT = "spellstone";

    private static boolean isSpellstoneSlot(SlotContext context) {
        return SPELLSTONE_SLOT.equals(context.identifier());
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        // 保险处理：如果因为旧存档、旧数据包或其他原因进入了非 spellstone 槽，
        // 立即清理属性并不触发效果。
        if (!isSpellstoneSlot(context)) {
            entity.getAttributes().removeAttributeModifiers(getDefaultModifiers());
            entity.getAttributes().removeAttributeModifiers(getNoArmorModifiers());
            return;
        }

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
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.passive"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.golem_heart.1",
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.GOLEM_HEART_DEFAULT_ARMOR_BONUS.get()))
        ));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.golem_heart.2"));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.golem_heart.3",
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.GOLEM_HEART_SUPER_ARMOR_BONUS.get())),
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.GOLEM_HEART_SUPER_ARMOR_TOUGHNESS_BONUS.get()))
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.golem_heart.4",
                SpellstoneTooltip.number(ConfigCommon.GOLEM_HEART_EXPLOSION_RESISTANCE.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.golem_heart.5",
                SpellstoneTooltip.number(ConfigCommon.GOLEM_HEART_MELEE_RESISTANCE.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.golem_heart.6",
                SpellstoneTooltip.number(String.format("%.0f%%", ConfigCommon.GOLEM_HEART_KNOCKBACK_RESISTANCE.get() * 100.0D))
        ));

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.golem_heart.7"));

        tooltip.add(SpellstoneTooltip.negative(
                "tooltip.enigmatic_legacy.golem_heart.8",
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.GOLEM_HEART_MAGIC_VULNERABILITY.get()))
        ));
    }
}
