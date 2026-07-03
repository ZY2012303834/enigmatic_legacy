package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.enchantment.ModEnchantments;
import org.enigmatic_legacy.tag.ModEnchantmentTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EnchantmentTagGenerator extends TagsProvider<Enchantment> {
    public EnchantmentTagGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                Registries.ENCHANTMENT,
                lookupProvider,
                EnigmaticLegacy.MODID,
                existingFileHelper
        );
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(ModEnchantmentTags.ETERNAL_BINDING_EXCLUSIVE)
                .add(Enchantments.BINDING_CURSE)
                .add(Enchantments.VANISHING_CURSE)
                .add(ModEnchantments.ETERNAL_BINDING);

        tag(EnchantmentTags.CURSE)
                .add(ModEnchantments.ETERNAL_BINDING);

        tag(EnchantmentTags.TREASURE)
                .add(ModEnchantments.ETERNAL_BINDING);

        tag(EnchantmentTags.TOOLTIP_ORDER)
                .add(ModEnchantments.ETERNAL_BINDING);
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Enchantment Tags";
    }
}
