package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.enigmatic_legacy.util.EnchanterPearlHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 附魔师的珍珠 / Enchanter's Pearl。
 * 原项目效果：
 * 1. 只能由七咒之戒佩戴者装备；
 * 2. 作为 charm 饰品佩戴时，额外提供 +1 charm 栏位。
 */
public class EnchanterPearl extends Item implements ICurioItem {
    public static final String EXTRA_CHARM_SLOT = "charm";
    public static final ResourceLocation EXTRA_CHARM_SLOT_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "enchanter_pearl_extra_charm_slot"
    );
    public static final double EXTRA_CHARM_SLOT_AMOUNT = 1.0D;

    public EnchanterPearl() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许右键尝试装备进 Curios 栏位。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 只有七咒之戒佩戴者可以装备。
     * 同时禁止重复佩戴多个附魔师的珍珠。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        return CursedRingHelper.hasCursedRing(player)
                && !EnchanterPearlHelper.hasOtherEnchanterPearl(player, context);
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

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.enchanter_pearl.1"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.enchanter_pearl.2"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.enchanter_pearl.3"
        ).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable(
                "tooltip.enigmatic_legacy.enchanter_pearl.cursed_only"
        ).withStyle(ChatFormatting.DARK_RED));
    }
}
