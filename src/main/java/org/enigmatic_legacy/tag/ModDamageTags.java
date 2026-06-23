package org.enigmatic_legacy.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 自定义伤害类型标签。
 */
public final class ModDamageTags {
    public static final TagKey<DamageType> ANGEL_BLESSING_IMMUNE_TO = create(
            "spellstone/angel_blessing/immune_to"
    );

    public static final TagKey<DamageType> ANGEL_BLESSING_VULNERABLE_TO = create(
            "spellstone/angel_blessing/vulnerable_to"
    );

    private static TagKey<DamageType> create(String path) {
        return TagKey.create(
                Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path)
        );
    }

    private ModDamageTags() {
    }
}