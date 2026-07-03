package org.enigmatic_legacy.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.enigmatic_legacy.EnigmaticLegacy;

public final class ModEnchantmentTags {
    public static final TagKey<Enchantment> ETERNAL_BINDING_EXCLUSIVE = create("exclusive_set/eternal_binding");

    private ModEnchantmentTags() {
    }

    private static TagKey<Enchantment> create(String path) {
        return TagKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path)
        );
    }
}
