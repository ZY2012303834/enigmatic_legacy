package org.enigmatic_legacy.generator;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
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
        ModelFile astralDustModel = astralDustModel();
        simpleBlock(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel);
        simpleBlockItem(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel);

        // 以太块六个面使用同一张贴图，cubeAll 可以直接生成最普通的完整方块模型。
        simpleBlockWithItem(ModBlocks.ETHERIUM_BLOCK.get(), cubeAll(ModBlocks.ETHERIUM_BLOCK.get()));
    }

    private ModelFile astralDustModel() {
        // 手写 cube_all 等价的完整立方体模型，但为侧面、顶面、底面指定不同的贴图区域。
        return models().getBuilder(ModBlocks.ASTRAL_DUST_SACK.getId().getPath())
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
    }
}
