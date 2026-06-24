package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 千咒卷轴 / Scroll of a Thousand Curses。
 * 类型：奥秘卷轴 scroll。
 * 只有承受七咒之人，也就是佩戴七咒之戒的玩家，才能使用。
 */
public class ScrollOfThousandCurses extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    public ScrollOfThousandCurses() {
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
     * 3. 千咒卷轴自身最多只能装备 1 个。
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

        return canEquipCursedScroll(player, context.index());
    }

    /**
     * 奥秘卷轴栏可以有 3 个槽位，
     * 但千咒卷轴本体最多只能装备 1 个。
     */
    private static boolean canEquipCursedScroll(Player player, int currentSlotIndex) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.getStacksHandler(SCROLL_SLOT))
                .map(scrollHandler -> {
                    var stacks = scrollHandler.getStacks();

                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        ItemStack equippedStack = stacks.getStackInSlot(slot);

                        if (!equippedStack.is(ModItems.CURSED_SCROLL.get())) {
                            continue;
                        }

                        /*
                         * 如果 Curios 正在刷新同一个槽位里的物品状态，
                         * 允许它继续保持装备，避免误判为重复装备。
                         */
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
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.1")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.2")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.3")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.4")
                    .withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.attack", "4%")
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.mining", "7%")
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.healing", "4%")
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_scroll.cursed_only")
                    .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}