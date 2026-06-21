package org.enigmatic_legacy.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * Mod entity registry.
 */
public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, EnigmaticLegacy.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PermanentItemEntity>> PERMANENT_ITEM =
            ENTITY_TYPES.register("permanent_item_entity", () -> EntityType.Builder
                    .<PermanentItemEntity>of(PermanentItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .updateInterval(2)
                    .build("permanent_item_entity"));

    private ModEntities() {
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
