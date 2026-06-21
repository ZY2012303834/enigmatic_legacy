package org.enigmatic_legacy.tab;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.potion.ModPotions;

public final class ModeTabs {

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
                        output.accept(ModItems.COSMIC_HEART.get());
                        output.accept(ModItems.BIG_LAMP.get());
                        output.accept(ModItems.BIG_SHROOMLAMP.get());
                        output.accept(ModItems.EARTH_HEART_FRAGMENT.get());
                        output.accept(ModItems.EARTH_HEART.get());
                        output.accept(ModItems.TWISTED_HEART.get());
                        output.accept(ModItems.EVIL_ESSENCE.get());
                        output.accept(ModItems.CURSED_RING.get());
                        output.accept(ModItems.IRON_RING.get());
                        output.accept(ModItems.EXQUISITE_RING.get());
                        output.accept(ModItems.EVIL_INGOT.get());
                        output.accept(ModItems.STORAGE_CRYSTAL.get());
                        output.accept(ModItems.SOUL_CRYSTAL.get());
                        output.accept(ModItems.TWISTED_MIRROR.get()); // 扭曲魔镜
                        output.accept(createRecallPotionStack()); // 召回药水
                        output.accept(ModItems.UNHOLY_GRAIL.get()); // 不洁圣杯
                    })
                    .build());

    private ModeTabs() {
    }

    private static ItemStack createRecallPotionStack() {
        ItemStack stack = PotionContents.createItemStack(Items.POTION, ModPotions.RECALL);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
