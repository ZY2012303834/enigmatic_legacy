package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 极恶锭 / Nefarious Ingot。
 *
 * <p>原项目注册名为 evil_ingot，显示名为 Nefarious Ingot。
 * 这是七咒相关的高级材料，用于后续邪恶装备、遗物或终局配方。
 */
public class EvilIngot extends Item {

    public EvilIngot() {
        super(new Properties()
                .stacksTo(8)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}