package org.enigmatic_legacy.generator.patchouli;

import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Patchouli 手册数据生成器总入口。
 * 作用：
 * 1. 注册 book.json 生成器；
 * 2. 注册英文书页生成器；
 * 3. 注册中文书页生成器。
 * 使用方式：
 * - 在 EnigmaticLegacy.java 中保留：
 *   modEventBus.addListener(PatchouliBookGenerator::gatherData);
 * - 然后运行 gradlew.bat runData。
 */
public final class PatchouliBookGenerator {
    private PatchouliBookGenerator() {
    }

    /**
     * NeoForge 数据生成事件入口。
     */
    public static void gatherData(GatherDataEvent event) {
        // 生成 data/enigmatic_legacy/patchouli_books/the_acknowledgment/book.json
        event.getGenerator()
                .getVanillaPack(event.includeServer())
                .addProvider(PatchouliBookDataGenerator::new);

        // 生成 assets/enigmatic_legacy/patchouli_books/the_acknowledgment/en_us/...
        event.getGenerator()
                .getVanillaPack(event.includeClient())
                .addProvider(EnglishPatchouliBookGenerator::new);

        // 生成 assets/enigmatic_legacy/patchouli_books/the_acknowledgment/zh_cn/...
        event.getGenerator()
                .getVanillaPack(event.includeClient())
                .addProvider(ChinesePatchouliBookGenerator::new);
    }
}
