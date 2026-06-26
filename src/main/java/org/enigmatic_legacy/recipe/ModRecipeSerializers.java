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
 * 用途：
 * - 普通 shaped / shapeless 配方不需要写在这里；
 * - 只有“动态输出”的特殊配方才需要注册 serializer。
 * 例如：
 * - 求知之书附魔转移；
 * - 修补混合物修复任意受损物品。
 */
public final class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnigmaticLegacy.MODID);

    /**
     * 求知之书附魔转移特殊配方。
     * JSON 类型：
     * enigmatic_legacy:enchantment_transposing
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<EnchantmentTransposingRecipe>> ENCHANTMENT_TRANSPOSING =
            RECIPE_SERIALIZERS.register(
                    "enchantment_transposing",
                    () -> new SimpleCraftingRecipeSerializer<>(EnchantmentTransposingRecipe::new)
            );

    /**
     * 修补混合物特殊修复配方。
     * JSON 类型：
     * enigmatic_legacy:mending_mixture_repair
     * 功能：
     * 任意受损可损坏物品 + 修补混合物
     * =
     * 满耐久同物品 + 空玻璃瓶
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<MendingMixtureRepairRecipe>> MENDING_MIXTURE_REPAIR =
            RECIPE_SERIALIZERS.register(
                    "mending_mixture_repair",
                    () -> new SimpleCraftingRecipeSerializer<>(MendingMixtureRepairRecipe::new)
            );

    private ModRecipeSerializers() {
    }

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}