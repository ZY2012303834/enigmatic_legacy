package org.enigmatic_legacy.tab;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
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
            CREATIVE_MODE_TABS.register("enigmatic_legacy", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.enigmatic_legacy"))
                            .icon(() -> ModItems.HEART_OF_CREATION.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.ASTRAL_DUST.get());              // 星尘
                                output.accept(ModItems.ENDER_ROD.get());                // 末影棒
                                output.accept(ModItems.ETHERIUM_ORE.get());             // 以太矿石
                                output.accept(ModItems.ETHERIUM_INGOT.get());           // 以太锭
                                output.accept(ModItems.ASTRAL_DUST_SACK.get());         // 袋装星尘
                                output.accept(ModItems.ETHERIUM_BLOCK.get());           // 以太块
                                output.accept(ModItems.COSMIC_HEART.get());             // 寰宇之心
                                output.accept(ModItems.BIG_LAMP.get());                 // 大灯笼
                                output.accept(ModItems.BIG_SHROOMLAMP.get());           // 菌光体灯笼
                                output.accept(ModItems.EARTH_HEART_FRAGMENT.get());     // 大地之心碎片
                                output.accept(ModItems.EARTH_HEART.get());              // 大地之心
                                output.accept(ModItems.TWISTED_HEART.get());            // 扭曲之心
                                output.accept(ModItems.EVIL_ESSENCE.get());             // 邪恶精髓
                                output.accept(ModItems.CURSED_RING.get());              // 七咒之戒
                                output.accept(ModItems.IRON_RING.get());                // 铁戒指
                                output.accept(ModItems.EXQUISITE_RING.get());           // 精美戒指
                                output.accept(ModItems.EVIL_INGOT.get());               // 极恶锭
                                output.accept(ModItems.STORAGE_CRYSTAL.get());          // 超维容器
                                output.accept(ModItems.SOUL_CRYSTAL.get());             // 灵魂水晶
                                output.accept(ModItems.FORBIDDEN_FRUIT.get());          // 禁忌之果
                                output.accept(ModItems.TWISTED_MIRROR.get());           // 扭曲魔镜
                                output.accept(ModItems.RECALL_POTION.get());            // 召回药水
                                output.accept(createUltimateNightVisionPotionStack(Items.POTION));              // 终极夜视药水
                                output.accept(createUltimateNightVisionPotionStack(Items.SPLASH_POTION));       // 喷溅型终极夜视药水
                                output.accept(createUltimateNightVisionPotionStack(Items.LINGERING_POTION));    // 滞留型终极夜视药水
                                output.accept(ModItems.UNHOLY_GRAIL.get());             // 不洁圣杯
                                output.accept(ModItems.GUARDIAN_HEART.get());           // 守卫者之心
                                output.accept(ModItems.ENDER_RING.get());               // 末影之戒
                                output.accept(ModItems.MAGNET_RING.get());              // 磁力戒指
                                output.accept(ModItems.DISLOCATION_RING.get());         // 转位之戒
                                output.accept(ModItems.ABYSSAL_HEART.get());            // 深渊之心
                                output.accept(ModItems.EXTRADIMENSIONAL_EYE.get());     // 超维之眼
                                output.accept(ModItems.ENCHANTMENT_TRANSPOSER.get());   // 求知之书
                                output.accept(ModItems.CURSE_TRANSPOSER.get());         // 噬咒之书

                                // 武器工具
                                output.accept(ModItems.THE_ACKNOWLEDGMENT.get());       // 启示之证
                                output.accept(ModItems.ETHERIUM_SWORD.get());           // 以太阔剑
                                output.accept(ModItems.ETHERIUM_PICKAXE.get()); // 以太镐
                                // 护符
                                output.accept(ModItems.ENIGMATIC_EYE.get());            // 莫测之眼
                                output.accept(ModItems.MONSTER_CHARM.get());            // 怪物猎人勋章
                                output.accept(ModItems.TREASURE_HUNTER_CHARM.get());    // 猎宝者护符
                                output.accept(ModItems.MEGA_SPONGE.get());              // 吸水海绵
                                output.accept(ModItems.ENCHANTER_PEARL.get());          // 附魔师的珍珠
                                output.accept(ModItems.UNWITNESSED_AMULET.get());       // 无主护符
                                output.accept(ModItems.ENIGMATIC_AMULET_RED.get());     // 神秘护符：红色
                                output.accept(ModItems.ENIGMATIC_AMULET_AQUA.get());    // 神秘护符：青色
                                output.accept(ModItems.ENIGMATIC_AMULET_VIOLET.get());  // 神秘护符：紫罗兰色
                                output.accept(ModItems.ENIGMATIC_AMULET_MAGENTA.get()); // 神秘护符：品红色
                                output.accept(ModItems.ENIGMATIC_AMULET_GREEN.get());   // 神秘护符：绿色
                                output.accept(ModItems.ENIGMATIC_AMULET_BLACK.get());   // 神秘护符：黑色
                                output.accept(ModItems.ENIGMATIC_AMULET_BLUE.get());    // 神秘护符：蓝色
                                // 术石
                                output.accept(ModItems.GOLEM_HEART.get());              // 魔像之心
                                output.accept(ModItems.ANGEL_BLESSING.get());           // 天使之祝
                                output.accept(ModItems.OCEAN_STONE.get());              // 海洋意志
                                output.accept(ModItems.BLAZING_CORE.get());             // 烈焰核心
                                output.accept(ModItems.EYE_OF_NEBULA.get());            // 星云之眼
                                output.accept(ModItems.VOID_PEARL.get());               // 虚空珍珠
                                output.accept(ModItems.THE_CUBE.get());                 // 非欧立方
                                output.accept(ModItems.HEART_OF_CREATION.get());        // 创造之心
                                // 卷轴
                                output.accept(ModItems.THICC_SCROLL.get());             // 空卷轴
                                output.accept(ModItems.DARKEST_SCROLL.get());           // 至暗卷轴
                                output.accept(ModItems.XP_SCROLL.get());                // 永恒智慧卷轴
                                output.accept(ModItems.HEAVEN_SCROLL.get());            // 天堂之礼
                                output.accept(ModItems.CURSED_SCROLL.get());            // 千咒卷轴
                                output.accept(ModItems.FABULOUS_SCROLL.get());          // 创造者的恩赐
                                output.accept(ModItems.AVARICE_SCROLL.get());           // 无尽贪婪契约
                            })
                            .build());

    private ModeTabs() {
    }

    /**
     * 创建终极夜视药水。
     * 原项目效果：
     * Night Vision, 19200 ticks。
     */
    private static ItemStack createUltimateNightVisionPotionStack(Item potionItem) {
        ItemStack stack = PotionContents.createItemStack(
                potionItem,
                ModPotions.ULTIMATE_NIGHT_VISION
        );
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
