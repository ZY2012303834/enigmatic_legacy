package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 物品标签数据生成器。
 * 用途：
 * 1. 生成飞升护符合成配方需要的神秘护符标签；
 * 2. 修复飞升护符合成配方中“神秘护符”显示为未知物品的问题；
 * 3. 保持 JSON 由数据生成器生成，不手写静态 JSON。
 */
public class ItemTagGenerator extends ItemTagsProvider {

    /**
     * 任意颜色神秘护符。
     * 对应生成路径：
     * data/enigmatic_legacy/tags/item/enigmatic_amulets.json
     * RecipeGenerator.java 中飞升护符配方使用：
     * .define('M', ENIGMATIC_AMULETS)
     */
    public static final TagKey<Item> ENIGMATIC_AMULETS = TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "enigmatic_amulets")
    );

    public ItemTagGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, EnigmaticLegacy.MODID, existingFileHelper);
    }

    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(event.includeServer());

        /*
         * ItemTagsProvider 需要方块标签依赖。
         * 当前这里只生成物品标签，所以提供一个空的方块标签 Provider。
         */
        EmptyBlockTagProvider blockTags = new EmptyBlockTagProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                event.getExistingFileHelper()
        );

        pack.addProvider(output -> blockTags);

        pack.addProvider(output -> new ItemTagGenerator(
                output,
                event.getLookupProvider(),
                blockTags.contentsGetter(),
                event.getExistingFileHelper()
        ));
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        /*
         * 飞升护符配方中的“任意颜色神秘护符”。
         *
         * 注意：
         * 这里只加入七种已经见证过颜色的神秘护符；
         * 不加入无主护身符，因为无主护身符还没有颜色，不应该直接参与飞升护符合成。
         */
        tag(ENIGMATIC_AMULETS)
                .add(ModItems.ENIGMATIC_AMULET_RED.get())
                .add(ModItems.ENIGMATIC_AMULET_AQUA.get())
                .add(ModItems.ENIGMATIC_AMULET_VIOLET.get())
                .add(ModItems.ENIGMATIC_AMULET_MAGENTA.get())
                .add(ModItems.ENIGMATIC_AMULET_GREEN.get())
                .add(ModItems.ENIGMATIC_AMULET_BLACK.get())
                .add(ModItems.ENIGMATIC_AMULET_BLUE.get());
    }

    /**
     * 空方块标签 Provider。
     *
     * ItemTagsProvider 构造方法需要 blockTags.contentsGetter()，
     * 这里不实际生成任何方块标签。
     */
    private static final class EmptyBlockTagProvider extends BlockTagsProvider {

        private EmptyBlockTagProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> lookupProvider,
                @Nullable ExistingFileHelper existingFileHelper
        ) {
            super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            // 当前不需要生成方块标签。
        }
    }
}