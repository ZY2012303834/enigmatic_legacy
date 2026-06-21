package org.enigmatic_legacy.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 模组药水注册类。
 *
 * <p>这里注册的是 Potion，也就是“药水内容”。
 * 它会被 vanilla 的 potion / splash_potion / lingering_potion 等物品通过 PotionContents 使用。
 */
public final class ModPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, EnigmaticLegacy.MODID);

    /**
     * 召回药水。
     *
     * <p>这是一个只有 1 tick 的瞬时效果药水。
     */
    public static final DeferredHolder<Potion, Potion> RECALL =
            POTIONS.register("recall", () -> new Potion(
                    "recall",
                    new MobEffectInstance(ModEffects.RECALL, 1)
            ));

    private ModPotions() {
    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}