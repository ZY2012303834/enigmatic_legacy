package org.enigmatic_legacy.tab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;

public class ModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnigmaticLegacy.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENIGMATIC_LEGACY =
            CREATIVE_MODE_TABS.register("enigmatic_legacy", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.enigmatic_legacy"))
                    .icon(() -> ModItems.ASTRAL_DUST.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ASTRAL_DUST.get());  // 星尘
                        output.accept(ModItems.ENDER_ROD.get());    // 末影棒
                        output.accept(ModItems.ETHERIUM_ORE.get()); // 以太矿
                        output.accept(ModItems.ETHERIUM_INGOT.get());   // 以太锭
                        output.accept(ModItems.THICC_SCROLL.get());     // 空卷轴
                        output.accept(ModItems.DARKEST_SCROLL.get());   // 至暗卷轴
                        output.accept(ModItems.ASTRAL_DUST_SACK.get()); // 袋装星尘
                        output.accept(ModItems.ETHERIUM_BLOCK.get());   // 以太块
                        output.accept(ModItems.COSMIC_HEART.get()); // 寰宇之心
                        output.accept(ModItems.BIG_LAMP.get()); // 大灯笼
                        output.accept(ModItems.MASSIVE_LAMP.get()); // 封装的大灯笼
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
