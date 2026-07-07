package org.enigmatic_legacy.item.items.spellstone;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.sound.ModSounds;
import org.enigmatic_legacy.util.EtheriumCoreHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 以太核心 / Etherium Core。
 *
 * <p>复刻 Enigmatic Addons 的 Etherium Core，并按本项目 1.21.1 NeoForge 结构重写。</p>
 *
 * <p>它是由魔像之心与以太护盾技术结合而成的防御型术石。</p>
 *
 * <p>主动能力会临时激活以太护盾，让佩戴者不必等到生命值过低也能触发护盾。</p>
 *
 * <p>被动能力提供护甲与护甲韧性，并把承受的部分伤害记录下来，转化为下一次攻击的额外伤害。</p>
 */
public class EtheriumCore extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    public static final String SHIELD_TICKS_TAG = "enigmatic_legacy.etherium_core_shield_ticks";
    public static final String STORED_DAMAGE_TAG = "enigmatic_legacy.etherium_core_stored_damage";
    public static final String ARMOR_HIDDEN_TAG = "etheriumCoreArmorHidden";

    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "etherium_core_armor"
    );

    private static final ResourceLocation TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "etherium_core_toughness"
    );

    private static final ResourceLocation ARMOR_MULTIPLIER_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "etherium_core_armor_multiplier"
    );

    private static final ResourceLocation TOUGHNESS_MULTIPLIER_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "etherium_core_toughness_multiplier"
    );

    private static final ResourceLocation KNOCKBACK_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "etherium_core_knockback_resistance"
    );

    public EtheriumCore() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * 手持右键逻辑。
     *
     * <p>普通右键返回 PASS，交给 Curios 处理右键装备。</p>
     *
     * <p>Shift + 右键切换护甲显示标记。实际隐藏渲染可由客户端渲染层读取该标记扩展。</p>
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide()) {
            boolean next = !isArmorHidden(stack);
            setArmorHidden(stack, next);
            player.displayClientMessage(Component.translatable(
                    next
                            ? "message.enigmatic_legacy.etherium_core.armor_hidden"
                            : "message.enigmatic_legacy.etherium_core.armor_visible"
            ), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /**
     * Curios 每 tick 被动逻辑。
     *
     * <p>持续提供护甲与护甲韧性，并让主动护盾倒计时。</p>
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        entity.getAttributes().removeAttributeModifiers(getAttributeModifiers());

        if (!isSpellstoneSlot(context)) {
            return;
        }

        entity.getAttributes().addTransientAttributeModifiers(getAttributeModifiers());

        if (!entity.level().isClientSide() && entity instanceof Player player) {
            int shieldTicks = player.getPersistentData().getInt(SHIELD_TICKS_TAG);
            if (shieldTicks > 0) {
                player.getPersistentData().putInt(SHIELD_TICKS_TAG, shieldTicks - 1);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(getAttributeModifiers());

        if (entity instanceof Player player) {
            player.getPersistentData().remove(SHIELD_TICKS_TAG);
            player.getPersistentData().remove(STORED_DAMAGE_TAG);
        }

        ICurioItem.super.onUnequip(context, newStack, stack);
    }

    /**
     * 主动能力入口。
     *
     * <p>这个方法由术石快捷键网络包调用，本身不会自动触发。</p>
     *
     * <p>主动能力会激活以太护盾一段时间。</p>
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        player.getPersistentData().putInt(SHIELD_TICKS_TAG, ConfigCommon.ETHERIUM_CORE_SHIELD_DURATION.get());

        BlockPos origin = player.blockPosition();
        level.playSound(
                null,
                origin,
                ModSounds.SHIELD_TRIGGER.get(),
                SoundSource.PLAYERS,
                1.35F,
                0.9F + player.getRandom().nextFloat() * 0.2F
        );

        player.getCooldowns().addCooldown(this, ConfigCommon.ETHERIUM_CORE_COOLDOWN.get());
    }

    public static boolean isShieldActive(Player player) {
        return player.getPersistentData().getInt(SHIELD_TICKS_TAG) > 0;
    }

    public static boolean isArmorHidden(Player player) {
        return EtheriumCoreHelper.findEtheriumCore(player)
                .map(EtheriumCore::isArmorHidden)
                .orElse(false);
    }

    public static boolean isArmorHidden(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return !tag.contains(ARMOR_HIDDEN_TAG) || tag.getBoolean(ARMOR_HIDDEN_TAG);
    }

    public static void setArmorHidden(ItemStack stack, boolean hidden) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(ARMOR_HIDDEN_TAG, hidden);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static double getStoredDamage(Player player) {
        return player.getPersistentData().getDouble(STORED_DAMAGE_TAG);
    }

    public static void setStoredDamage(Player player, double value) {
        double clamped = Math.clamp(value, 0.0D, ConfigCommon.ETHERIUM_CORE_MAX_STORED_DAMAGE.get());

        if (clamped <= 0.0D) {
            player.getPersistentData().remove(STORED_DAMAGE_TAG);
        } else {
            player.getPersistentData().putDouble(STORED_DAMAGE_TAG, clamped);
        }
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        ARMOR_ID,
                        ConfigCommon.ETHERIUM_CORE_ARMOR_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        ARMOR_MULTIPLIER_ID,
                        ConfigCommon.ETHERIUM_CORE_ARMOR_MULTIPLIER.get() / 100.0D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        modifiers.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(
                        TOUGHNESS_ID,
                        ConfigCommon.ETHERIUM_CORE_TOUGHNESS_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        modifiers.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(
                        TOUGHNESS_MULTIPLIER_ID,
                        ConfigCommon.ETHERIUM_CORE_TOUGHNESS_MULTIPLIER.get() / 100.0D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        modifiers.put(
                Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(
                        KNOCKBACK_RESISTANCE_ID,
                        ConfigCommon.ETHERIUM_CORE_KNOCKBACK_RESISTANCE.get() / 100.0D,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return modifiers;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift()); // 按住 Shift 查看详情。
            return;
        }

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.active" // 主动能力：
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.active.1", // 激活以太护盾 %s 秒。
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.ETHERIUM_CORE_SHIELD_DURATION.get() / 20.0F))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.cooldown", // 冷却：%s 秒。
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.ETHERIUM_CORE_COOLDOWN.get() / 20.0F))
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.passive" // 被动能力：
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.1",
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.ETHERIUM_CORE_ARMOR_BONUS.get())),
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.ETHERIUM_CORE_TOUGHNESS_BONUS.get()))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.2",
                SpellstoneTooltip.percent("+" + ConfigCommon.ETHERIUM_CORE_ARMOR_MULTIPLIER.get() + "%"),
                SpellstoneTooltip.percent("+" + ConfigCommon.ETHERIUM_CORE_TOUGHNESS_MULTIPLIER.get() + "%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.3",
                SpellstoneTooltip.percent("+" + ConfigCommon.ETHERIUM_CORE_KNOCKBACK_RESISTANCE.get() + "%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.4",
                SpellstoneTooltip.percent(ConfigCommon.ETHERIUM_CORE_DAMAGE_CONVERSION.get() + "%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.5",
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.ETHERIUM_CORE_MAX_STORED_DAMAGE.get()))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.6"
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.7",
                SpellstoneTooltip.percent("+" + ConfigCommon.ETHERIUM_CORE_SHIELD_THRESHOLD_BONUS.get() + "%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.etherium_core.passive.8"
        ));
    }
}
