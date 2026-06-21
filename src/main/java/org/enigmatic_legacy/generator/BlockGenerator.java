package org.enigmatic_legacy.generator;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LanternBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.ModBlocks;

public class BlockGenerator extends BlockStateProvider {

    public BlockGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnigmaticLegacy.MODID, existingFileHelper);
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeClient())
                .addProvider(output -> new BlockGenerator(output, event.getExistingFileHelper()));
    }

    @Override
    protected void registerStatesAndModels() {
        astralDustSack();   // 星尘袋
        etheriumBlock();    // 以太块
        bigLamp();          // 大灯笼
        massiveLamp();      // 封装的大灯笼
        bigShroomlamp();    // 菌光体灯笼
    }

    private void astralDustSack() {
        ModelFile astralDustModel = models().getBuilder(ModBlocks.ASTRAL_DUST_SACK.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"))
                .texture("0", modLoc("block/astral_dust_sack"))
                .texture("particle", modLoc("block/astral_dust_sack"))
                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .face(Direction.NORTH).uvs(8, 0, 16, 8).texture("#0").end()
                .face(Direction.EAST).uvs(8, 0, 16, 8).texture("#0").end()
                .face(Direction.SOUTH).uvs(8, 0, 16, 8).texture("#0").end()
                .face(Direction.WEST).uvs(8, 0, 16, 8).texture("#0").end()
                .face(Direction.UP).uvs(0, 0, 8, 8).texture("#0").end()
                .face(Direction.DOWN).uvs(0, 8, 8, 16).texture("#0").end()
                .end();

        simpleBlock(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel);
        simpleBlockItem(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel);
    }

    private void etheriumBlock() {
        simpleBlockWithItem(ModBlocks.ETHERIUM_BLOCK.get(), cubeAll(ModBlocks.ETHERIUM_BLOCK.get()));
    }

    private void bigLamp() {
        ModelFile standingLamp = bigLampModel("the_lamp/big_lamp", false, modLoc("block/the_lamp"));
        ModelFile hangingLamp = bigLampModel("the_lamp/big_hanging_lamp", true, modLoc("block/the_lamp"));

        getVariantBuilder(ModBlocks.BIG_LAMP.get())
                .partialState()
                .with(LanternBlock.HANGING, false)
                .modelForState()
                .modelFile(standingLamp)
                .addModel()
                .partialState()
                .with(LanternBlock.HANGING, true)
                .modelForState()
                .modelFile(hangingLamp)
                .addModel();

        simpleBlockItem(ModBlocks.BIG_LAMP.get(), standingLamp);
    }

    private ModelFile bigLampModel(String modelName, boolean hanging, ResourceLocation lampCoreTexture) {
        BlockModelBuilder model = models().getBuilder(modelName)
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .ao(false)
                .renderType("cutout")
                .texture("lantern", mcLoc("block/lantern"))
                .texture("metalplate", modLoc("block/plate"))
                .texture("lampcore", lampCoreTexture)
                .texture("connector", modLoc("block/lamp_connector"))
                .texture("particle", modLoc("block/plate"));

        int yOffset = hanging ? 1 : 0;

        // 发光核心
        cube(model, 3, 2 + yOffset, 3, 13, 12 + yOffset, 13, "#lampcore");

        // 底部金属框
        cube(model, 1, yOffset, 1, 15, 2 + yOffset, 3, "#metalplate");
        cube(model, 1, yOffset, 13, 15, 2 + yOffset, 15, "#metalplate");
        cube(model, 1, yOffset, 1, 3, 2 + yOffset, 15, "#metalplate");
        cube(model, 13, yOffset, 1, 15, 2 + yOffset, 15, "#metalplate");

        // 顶部金属框
        cube(model, 1, 12 + yOffset, 1, 15, 14 + yOffset, 3, "#metalplate");
        cube(model, 1, 12 + yOffset, 13, 15, 14 + yOffset, 15, "#metalplate");
        cube(model, 1, 12 + yOffset, 1, 3, 14 + yOffset, 15, "#metalplate");
        cube(model, 13, 12 + yOffset, 1, 15, 14 + yOffset, 15, "#metalplate");

        // 四角立柱
        cube(model, 1, 2 + yOffset, 1, 3, 12 + yOffset, 3, "#metalplate");
        cube(model, 13, 2 + yOffset, 1, 15, 12 + yOffset, 3, "#metalplate");
        cube(model, 1, 2 + yOffset, 13, 3, 12 + yOffset, 15, "#metalplate");
        cube(model, 13, 2 + yOffset, 13, 15, 12 + yOffset, 15, "#metalplate");

        if (hanging) {
            // 顶部悬挂连接件
            cube(model, 6, 14, 6, 10, 16, 10, "#connector");
            cube(model, 7, 11, 7, 9, 16, 9, "#connector");
        }

        return model;
    }

    private void massiveLamp() {
        ModelFile model = massiveLampModel();

        simpleBlock(ModBlocks.MASSIVE_LAMP.get(), model);
        simpleBlockItem(ModBlocks.MASSIVE_LAMP.get(), model);
    }

    private ModelFile massiveLampModel() {
        BlockModelBuilder model = models().getBuilder("the_lamp/massive_lamp")
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .ao(false)
                .renderType("translucent")
                .texture("casing", mcLoc("block/glass"))
                .texture("metalplate", modLoc("block/plate"))
                .texture("lampcore", modLoc("block/the_lamp"))
                .texture("particle", modLoc("block/plate"));

        // 外层玻璃壳：薄面版本，避免完整透明方块造成闪烁
        cube(model, 0, 0, 0, 16, 16, 0.1F, "#casing");
        cube(model, 0, 0, 15.9F, 16, 16, 16, "#casing");
        cube(model, 0, 0, 0, 0.1F, 16, 16, "#casing");
        cube(model, 15.9F, 0, 0, 16, 16, 16, "#casing");
        cube(model, 0, 15.9F, 0, 16, 16, 16, "#casing");
        cube(model, 0, 0, 0, 16, 0.1F, 16, "#casing");

        // 内部发光核心
        cube(model, 3, 3, 3, 13, 13, 13, "#lampcore");

        // 金属框
        cube(model, 1, 1, 1, 15, 3, 3, "#metalplate");
        cube(model, 1, 1, 13, 15, 3, 15, "#metalplate");
        cube(model, 1, 1, 1, 3, 3, 15, "#metalplate");
        cube(model, 13, 1, 1, 15, 3, 15, "#metalplate");

        cube(model, 1, 13, 1, 15, 15, 3, "#metalplate");
        cube(model, 1, 13, 13, 15, 15, 15, "#metalplate");
        cube(model, 1, 13, 1, 3, 15, 15, "#metalplate");
        cube(model, 13, 13, 1, 15, 15, 15, "#metalplate");

        cube(model, 1, 3, 1, 3, 13, 3, "#metalplate");
        cube(model, 13, 3, 1, 15, 13, 3, "#metalplate");
        cube(model, 1, 3, 13, 3, 13, 15, "#metalplate");
        cube(model, 13, 3, 13, 15, 13, 15, "#metalplate");

        return model;
    }

    private static void cube(
            BlockModelBuilder model,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            String texture
    ) {
        model.element()
                .from(x1, y1, z1)
                .to(x2, y2, z2)
                .face(Direction.NORTH).texture(texture).end()
                .face(Direction.EAST).texture(texture).end()
                .face(Direction.SOUTH).texture(texture).end()
                .face(Direction.WEST).texture(texture).end()
                .face(Direction.UP).texture(texture).end()
                .face(Direction.DOWN).texture(texture).end()
                .end();
    }

    private void bigShroomlamp() {
        ModelFile standingLamp = bigLampModel(
                "the_lamp/big_shroomlamp",
                false,
                mcLoc("block/shroomlight")
        );

        ModelFile hangingLamp = bigLampModel(
                "the_lamp/big_hanging_shroomlamp",
                true,
                mcLoc("block/shroomlight")
        );

        getVariantBuilder(ModBlocks.BIG_SHROOMLAMP.get())
                .partialState()
                .with(LanternBlock.HANGING, false)
                .modelForState()
                .modelFile(standingLamp)
                .addModel()
                .partialState()
                .with(LanternBlock.HANGING, true)
                .modelForState()
                .modelFile(hangingLamp)
                .addModel();

        simpleBlockItem(ModBlocks.BIG_SHROOMLAMP.get(), standingLamp);
    }
}