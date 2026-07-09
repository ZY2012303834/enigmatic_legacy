package org.enigmatic_legacy.item.items.ring;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 极尽奢华之戒 / Ring of Ultimate Luxury。
 *
 * <p>这是 Enigmatic Addons 中 Avarice Ring 的 1.21.1 NeoForge 移植。</p>
 * <p>物品类只负责 Curios 装备限制和 tooltip；真正的战斗、交易和掉落逻辑在事件类中处理。</p>
 */
public class UltimateLuxuryRing extends Item implements ICurioItem {
    private static final String RING_SLOT = "ring";

    /**
     * 创建极尽奢华之戒。
     *
     * <p>保持原拓展定位：不可堆叠、史诗稀有度、防火。</p>
     */
    public UltimateLuxuryRing() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许玩家手持右键直接装备，但仍复用 {@link #canEquip(SlotContext, ItemStack)} 的完整限制。
     *
     * <p>这样右键装备和手动拖入 Curios 槽位的规则完全一致：</p>
     * <p>必须是 ring 槽，并且玩家必须满足七咒限定物品的装备资格。</p>
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * Curios 装备限制。
     *
     * <p>极尽奢华之戒只能放入 {@code ring} 槽。</p>
     * <p>它属于七咒限定物品，因此委托 {@link CursedRingApi#canEquipRestrictedCurio(SlotContext, ItemStack)}
     * 处理七咒之戒佩戴、登录恢复宽限和当前槽位刷新等边界情况。</p>
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null
                && RING_SLOT.equals(context.identifier())
                && CursedRingApi.canEquipRestrictedCurio(context, stack);
    }

    /**
     * 添加物品提示。
     *
     * <p>这里只展示规则说明，不在物品类里读取客户端玩家并计算实时增伤。</p>
     * <p>原因是物品类会在通用环境加载，避免直接依赖 {@code Minecraft.getInstance()} 可以降低专用服务端类加载风险。</p>
     * <p>如后续需要实时数值，建议在专门的客户端 tooltip 事件中补充。</p>
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.avarice_scroll.1", SpellstoneTooltip.number("+1")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ultimate_luxury_ring.1"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ultimate_luxury_ring.2"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ultimate_luxury_ring.3"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ultimate_luxury_ring.5"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.ultimate_luxury_ring.6"));
            tooltip.add(SpellstoneTooltip.empty());
            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.avarice_scroll.cursed_only"));
        } else {
            tooltip.add(SpellstoneTooltip.holdShift());
        }
    }
}
