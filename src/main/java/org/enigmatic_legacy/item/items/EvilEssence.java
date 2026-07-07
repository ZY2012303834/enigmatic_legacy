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
 * 邪恶精髓 / Nefarious Essence。
 *
 * <p>原项目注册名为 evil_essence，显示名为 Nefarious Essence。
 * 这是受诅者相关材料，主要用于后续扭曲之心、邪恶锭等配方。
 */
public class EvilEssence extends Item {

    public EvilEssence() {
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
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.evilEssence1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.evilEssence2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.cursed_ones_only")
                .withStyle(ChatFormatting.RED));
    }
}