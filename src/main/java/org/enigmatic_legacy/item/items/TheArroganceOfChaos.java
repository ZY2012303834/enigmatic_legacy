package org.enigmatic_legacy.item.items;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AbyssalHeartHelper;
import org.enigmatic_legacy.util.MajesticElytraHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Locale;

/**
 * 混沌之傲 / The Arrogance of Chaos。
 *
 * <p>复刻自 Enigmatic Addons 的 chaos_elytra，是壮丽鞘翅的七咒高阶形态。
 * 它同时是一件胸甲槽鞘翅和 Curios back 背饰：胸甲槽用于兼容原版鞘翅流程，
 * back 槽则复用本项目已经为壮丽鞘翅修好的起飞与持续滑翔 mixin。</p>
 *
 * <p>重要限制：该物品属于深渊之心资格限定装备。玩家必须佩戴七咒之戒，
 * 且七咒佩戴时间达到总游玩时间 99.5%，才会提供飞行、助推、减伤、俯冲伤害等实际效果。</p>
 */
public class TheArroganceOfChaos extends ElytraItem implements ICurioItem {
    public TheArroganceOfChaos() {
        super(new Properties()
                .stacksTo(1)
                .durability(3600)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairCandidate) {
        return repairCandidate.is(ModItems.ETHERIUM_INGOT.get());
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    /**
     * 只允许耐久与经验修补这类鞘翅合理附魔。
     *
     * <p>混沌之傲虽然是高阶七咒装备，但本体仍是鞘翅，不应该因为实现为装备物品而吃到
     * 胸甲保护、荆棘等普通护甲附魔。</p>
     */
    @Override
    public boolean isPrimaryItemFor(
            @NotNull ItemStack stack,
            @NotNull Holder<Enchantment> enchantment
    ) {
        return isAllowedElytraEnchantment(enchantment);
    }

    @Override
    public boolean supportsEnchantment(
            @NotNull ItemStack stack,
            @NotNull Holder<Enchantment> enchantment
    ) {
        return isAllowedElytraEnchantment(enchantment);
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    /**
     * 胸甲槽和背饰槽真正提供飞行前，都必须重新校验深渊之心资格。
     *
     * <p>Curios 登录恢复时可能临时允许物品留在槽位里；这里不依赖“是否还在槽位中”，
     * 而是以玩家当前是否佩戴七咒之戒，并且七咒佩戴时间是否达到总游玩时间 99.5% 作为最终生效条件。</p>
     */
    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return entity instanceof Player player
                && AbyssalHeartHelper.isWorthy(player)
                && ElytraItem.isFlyEnabled(stack);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        int nextFlightTick = flightTicks + 1;

        /*
         * 和壮丽鞘翅保持一致：只要该物品正在提供滑翔 tick，就持续清空摔落距离。
         * 这样可以避免背饰槽接入原版滑翔后，某些落地路径仍按普通下落累计摔落伤害。
         */
        entity.resetFallDistance();

        if (!entity.level().isClientSide) {
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }

                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }

        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 混沌之傲只能放入 Curios back 背饰槽，并且需要深渊之心资格。
     *
     * <p>这里保留 Curios 受限物品的通用检查，再额外要求玩家七咒佩戴时间达到 99.5%。
     * 这个条件只是使用门槛，不再提供护甲、伤害、范围或减伤倍率强化。
     * 登录恢复时 Curios 可能先校验背饰槽，再恢复七咒之戒或统计缓存；
     * 因此加载宽限内允许它先保留在槽位里，真正效果仍由 {@link #canUse(Player)} 严格校验。</p>
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!MajesticElytraHelper.BACK_SLOT.equals(context.identifier())) {
            return false;
        }

        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        return AbyssalHeartHelper.isWorthy(player)
                || (CursedRingApi.isInLoadGrace(player)
                && CursedRingApi.canEquipRestrictedCurio(context, stack));
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.chaos_elytra.1",
                SpellstoneTooltip.percent(formatPercent(getDisplayedDamageResistance()))
        ));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.chaos_elytra.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.chaos_elytra.3"));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.chaos_elytra.4",
                SpellstoneTooltip.number("1")
        ));
        tooltip.add(SpellstoneTooltip.empty());
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.chaos_elytra.5"));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.chaos_elytra.cursed_only"));
    }

    public static boolean isChaosElytra(ItemStack stack) {
        return stack.is(ModItems.CHAOS_ELYTRA.get()) && ElytraItem.isFlyEnabled(stack);
    }

    public static boolean canUse(Player player) {
        return player != null
                && AbyssalHeartHelper.isWorthy(player)
                /*
                 * 多个 Curios back 槽或数据包扩展槽位可能同时放入多件鞘翅。
                 * 混沌之傲的减伤和落地伤害必须绑定到当前真正提供飞行的那一件，
                 * 否则会出现其它鞘翅消耗耐久时，背包里另一件混沌之傲仍然触发效果的问题。
                 */
                && MajesticElytraHelper.getEquippedStack(player).is(ModItems.CHAOS_ELYTRA.get());
    }

    public static double getDamageResistance() {
        double resistance = ConfigCommon.CHAOS_ELYTRA_DAMAGE_RESISTANCE.get() / 100.0D;

        /*
         * 不能让最终减伤达到 100%。
         * 如果整合包把配置改到 100%，背后伤害、摔落伤害和撞墙伤害会被完全清零；
         * 玩家反馈会变成“佩戴混沌之傲后不会受到伤害”。
         * 这里保留一个 5% 的最低受伤比例，既保留高额防御定位，也避免完全免疫。
         */
        return Math.min(0.95D, Math.max(0.0D, resistance));
    }

    private static boolean isAllowedElytraEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING);
    }

    private static double getDisplayedDamageResistance() {
        return ConfigCommon.CHAOS_ELYTRA_DAMAGE_RESISTANCE.get() / 100.0D;
    }

    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.0f%%", value * 100.0D);
    }
}
