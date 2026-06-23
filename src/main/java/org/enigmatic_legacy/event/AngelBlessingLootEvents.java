package org.enigmatic_legacy.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.LootPool;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import org.enigmatic_legacy.item.ModItems;

/**
 * 天使之祝地牢战利品注入。
 */
public final class AngelBlessingLootEvents {
    private AngelBlessingLootEvents() {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();

        if (!name.equals(BuiltInLootTables.DESERT_PYRAMID.location())
                && !name.equals(BuiltInLootTables.JUNGLE_TEMPLE.location())) {
            return;
        }

        LootPool pool = LootPool.lootPool()
                .name("enigmatic_legacy_angel_blessing")
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ModItems.ANGEL_BLESSING.get())
                        .setWeight(1)
                        .when(LootItemRandomChanceCondition.randomChance(0.08F))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F))))
                .build();

        event.getTable().addPool(pool);
    }
}