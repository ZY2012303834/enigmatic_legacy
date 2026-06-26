package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 无尽贪婪契约 / Pact of Infinite Avarice。
 * 栏位：奥秘卷轴 scroll。
 * 只有承受七咒的人，也就是佩戴七咒之戒的玩家，才能使用。
 */
public class PactOfInfiniteAvarice extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    public PactOfInfiniteAvarice() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 普通右键允许直接装备到奥秘卷轴栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 装备限制：
     * 1. 只能放入 scroll 奥秘卷轴栏；
     * 2. 必须佩戴七咒之戒；
     * 3. 无尽贪婪契约最多只能装备 1 个。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context == null || !SCROLL_SLOT.equals(context.identifier())) {
            return false;
        }

        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        if (!CursedRingHelper.hasCursedRing(player)) {
            return false;
        }

        return canEquipAvariceScroll(player, context.index());
    }

    /**
     * 奥秘卷轴栏可以有 3 个槽位，
     * 但无尽贪婪契约本体最多只能装备 1 个。
     */
    private static boolean canEquipAvariceScroll(Player player, int currentSlotIndex) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.getStacksHandler(SCROLL_SLOT))
                .map(scrollHandler -> {
                    var stacks = scrollHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack equippedStack = stacks.getStackInSlot(slot);

                        if (!equippedStack.is(ModItems.AVARICE_SCROLL.get())) {
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
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.avarice_scroll.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.avarice_scroll.3", SpellstoneTooltip.percent("100%")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.avarice_scroll.4", SpellstoneTooltip.percent("15%"), SpellstoneTooltip.number("1")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.avarice_scroll.5", SpellstoneTooltip.percent("35%")));

            tooltip.add(SpellstoneTooltip.empty());

            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.avarice_scroll.cursed_only"));
        } else {
            tooltip.add(SpellstoneTooltip.holdShift());
        }
    }
}
