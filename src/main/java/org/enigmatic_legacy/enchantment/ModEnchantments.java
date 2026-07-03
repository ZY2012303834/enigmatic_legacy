package org.enigmatic_legacy.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.enigmatic_legacy.EnigmaticLegacy;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> ETERNAL_BINDING = create("eternal_binding");

    private ModEnchantments() {
    }

    private static ResourceKey<Enchantment> create(String name) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, name)
        );
    }
}
