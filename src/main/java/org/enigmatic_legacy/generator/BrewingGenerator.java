package org.enigmatic_legacy.generator;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.enigmatic_legacy.potion.ModPotions;

/**
 * 酿造配方注册器。
 *
 * <p>注意：这不是普通 DataProvider。
 * Minecraft / NeoForge 1.21.1 的酿造配方不能通过 runData 生成 JSON。
 * 酿造配方需要在代码中监听 RegisterBrewingRecipesEvent 注册。
 */
public final class BrewingGenerator {

    private BrewingGenerator() {
    }

    /**
     * 注册召回药水酿造配方。
     *
     * <p>复刻原项目：
     * <pre>
     * Awkward Potion + Eye of Ender -> Potion of Recall
     * 粗制药水 + 末影之眼 -> 召回药水
     * </pre>
     */
    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(
                Potions.AWKWARD,
                Items.ENDER_EYE,
                ModPotions.RECALL
        );
    }
}