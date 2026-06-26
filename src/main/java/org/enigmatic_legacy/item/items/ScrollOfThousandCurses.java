package org.enigmatic_legacy.item.items;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.ScrollOfThousandCursesHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Locale;

/**
 * 千咒卷轴 / Scroll of a Thousand Curses。
 * 类型：奥秘卷轴 scroll。
 * 只有承受七咒之人，也就是佩戴七咒之戒的玩家，才能使用。
 */
public class ScrollOfThousandCurses extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    private static final String CACHED_CURSE_FACTOR_TAG = "cachedCurseFactor";

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

    /**
     * 装备在奥秘卷轴栏时，每 tick 缓存当前诅咒因子。
     * 这样 tooltip 就能显示当前加成：
     * 当前诅咒因子 = 装备诅咒项数 + 七咒之戒固定 7 项。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        int curseFactor = ScrollOfThousandCursesHelper.getCurseFactor(player);
        setCachedCurseFactor(stack, curseFactor);
    }

    private static int getCachedCurseFactor(ItemStack stack) {
        return Math.max(0, getTag(stack).getInt(CACHED_CURSE_FACTOR_TAG));
    }

    private static void setCachedCurseFactor(ItemStack stack, int curseFactor) {
        CompoundTag tag = getTag(stack);
        tag.putInt(CACHED_CURSE_FACTOR_TAG, Math.max(0, curseFactor));
        setTag(stack, tag);
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.0f%%", value * 100.0D);
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
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.1"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.2"));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.3", SpellstoneTooltip.number("1")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.4", SpellstoneTooltip.number("7")));

            tooltip.add(SpellstoneTooltip.empty());

            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.attack", SpellstoneTooltip.percent("4%")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.mining", SpellstoneTooltip.percent("7%")));
            tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.cursed_scroll.healing", SpellstoneTooltip.percent("4%")));

            int curseFactor = getCachedCurseFactor(stack);

            if (curseFactor > 0) {
                tooltip.add(SpellstoneTooltip.empty());

                tooltip.add(SpellstoneTooltip.text(
                        "tooltip.enigmatic_legacy.cursed_scroll.current.factor",
                        SpellstoneTooltip.number(curseFactor)
                ));

                tooltip.add(SpellstoneTooltip.text(
                        "tooltip.enigmatic_legacy.cursed_scroll.current.attack",
                        SpellstoneTooltip.percent(formatPercent(curseFactor * 0.04D))
                ));

                tooltip.add(SpellstoneTooltip.text(
                        "tooltip.enigmatic_legacy.cursed_scroll.current.mining",
                        SpellstoneTooltip.percent(formatPercent(curseFactor * 0.07D))
                ));

                tooltip.add(SpellstoneTooltip.text(
                        "tooltip.enigmatic_legacy.cursed_scroll.current.healing",
                        SpellstoneTooltip.percent(formatPercent(curseFactor * 0.04D))
                ));
            }

            tooltip.add(SpellstoneTooltip.empty());

            tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.cursed_scroll.cursed_only"));
        } else {
            tooltip.add(SpellstoneTooltip.holdShift());
        }
    }
}
