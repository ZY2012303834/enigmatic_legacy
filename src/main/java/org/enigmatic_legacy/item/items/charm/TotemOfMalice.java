package org.enigmatic_legacy.item.items.charm;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Optional;

/**
 * 恶意图腾 / Totem of Malice。
 *
 * <p>复刻 Enigmatic Addons 中的恶意图腾，并按当前项目的数据组件与 Curios API 重写。</p>
 * <p>图腾拥有自定义能量，不使用原版物品耐久；能量耗尽后图腾保留但进入禁用状态。</p>
 * <p>禁用状态可以通过铁砧使用邪恶精髓恢复能量。</p>
 */
public class TotemOfMalice extends Item implements ICurioItem {
    public static final double RAIDER_DAMAGE_BOOST = 1.5D;
    public static final double RAIDER_DAMAGE_RESISTANCE = 0.5D;

    private static final String CHARM_SLOT = "charm";
    private static final String POWER_TAG = "MalicePower";
    private static final int BASE_POWER = 2;
    private static final int MAX_POWER_CAP = 8;
    private static final int ENCHANTMENT_VALUE = 12;

    /**
     * 创建恶意图腾。
     *
     * <p>原拓展定位是史诗级、不可堆叠、防火 Curios 物品。</p>
     * <p>这里不设置原版 durability，因为实际耐久是 {@link #POWER_TAG} 自定义能量。</p>
     */
    public TotemOfMalice() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 判断玩家是否携带或佩戴恶意图腾，并且满足七咒使用资格。
     *
     * <p>该方法用于袭击者伤害增减这类被动效果。</p>
     * <p>被动效果不要求图腾仍有能量；即使图腾被禁用，已装备到有效位置的恶意仍然会影响袭击者。</p>
     * <p>有效位置限定为主手、副手和 {@code charm} 护符栏，背包其它格子不再生效。</p>
     *
     * @param player 待检查玩家
     * @return 玩家承受七咒且主副手或护符栏存在恶意图腾时返回 true
     */
    public static boolean hasUsableTotem(Player player) {
        return CursedRingApi.hasCursedRing(player)
                && (hasTotemInHands(player) || findTotemInCharmSlot(player, false).isPresent());
    }

    /**
     * 查找可以触发保命效果的恶意图腾。
     *
     * <p>保命效果要求图腾拥有至少 1 点能量。</p>
     * <p>优先使用主副手中的图腾，随后才查找 {@code charm} 护符栏。</p>
     * <p>背包其它位置的图腾不会触发保命，避免“随身携带即可生效”的旧逻辑继续存在。</p>
     *
     * @param player 待检查玩家
     * @return 找到的有效图腾；没有时返回空 Optional
     */
    public static Optional<ItemStack> findChargedTotem(Player player) {
        if (!CursedRingApi.hasCursedRing(player)) {
            return Optional.empty();
        }

        for (ItemStack stack : player.getHandSlots()) {
            if (isChargedTotem(stack)) {
                return Optional.of(stack);
            }
        }

        return findTotemInCharmSlot(player, true);
    }

    /**
     * 消耗 1 点恶意图腾能量。
     *
     * <p>该方法只修改自定义能量，不会缩减物品数量。</p>
     * <p>这对应原拓展变更：能量耗尽后图腾不会消失，只是无法继续触发复活。</p>
     *
     * @param stack 需要消耗能量的恶意图腾
     */
    public static void consumePower(ItemStack stack) {
        setPower(stack, getPower(stack) - 1);
    }

    /**
     * 判断某个物品栈是否是有能量的恶意图腾。
     *
     * @param stack 待检查物品栈
     * @return 是恶意图腾且能量大于 0 时返回 true
     */
    public static boolean isChargedTotem(ItemStack stack) {
        return stack.is(ModItems.TOTEM_OF_MALICE.get()) && getPower(stack) > 0;
    }

    /**
     * 获取恶意图腾当前能量。
     *
     * <p>没有写入过能量数据的图腾视为 0 点能量，也就是禁用状态。</p>
     * <p>读取时会按当前耐久附魔等级重新夹取上限，避免附魔变化后出现非法能量值。</p>
     *
     * @param stack 恶意图腾物品栈
     * @return 当前能量，范围为 0 到当前最大能量
     */
    public static int getPower(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        return Mth.clamp(tag.getInt(POWER_TAG), 0, getMaxPower(stack));
    }

    /**
     * 设置恶意图腾当前能量。
     *
     * <p>写入值会被限制在 0 到当前最大能量之间。</p>
     * <p>该数据保存到 {@link DataComponents#CUSTOM_DATA}，符合 1.21+ 的物品数据组件体系。</p>
     *
     * @param stack 恶意图腾物品栈
     * @param power 目标能量
     */
    public static void setPower(ItemStack stack, int power) {
        CompoundTag tag = copyCustomData(stack);
        tag.putInt(POWER_TAG, Mth.clamp(power, 0, getMaxPower(stack)));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 获取恶意图腾最大能量。
     *
     * <p>原拓展规则：基础上限为 2，耐久附魔每级提高 1 点，最高不超过 8 点。</p>
     *
     * @param stack 恶意图腾物品栈
     * @return 当前最大能量
     */
    public static int getMaxPower(ItemStack stack) {
        return Math.min(BASE_POWER + getUnbreakingLevel(stack), MAX_POWER_CAP);
    }

    /**
     * 创建默认显示/给予的恶意图腾物品栈。
     *
     * <p>恶意图腾的“耐久”由 {@link #POWER_TAG} 保存，而不是原版 damage。</p>
     * <p>让默认实例带满基础能量，可以保证创造栏、展示动画和普通获得途径都呈现为可用状态。</p>
     *
     * @return 带满基础恶意能量的默认物品栈
     */
    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setPower(stack, getMaxPower(stack));
        return stack;
    }

    /**
     * 在合成完成后把恶意图腾初始化为满能量。
     *
     * <p>配方系统创建的结果物品栈不一定来自 {@link #getDefaultInstance()}，因此这里再补一次。</p>
     * <p>这样玩家真正制造出的恶意图腾会直接处于可用状态，而不是禁用状态。</p>
     *
     * @param stack  合成得到的恶意图腾
     * @param level  合成所在世界
     * @param player 合成玩家
     */
    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        super.onCraftedBy(stack, level, player);
        setPower(stack, getMaxPower(stack));
    }

    /**
     * 禁止玩家直接右键装备恶意图腾。
     *
     * <p>恶意图腾仍然可以通过 Curios 界面手动放入 {@code charm} 槽。</p>
     * <p>这样可以避免玩家把它当作普通右键饰品误装备，同时保留 {@link #canEquip(SlotContext, ItemStack)} 的完整限制。</p>
     *
     * @param context Curios 槽位上下文
     * @param stack   尝试右键装备的恶意图腾
     * @return 总是 false，表示不允许右键装备
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    /**
     * Curios 装备限制。
     *
     * <p>恶意图腾只能放入 {@code charm} 槽。</p>
     * <p>它属于七咒限定物品，因此必须委托 {@link CursedRingApi#canEquipRestrictedCurio(SlotContext, ItemStack)}
     * 处理七咒资格、登录宽限和槽位刷新等边界情况。</p>
     *
     * @param context Curios 槽位上下文
     * @param stack   被装备的恶意图腾
     * @return 可以装备时返回 true
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null
                && CHARM_SLOT.equals(context.identifier())
                && CursedRingApi.canEquipRestrictedCurio(context, stack);
    }

    /**
     * 添加恶意图腾提示文本。
     *
     * <p>提示会显示当前能量、袭击者伤害联动和保命效果。</p>
     * <p>能量耗尽时不切换名称，也不额外显示禁用说明，只通过能量条提示当前状态。</p>
     *
     * @param stack   当前物品栈
     * @param context tooltip 上下文
     * @param tooltip 待追加的 tooltip 列表
     * @param flag    tooltip 显示标记
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull java.util.List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.totem_of_malice.1"));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.totem_of_malice.2",
                SpellstoneTooltip.percent("+150%")
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.totem_of_malice.3",
                SpellstoneTooltip.percent("50%")
        ));
        tooltip.add(SpellstoneTooltip.empty());

        if (getPower(stack) > 0) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.totem_of_malice.4"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.totem_of_malice.5"));
        }

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.totem_of_malice.power",
                SpellstoneTooltip.number(getPower(stack)),
                SpellstoneTooltip.number(getMaxPower(stack))
        ));
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.totem_of_malice.cursed_only"));
    }

    /**
     * 有能量时显示附魔光效。
     *
     * @param stack 当前物品栈
     * @return 能量大于 0 时返回 true
     */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return getPower(stack) > 0;
    }

    /**
     * 始终显示自定义能量条。
     *
     * @param stack 当前物品栈
     * @return 总是 true
     */
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getPower(stack) < getMaxPower(stack);
    }

    /**
     * 计算自定义能量条宽度。
     *
     * @param stack 当前物品栈
     * @return 0 到 13 之间的宽度
     */
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round((float) getPower(stack) * 13.0F / getMaxPower(stack));
    }

    /**
     * 根据剩余能量计算能量条颜色。
     *
     * @param stack 当前物品栈
     * @return RGB 颜色值
     */
    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float ratio = (float) getPower(stack) / getMaxPower(stack);
        return Mth.hsvToRgb(ratio / 3.0F, 1.0F, 0.5F + ratio * 0.5F);
    }

    /**
     * 恶意图腾可以附魔。
     *
     * @param stack 当前物品栈
     * @return 总是 true
     */
    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    /**
     * 恶意图腾附魔能力。
     *
     * @return 附魔能力数值
     */
    @Override
    public int getEnchantmentValue() {
        return ENCHANTMENT_VALUE;
    }

    /**
     * 限制恶意图腾只能接受耐久附魔。
     *
     * <p>耐久附魔会提高恶意能量上限；其它附魔没有对应规则，因此不允许。</p>
     *
     * @param stack       当前物品栈
     * @param enchantment 待检查附魔
     * @return 只有耐久附魔返回 true
     */
    @Override
    public boolean isPrimaryItemFor(
            @NotNull ItemStack stack,
            @NotNull Holder<Enchantment> enchantment
    ) {
        return isUnbreaking(enchantment);
    }

    @Override
    public boolean supportsEnchantment(
            @NotNull ItemStack stack,
            @NotNull Holder<Enchantment> enchantment
    ) {
        return isUnbreaking(enchantment);
    }

    /**
     * 检查玩家主副手是否存在恶意图腾。
     *
     * <p>该方法用于袭击者伤害联动，因此禁用图腾也算存在。</p>
     *
     * @param player 待检查玩家
     * @return 主手或副手存在符合条件的恶意图腾时返回 true
     */
    private static boolean hasTotemInHands(Player player) {
        for (ItemStack stack : player.getHandSlots()) {
            if (isRelevantTotem(stack, false)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 在玩家的 {@code charm} 护符栏中查找恶意图腾。
     *
     * <p>这里直接访问指定槽位处理器，而不是查找任意 Curios 槽，确保旧存档或异常槽位中的图腾不会生效。</p>
     *
     * @param player      待检查玩家
     * @param chargedOnly 是否只接受有能量的图腾
     * @return 护符栏中找到的第一个符合条件的恶意图腾
     */
    private static Optional<ItemStack> findTotemInCharmSlot(Player player, boolean chargedOnly) {
        return CuriosLookupApi.getStacksHandler(player, CHARM_SLOT)
                .flatMap(stacksHandler -> {
                    var stacks = stacksHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack stack = stacks.getStackInSlot(slot);

                        if (isRelevantTotem(stack, chargedOnly)) {
                            return Optional.of(stack);
                        }
                    }

                    return Optional.empty();
                });
    }

    /**
     * 判断物品栈是否是当前检查场景可用的恶意图腾。
     *
     * @param stack       待检查物品栈
     * @param chargedOnly 是否要求恶意能量大于 0
     * @return 是恶意图腾，且在需要时拥有能量，则返回 true
     */
    private static boolean isRelevantTotem(ItemStack stack, boolean chargedOnly) {
        if (!stack.is(ModItems.TOTEM_OF_MALICE.get())) {
            return false;
        }

        return !chargedOnly || getPower(stack) > 0;
    }

    private static int getUnbreakingLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(Enchantments.UNBREAKING)) {
                return entry.getIntValue();
            }
        }

        return 0;
    }

    private static boolean isUnbreaking(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING);
    }

    private static CompoundTag copyCustomData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
}
