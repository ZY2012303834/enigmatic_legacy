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
import java.util.concurrent.CompletableFuture;

/**
 * Patchouli 手册数据生成器。
 * 生成：
 * src/generated/resources/data/enigmatic_legacy/patchouli_books/the_acknowledgment/book.json
 */
public class PatchouliBookGenerator implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public PatchouliBookGenerator(PackOutput output) {
        this.pathProvider = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
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
        return DataProvider.saveStable(
                output,
                createTheAcknowledgmentBook(),
                getBookPath("the_acknowledgment")
        );
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Patchouli Books";
    }

    private Path getBookPath(String bookName) {
        return this.pathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                bookName + "/book"
        ));
    }

    /**
     * 启示之证 / The Acknowledgment。
     * 结构仿照 Enigmatic Legacy Plus：
     * data/enigmaticlegacyplus/patchouli_books/the_acknowledgment/book.json
     */
    private static JsonObject createTheAcknowledgmentBook() {
        JsonObject book = new JsonObject();

        book.addProperty("name", "item.enigmatic_legacy.the_acknowledgment");
        book.addProperty("landing_text", "book.enigmatic_legacy.landing_text");
        book.addProperty("version", "20");

        // 如果你的创造模式标签 ID 不同，启动报 Unknown creative tab 时先删掉这一行。
        book.addProperty("creative_tab", "enigmatic_legacy:enigmatic_legacy");

        book.addProperty("use_blocky_font", false);
        book.addProperty("filler_texture", "enigmatic_legacy:textures/gui/page_filler.png");
        book.addProperty("book_texture", "enigmatic_legacy:textures/gui/the_acknowledgment.png");

        // Plus 原文件这里是 enigmaticlegacy:the_acknowledgment；
        // 你的项目应使用自己的命名空间。
        book.addProperty("model", "enigmatic_legacy:the_acknowledgment");

        book.addProperty("dont_generate_book", true);
        book.addProperty("custom_book_item", "enigmatic_legacy:the_acknowledgment");
        book.addProperty("i18n", true);

        book.addProperty("nameplate_color", "FFAA00");
        book.addProperty("link_color", "00AAAA");
        book.addProperty("link_hover_color", "AA00AA");
        book.addProperty("progress_bar_color", "AA00AA");
        book.addProperty("progress_bar_background", "555555");
        book.addProperty("show_progress", false);

        book.addProperty("advancements_tab", "enigmatic_legacy:main/root");

        JsonArray advancementNamespaces = new JsonArray();
        advancementNamespaces.add(EnigmaticLegacy.MODID);
        book.add("advancement_namespaces", advancementNamespaces);

        book.addProperty("show_toasts", true);
        book.addProperty("pause_game", false);
        book.addProperty("text_overflow_mode", "overflow");
        book.addProperty("use_resource_pack", true);

        book.add("macros", createMacros());

        return book;
    }

    private static JsonObject createMacros() {
        JsonObject macros = new JsonObject();

        macros.addProperty("<br/>", "$(br)");
        macros.addProperty("<br2/>", "$(br2)");

        macros.addProperty("<&0>", "$(0)");
        macros.addProperty("<&1>", "$(1)");
        macros.addProperty("<&2>", "$(2)");
        macros.addProperty("<&3>", "$(3)");
        macros.addProperty("<&4>", "$(4)");
        macros.addProperty("<&5>", "$(5)");
        macros.addProperty("<&6>", "$(6)");
        macros.addProperty("<&7>", "$(7)");
        macros.addProperty("<&8>", "$(8)");
        macros.addProperty("<&9>", "$(9)");
        macros.addProperty("<&a>", "$(a)");
        macros.addProperty("<&b>", "$(b)");
        macros.addProperty("<&c>", "$(c)");
        macros.addProperty("<&d>", "$(d)");
        macros.addProperty("<&e>", "$(e)");
        macros.addProperty("<&f>", "$(f)");
        macros.addProperty("<&k>", "$(k)");
        macros.addProperty("<&l>", "$(l)");
        macros.addProperty("<&m>", "$(m)");
        macros.addProperty("<&n>", "$(n)");
        macros.addProperty("<&o>", "$(o)");

        macros.addProperty("<clear/>", "$(clear)");
        macros.addProperty("<percent/>", "%");
        macros.addProperty("<playername>", "$(playername)");

        return macros;
    }
}