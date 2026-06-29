package org.enigmatic_legacy.item.items.charm;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 阳灼护符 / Charm of Scorched Sun。
 * 复刻自 Enigmatic Addons 的 ScorchedCharm。
 * 定位：
 * 1. Curios 护符栏物品；
 * 2. 提供火焰、岩浆、生存、生命汲取相关能力；
 * 3. 可以在岩浆上行走；
 * 4. 下蹲时允许潜入岩浆。
 */
public class ScorchedCharm extends Item implements ICurioItem {

    private static final String CHARM_SLOT = "charm";

    public ScorchedCharm() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
        );
    }

    /**
     * 只允许通过右键装备到 charm 护符栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isCharmSlot(context);
    }

    /**
     * 只允许放入 charm 护符栏。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isCharmSlot(context);
    }

    private static boolean isCharmSlot(SlotContext context) {
        return context != null && CHARM_SLOT.equals(context.identifier());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.scorched_charm.1")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.scorched_charm.2")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.scorched_charm.3")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.scorched_charm.4")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.scorched_charm.5")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
    }
}