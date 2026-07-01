package org.enigmatic_legacy.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, EnigmaticLegacy.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AntiqueBookBagMenu>> ANTIQUE_BOOK_BAG =
            MENUS.register("antique_book_bag", () -> IMenuTypeExtension.create(AntiqueBookBagMenu::new));

    private ModMenus() {
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
