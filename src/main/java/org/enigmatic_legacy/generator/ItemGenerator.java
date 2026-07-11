package org.enigmatic_legacy.generator;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
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
        basicItem(ModItems.TOTEM_OF_MALICE.getId());
        basicItem(ModItems.SCORCHED_CHARM.getId()); // 阳灼护符

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
        basicItem(ModItems.ASTRAL_FRUIT.getId());
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
        basicItem(ModItems.ULTIMATE_LUXURY_RING.getId());
        basicItem(ModItems.MONSTER_CHARM.getId());
        basicItem(ModItems.TREASURE_HUNTER_CHARM.getId());
        basicItem(ModItems.MEGA_SPONGE.getId());
        basicItem(ModItems.ENCHANTER_PEARL.getId());

        enigmaticEye();

        // 启示之证使用原项目手写 3D separate-transforms 模型。
        // 入口 JSON 位于 src/main/resources，避免 datagen 覆盖成普通 2D。
        // 无止之言同理，使用原项目三阶段手写 3D 模型和 item property 切换。
        basicItem(ModItems.EXTRADIMENSIONAL_EYE.getId()); // 超维之眼
        basicItem(ModItems.ENCHANTMENT_TRANSPOSER.getId()); // 求知之书
        basicItem(ModItems.CURSE_TRANSPOSER.getId()); // 噬咒之书
        basicItem(ModItems.ANTIQUE_BOOK_BAG.getId()); // 古旧书袋

        withExistingParent("item/recall_potion", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/recall_potion"));
        withExistingParent("item/redemption_potion", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/redemption_potion"));

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

        // 兽友指南使用原项目手写 3D separate-transforms 模型。
        // 入口 JSON 位于 src/main/resources，避免 datagen 覆盖成普通 2D。
        // 野猎指南同理，使用原项目 Blockbench 模型和 2D GUI 模型。

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

        // 饕餮之锅模型。
        // 原项目不是普通 handheld，而是 idle / blocking 两套模型。
        // 这里生成模型切换入口，具体 3D in_hand 模型使用原项目 JSON。
        eldritchPan();

        basicItem(ModItems.MAGIC_QUARTZ_RING.getId()); // 魔法石英戒指
        earthPromise();

        basicItem(ModItems.ICHOR_DROPLET.getId()); // 灵液滴

        basicItem(ModItems.PURE_HEART.getId()); // 纯净之心

        // 飞升护符模型。
        // 原项目为普通 2D item/generated 模型，没有 3D 模型。
        basicItem(ModItems.ASCENSION_AMULET.getId());
        basicItem(ModItems.ELDRITCH_AMULET.getId());

        basicItem(ModItems.ETHERIUM_HELMET.getId());
        basicItem(ModItems.ETHERIUM_CHESTPLATE.getId());
        basicItem(ModItems.ETHERIUM_LEGGINGS.getId());
        basicItem(ModItems.ETHERIUM_BOOTS.getId());
        basicItem(ModItems.MAJESTIC_ELYTRA.getId());

        basicItem(ModItems.GOLEM_HEART.getId());
        basicItem(ModItems.ANGEL_BLESSING.getId());

        basicItem(ModItems.OCEAN_STONE.getId());
        basicItem(ModItems.BLAZING_CORE.getId());   // 烈焰之核
        basicItem(ModItems.FORGOTTEN_ICE_CRYSTAL.getId()); // 忘却冰晶
        withExistingParent(ModItems.ETHERIUM_CORE.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/etherium_frame"))
                .texture("layer1", modLoc("item/etherium_core"));
        basicItem(ModItems.REVIVAL_LEAVES.getId()); // 复苏之叶
        basicItem(ModItems.EYE_OF_NEBULA.getId()); // 星云之眼
        basicItem(ModItems.VOID_PEARL.getId()); // 虚空珍珠
        basicItem(ModItems.THE_CUBE.getId()); // 非欧立方
        basicItem(ModItems.HEART_OF_CREATION.getId()); // 创造之心
        // 卷轴
        basicItem(ModItems.XP_SCROLL.getId()); // 永恒智慧卷轴
        basicItem(ModItems.CURSED_XP_SCROLL.getId()); // 无知诅咒卷轴
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

    private void earthPromise() {
        var broken = withExistingParent("item/earth_promise_broken", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/earth_promise_broken"));

        withExistingParent("item/earth_promise", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/earth_promise"))
                .override()
                .predicate(mcLoc("broken"), 1.0F)
                .model(broken)
                .end();
    }

    /**
     * 生成烈焰之傲壁垒模型。
     * 结构来自原项目：
     * - infernal_shield：根据 minecraft:blocking 切换 idle / blocking；
     * - idle / blocking：使用 separate_transforms 分离背包 2D 图标和手持 3D 模型；
     * - in_hand 模型为原项目 Blockbench 导出的 3D 盾牌模型。
     */
    private void infernalShield() {
        var inventoryModel = withExistingParent("item/infernal_shield_in_inventory", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/infernal_shield"));

        var idleInHandModel = withExistingParent(
                "item/infernal_shield_idle_in_hand_proxy",
                modLoc("item/infernal_shield_idle_in_hand")
        );
        var blockingInHandModel = withExistingParent(
                "item/infernal_shield_blocking_in_hand_proxy",
                modLoc("item/infernal_shield_blocking_in_hand")
        );

        var idleModel = getBuilder("item/infernal_shield_idle")
                .guiLight(net.minecraft.client.renderer.block.model.BlockModel.GuiLight.FRONT);

        idleModel.customLoader(SeparateTransformsModelBuilder::begin)
                .base(inventoryModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.HEAD, idleInHandModel)
                .perspective(ItemDisplayContext.FIXED, idleInHandModel)
                .perspective(ItemDisplayContext.GROUND, idleInHandModel);

        var blockingModel = getBuilder("item/infernal_shield_blocking")
                .guiLight(net.minecraft.client.renderer.block.model.BlockModel.GuiLight.FRONT);

        blockingModel.customLoader(SeparateTransformsModelBuilder::begin)
                .base(inventoryModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.HEAD, blockingInHandModel)
                .perspective(ItemDisplayContext.FIXED, blockingInHandModel)
                .perspective(ItemDisplayContext.GROUND, blockingInHandModel);

        getBuilder("item/infernal_shield")
                .override()
                .predicate(mcLoc("blocking"), 0.0F)
                .model(idleModel)
                .end()
                .override()
                .predicate(mcLoc("blocking"), 1.0F)
                .model(blockingModel)
                .end();
    }

    private void wayfinderOfTheDamned() {
        var baseModel = withExistingParent("item/wayfinder_of_the_damned", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/compass_00"));

        for (int frame = 0; frame < 32; frame++) {
            var frameModel = withExistingParent("item/wayfinder_of_the_damned_" + wayfinderFrameName(frame), mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/compass_" + wayfinderFrameName(frame)));

            baseModel.override()
                    .predicate(modLoc("angle"), frame / 32.0F)
                    .model(frameModel)
                    .end();
        }
    }

    /**
     * 生成饕餮之锅模型。
     * 原项目结构：
     * - eldritch_pan：
     *   根据 minecraft:blocking 切换 idle / blocking；
     * - eldritch_pan_idle：
     *   背包里使用 2D 动图 eldritch_pan；
     *   手里使用 3D 模型 eldritch_pan_idle_in_hand；
     * - eldritch_pan_blocking：
     *   背包里仍使用 2D 动图 eldritch_pan；
     *   手里使用 3D 模型 eldritch_pan_blocking_in_hand。
     * 注意：
     * - eldritch_pan_idle_in_hand.json 很大，是 Blockbench 导出的模型；
     * - eldritch_pan_blocking_in_hand.json 继承 idle_in_hand，只改 display；
     * - 这两个建议直接放静态 JSON，不建议用 datagen 手写元素。
     */
    private void eldritchPan() {
        /*
         * 背包 / GUI / 掉落物基础图标。
         *
         * 这里使用 item/generated + layer0，
         * 可以正常播放 eldritch_pan.png.mcmeta 动画。
         */
        var inventoryModel = withExistingParent("item/eldritch_pan_in_inventory", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/eldritch_pan"));

        /*
         * 手持普通状态。
         *
         * 这个 parent 指向静态 JSON：
         * src/main/resources/assets/enigmatic_legacy/models/item/eldritch_pan_idle_in_hand.json
         */
        var idleInHandModel = withExistingParent(
                "item/eldritch_pan_idle_in_hand_proxy",
                modLoc("item/eldritch_pan_idle_in_hand")
        );

        /*
         * 手持格挡状态。
         *
         * 这个 parent 指向静态 JSON：
         * src/main/resources/assets/enigmatic_legacy/models/item/eldritch_pan_blocking_in_hand.json
         */
        var blockingInHandModel = withExistingParent(
                "item/eldritch_pan_blocking_in_hand_proxy",
                modLoc("item/eldritch_pan_blocking_in_hand")
        );

        /*
         * 普通状态模型：
         * - 背包显示 2D 动图；
         * - 手里显示 3D 锅。
         */
        var idleModel = getBuilder("item/eldritch_pan_idle")
                .guiLight(net.minecraft.client.renderer.block.model.BlockModel.GuiLight.FRONT);

        idleModel.customLoader(SeparateTransformsModelBuilder::begin)
                .base(inventoryModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, idleInHandModel)
                .perspective(ItemDisplayContext.HEAD, idleInHandModel)
                .perspective(ItemDisplayContext.FIXED, idleInHandModel)
                .perspective(ItemDisplayContext.GROUND, idleInHandModel);

        /*
         * 格挡状态模型：
         * - 背包仍显示 2D 动图；
         * - 手里切换到 blocking display。
         */
        var blockingModel = getBuilder("item/eldritch_pan_blocking")
                .guiLight(net.minecraft.client.renderer.block.model.BlockModel.GuiLight.FRONT);

        blockingModel.customLoader(SeparateTransformsModelBuilder::begin)
                .base(inventoryModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, blockingInHandModel)
                .perspective(ItemDisplayContext.HEAD, blockingInHandModel)
                .perspective(ItemDisplayContext.FIXED, blockingInHandModel)
                .perspective(ItemDisplayContext.GROUND, blockingInHandModel);

        /*
         * 总入口模型。
         *
         * 未格挡：
         * minecraft:blocking = 0 -> eldritch_pan_idle
         *
         * 正在格挡：
         * minecraft:blocking = 1 -> eldritch_pan_blocking
         */
        getBuilder("item/eldritch_pan")
                .override()
                .predicate(mcLoc("blocking"), 0.0F)
                .model(idleModel)
                .end()
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
