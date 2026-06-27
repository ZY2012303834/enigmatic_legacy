package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;

public class ItemGenerator extends ItemModelProvider {

    public ItemGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnigmaticLegacy.MODID, existingFileHelper);
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(output ->
                new ItemGenerator(output, event.getExistingFileHelper()));
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.ASTRAL_DUST.getId());
        basicItem(ModItems.ENDER_ROD.getId());
        basicItem(ModItems.ETHERIUM_ORE.getId());
        basicItem(ModItems.ETHERIUM_INGOT.getId());
        basicItem(ModItems.THICC_SCROLL.getId());
        basicItem(ModItems.DARKEST_SCROLL.getId());
        basicItem(ModItems.COSMIC_HEART.getId());
        basicItem(ModItems.EARTH_HEART_FRAGMENT.getId());
        basicItem(ModItems.EARTH_HEART.getId());
        basicItem(ModItems.BLOODSTAINED_VALOR_EMBLEM.getId());

        var twistedHeartOn = withExistingParent("item/twisted_heart_on", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twisted_heart_on"));

        withExistingParent("item/twisted_heart", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twisted_heart"))
                .override()
                .predicate(modLoc("activated"), 1.0F)
                .model(twistedHeartOn)
                .end();

        basicItem(ModItems.EVIL_ESSENCE.getId());
        basicItem(ModItems.CURSED_RING.getId());
        basicItem(ModItems.IRON_RING.getId());
        basicItem(ModItems.EXQUISITE_RING.getId());
        basicItem(ModItems.EVIL_INGOT.getId());
        basicItem(ModItems.STORAGE_CRYSTAL.getId());
        basicItem(ModItems.SOUL_CRYSTAL.getId());
        basicItem(ModItems.FORBIDDEN_FRUIT.getId());
        // 扭曲魔镜
        withExistingParent("item/twisted_mirror", mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/twisted_mirror"));
        basicItem(ModItems.UNHOLY_GRAIL.getId()); // 不洁圣杯
        basicItem(ModItems.GUARDIAN_HEART.getId()); // 守卫者之心
        basicItem(ModItems.ENDER_RING.getId());

        basicItem(ModItems.UNWITNESSED_AMULET.getId());

        basicItem(ModItems.ENIGMATIC_AMULET_RED.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_AQUA.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_VIOLET.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_MAGENTA.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_GREEN.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_BLACK.getId());
        basicItem(ModItems.ENIGMATIC_AMULET_BLUE.getId());

        basicItem(ModItems.MAGNET_RING.getId());
        basicItem(ModItems.DISLOCATION_RING.getId());
        basicItem(ModItems.MONSTER_CHARM.getId());
        basicItem(ModItems.TREASURE_HUNTER_CHARM.getId());
        basicItem(ModItems.MEGA_SPONGE.getId());
        basicItem(ModItems.ENCHANTER_PEARL.getId());

        enigmaticEye();

        basicItem(ModItems.THE_ACKNOWLEDGMENT.getId());
        basicItem(ModItems.EXTRADIMENSIONAL_EYE.getId()); // 超维之眼
        basicItem(ModItems.ENCHANTMENT_TRANSPOSER.getId()); // 求知之书
        basicItem(ModItems.CURSE_TRANSPOSER.getId()); // 噬咒之书

        withExistingParent("item/recall_potion", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/recall_potion"));

        // 兼容某处错误引用的 enigmatic_legacy:item/item/the_acknowledgment。
        // 不影响正常 the_acknowledgment 模型，只是防止资源加载警告。
        withExistingParent("item/item/the_acknowledgment", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/the_acknowledgment"));

        basicItem(ModItems.ETHERIUM_SWORD.getId()); // 以太阔剑
        basicItem(ModItems.ETHERIUM_PICKAXE.getId()); // 以太搞
        basicItem(ModItems.ETHERIUM_SHOVEL.getId());  // 以太锹
        withExistingParent("item/etherium_axe", mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/etherium_waraxe")); // 以太斧

        // 行刑者之斧模型。
        // 这里使用 item/handheld，表示它是剑、斧、镐这类手持武器/工具模型。
        withExistingParent("item/axe_of_executioner", mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/axe_of_executioner")); // 行刑者之斧

        basicItem(ModItems.MENDING_MIXTURE.getId());    // 修补混合物

        // 使用 32 帧 compass_00 ~ compass_31 贴图。
        // angle 属性由 WayfinderClientEvents 在客户端注册。
        wayfinderOfTheDamned();    // 被诅咒者的寻路指针

        // 末影之屠模型。
        // 使用 handheld，保证拿在手里像剑一样显示。
        withExistingParent("item/ender_slayer", mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/ender_slayer"));

        // 烈焰之傲壁垒模型。
        // 不能使用 basicItem，否则只会显示普通 2D 物品。
        // 这里生成 shield / shield_blocking 两个模型，让它拥有原版盾牌举盾动画。
        infernalShield();

        basicItem(ModItems.ETHERIUM_HELMET.getId());
        basicItem(ModItems.ETHERIUM_CHESTPLATE.getId());
        basicItem(ModItems.ETHERIUM_LEGGINGS.getId());
        basicItem(ModItems.ETHERIUM_BOOTS.getId());

        basicItem(ModItems.GOLEM_HEART.getId());
        basicItem(ModItems.ANGEL_BLESSING.getId());

        basicItem(ModItems.OCEAN_STONE.getId());
        basicItem(ModItems.BLAZING_CORE.getId());   // 烈焰之核
        basicItem(ModItems.EYE_OF_NEBULA.getId()); // 星云之眼
        basicItem(ModItems.VOID_PEARL.getId()); // 虚空珍珠
        basicItem(ModItems.THE_CUBE.getId()); // 非欧立方
        basicItem(ModItems.HEART_OF_CREATION.getId()); // 创造之心
        // 卷轴
        basicItem(ModItems.XP_SCROLL.getId()); // 永恒智慧卷轴
        basicItem(ModItems.HEAVEN_SCROLL.getId()); // 天堂之礼
        basicItem(ModItems.CURSED_SCROLL.getId()); // 千咒卷轴
        basicItem(ModItems.FABULOUS_SCROLL.getId()); // 创造者的恩赐
        basicItem(ModItems.AVARICE_SCROLL.getId()); // 无尽贪婪契约
        basicItem(ModItems.ABYSSAL_HEART.getId()); // 深渊之心
    }

    private void enigmaticEye() {
        var dormant1 = withExistingParent("item/enigmatic_eye_dormant_1", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/enigmatic_eye_dormant_1"));

        var dormant2 = withExistingParent("item/enigmatic_eye_dormant_2", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/enigmatic_eye_dormant_2"));

        var active = withExistingParent("item/enigmatic_eye_active", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/enigmatic_eye"));

        withExistingParent("item/enigmatic_eye", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/enigmatic_eye_dormant"))
                .override()
                .predicate(modLoc("enigmatic_eye_activated"), 0.33F)
                .model(dormant1)
                .end()
                .override()
                .predicate(modLoc("enigmatic_eye_activated"), 0.66F)
                .model(dormant2)
                .end()
                .override()
                .predicate(modLoc("enigmatic_eye_activated"), 1.0F)
                .model(active)
                .end();
    }

    /**
     * 生成被诅咒者的寻路指针模型。
     *
     * 原项目效果：
     * - 有目标时，指针指向灵魂水晶；
     * - 没有目标时，指针随机旋转。
     *
     * 实现方式：
     * - 基础模型：wayfinder_of_the_damned；
     * - 旋转帧：compass_00 ~ compass_31；
     * - 模型属性：enigmatic_legacy:angle；
     * - 客户端会根据 angle 自动选择对应帧。
     *
     * 需要贴图：
     * src/main/resources/assets/enigmatic_legacy/textures/item/wayfinder_of_the_damned.png
     * src/main/resources/assets/enigmatic_legacy/textures/item/compass_00.png
     * ...
     * src/main/resources/assets/enigmatic_legacy/textures/item/compass_31.png
     */
    private void wayfinderOfTheDamned() {
        var base = withExistingParent("item/wayfinder_of_the_damned", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/wayfinder_of_the_damned"));

        for (int frame = 0; frame < 32; frame++) {
            String frameName = wayfinderFrameName(frame);

            var frameModel = withExistingParent("item/wayfinder_of_the_damned_" + frameName, mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/compass_" + frameName));

            /*
             * override 顺序必须从小到大。
             * 当 angle >= 当前 predicate 时，就会使用当前帧。
             */
            base.override()
                    .predicate(modLoc("angle"), frame / 32.0F)
                    .model(frameModel)
                    .end();
        }
    }

    /**
     * 生成烈焰之傲壁垒盾牌模型。
     * 修复内容：
     * - 不再使用 item/generated 的普通 2D 物品模型；
     * - 改为继承原版 minecraft:item/shield；
     * - 举盾时通过 minecraft:blocking predicate 切换到 shield_blocking 模型；
     * - 这样才能显示原版盾牌举起、格挡时的大盾视觉效果。
     * 生成文件：
     * - assets/enigmatic_legacy/models/item/infernal_shield.json
     * - assets/enigmatic_legacy/models/item/infernal_shield_blocking.json
     * 需要贴图：
     * - assets/enigmatic_legacy/textures/item/infernal_shield.png
     */
    private void infernalShield() {
        /*
         * 举盾状态模型。
         *
         * parent 使用 minecraft:item/shield_blocking，
         * 这是原版盾牌格挡时的模型。
         */
        var blockingModel = withExistingParent("item/infernal_shield_blocking", mcLoc("item/shield_blocking"))
                .texture("particle", modLoc("item/infernal_shield"));

        /*
         * 普通盾牌模型。
         *
         * parent 使用 minecraft:item/shield，
         * 并添加 blocking override。
         *
         * 当客户端物品属性 minecraft:blocking >= 1.0F 时，
         * 自动切换到 infernal_shield_blocking。
         */
        withExistingParent("item/infernal_shield", mcLoc("item/shield"))
                .texture("particle", modLoc("item/infernal_shield"))
                .override()
                .predicate(mcLoc("blocking"), 1.0F)
                .model(blockingModel)
                .end();
    }

    /**
     * 把帧编号转为两位数字。
     * 例如：
     * 0  -> 00
     * 1  -> 01
     * 10 -> 10
     * 31 -> 31
     */
    private static String wayfinderFrameName(int frame) {
        return frame < 10 ? "0" + frame : Integer.toString(frame);
    }
}
