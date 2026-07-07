package org.enigmatic_legacy.item.items.ring;

import com.google.common.collect.ImmutableMultimap;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.util.EarthPromiseHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.enigmatic_legacy.util.TreasureHunterCharmHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 地灵之诺 / Promise of the Earth。
 *
 * <p>这是一个 Curios 戒指类物品，本文件只负责“物品本体”的行为：
 * 装备限制、Curios 提供的属性修饰、财富等级加成和客户端 tooltip。
 *
 * <p>被动触发、冷却、受伤减免、挖掘速度等事件型逻辑不放在物品类里，
 * 而是由 {@code EarthPromiseEvents} 监听 Forge/NeoForge 事件统一处理。
 * 这样可以避免物品类在客户端 tooltip 和服务端事件之间混入过多职责。
 */
public class EarthPromise extends Item implements ICurioItem {
    /**
     * Curios 的戒指槽标识。
     *
     * <p>Curios 会把槽位类型作为字符串传入 {@link SlotContext#identifier()}。
     * 这里使用常量，避免多个方法里散落硬编码字符串。
     */
    private static final String RING_SLOT = "ring";

    /**
     * 基础物品属性。
     *
     * <p>地灵之诺不能堆叠，稀有度为 EPIC。实际效果是否生效不在这里判断，
     * 而是在装备限制、属性修饰器和事件逻辑里根据七咒之戒状态分别判断。
     */
    public EarthPromise() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    /**
     * 允许玩家右键使用物品时直接装备。
     *
     * <p>Curios 右键装备会先走这个方法，再由 {@link #canEquip(SlotContext, ItemStack)}
     * 判断目标槽位是否可用。这里复用 {@code canEquip}，保证“右键装备”和
     * “拖入 Curios 槽”遵守同一套槽位限制。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 限制地灵之诺只能放入 Curios 的 ring 槽。
     *
     * <p>新装备时必须佩戴七咒之戒；如果这是 Curios 正在刷新当前槽位里的旧物品，
     * 则交给 {@link CursedRingApi} 放行，避免登录、维度切换或槽位刷新时误卸下。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return RING_SLOT.equals(context.identifier()) && CursedRingApi.canEquipRestrictedCurio(context, stack);
    }

    /**
     * 给 Curios 的财富等级钩子提供额外 Fortune。
     *
     * <p>基础加成为 +2。如果同时装备寻宝护符，则额外 +1。
     * 返回值叠加 {@code ICurioItem.super.getFortuneLevel(...)}，是为了保留
     * Curios 或其他父级默认实现可能提供的数值，不强行覆盖。
     */
    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        int bonus = 2;

        if (TreasureHunterCharmHelper.hasTreasureHunterCharm(entity)) {
            bonus += 1;
        }

        return ICurioItem.super.getFortuneLevel(slotContext, lootContext, stack) + bonus;
    }

    /**
     * 向 Curios 暴露装备后的属性修饰器。
     *
     * <p>这个方法会被 Curios 用来把物品效果接入原版属性系统。
     * 和 {@link #canEquip(SlotContext, ItemStack)} 不同，这里会检查
     * {@link EarthPromiseHelper#canUseEarthPromise(Player)}：
     * 只有玩家满足“七咒者”条件时，护甲和盔甲韧性才真正生效。
     *
     * <p>如果实体不是玩家，或者玩家不能使用地灵之诺，则返回空表。
     * 返回空表比禁止装备更稳妥，因为它不会触发 Curios 把物品从槽位弹出，
     * 只是让属性加成临时失效。
     */
    @Override
    public @NotNull Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();

        if (!(slotContext.entity() instanceof Player player) || !EarthPromiseHelper.canUseEarthPromise(player)) {
            return builder.build();
        }

        // 固定加护甲值。数值来自通用配置，便于整合包或服务端调整平衡。
        builder.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "earth_promise_armor"),
                        ConfigCommon.EARTH_PROMISE_ARMOR_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        // 固定加盔甲韧性，同样只在七咒者条件满足时生效。
        builder.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "earth_promise_toughness"),
                        ConfigCommon.EARTH_PROMISE_TOUGHNESS_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return builder.build();
    }

    /**
     * 构建客户端 tooltip。
     *
     * <p>这个方法只负责显示文本，不负责实际效果。显示出来的数值全部读取配置，
     * 保证玩家看到的数值和服务端逻辑使用的数值一致。
     *
     * <p>未按 Shift 时只展示简略提示和“仅七咒者可用”的限制；
     * 按住 Shift 后展示完整能力、冷却、抗性修正和装备属性。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        // 普通状态下保持 tooltip 简短，避免背包里信息过长。
        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift());
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.cursed_ones_only"));
            return;
        }

        // 被动能力触发阈值：伤害达到当前生命值的一定百分比时触发。
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.1",
                SpellstoneTooltip.percent(ConfigCommon.EARTH_PROMISE_ABILITY_TRIGGER_PERCENT.get() + "%")
        ));

        // 被动能力冷却。配置单位是 tick，这里换算成秒展示给玩家。
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.2",
                SpellstoneTooltip.number(ConfigCommon.EARTH_PROMISE_COOLDOWN.get() / 20)
        ));

        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.earth_promise.3")
                .withStyle(ChatFormatting.DARK_PURPLE));

        // 对第一诅咒伤害惩罚的修正比例。
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.4",
                SpellstoneTooltip.percent(ConfigCommon.EARTH_PROMISE_TOTAL_RESISTANCE.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(Component.translatable("curios.modifiers.ring")
                .withStyle(ChatFormatting.GOLD));

        // 以下几行模拟 Curios 属性列表的展示风格，实际属性仍由 getAttributeModifiers 提供。
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.armor",
                SpellstoneTooltip.number("+" + format(ConfigCommon.EARTH_PROMISE_ARMOR_BONUS.get()))
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.toughness",
                SpellstoneTooltip.number("+" + format(ConfigCommon.EARTH_PROMISE_TOUGHNESS_BONUS.get()))
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.mining_speed",
                SpellstoneTooltip.percent("+" + ConfigCommon.EARTH_PROMISE_BREAK_SPEED_BONUS.get() + "%")
        ));

        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.earth_promise.modifier.fortune",
                SpellstoneTooltip.number("+2")
        ));

        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.cursed_ones_only"));
    }

    /**
     * 格式化配置里的 double 数值。
     *
     * <p>护甲和韧性在配置中是 double。若数值本身是整数，则显示为 {@code 5}
     * 而不是 {@code 5.0}，让 tooltip 更接近原版装备属性文本。
     */
    private static String format(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }

        return Double.toString(value);
    }
}
