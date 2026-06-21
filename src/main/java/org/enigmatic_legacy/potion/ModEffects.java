package org.enigmatic_legacy.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.potion.effect.RecallEffect;

/**
 * 模组药水效果注册类。
 *
 * <p>这里注册的是 MobEffect，也就是实体身上的状态效果。
 */
public final class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, EnigmaticLegacy.MODID);

    /**
     * 召回效果。
     *
     * <p>由召回药水触发，效果执行后会把玩家传送回重生点。
     */
    public static final DeferredHolder<MobEffect, RecallEffect> RECALL =
            MOB_EFFECTS.register("recall", RecallEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}