package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.tag.ModDamageTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * 伤害类型标签数据生成器。
 */
public class DamageTagGenerator extends TagsProvider<DamageType> {

    public DamageTagGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                Registries.DAMAGE_TYPE,
                lookupProvider,
                EnigmaticLegacy.MODID,
                existingFileHelper
        );
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        // 天使之祝免疫：摔落伤害、鞘翅撞墙伤害。
        tag(ModDamageTags.ANGEL_BLESSING_IMMUNE_TO)
                .addTag(DamageTypeTags.IS_FALL)
                .add(DamageTypes.FLY_INTO_WALL);

        // 天使之祝弱点：凋零伤害、虚空伤害。
        tag(ModDamageTags.ANGEL_BLESSING_VULNERABLE_TO)
                .add(DamageTypes.WITHER)
                .add(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Damage Type Tags";
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new DamageTagGenerator(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        event.getExistingFileHelper()
                )
        );
    }
}