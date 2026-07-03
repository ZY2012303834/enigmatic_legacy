package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.enchantment.ModEnchantments;
import org.enigmatic_legacy.tag.ModEnchantmentTags;

import java.util.Set;

public final class EnchantmentGenerator {
    private EnchantmentGenerator() {
    }

    public static DatapackBuiltinEntriesProvider createProvider(GatherDataEvent event) {
        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.ENCHANTMENT, EnchantmentGenerator::bootstrap);

        return new DatapackBuiltinEntriesProvider(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider(),
                builder,
                Set.of(EnigmaticLegacy.MODID)
        );
    }

    public static void gatherData(GatherDataEvent event) {
        DatapackBuiltinEntriesProvider enchantments = createProvider(event);

        event.getGenerator().addProvider(event.includeServer(), enchantments);
        event.getGenerator().addProvider(
                event.includeServer(),
                new EnchantmentTagGenerator(
                        event.getGenerator().getPackOutput(),
                        enchantments.getRegistryProvider(),
                        event.getExistingFileHelper()
                )
        );
    }

    private static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        context.register(
                ModEnchantments.ETERNAL_BINDING,
                Enchantment.enchantment(Enchantment.definition(
                                items.getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE),
                                items.getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE),
                                1,
                                1,
                                Enchantment.constantCost(25),
                                Enchantment.constantCost(50),
                                8,
                                EquipmentSlotGroup.ARMOR
                        ))
                        .exclusiveWith(enchantments.getOrThrow(ModEnchantmentTags.ETERNAL_BINDING_EXCLUSIVE))
                        .build(ModEnchantments.ETERNAL_BINDING.location())
        );
    }
}
