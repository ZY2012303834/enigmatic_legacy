package org.enigmatic_legacy.item.items.scroll;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.ClientTooltipState;
import org.enigmatic_legacy.util.CursedSufferingTooltip;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 暴戾之咒 / The Curse of Violence。
 *
 * <p>复刻自 Enigmatic Addons 的 violence_scroll。
 * 它是千咒卷轴与噬咒之书路线的高阶产物：可以从物品或附魔书上吸收诅咒附魔，
 * 将不同诅咒记录为“已吸收诅咒”，并把这些诅咒转化为攻击速度、实体交互距离、
 * 击退抗性以及连续攻击同一目标时的额外伤害。</p>
 *
 * <p>由于原扩展要求“真正承受七咒之人”才能使用，本项目用深渊之心的 99.5%
 * 七咒折磨资格作为等价限制；实际效果也会在事件类中再次校验，避免登录恢复或
 * 其它模组强行放入 Curios 槽时产生无资格效果。</p>
 */
public class CurseOfViolence extends Item implements ICurioItem {
    public static final String VIOLENCE_SEVEN_CURSES_TAG = "ViolenceSevenCurses";

    private static final String SCROLL_SLOT = "scroll";
    private static final String OWNER_TAG = "LastHolder";
    private static final String DURABILITY_TAG = "Durability";
    private static final String STORED_DAMAGE_TAG = "VDamage";
    private static final String STORED_TARGET_TAG = "VTarget";
    private static final String ABSORBED_CURSES_TAG = "AbsorbedCurses";
    private static final String ABSORBED_CURSE_ID_TAG = "id";

    private static final ResourceLocation ATTACK_SPEED_MODIFIER = modLoc("violence_scroll_attack_speed");
    private static final ResourceLocation ENTITY_REACH_MODIFIER = modLoc("violence_scroll_entity_reach");
    private static final ResourceLocation KNOCKBACK_RESISTANCE_MODIFIER = modLoc("violence_scroll_knockback_resistance");

    public CurseOfViolence() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许手持右键直接装备，但仍复用 canEquip 中的全部限制。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 装备限制：
     * 1. 必须放入 scroll 槽；
     * 2. 必须是玩家；
     * 3. 必须通过七咒受限物品统一检查；
     * 4. 必须达到深渊之心资格；
     * 5. 同一玩家最多装备一个暴戾之咒。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context == null || !SCROLL_SLOT.equals(context.identifier())) {
            return false;
        }

        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        if (!CursedRingApi.canEquipRestrictedCurio(context, stack)) {
            return false;
        }

        /*
         * Curios 登录恢复时可能先恢复本卷轴，再恢复七咒之戒或统计缓存。
         * 已经在当前槽位里的卷轴允许暂时保留；真正生效仍由事件和 tick 中的
         * AbyssalHeartHelper.isWorthy(player) 控制。
         */
        if (!AbyssalHeartHelper.isWorthy(player)
                && !CuriosLookupApi.isStackInSlot(player, context, stack)
                && !CursedRingApi.isInLoadGrace(player)) {
            return false;
        }

        return canEquipViolenceScroll(player, context.index());
    }

    private static boolean canEquipViolenceScroll(Player player, int currentSlotIndex) {
        return CuriosLookupApi.getStacksHandler(player, SCROLL_SLOT)
                .map(scrollHandler -> {
                    var stacks = scrollHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack equippedStack = stacks.getStackInSlot(slot);

                        if (!equippedStack.is(ModItems.VIOLENCE_SCROLL.get())) {
                            continue;
                        }

                        if (slot == currentSlotIndex) {
                            continue;
                        }

                        return false;
                    }

                    return true;
                })
                .orElse(true);
    }

    /**
     * 背包内 tick 用于处理“卷轴自身带有诅咒附魔”的情况。
     *
     * <p>玩家可能通过铁砧或其它模组让卷轴本体获得诅咒附魔。
     * 原扩展会自动吞掉这些诅咒并把它们计入暴戾之咒，这里保留该行为。</p>
     */
    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull net.minecraft.world.entity.Entity entity,
            int slotId,
            boolean isSelected
    ) {
        if (level.isClientSide() || !(entity instanceof Player player) || !AbyssalHeartHelper.isWorthy(player)) {
            return;
        }

        setOwner(stack, player);
        absorbOwnCurses(stack, 15);
    }

    /**
     * 装备 tick：
     * 1. 记录最后持有者，让物品耐久条可以显示；
     * 2. 吸收卷轴自身诅咒；
     * 3. 按已吸收诅咒数刷新属性修饰。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        if (!AbyssalHeartHelper.isWorthy(player)) {
            removeAttributeModifiers(player);
            return;
        }

        setOwner(stack, player);
        absorbOwnCurses(stack, 15);
        refreshAttributeModifiers(player, stack);
    }

    /**
     * 卸下时清理瞬时属性，并重置“上一击伤害”缓存。
     *
     * <p>连续攻击同一目标的额外伤害依赖上一击伤害缓存。
     * 卸下卷轴后重置它，避免下一次重新装备时继承旧战斗状态。</p>
     */
    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (context.entity() instanceof Player player && !player.level().isClientSide()) {
            setStoredDamage(stack, 0.0F);
            removeAttributeModifiers(player);
        }
    }

    /**
     * 背包界面中“拿着卷轴右键其它物品”时吸收目标物品上的诅咒。
     */
    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player) {
        if (action == ClickAction.PRIMARY || !AbyssalHeartHelper.isWorthy(player) || !slot.hasItem()) {
            return super.overrideStackedOnOther(stack, slot, action, player);
        }

        ItemStack target = slot.getItem();

        if (!hasAnyCurse(target)) {
            return super.overrideStackedOnOther(stack, slot, action, player);
        }

        slot.set(absorbCursesFromTarget(stack, target));
        playAbsorbSound(player);
        return true;
    }

    /**
     * 背包界面中“拿着其它物品右键卷轴”时吸收目标物品上的诅咒。
     */
    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (action == ClickAction.PRIMARY || !AbyssalHeartHelper.isWorthy(player) || other.isEmpty()) {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }

        if (!hasAnyCurse(other)) {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }

        access.set(absorbCursesFromTarget(stack, other));
        playAbsorbSound(player);
        return true;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getOwner(stack) != null;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(getDurability(stack) * 13.0F / Math.max(1, ConfigCommon.CURSE_OF_VIOLENCE_MAX_DURABILITY.get()));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float fullness = Mth.clamp((float) getDurability(stack) / Math.max(1, ConfigCommon.CURSE_OF_VIOLENCE_MAX_DURABILITY.get()), 0.0F, 1.0F);
        return 255 << 24 | ((int) (fullness * 128.0F + 127.0F) << 16);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        int curseCount = getCurseCount(stack);

        tooltip.add(SpellstoneTooltip.empty());

        if (ClientTooltipState.isShiftDown()) {
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.violence_scroll.damage_store",
                    SpellstoneTooltip.percent(formatPercent(getStoredDamageModifier(stack) / 100.0D))
            ));

            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.invulnerable.title"));
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.violence_scroll.invulnerable.damage",
                    SpellstoneTooltip.percent("+" + formatPercent(ConfigCommon.CURSE_OF_VIOLENCE_INVULNERABLE_ATTACK_MODIFIER.get() / 100.0D))
            ));
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.violence_scroll.invulnerable.heal",
                    SpellstoneTooltip.percent(formatPercent(ConfigCommon.CURSE_OF_VIOLENCE_INVULNERABLE_HEAL_MULTIPLIER.get() / 100.0D))
            ));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.invulnerable.reset"));

            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.absorb"));
            addCurseCountTooltip(tooltip, curseCount);

            tooltip.add(SpellstoneTooltip.empty());
            addCurrentBonusTooltip(tooltip, curseCount);

            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.weakened_curses.1"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.weakened_curses.2"));

            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.violence_scroll.worthy_only"));
            CursedSufferingTooltip.appendTooltip(tooltip);
        } else {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.lore.1"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.lore.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.lore.3"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.violence_scroll.lore.4"));

            tooltip.add(SpellstoneTooltip.empty());
            addCurseCountTooltip(tooltip, curseCount);

            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.holdShift());
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.violence_scroll.cursed_only"));
        }
    }

    private static void addCurseCountTooltip(List<Component> tooltip, int curseCount) {
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.violence_scroll.count",
                SpellstoneTooltip.number(curseCount)
        ));
    }

    private static void addCurrentBonusTooltip(List<Component> tooltip, int curseCount) {
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.violence_scroll.attack_speed",
                SpellstoneTooltip.percent("+" + formatPercent(curseCount * ConfigCommon.CURSE_OF_VIOLENCE_ATTACK_SPEED_BOOST.get() / 100.0D))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.violence_scroll.entity_reach",
                SpellstoneTooltip.percent("+" + formatPercent(curseCount * ConfigCommon.CURSE_OF_VIOLENCE_ENTITY_REACH_BOOST.get() / 100.0D))
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.violence_scroll.knockback_resistance",
                SpellstoneTooltip.number(String.format(Locale.ROOT, "+%.3f", curseCount * ConfigCommon.CURSE_OF_VIOLENCE_KNOCKBACK_RESISTANCE_BOOST.get()))
        ));
    }

    /**
     * 刷新由吸收诅咒带来的属性。
     *
     * <p>addTransientModifier 不会写入存档，但如果每 tick 只添加不移除，
     * 某些环境下仍可能因为重复 UUID 导致日志噪声或修饰残留。
     * 因此每次刷新前先移除本卷轴的三个固定修饰器，再按当前诅咒数重建。</p>
     */
    public static void refreshAttributeModifiers(Player player, ItemStack stack) {
        removeAttributeModifiers(player);

        int curseCount = getCurseCount(stack);

        if (curseCount <= 0) {
            return;
        }

        addModifier(
                player,
                Attributes.ATTACK_SPEED,
                ATTACK_SPEED_MODIFIER,
                curseCount * ConfigCommon.CURSE_OF_VIOLENCE_ATTACK_SPEED_BOOST.get() / 100.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );

        addModifier(
                player,
                Attributes.ENTITY_INTERACTION_RANGE,
                ENTITY_REACH_MODIFIER,
                curseCount * ConfigCommon.CURSE_OF_VIOLENCE_ENTITY_REACH_BOOST.get() / 100.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );

        addModifier(
                player,
                Attributes.KNOCKBACK_RESISTANCE,
                KNOCKBACK_RESISTANCE_MODIFIER,
                curseCount * ConfigCommon.CURSE_OF_VIOLENCE_KNOCKBACK_RESISTANCE_BOOST.get(),
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    public static void removeAttributeModifiers(Player player) {
        removeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
        removeModifier(player, Attributes.ENTITY_INTERACTION_RANGE, ENTITY_REACH_MODIFIER);
        removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER);
    }

    private static void addModifier(
            Player player,
            Holder<Attribute> attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null || amount == 0.0D) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(id, amount, operation));
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    /**
     * 吸收目标物品上的诅咒并返回“已移除诅咒”的目标副本。
     */
    private static ItemStack absorbCursesFromTarget(ItemStack scroll, ItemStack target) {
        ItemStack result = target.copyWithCount(1);
        int absorbed = addCursesFromStack(scroll, result);

        if (absorbed <= 0) {
            return target;
        }

        if (target.is(Items.ENCHANTED_BOOK)) {
            addDurability(scroll, 20 + 30 * absorbed);

            if (!result.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()) {
                return result;
            }

            return new ItemStack(Items.BOOK);
        }

        addDurability(scroll, 20 + 40 * absorbed);
        return result;
    }

    /**
     * 吸收卷轴自身携带的诅咒。
     */
    private static void absorbOwnCurses(ItemStack stack, int durabilityPerCurse) {
        int absorbed = addCursesFromStack(stack, stack);

        if (absorbed > 0) {
            addDurability(stack, durabilityPerCurse * absorbed);
        }
    }

    /**
     * 读取目标 ItemStack 的普通附魔和附魔书储存附魔，记录其中的诅咒并从目标上移除。
     *
     * <p>返回值是“本次发现的诅咒项数”，用于决定怒意能量增加多少。
     * 是否已经吸收过同名诅咒不会影响能量增长；原扩展也是吸收动作发生一次就奖励能量，
     * 但显示和属性只按唯一诅咒数量计算。</p>
     */
    private static int addCursesFromStack(ItemStack scroll, ItemStack target) {
        int absorbed = 0;

        EnchantmentRemovalResult enchantments = removeCurses(target.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        absorbed += enchantments.absorbedCount();

        if (enchantments.changed()) {
            if (enchantments.remaining().isEmpty()) {
                target.remove(DataComponents.ENCHANTMENTS);
            } else {
                target.set(DataComponents.ENCHANTMENTS, enchantments.remaining());
            }
            addCurseIds(scroll, enchantments.curseIds());
        }

        EnchantmentRemovalResult storedEnchantments = removeCurses(target.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
        absorbed += storedEnchantments.absorbedCount();

        if (storedEnchantments.changed()) {
            if (storedEnchantments.remaining().isEmpty()) {
                target.remove(DataComponents.STORED_ENCHANTMENTS);
            } else {
                target.set(DataComponents.STORED_ENCHANTMENTS, storedEnchantments.remaining());
            }
            addCurseIds(scroll, storedEnchantments.curseIds());
        }

        return absorbed;
    }

    public static boolean hasAnyCurse(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return containsCurse(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY))
                || containsCurse(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
    }

    private static boolean containsCurse(ItemEnchantments enchantments) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(EnchantmentTags.CURSE)) {
                return true;
            }
        }

        return false;
    }

    private static EnchantmentRemovalResult removeCurses(ItemEnchantments enchantments) {
        if (enchantments.isEmpty()) {
            return EnchantmentRemovalResult.EMPTY;
        }

        Set<ResourceLocation> curseIds = new HashSet<>();

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();

            if (!enchantment.is(EnchantmentTags.CURSE)) {
                continue;
            }

            enchantment.unwrapKey()
                    .map(ResourceKey::location)
                    .ifPresent(curseIds::add);
        }

        if (curseIds.isEmpty()) {
            return EnchantmentRemovalResult.EMPTY;
        }

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        mutable.removeIf(enchantment -> enchantment.is(EnchantmentTags.CURSE));
        return new EnchantmentRemovalResult(true, mutable.toImmutable(), curseIds, curseIds.size());
    }

    public static boolean removeOneCurse(ItemStack stack) {
        return removeOneCurse(stack, DataComponents.ENCHANTMENTS)
                || removeOneCurse(stack, DataComponents.STORED_ENCHANTMENTS);
    }

    private static boolean removeOneCurse(ItemStack stack, net.minecraft.core.component.DataComponentType<ItemEnchantments> componentType) {
        ItemEnchantments enchantments = stack.getOrDefault(componentType, ItemEnchantments.EMPTY);

        if (enchantments.isEmpty()) {
            return false;
        }

        boolean[] removed = {false};
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        mutable.removeIf(enchantment -> {
            if (removed[0] || !enchantment.is(EnchantmentTags.CURSE)) {
                return false;
            }

            removed[0] = true;
            return true;
        });

        if (!removed[0]) {
            return false;
        }

        ItemEnchantments remaining = mutable.toImmutable();

        if (remaining.isEmpty()) {
            stack.remove(componentType);
        } else {
            stack.set(componentType, remaining);
        }

        return true;
    }

    private static void addCurseIds(ItemStack stack, Set<ResourceLocation> curseIds) {
        if (curseIds.isEmpty()) {
            return;
        }

        CompoundTag tag = getTag(stack);
        ListTag list = tag.getList(ABSORBED_CURSES_TAG, Tag.TAG_COMPOUND);
        Set<String> knownIds = new HashSet<>();

        for (int index = 0; index < list.size(); index++) {
            knownIds.add(list.getCompound(index).getString(ABSORBED_CURSE_ID_TAG));
        }

        for (ResourceLocation curseId : curseIds) {
            String id = curseId.toString();

            if (!knownIds.add(id)) {
                continue;
            }

            CompoundTag entry = new CompoundTag();
            entry.putString(ABSORBED_CURSE_ID_TAG, id);
            list.add(entry);
        }

        tag.put(ABSORBED_CURSES_TAG, list);
        setTag(stack, tag);
    }

    public static int getCurseCount(ItemStack stack) {
        return getTag(stack).getList(ABSORBED_CURSES_TAG, Tag.TAG_COMPOUND).size();
    }

    public static int getDurability(ItemStack stack) {
        return Math.max(0, getTag(stack).getInt(DURABILITY_TAG));
    }

    public static void addDurability(ItemStack stack, int amount) {
        CompoundTag tag = getTag(stack);
        int maxDurability = Math.max(1, ConfigCommon.CURSE_OF_VIOLENCE_MAX_DURABILITY.get());
        int durability = Mth.clamp(tag.getInt(DURABILITY_TAG) + amount, 0, maxDurability);
        tag.putInt(DURABILITY_TAG, durability);
        setTag(stack, tag);
    }

    public static double getStoredDamageModifier(ItemStack stack) {
        int curseCount = getCurseCount(stack);
        double apply = Math.pow(curseCount * ConfigCommon.CURSE_OF_VIOLENCE_BOOST_PER_CURSE_MODIFIER.get(), 0.725D);
        apply *= (double) getDurability(stack) / Math.max(1, ConfigCommon.CURSE_OF_VIOLENCE_MAX_DURABILITY.get());
        return Math.min(95.0D, ConfigCommon.CURSE_OF_VIOLENCE_BASE_CURSE_MODIFIER.get() + apply);
    }

    public static float getStoredDamage(ItemStack stack) {
        return getTag(stack).getFloat(STORED_DAMAGE_TAG);
    }

    public static void setStoredDamage(ItemStack stack, float damage) {
        CompoundTag tag = getTag(stack);
        tag.putFloat(STORED_DAMAGE_TAG, Math.max(0.0F, damage));
        setTag(stack, tag);
    }

    public static UUID getStoredTarget(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.hasUUID(STORED_TARGET_TAG) ? tag.getUUID(STORED_TARGET_TAG) : null;
    }

    public static void setStoredTarget(ItemStack stack, UUID targetId) {
        CompoundTag tag = getTag(stack);
        tag.putUUID(STORED_TARGET_TAG, targetId);
        tag.putFloat(STORED_DAMAGE_TAG, 0.0F);
        setTag(stack, tag);
    }

    private static UUID getOwner(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
    }

    private static void setOwner(ItemStack stack, Player player) {
        CompoundTag tag = getTag(stack);
        tag.putUUID(OWNER_TAG, player.getUUID());
        setTag(stack, tag);
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void playAbsorbSound(Player player) {
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS,
                0.8F,
                1.2F + player.getRandom().nextFloat() * 0.4F
        );
    }

    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.1f%%", value * 100.0D);
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path);
    }

    private record EnchantmentRemovalResult(
            boolean changed,
            ItemEnchantments remaining,
            Set<ResourceLocation> curseIds,
            int absorbedCount
    ) {
        private static final EnchantmentRemovalResult EMPTY = new EnchantmentRemovalResult(
                false,
                ItemEnchantments.EMPTY,
                Set.of(),
                0
        );
    }
}
