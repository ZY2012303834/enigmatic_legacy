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
 * <p>重要限制：该物品属于七咒限定装备。未佩戴七咒之戒时，即使物品仍停留在胸甲槽或背饰槽，
 * 也不会提供飞行、助推、减伤、俯冲伤害等实际效果。</p>
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
     * 胸甲槽和背饰槽真正提供飞行前，都必须重新校验七咒资格。
     *
     * <p>Curios 登录恢复时可能临时允许物品留在槽位里；这里不依赖“是否还在槽位中”，
     * 而是以玩家当前是否佩戴七咒之戒作为最终生效条件。</p>
     */
    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return entity instanceof Player player
                && CursedRingApi.hasCursedRing(player)
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
     * 混沌之傲只能放入 Curios back 背饰槽，并且属于七咒限定装备。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!MajesticElytraHelper.BACK_SLOT.equals(context.identifier())) {
            return false;
        }

        return CursedRingApi.canEquipRestrictedCurio(context, stack);
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
                SpellstoneTooltip.number(Integer.toString(ConfigCommon.CHAOS_ELYTRA_DESCENDING_COOLDOWN.get()))
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
                && CursedRingApi.hasCursedRing(player)
                && !MajesticElytraHelper.getEquippedChaosElytraStack(player).isEmpty();
    }

    public static boolean hasAbyssBoost(Player player) {
        return AbyssalHeartHelper.isWorthy(player);
    }

    public static double getDamageResistance(Player player) {
        double resistance = ConfigCommon.CHAOS_ELYTRA_DAMAGE_RESISTANCE.get() / 100.0D;

        if (hasAbyssBoost(player)) {
            resistance *= 1.25D;
        }

        return Math.min(1.0D, Math.max(0.0D, resistance));
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
