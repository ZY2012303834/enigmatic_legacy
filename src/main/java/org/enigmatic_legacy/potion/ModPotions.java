package org.enigmatic_legacy.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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


    private ModPotions() {
    }

    /**
     * 终极夜视药水。
     * 原项目：
     * ULTIMATE_NIGHT_VISION = Night Vision, 19200 ticks
     */
    public static final DeferredHolder<Potion, Potion> ULTIMATE_NIGHT_VISION = POTIONS.register(
            "ultimate_night_vision",
            () -> new Potion(
                    "ultimate_night_vision",
                    new MobEffectInstance(MobEffects.NIGHT_VISION, 19200)
            )
    );

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}