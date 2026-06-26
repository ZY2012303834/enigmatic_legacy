package org.enigmatic_legacy.generator.patchouli;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/**
 * Patchouli ????????????
 */
abstract class AbstractPatchouliBookContentGenerator implements DataProvider {
    protected static final String BOOK = "the_acknowledgment";
    private final PackOutput.PathProvider assetPathProvider;
    protected AbstractPatchouliBookContentGenerator(PackOutput output) {
        this.assetPathProvider = output.createPathProvider(
                PackOutput.Target.RESOURCE_PACK,
                "patchouli_books"
        );
    }
    @Override
    public final @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        addContent(output, futures);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
    protected abstract void addContent(CachedOutput output, List<CompletableFuture<?>> futures);
    private Path getAssetPath(String language, String relativePath) {
        return this.assetPathProvider.json(ResourceLocation.fromNamespaceAndPath(
                EnigmaticLegacy.MODID,
                BOOK + "/" + language + "/" + relativePath
        ));
    }
    protected CompletableFuture<?> save(
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
    protected void addCategories(
            CachedOutput output,
            List<CompletableFuture<?>> futures,
            String lang,
            String worldName,
            String worldDesc,
            String materialsName,
            String materialsDesc,
            String relicsName,
            String relicsDesc
    ) {
        futures.add(save(output, lang, "categories/world", category(
                worldName,
                worldDesc,
                "minecraft:grass_block",
                0
        )));

        futures.add(save(output, lang, "categories/materials", category(
                materialsName,
                materialsDesc,
                "enigmatic_legacy:astral_dust",
                10
        )));

        futures.add(save(output, lang, "categories/relics", category(
                relicsName,
                relicsDesc,
                "enigmatic_legacy:the_acknowledgment",
                20
        )));
    }
    protected static JsonObject createBookJson() {
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
    protected static JsonObject category(String name, String description, String icon, int sortnum) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("icon", icon);
        json.addProperty("sortnum", sortnum);
        json.addProperty("secret", false);
        return json;
    }
    protected static JsonObject simpleSpotlight(
            String name,
            String category,
            String icon,
            int sortnum,
            String text
    ) {
        return entry(
                name,
                category,
                icon,
                sortnum,
                spotlightPage(icon, name, text)
        );
    }
    protected static JsonObject recipeEntry(
            String name,
            String category,
            String icon,
            int sortnum,
            String introText,
            String recipe
    ) {
        return entry(
                name,
                category,
                icon,
                sortnum,
                spotlightPage(icon, name, introText),
                craftingPage(recipe, "合成配方 / Crafting Recipe")
        );
    }
    protected static JsonObject entry(
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
        json.addProperty("read_by_default", true);

        JsonArray pageArray = new JsonArray();

        for (JsonObject page : pages) {
            pageArray.add(page);
        }

        json.add("pages", pageArray);

        return json;
    }
    protected static JsonObject textPage(String title, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("title", title);
        page.addProperty("text", text);
        return page;
    }
    protected static JsonObject spotlightPage(String item, String title, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:spotlight");
        page.addProperty("item", item);
        page.addProperty("link_recipe", true);
        page.addProperty("title", title);
        page.addProperty("text", text);
        return page;
    }
    protected static JsonObject craftingPage(String recipe, String text) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:crafting");
        page.addProperty("recipe", recipe);
        page.addProperty("title", " ");
        page.addProperty("text", text);
        return page;
    }
}
