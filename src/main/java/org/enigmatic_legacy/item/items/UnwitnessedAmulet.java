package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class UnwitnessedAmulet extends Item {
    public UnwitnessedAmulet() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack result = EnigmaticAmulet.createRandom(player.getRandom());

        if (!level.isClientSide) {
            EnigmaticAmulet.setOwner(result, player.getGameProfile().getName());
        }

        return InteractionResultHolder.sidedSuccess(result, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.unwitnessed_amulet.1")
                .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.unwitnessed_amulet.2")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.unwitnessed_amulet.3")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.unwitnessed_amulet.use")
                .withStyle(ChatFormatting.GOLD));
    }
}