package org.enigmatic_legacy.generator.patchouli;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Patchouli 手册 book.json 数据生成器。
 * 作用：
 * - 只负责生成手册入口 book.json。
 * - 具体分类和条目由 EnglishPatchouliBookGenerator / ChinesePatchouliBookGenerator 生成。
 */
final class PatchouliBookDataGenerator implements DataProvider {
    private final PackOutput.PathProvider dataPathProvider;

    PatchouliBookDataGenerator(PackOutput output) {
        this.dataPathProvider = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                "patchouli_books"
        );
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return DataProvider.saveStable(
                output,
                AbstractPatchouliBookContentGenerator.createBookJson(),
                getDataBookPath()
        );
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Patchouli Book Data";
    }

    /**
     * 返回 book.json 的生成路径。
     */
    private Path getDataBookPath() {
        return this.dataPathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                AbstractPatchouliBookContentGenerator.BOOK + "/book"
        ));
    }
}
