package org.enigmatic_legacy.generator;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModPotions;

import java.util.Objects;

/**
 * 酿造配方注册器。
 *
 * <p>注意：酿造不是普通 JSON 配方，不能靠 runData 生成。
 * 这里通过 RegisterBrewingRecipesEvent 在代码中注册。
 */
public final class BrewingGenerator {

    private BrewingGenerator() {
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        ItemStack awkwardPotion = PotionContents.createItemStack(
                Items.POTION,
                Potions.AWKWARD
        );

        ItemStack recallPotion = new ItemStack(ModItems.RECALL_POTION.get());

        // 给召回药水强制添加附魔光效。
        recallPotion.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        Ingredient awkwardPotionIngredient = DataComponentIngredient.of(
                false,
                DataComponents.POTION_CONTENTS,
                Objects.requireNonNull(awkwardPotion.get(DataComponents.POTION_CONTENTS)),
                Items.POTION
        );

        event.getBuilder().addRecipe(
                awkwardPotionIngredient,
                Ingredient.of(Items.ENDER_EYE),
                recallPotion
        );

        ItemStack longNightVisionPotion = PotionContents.createItemStack(
                Items.POTION,
                Potions.LONG_NIGHT_VISION
        );

        ItemStack ultimateNightVisionPotion = PotionContents.createItemStack(
                Items.POTION,
                ModPotions.ULTIMATE_NIGHT_VISION
        );

        // 终极药水加附魔光效，方便和普通药水区分。
        ultimateNightVisionPotion.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        Ingredient longNightVisionPotionIngredient = DataComponentIngredient.of(
                false,
                DataComponents.POTION_CONTENTS,
                Objects.requireNonNull(longNightVisionPotion.get(DataComponents.POTION_CONTENTS)),
                Items.POTION
        );

        event.getBuilder().addRecipe(
                longNightVisionPotionIngredient,
                Ingredient.of(ModItems.ASTRAL_DUST.get()),
                ultimateNightVisionPotion
        );
    }
}
