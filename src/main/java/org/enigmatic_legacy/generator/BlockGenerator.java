package org.enigmatic_legacy.generator;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
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
        // 绑定当前模组 ID，生成的 blockstates/models 会落到 enigmatic_legacy 命名空间下。
        super(output, EnigmaticLegacy.MODID, existingFileHelper);
    }

    public static void gatherData(GatherDataEvent event) {
        // 方块状态、方块模型和方块物品模型都属于客户端资源，只在 includeClient 为 true 时生成。
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(output ->
                new BlockGenerator(output, event.getExistingFileHelper()));
    }

    @Override
    protected void registerStatesAndModels() {
        // 袋装星尘需要每个面的 UV 不同，先构建自定义模型，再分别注册方块状态和物品模型。
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
        simpleBlock(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel); // 袋装星尘
        simpleBlockItem(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel); //袋装星尘物品

        simpleBlockWithItem(ModBlocks.ETHERIUM_BLOCK.get(), cubeAll(ModBlocks.ETHERIUM_BLOCK.get()));   // 以太块

        //大灯笼
        getVariantBuilder(ModBlocks.BIG_LAMP.get())
                .partialState()
                .with(LanternBlock.HANGING, false)
                .modelForState()
                .modelFile(bigLampModel())
                .addModel()
                .partialState()
                .with(LanternBlock.HANGING, true)
                .modelForState()
                .modelFile(bigHangingLampModel())
                .addModel();

        simpleBlockItem(ModBlocks.BIG_LAMP.get(), bigLampModel());  //大灯笼
    }

    private ModelFile bigLampModel() {
        BlockModelBuilder model = models().getBuilder("the_lamp/big_lamp")
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .ao(false)
                .renderType("cutout")
                .texture("lantern", mcLoc("block/lantern"))
                .texture("metalplate", modLoc("block/plate"))
                .texture("lampcore", modLoc("block/the_lamp"))
                .texture("particle", modLoc("block/plate"));

        cube(model, 3, 2, 3, 13, 12, 13, "#lampcore");

        cube(model, 1, 0, 1, 15, 2, 3, "#metalplate");
        cube(model, 1, 0, 13, 15, 2, 15, "#metalplate");
        cube(model, 1, 0, 1, 3, 2, 15, "#metalplate");
        cube(model, 13, 0, 1, 15, 2, 15, "#metalplate");

        cube(model, 1, 12, 1, 15, 14, 3, "#metalplate");
        cube(model, 1, 12, 13, 15, 14, 15, "#metalplate");
        cube(model, 1, 12, 1, 3, 14, 15, "#metalplate");
        cube(model, 13, 12, 1, 15, 14, 15, "#metalplate");

        cube(model, 1, 2, 1, 3, 12, 3, "#metalplate");
        cube(model, 13, 2, 1, 15, 12, 3, "#metalplate");
        cube(model, 1, 2, 13, 3, 12, 15, "#metalplate");
        cube(model, 13, 2, 13, 15, 12, 15, "#metalplate");

        cube(model, 5, 0, 5, 11, 2, 11, "#lantern");

        return model;
    }

    private ModelFile bigHangingLampModel() {
        BlockModelBuilder model = models().getBuilder("the_lamp/big_hanging_lamp")
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .ao(false)
                .renderType("cutout")
                .texture("lantern", mcLoc("block/lantern"))
                .texture("metalplate", modLoc("block/plate"))
                .texture("lampcore", modLoc("block/the_lamp"))
                .texture("particle", modLoc("block/plate"));

        cube(model, 3, 3, 3, 13, 13, 13, "#lampcore");

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

        cube(model, 5, 13, 5, 11, 14, 11, "#lantern");

        // 悬挂部分：用两个薄片模拟灯笼挂钩/吊链
        model.element()
                .from(6.5F, 12F, 8F)
                .to(9.5F, 16F, 8F)
                .shade(false)
                .rotation()
                .angle(45F)
                .axis(Direction.Axis.Y)
                .origin(8F, 9F, 8F)
                .end()
                .face(Direction.NORTH).uvs(11F, 1F, 14F, 5F).texture("#lantern").end()
                .face(Direction.SOUTH).uvs(11F, 1F, 14F, 5F).texture("#lantern").end()
                .end();

        model.element()
                .from(8F, 11F, 6.5F)
                .to(8F, 17F, 9.5F)
                .shade(false)
                .rotation()
                .angle(45F)
                .axis(Direction.Axis.Y)
                .origin(8F, 9F, 8F)
                .end()
                .face(Direction.EAST).uvs(11F, 6F, 14F, 12F).texture("#lantern").end()
                .face(Direction.WEST).uvs(11F, 6F, 14F, 12F).texture("#lantern").end()
                .end();

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

}
