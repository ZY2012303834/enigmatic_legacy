package org.enigmatic_legacy.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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
import org.enigmatic_legacy.api.RelicItemApi;
import org.enigmatic_legacy.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 物品标签数据生成器。
 * 用途：
 * 1. 生成飞升护符合成配方需要的神秘护符标签；
 * 2. 修复飞升护符合成配方中“神秘护符”显示为未知物品的问题；
 * 3. 把模组内武器、工具、护甲、盾牌加入原版 1.21.1 数据驱动附魔标签；
 * 4. 修复模组武器 / 装备 / 盾牌附魔能力受限的问题。
 * 注意：
 * 1. 当前项目环境没有 Item.Properties#enchantable(...)，不要再使用它；
 * 2. 1.21.1 主要通过 minecraft:enchantable/* 标签控制物品可用附魔类型；
 * 3. JSON 继续由 datagen 生成，不手写静态 JSON。
 */
public class ItemTagGenerator extends ItemTagsProvider {

    /**
     * 任意颜色神秘护符。
     * 对应生成路径：
     * data/enigmatic_legacy/tags/item/enigmatic_amulets.json
     */
    public static final TagKey<Item> ENIGMATIC_AMULETS = modItemTag("enigmatic_amulets");

    // 原版物品分类标签
    private static final TagKey<Item> BOOKSHELF_BOOKS = minecraftItemTag("bookshelf_books");
    private static final TagKey<Item> MINECRAFT_SWORDS = minecraftItemTag("swords");
    private static final TagKey<Item> MINECRAFT_AXES = minecraftItemTag("axes");
    private static final TagKey<Item> MINECRAFT_PICKAXES = minecraftItemTag("pickaxes");
    private static final TagKey<Item> MINECRAFT_SHOVELS = minecraftItemTag("shovels");

    // 原版 1.21.1 数据驱动附魔系统标签
    private static final TagKey<Item> ENCHANTABLE_DURABILITY = minecraftItemTag("enchantable/durability");
    private static final TagKey<Item> ENCHANTABLE_VANISHING = minecraftItemTag("enchantable/vanishing");

    private static final TagKey<Item> ENCHANTABLE_WEAPON = minecraftItemTag("enchantable/weapon");
    private static final TagKey<Item> ENCHANTABLE_SWORD = minecraftItemTag("enchantable/sword");
    private static final TagKey<Item> ENCHANTABLE_AXE = minecraftItemTag("enchantable/axe");
    private static final TagKey<Item> ENCHANTABLE_SHARP_WEAPON = minecraftItemTag("enchantable/sharp_weapon");
    private static final TagKey<Item> ENCHANTABLE_FIRE_ASPECT = minecraftItemTag("enchantable/fire_aspect");

    private static final TagKey<Item> ENCHANTABLE_MINING = minecraftItemTag("enchantable/mining");
    private static final TagKey<Item> ENCHANTABLE_MINING_LOOT = minecraftItemTag("enchantable/mining_loot");

    private static final TagKey<Item> ENCHANTABLE_ARMOR = minecraftItemTag("enchantable/armor");
    private static final TagKey<Item> ENCHANTABLE_EQUIPPABLE = minecraftItemTag("enchantable/equippable");
    private static final TagKey<Item> ENCHANTABLE_HEAD_ARMOR = minecraftItemTag("enchantable/head_armor");
    private static final TagKey<Item> ENCHANTABLE_CHEST_ARMOR = minecraftItemTag("enchantable/chest_armor");
    private static final TagKey<Item> ENCHANTABLE_LEG_ARMOR = minecraftItemTag("enchantable/leg_armor");
    private static final TagKey<Item> ENCHANTABLE_FOOT_ARMOR = minecraftItemTag("enchantable/foot_armor");

    /*
     * Java 原版盾牌主要通过 durability / vanishing 获得耐久、经验修补、消失诅咒等附魔。
     * 这里额外生成 shield 标签，方便以后自定义盾牌附魔或其他模组兼容。
     */
    private static final TagKey<Item> ENCHANTABLE_SHIELD = minecraftItemTag("enchantable/shield");

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
        addEnigmaticAmuletTags();
        addWeaponEnchantableTags();
        addToolEnchantableTags();
        addArmorEnchantableTags();
        addElytraEnchantableTags();
        addShieldEnchantableTags();
        addBookTags();
        addRelicEnchantableTags();
    }

    /**
     * 飞升护符配方中的“任意颜色神秘护符”。
     */
    private void addEnigmaticAmuletTags() {
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
     * 武器附魔标签。
     * 这些物品会获得剑 / 武器类附魔支持：
     * 1. 锋利、亡灵杀手、节肢杀手；
     * 2. 击退；
     * 3. 抢夺；
     * 4. 火焰附加；
     * 5. 横扫之刃，如果版本 / 附魔存在；
     * 6. 耐久、经验修补、消失诅咒。
     */
    private void addWeaponEnchantableTags() {
        /*
         * 剑类物品：
         * - 末影之屠；
         * - 行刑者之斧，按你的要求现在作为剑类属性和剑类附魔处理；
         * - 以太阔剑；
         * - 倒转之启；
         * - 无止之言。
         */
        tag(MINECRAFT_SWORDS)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get());

        tag(ENCHANTABLE_SWORD)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_WEAPON)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get())
                .add(ModItems.ETHERIUM_AXE.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_SHARP_WEAPON)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get())
                .add(ModItems.ETHERIUM_AXE.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_FIRE_ASPECT)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get())
                .add(ModItems.ETHERIUM_AXE.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get())
                .add(ModItems.ETHERIUM_AXE.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_VANISHING)
                .add(ModItems.ENDER_SLAYER.get())
                .add(ModItems.AXE_OF_EXECUTIONER.get())
                .add(ModItems.ETHERIUM_SWORD.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get())
                .add(ModItems.ETHERIUM_AXE.get())
                .add(ModItems.VORACIOUS_PAN.get());
    }

    /**
     * 工具附魔标签。
     * 修复：
     * 1. 以太镐可以正常获得效率、时运、精准采集、耐久、经验修补；
     * 2. 以太锹可以正常获得效率、时运、精准采集、耐久、经验修补；
     * 3. 以太斧可以正常获得工具附魔和武器附魔。
     */
    private void addToolEnchantableTags() {
        tag(MINECRAFT_PICKAXES)
                .add(ModItems.ETHERIUM_PICKAXE.get());

        tag(MINECRAFT_SHOVELS)
                .add(ModItems.ETHERIUM_SHOVEL.get());

        tag(MINECRAFT_AXES)
                .add(ModItems.ETHERIUM_AXE.get());

        tag(ENCHANTABLE_MINING)
                .add(ModItems.ETHERIUM_PICKAXE.get())
                .add(ModItems.ETHERIUM_SHOVEL.get())
                .add(ModItems.ETHERIUM_AXE.get());

        tag(ENCHANTABLE_MINING_LOOT)
                .add(ModItems.ETHERIUM_PICKAXE.get())
                .add(ModItems.ETHERIUM_SHOVEL.get())
                .add(ModItems.ETHERIUM_AXE.get());

        tag(ENCHANTABLE_AXE)
                .add(ModItems.ETHERIUM_AXE.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.ETHERIUM_PICKAXE.get())
                .add(ModItems.ETHERIUM_SHOVEL.get())
                .add(ModItems.ETHERIUM_AXE.get());

        tag(ENCHANTABLE_VANISHING)
                .add(ModItems.ETHERIUM_PICKAXE.get())
                .add(ModItems.ETHERIUM_SHOVEL.get())
                .add(ModItems.ETHERIUM_AXE.get());
    }

    /**
     * 护甲附魔标签。
     * 修复以太套装附魔能力受限：
     * 1. 全套支持保护类、荆棘、耐久、经验修补、消失诅咒；
     * 2. 头盔支持水下呼吸、水下速掘；
     * 3. 胸甲支持胸甲类附魔；
     * 4. 护腿支持迅捷潜行；
     * 5. 靴子支持摔落保护、深海探索者、冰霜行者、灵魂疾行。
     */
    private void addArmorEnchantableTags() {
        tag(ENCHANTABLE_ARMOR)
                .add(ModItems.ETHERIUM_HELMET.get())
                .add(ModItems.ETHERIUM_CHESTPLATE.get())
                .add(ModItems.ETHERIUM_LEGGINGS.get())
                .add(ModItems.ETHERIUM_BOOTS.get());

        tag(ENCHANTABLE_EQUIPPABLE)
                .add(ModItems.ETHERIUM_HELMET.get())
                .add(ModItems.ETHERIUM_CHESTPLATE.get())
                .add(ModItems.ETHERIUM_LEGGINGS.get())
                .add(ModItems.ETHERIUM_BOOTS.get());

        tag(ENCHANTABLE_HEAD_ARMOR)
                .add(ModItems.ETHERIUM_HELMET.get());

        tag(ENCHANTABLE_CHEST_ARMOR)
                .add(ModItems.ETHERIUM_CHESTPLATE.get());

        tag(ENCHANTABLE_LEG_ARMOR)
                .add(ModItems.ETHERIUM_LEGGINGS.get());

        tag(ENCHANTABLE_FOOT_ARMOR)
                .add(ModItems.ETHERIUM_BOOTS.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.ETHERIUM_HELMET.get())
                .add(ModItems.ETHERIUM_CHESTPLATE.get())
                .add(ModItems.ETHERIUM_LEGGINGS.get())
                .add(ModItems.ETHERIUM_BOOTS.get());

        tag(ENCHANTABLE_VANISHING)
                .add(ModItems.ETHERIUM_HELMET.get())
                .add(ModItems.ETHERIUM_CHESTPLATE.get())
                .add(ModItems.ETHERIUM_LEGGINGS.get())
                .add(ModItems.ETHERIUM_BOOTS.get());
    }

    private void addElytraEnchantableTags() {
        tag(ENCHANTABLE_EQUIPPABLE)
                .add(ModItems.MAJESTIC_ELYTRA.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.MAJESTIC_ELYTRA.get());
    }

    /**
     * 盾牌 / 格挡类物品附魔标签。
     * 修复：
     * 1. 烈焰之傲壁垒支持耐久、经验修补、消失诅咒；
     * 2. 饕餮之锅既是武器又能格挡，所以同时支持武器附魔和盾牌耐久类附魔。
     */
    private void addShieldEnchantableTags() {
        tag(ENCHANTABLE_SHIELD)
                .add(ModItems.BULWARK_OF_BLAZING_PRIDE.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_DURABILITY)
                .add(ModItems.BULWARK_OF_BLAZING_PRIDE.get())
                .add(ModItems.VORACIOUS_PAN.get());

        tag(ENCHANTABLE_VANISHING)
                .add(ModItems.BULWARK_OF_BLAZING_PRIDE.get())
                .add(ModItems.VORACIOUS_PAN.get());
    }

    private void addRelicEnchantableTags() {
        tag(ENCHANTABLE_VANISHING)
                .add(RelicItemApi.curseEnchantableRelics());

        tag(ENCHANTABLE_EQUIPPABLE)
                .add(RelicItemApi.curseEnchantableRelics());
    }

    private void addBookTags() {
        // 这些遗物按书类处理，古旧书袋只允许收纳 bookshelf_books 标签内的物品。
        tag(BOOKSHELF_BOOKS)
                .add(ModItems.ANIMAL_GUIDEBOOK.get())
                .add(ModItems.HUNTER_GUIDEBOOK.get())
                .add(ModItems.ODE_TO_LIVING.get())
                .add(ModItems.THE_ACKNOWLEDGMENT.get())
                .add(ModItems.THE_TWIST.get())
                .add(ModItems.THE_INFINITUM.get());
    }

    private static TagKey<Item> modItemTag(String path) {
        return TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, path)
        );
    }

    private static TagKey<Item> minecraftItemTag(String path) {
        return TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("minecraft", path)
        );
    }

    /**
     * 空方块标签 Provider。
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
