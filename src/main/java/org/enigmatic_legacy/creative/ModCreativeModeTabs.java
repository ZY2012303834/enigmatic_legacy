package org.enigmatic_legacy.creative;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnigmaticLegacy.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENIGMATIC_LEGACY =
            CREATIVE_MODE_TABS.register("enigmatic_legacy", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.enigmatic_legacy"))
                    .icon(() -> ModItems.ASTRAL_DUST.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ASTRAL_DUST.get());
                        output.accept(ModItems.ENDER_ROD.get());
                        output.accept(ModItems.ETHERIUM_ORE.get());
                        output.accept(ModItems.ETHERIUM_INGOT.get());
                        output.accept(ModItems.THICC_SCROLL.get());
                        output.accept(ModItems.DARKEST_SCROLL.get());
                        output.accept(ModItems.ASTRAL_DUST_SACK.get());
                        output.accept(ModItems.ETHERIUM_BLOCK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
