package org.enigmatic_legacy.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * Patchouli 手册数据生成器。
 * 生成：
 * 1. data/enigmatic_legacy/patchouli_books/the_acknowledgment/book.json
 * 2. assets/enigmatic_legacy/patchouli_books/the_acknowledgment/<语言>/categories/*.json
 * 3. assets/enigmatic_legacy/patchouli_books/the_acknowledgment/<语言>/entries/../*.json
 */

public class PatchouliBookGenerator implements DataProvider {
    private static final String BOOK = "the_acknowledgment";

    private final PackOutput.PathProvider dataPathProvider;
    private final PackOutput.PathProvider assetPathProvider;

    public PatchouliBookGenerator(PackOutput output) {
        this.dataPathProvider = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                "patchouli_books"
        );

        this.assetPathProvider = output.createPathProvider(
                PackOutput.Target.RESOURCE_PACK,
                "patchouli_books"
        );
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator()
                .getVanillaPack(event.includeServer())
                .addProvider(PatchouliBookGenerator::new);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(DataProvider.saveStable(
                output,
                createBookJson(),
                getDataBookPath()
        ));

        addEnglishContent(output, futures);
        addChineseContent(output, futures);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Patchouli Books";
    }

    private Path getDataBookPath() {
        return this.dataPathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                BOOK + "/book"
        ));
    }

    private Path getAssetPath(String language, String relativePath) {
        return this.assetPathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                BOOK + "/" + language + "/" + relativePath
        ));
    }

    private void addEnglishContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "en_us", "categories/world", category(
                "The World",
                "Laws, consequences, and hidden truths of this world.",
                "minecraft:grass_block",
                0
        )));

        futures.add(save(output, "en_us", "categories/relics", category(
                "Relics",
                "Ancient artifacts and enigmatic tools.",
                "enigmatic_legacy:the_acknowledgment",
                10
        )));

        futures.add(save(output, "en_us", "entries/world/soul_loss", entry(
                "Soul Loss",
                "world",
                "minecraft:soul_lantern",
                0,
                textPage(
                        "Soul Loss",
                        "Death is rarely without consequence. Some forces return what was lost, but never without reminding you that the world keeps count."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/the_acknowledgment", entry(
                "The Acknowledgment",
                "relics",
                "enigmatic_legacy:the_acknowledgment",
                0,
                spotlightPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "The Acknowledgment",
                        "A strange book that records knowledge of forgotten relics. Right-click to open it."
                ),
                textPage(
                        "Weaponized Knowledge",
                        "Despite being a book, it can be used as a weapon. Struck enemies are set aflame for a short time."
                )
        )));

        futures.add(save(output, "en_us", "entries/relics/enigmatic_eye", entry(
                "Inscrutable Eye",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                1,
                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "Dormant Eye",
                        "The Eye sleeps until awakened. Once awakened, it may be worn as a charm."
                ),
                textPage(
                        "Awakened Sight",
                        "When equipped, the awakened Eye grants an additional charm slot and increases block interaction range."
                )
        )));
    }

    private void addChineseContent(CachedOutput output, List<CompletableFuture<?>> futures) {
        futures.add(save(output, "zh_cn", "categories/world", category(
                "世界",
                "关于此世法则、代价与隐秘真相的记录。",
                "minecraft:grass_block",
                0
        )));

        futures.add(save(output, "zh_cn", "categories/relics", category(
                "遗物",
                "古老遗物与神秘器具的记载。",
                "enigmatic_legacy:the_acknowledgment",
                10
        )));

        futures.add(save(output, "zh_cn", "entries/world/soul_loss", entry(
                "灵魂损耗",
                "world",
                "minecraft:soul_lantern",
                0,
                textPage(
                        "灵魂损耗",
                        "死亡从来不是毫无代价的。有些力量会归还失去之物，但世界始终记得你付出过什么。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/the_acknowledgment", entry(
                "启示之证",
                "relics",
                "enigmatic_legacy:the_acknowledgment",
                0,
                spotlightPage(
                        "enigmatic_legacy:the_acknowledgment",
                        "启示之证",
                        "一本记录被遗忘遗物知识的奇异书册。右键即可翻阅。"
                ),
                textPage(
                        "化作武器的知识",
                        "尽管它是一本书，却同样可以作为武器使用。被击中的敌人会在短时间内燃烧。"
                )
        )));

        futures.add(save(output, "zh_cn", "entries/relics/enigmatic_eye", entry(
                "全知之眼",
                "relics",
                "enigmatic_legacy:enigmatic_eye",
                1,
                spotlightPage(
                        "enigmatic_legacy:enigmatic_eye",
                        "休眠之眼",
                        "此眼沉睡着，直到被唤醒。苏醒后，它可以作为护符佩戴。"
                ),
                textPage(
                        "苏醒之视",
                        "佩戴已唤醒的全知之眼时，会额外提供一个护符栏位，并提高方块交互距离。"
                )
        )));
    }

    private CompletableFuture<?> save(
            CachedOutput output,
            String language,
            String relativePath,
            JsonObject json
    ) {
        return DataProvider.saveStable(
                output,
                json,
                getAssetPath(language, relativePath)
        );
    }

    private static JsonObject createBookJson() {
        JsonObject book = new JsonObject();

        book.addProperty("name", "item.enigmatic_legacy.the_acknowledgment");
        book.addProperty("landing_text", "book.enigmatic_legacy.landing_text");
        book.addProperty("version", "20");
        book.addProperty("use_blocky_font", false);
        book.addProperty("filler_texture", "enigmatic_legacy:textures/gui/page_filler.png");
        book.addProperty("book_texture", "enigmatic_legacy:textures/gui/the_acknowledgment.png");
        book.addProperty("model", "enigmatic_legacy:item/the_acknowledgment");
        book.addProperty("dont_generate_book", true);
        book.addProperty("custom_book_item", "enigmatic_legacy:the_acknowledgment");
        book.addProperty("i18n", true);

        book.addProperty("nameplate_color", "FFAA00");
        book.addProperty("link_color", "00AAAA");
        book.addProperty("link_hover_color", "AA00AA");
        book.addProperty("progress_bar_color", "AA00AA");
        book.addProperty("progress_bar_background", "555555");
        book.addProperty("show_progress", false);

        book.addProperty("show_toasts", true);
        book.addProperty("pause_game", false);
        book.addProperty("text_overflow_mode", "overflow");

        // Patchouli 1.20+：book.json 在 data，实际书页内容在 assets。
        book.addProperty("use_resource_pack", true);

        return book;
    }

    private static JsonObject category(String name, String description, String icon, int sortnum) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("icon", icon);
        json.addProperty("sortnum", sortnum);
        json.addProperty("secret", false);
        return json;
    }

    private static JsonObject entry(
            String name,
            String category,
            String icon,
            int sortnum,
            JsonObject... pages
    ) {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("category", EnigmaticLegacy.MODID + ":" + category);
        json.addProperty("icon", icon);
        json.addProperty("sortnum", sortnum);
        json.addProperty("secret", false);

        JsonArray pageArray = new JsonArray();

        for (JsonObject page : pages) {
            pageArray.add(page);
        }

        json.add("pages", pageArray);

        return json;
    }

    private static JsonObject textPage(String title, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("title", title);
        page.addProperty("text", text);
        return page;
    }

    private static JsonObject spotlightPage(String item, String title, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:spotlight");
        page.addProperty("item", item);
        page.addProperty("title", title);
        page.addProperty("text", text);
        return page;
    }
}