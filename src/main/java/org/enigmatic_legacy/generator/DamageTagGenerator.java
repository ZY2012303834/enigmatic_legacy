package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.damage.ModDamageTypes;
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
        // 虚空珍珠黑暗光环伤害：类似坠入虚空造成的伤害。
        // 注意：这里不要加入 Tags.DamageTypes.IS_MAGIC，避免被魔法抗性/魔法增伤影响。

        // 无视护甲。
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .addOptional(ModDamageTypes.DARKNESS.location());

        // 无视盾牌。
        tag(DamageTypeTags.BYPASSES_SHIELD)
                .addOptional(ModDamageTypes.DARKNESS.location());

        // 无视抗性效果，例如 Resistance。
        tag(DamageTypeTags.BYPASSES_RESISTANCE)
                .addOptional(ModDamageTypes.DARKNESS.location());

        // 无视保护类附魔减伤。
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .addOptional(ModDamageTypes.DARKNESS.location());

        // 无视药水/状态效果类减伤。
        tag(DamageTypeTags.BYPASSES_EFFECTS)
                .addOptional(ModDamageTypes.DARKNESS.location());

        // 可选：类似虚空伤害，绕过受伤冷却。
        // 如果你希望 0.5 秒稳定造成 4 点伤害，建议保留。
        tag(DamageTypeTags.BYPASSES_COOLDOWN)
                .addOptional(ModDamageTypes.DARKNESS.location());
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