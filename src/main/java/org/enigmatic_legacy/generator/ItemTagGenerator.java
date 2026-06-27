package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
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
 * 1. 把自定义武器加入原版武器 / 工具标签；
 * 2. 把自定义武器加入 1.21+ 数据驱动附魔系统使用的 enchantable 标签；
 * 3. 修复末影之屠、行刑者之斧不能在附魔台 / 铁砧中正常附魔的问题。
 */
public class ItemTagGenerator extends ItemTagsProvider {

    /**
     * 原版 1.21+ 附魔系统使用的可附魔物品标签。
     * 这里手动创建 TagKey，避免因为不同映射下 ItemTags 常量名变化导致编译问题。
     */
    private static final TagKey<Item> ENCHANTABLE_DURABILITY = minecraftItemTag("enchantable/durability");
    private static final TagKey<Item> ENCHANTABLE_WEAPON = minecraftItemTag("enchantable/weapon");
    private static final TagKey<Item> ENCHANTABLE_SWORD = minecraftItemTag("enchantable/sword");
    private static final TagKey<Item> ENCHANTABLE_SHARP_WEAPON = minecraftItemTag("enchantable/sharp_weapon");
    private static final TagKey<Item> ENCHANTABLE_FIRE_ASPECT = minecraftItemTag("enchantable/fire_aspect");

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
         * ItemTagsProvider 构造方法需要一个 blockTags.contentsGetter()。
         * 当前这里只是为了给物品标签提供依赖，不实际生成任何方块标签。
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
         * 末影之屠：
         * - 加入 swords，保证它被识别为剑；
         * - 加入 sword / weapon / sharp_weapon / fire_aspect / durability 附魔标签；
         * - 支持锋利、亡灵杀手、节肢杀手、击退、抢夺、火焰附加、耐久、经验修补等常规剑类附魔。
         */
        tag(ItemTags.SWORDS)
                .add(ModItems.ENDER_SLAYER.get());

        tag(ENCHANTABLE_SWORD)
                .add(ModItems.ENDER_SLAYER.get());

        tag(ENCHANTABLE_WEAPON)
                .add(ModItems.ENDER_SLAYER.get());

        tag(ENCHANTABLE_SHARP_WEAPON)
                .add(ModItems.ENDER_SLAYER.get());

        tag(ENCHANTABLE_FIRE_ASPECT)
                .add(ModItems.ENDER_SLAYER.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.ENDER_SLAYER.get());

        /*
         * 行刑者之斧：
         * - 名字仍然是斧；
         * - 但按用户要求，属性和附魔都作为“剑”处理；
         * - 不再加入 axes / axe / mining / mining_loot；
         * - 加入 swords 和剑类 enchantable 标签；
         * - 这样可以获得锋利、亡灵杀手、节肢杀手、击退、抢夺、火焰附加、耐久、经验修补等剑类附魔；
         * - 抢夺等级仍然会影响斩首概率。
         */
        tag(ItemTags.SWORDS)
                .add(ModItems.AXE_OF_EXECUTIONER.get());

        tag(ENCHANTABLE_SWORD)
                .add(ModItems.AXE_OF_EXECUTIONER.get());

        tag(ENCHANTABLE_WEAPON)
                .add(ModItems.AXE_OF_EXECUTIONER.get());

        tag(ENCHANTABLE_SHARP_WEAPON)
                .add(ModItems.AXE_OF_EXECUTIONER.get());

        tag(ENCHANTABLE_FIRE_ASPECT)
                .add(ModItems.AXE_OF_EXECUTIONER.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.AXE_OF_EXECUTIONER.get());
    }

    private static TagKey<Item> minecraftItemTag(String path) {
        return TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("minecraft", path)
        );
    }

    /**
     * 空方块标签 Provider。
     * ItemTagsProvider 需要 blockTags.contentsGetter()，
     * 这里提供一个空实现，避免额外创建无关方块标签。
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