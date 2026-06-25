package org.enigmatic_legacy.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 本模组特殊配方序列化器注册。
 */
public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnigmaticLegacy.MODID);

    /**
     * 求知之书附魔转移特殊配方。
     *
     * JSON 类型：
     * enigmatic_legacy:enchantment_transposing
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<EnchantmentTransposingRecipe>> ENCHANTMENT_TRANSPOSING =
            RECIPE_SERIALIZERS.register(
                    "enchantment_transposing",
                    () -> new SimpleCraftingRecipeSerializer<>(EnchantmentTransposingRecipe::new)
            );

    private ModRecipeSerializers() {
    }

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}