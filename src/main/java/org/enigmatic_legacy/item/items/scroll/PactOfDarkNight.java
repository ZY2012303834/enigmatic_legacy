package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.ClientTooltipState;
import org.enigmatic_legacy.util.PactOfDarkNightHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Locale;

/**
 * 暗夜契约卷轴 / Pact of Dark Night。
 *
 * <p>复刻自 Enigmatic Addons 的 night_scroll。该卷轴属于七咒受限物品，
 * 只能放在 Curios 的 scroll 槽中，并且只有真正佩戴七咒之戒时才会生效。</p>
 *
 * <p>暗夜契约的战斗能力会随暗度变化：最低仍有少量效果，
 * 黑暗效果或极暗环境下达到最高。事件类负责实际伤害、减伤、吸血和幻翼抑制，
 * 本类负责装备限制、无资格佩戴惩罚和 tooltip。</p>
 */
public class PactOfDarkNight extends Item implements ICurioItem {
    /**
     * 本项目为卷轴物品定义的 Curios 槽位名。
     */
    private static final String SCROLL_SLOT = "scroll";

    public PactOfDarkNight() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许手持右键直接装备到 Curios 槽位，但仍复用 canEquip 中的完整限制。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 装备限制：
     * 1. 必须是 scroll 槽；
     * 2. 必须是玩家；
     * 3. 必须通过七咒受限物品统一校验；
     * 4. 同一名玩家最多装备 1 个暗夜契约卷轴。
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

        return canEquipNightScroll(player, context.index());
    }

    /**
     * 检查玩家 scroll 槽内是否已经存在另一个暗夜契约卷轴。
     *
     * <p>Curios 刷新当前槽位时可能会重新校验同一个 ItemStack，
     * 因此遇到 currentSlotIndex 对应槽位时要跳过，避免误判为重复装备。</p>
     */
    private static boolean canEquipNightScroll(Player player, int currentSlotIndex) {
        return CuriosLookupApi.getStacksHandler(player, SCROLL_SLOT)
                .map(scrollHandler -> {
                    var stacks = scrollHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack equippedStack = stacks.getStackInSlot(slot);

                        if (!equippedStack.is(ModItems.NIGHT_SCROLL.get())) {
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
     * 无七咒资格时的兜底惩罚。
     *
     * <p>受限物品通常会被 CursedRingRequirementEvents 弹出。
     * 这里保留扩展项目的行为：如果卷轴因为登录宽限或异常途径暂时留在槽位中，
     * 非创造/旁观玩家会被持续施加黑暗效果，直到资格恢复或物品被移除。</p>
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        if (CursedRingApi.hasCursedRing(player) || player.isCreative() || player.isSpectator()) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20));
    }

    /**
     * Tooltip 中显示当前暗度下的预估数值。
     *
     * <p>客户端拿不到服务端配置同步时仍会读取当前配置镜像；
     * 如果没有玩家实例，例如物品在创造栏渲染，就按最低暗度倍率显示。</p>
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (ClientTooltipState.isShiftDown()) {
            Player player = ClientTooltipState.getClientPlayer();
            double darkModifier = player == null ? PactOfDarkNightHelper.MIN_DARK_MODIFIER : PactOfDarkNightHelper.getDarkModifier(player);

            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.night_scroll.1"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.night_scroll.2"));
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.night_scroll.3",
                    Component.literal(formatPercent(getDamageBoost(darkModifier))).withStyle(ChatFormatting.GOLD)
            ));
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.night_scroll.4",
                    Component.literal(formatPercent(getDamageResistance(darkModifier))).withStyle(ChatFormatting.GOLD)
            ));
            tooltip.add(SpellstoneTooltip.text(
                    "tooltip.enigmatic_legacy.night_scroll.5",
                    Component.literal(formatPercent(getLifeSteal(darkModifier))).withStyle(ChatFormatting.GOLD)
            ));

            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.night_scroll.cursed_only"));
        } else {
            tooltip.add(SpellstoneTooltip.holdShift());
        }
    }

    public static double getDamageBoost(double darkModifier) {
        return ConfigCommon.PACT_OF_DARK_NIGHT_AVERAGE_DAMAGE_BOOST.get() / 100.0D * darkModifier;
    }

    public static double getDamageResistance(double darkModifier) {
        return ConfigCommon.PACT_OF_DARK_NIGHT_AVERAGE_DAMAGE_RESISTANCE.get() / 100.0D * darkModifier;
    }

    public static double getLifeSteal(double darkModifier) {
        return ConfigCommon.PACT_OF_DARK_NIGHT_AVERAGE_LIFE_STEAL.get() / 100.0D * darkModifier;
    }

    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.0f%%", value * 100.0D);
    }
}
