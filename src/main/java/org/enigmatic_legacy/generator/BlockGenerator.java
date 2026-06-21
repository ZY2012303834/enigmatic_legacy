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
        super(output, EnigmaticLegacy.MODID, existingFileHelper);
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().getVanillaPack(event.includeClient()).addProvider(output ->
                new BlockGenerator(output, event.getExistingFileHelper()));
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile astralDustModel = astralDustModel();
        simpleBlock(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel);
        simpleBlockItem(ModBlocks.ASTRAL_DUST_SACK.get(), astralDustModel);
        simpleBlockWithItem(ModBlocks.ETHERIUM_BLOCK.get(), cubeAll(ModBlocks.ETHERIUM_BLOCK.get()));
    }

    private ModelFile astralDustModel() {
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
