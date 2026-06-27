package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 兽友指南 / Guide to Animal Companionship。
 * <p>
 * 原项目类名 PetGuidebook。物品栏中持有时保护动物，并削弱七咒之戒第二诅咒对可驯服动物的影响。
 */
public class AnimalGuidebook extends Item {

    public AnimalGuidebook() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    public static boolean hasGuidebook(Player player) {
        if (player.getMainHandItem().is(ModItems.ANIMAL_GUIDEBOOK.get())
                || player.getOffhandItem().is(ModItems.ANIMAL_GUIDEBOOK.get())) {
            return true;
        }

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.ANIMAL_GUIDEBOOK.get())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isProtectedAnimal(LivingEntity entity) {
        if (entity instanceof Hoglin) {
            return true;
        }

        if (!(entity instanceof Animal)) {
            return false;
        }

        return !(entity instanceof NeutralMob) || entity instanceof Bee || entity instanceof Wolf;
    }

    public static boolean isTamableAnimal(LivingEntity entity) {
        if (entity instanceof TamableAnimal || entity instanceof Bee) {
            return true;
        }

        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return ConfigCommon.ANIMAL_GUIDE_ANIMAL_EXCLUSION_LIST.get().contains(entityId.toString());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.1")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.2")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.3")
                    .withStyle(ChatFormatting.GOLD));

            Player player = Minecraft.getInstance().player;
            if (player != null && CursedRingHelper.hasCursedRing(player)) {
                tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
                tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.4")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
                tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.5")
                        .withStyle(ChatFormatting.DARK_PURPLE));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
        }

        Player player = Minecraft.getInstance().player;
        if (player != null && player.isCreative()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.creative.1")
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.animal_guidebook.creative.2")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack stack,
            @NotNull Player player,
            @NotNull LivingEntity target,
            @NotNull InteractionHand hand
    ) {
        if (!player.isCreative()) {
            return InteractionResult.PASS;
        }

        if (!player.level().isClientSide()) {
            boolean tamable = isTamableAnimal(target);
            player.sendSystemMessage(Component.translatable(
                    tamable
                            ? "message.enigmatic_legacy.animal_guidebook.tamable"
                            : "message.enigmatic_legacy.animal_guidebook.not_tamable"
            ).withStyle(tamable ? ChatFormatting.GREEN : ChatFormatting.RED));
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean isTargetingGuidebookHolder(Mob mob, Player player) {
        return mob.getTarget() == player && hasGuidebook(player);
    }
}
