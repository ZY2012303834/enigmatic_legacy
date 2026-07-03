package org.enigmatic_legacy.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdvancementGenerator implements DataProvider {
    private static final String ROOT = EnigmaticLegacy.MODID + ":main/root";

    private final PackOutput output;

    public AdvancementGenerator(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (AdvancementSpec advancement : advancements()) {
            futures.add(saveAdvancement(cachedOutput, advancement));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> saveAdvancement(CachedOutput cachedOutput, AdvancementSpec advancement) {
        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("advancement")
                .resolve(advancement.path() + ".json");

        return DataProvider.saveStable(cachedOutput, advancement.toJson(), path);
    }

    private static List<AdvancementSpec> advancements() {
        String rootPath = "main/root";
        List<AdvancementSpec> advancements = new ArrayList<>();

        advancements.add(AdvancementSpec.root(rootPath, "enigmatic_amulet_red", "root", 0.0F, 0.0F,
                "enigmatic_amulet_red", "enigmatic_amulet_aqua", "enigmatic_amulet_violet", "enigmatic_amulet_magenta",
                "enigmatic_amulet_green", "enigmatic_amulet_black", "enigmatic_amulet_blue"));

        advancements.add(AdvancementSpec.framed("main/astral_dust", ROOT, "astral_dust", "astralDust", -2.0F, 1.0F, "goal"));
        advancements.add(AdvancementSpec.single("main/smelt_etherium", "main/astral_dust", "etherium_ingot", "smeltEtherium", -2.0F, 2.0F));
        advancements.add(AdvancementSpec.single("main/etherium_tool", "main/smelt_etherium", "etherium_axe", "etheriumTool", -3.0F, 3.0F,
                "etherium_sword", "etherium_pickaxe", "etherium_shovel", "etherium_axe"));
        advancements.add(AdvancementSpec.allFramed("main/etherium_gear", "main/smelt_etherium", "etherium_helmet", "etheriumGear", -1.0F, 3.0F, "challenge",
                "etherium_helmet", "etherium_chestplate", "etherium_leggings", "etherium_boots"));
        advancements.add(AdvancementSpec.framed("main/mending_mixture", "main/recall_potion", "mending_mixture", "mendingMixture", -4.0F, 2.0F, "goal"));

        advancements.add(AdvancementSpec.framed("main/recall_potion", ROOT, "recall_potion", "recallPotion", -5.0F, 1.0F, "goal"));
        advancements.add(AdvancementSpec.hidden("main/megasponge", "main/recall_potion", "mega_sponge", "megasponge", -6.0F, 2.0F, "task"));
        advancements.add(AdvancementSpec.hidden("main/forbidden_axe", "main/megasponge", "axe_of_executioner", "forbiddenAxe", -7.0F, 3.0F, "challenge"));
        advancements.add(AdvancementSpec.hidden("main/unholy_grail", "main/megasponge", "unholy_grail", "unholyGrail", -6.0F, 3.0F, "task"));
        advancements.add(AdvancementSpec.framed("main/unholy_grail_worthy", "main/unholy_grail", "unholy_grail", "unholyGrailWorthy", -6.0F, 4.0F, "challenge"));

        advancements.add(AdvancementSpec.single("main/magnet_ring", ROOT, "magnet_ring", "magnetRing", 2.0F, 1.0F));
        advancements.add(AdvancementSpec.single("main/super_magnet_ring", "main/magnet_ring", "dislocation_ring", "superMagnetRing", 2.0F, 2.0F));

        advancements.add(AdvancementSpec.hidden("main/discover_spellstone", ROOT, "golem_heart", "discoverSpellstone", 0.0F, -1.0F, "task",
                "golem_heart", "angel_blessing", "ocean_stone", "blazing_core", "eye_of_nebula", "void_pearl", "the_cube", "heart_of_creation"));
        advancements.add(AdvancementSpec.hidden("main/void_pearl", "main/discover_spellstone", "void_pearl", "voidPearl", -1.0F, -2.0F, "challenge"));
        advancements.add(AdvancementSpec.allFramed("main/all_spellstones", "main/discover_spellstone", "heart_of_creation", "allSpellstones", 1.0F, -2.0F, "challenge",
                "golem_heart", "angel_blessing", "ocean_stone", "blazing_core", "eye_of_nebula", "void_pearl", "the_cube", "heart_of_creation"));
        advancements.add(AdvancementSpec.hidden("main/the_cube", "main/all_spellstones", "the_cube", "theCube", 1.0F, -3.0F, "goal"));

        advancements.add(AdvancementSpec.hidden("main/discover_scroll", ROOT, "thicc_scroll", "discoverScroll", -3.0F, -1.0F, "task",
                "thicc_scroll", "xp_scroll", "heaven_scroll", "cursed_scroll", "fabulous_scroll", "avarice_scroll"));
        advancements.add(AdvancementSpec.hidden("main/heaven_scroll", "main/discover_scroll", "heaven_scroll", "heavenScroll", -3.0F, -2.0F, "task"));
        advancements.add(AdvancementSpec.single("main/fabulous_scroll", "main/heaven_scroll", "fabulous_scroll", "fabulousScroll", -3.0F, -3.0F));

        advancements.add(AdvancementSpec.hidden("main/cursed_ring", ROOT, "cursed_ring", "cursedRing", 4.0F, 1.0F, "task"));
        advancements.add(AdvancementSpec.hidden("main/forbidden_fruit", ROOT, "forbidden_fruit", "forbiddenFruit", 5.0F, -1.0F, "task"));
        advancements.add(AdvancementSpec.framed("main/twisted_heart", "main/cursed_ring", "twisted_heart", "twistedHeart", 4.0F, 2.0F, "goal"));
        advancements.add(AdvancementSpec.framed("main/guardian_heart", "main/cursed_ring", "guardian_heart", "guardianHeart", 5.0F, 2.0F, "challenge"));
        advancements.add(AdvancementSpec.framed("main/infernal_shield", "main/twisted_heart", "infernal_shield", "infernalShield", 3.0F, 3.0F, "goal"));
        advancements.add(AdvancementSpec.framed("main/twisted_mirror", "main/twisted_heart", "twisted_mirror", "twistedMirror", 4.0F, 3.0F, "goal"));
        advancements.add(AdvancementSpec.framed("main/the_twist", "main/twisted_heart", "the_twist", "theTwist", 5.0F, 3.0F, "challenge"));
        advancements.add(AdvancementSpec.framed("main/earth_promise", "main/twisted_heart", "earth_promise", "earthPromise", 6.0F, 3.0F, "goal"));
        advancements.add(AdvancementSpec.framed("main/astral_fruit", "main/cursed_ring", "astral_fruit", "astralFruit", 6.0F, 2.0F, "challenge"));
        advancements.add(AdvancementSpec.framed("main/abyssal_heart", "main/cursed_ring", "abyssal_heart", "abyssalHeart", 7.0F, 2.0F, "challenge"));
        advancements.add(AdvancementSpec.single("main/the_infinitum", "main/abyssal_heart", "the_infinitum", "theInfinitum", 7.0F, 3.0F));

        return advancements;
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Advancements";
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new AdvancementGenerator(event.getGenerator().getPackOutput())
        );
    }

    private record AdvancementSpec(String path, String parent, String icon, String key, float x, float y,
                                   String frame, boolean hidden, boolean showToast, boolean announceToChat,
                                   List<String> items, boolean requireAll) {
        private static AdvancementSpec root(String path, String icon, String key, float x, float y, String... items) {
            return new AdvancementSpec(path, null, icon, key, x, y, "task", false, false, false, itemList(icon, items), false);
        }

        private static AdvancementSpec single(String path, String parent, String icon, String key, float x, float y, String... items) {
            return new AdvancementSpec(path, normalizeParent(parent), icon, key, x, y, "task", false, true, true, itemList(icon, items), false);
        }

        private static AdvancementSpec framed(String path, String parent, String icon, String key, float x, float y, String frame, String... items) {
            return new AdvancementSpec(path, normalizeParent(parent), icon, key, x, y, frame, false, true, true, itemList(icon, items), false);
        }

        private static AdvancementSpec hidden(String path, String parent, String icon, String key, float x, float y, String frame) {
            return new AdvancementSpec(path, normalizeParent(parent), icon, key, x, y, frame, true, true, true, List.of(icon), false);
        }

        private static AdvancementSpec hidden(String path, String parent, String icon, String key, float x, float y, String frame, String... items) {
            return new AdvancementSpec(path, normalizeParent(parent), icon, key, x, y, frame, true, true, true, itemList(icon, items), false);
        }

        private static AdvancementSpec allFramed(String path, String parent, String icon, String key, float x, float y, String frame, String... items) {
            return new AdvancementSpec(path, normalizeParent(parent), icon, key, x, y, frame, false, true, true, itemList(icon, items), true);
        }

        private JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (parent != null) {
                json.addProperty("parent", parent);
            }

            json.add("display", display());
            json.add("criteria", criteria());
            json.add("requirements", requirements());

            return json;
        }

        private JsonObject display() {
            JsonObject display = new JsonObject();
            JsonObject iconJson = new JsonObject();
            iconJson.addProperty("id", itemId(icon));
            display.add("icon", iconJson);
            display.add("title", translation(key.equals("root") ? "advancementTab." + EnigmaticLegacy.MODID : "advancement." + EnigmaticLegacy.MODID + ":" + key));
            display.add("description", translation(key.equals("root") ? "advancementTab." + EnigmaticLegacy.MODID + ".desc" : "advancement." + EnigmaticLegacy.MODID + ":" + key + ".desc"));
            if (parent == null) {
                display.addProperty("background", "minecraft:textures/block/end_stone_bricks.png");
            }
            display.addProperty("frame", frame);
            display.addProperty("show_toast", showToast);
            /*
             * 本项目进度聊天提示改为由 EnigmaticAdvancementEvents 手动发送。
             *
             * 原因：
             * 原版进度聊天里的 [ ] 括号颜色不能通过 advancement JSON 单独控制；
             * 所以这里关闭原版聊天广播，避免出现两条重复提示。
             */
            display.addProperty("announce_to_chat", false);
            display.addProperty("hidden", hidden);
            display.addProperty("x", x);
            display.addProperty("y", y);
            return display;
        }

        private JsonObject criteria() {
            JsonObject criteria = new JsonObject();

            /*
             * “前途黑暗”进度特殊处理：
             *
             * 原来是 inventory_changed，只要背包里有七咒之戒就会触发。
             * 现在改为 minecraft:impossible，让 JSON 自身永远不会自动完成。
             * 实际完成逻辑交给 EnigmaticAdvancementEvents：
             * 只有玩家真正佩戴七咒之戒时才授予。
             */
            if ("cursedRing".equals(key)) {
                JsonObject criterion = new JsonObject();
                criterion.addProperty("trigger", "minecraft:impossible");
                criteria.add("equipped_cursed_ring", criterion);
                return criteria;
            }

            /*
             * “不听老人言”需要玩家真正喝完不洁圣杯才触发。
             * 这里禁止 inventory_changed 自动完成，实际授予交给 UnholyGrail。
             */
            if ("unholyGrail".equals(key)) {
                JsonObject criterion = new JsonObject();
                criterion.addProperty("trigger", "minecraft:impossible");
                criteria.add("drank_unholy_grail", criterion);
                return criteria;
            }

            if (requireAll) {
                for (String item : items) {
                    criteria.add("has_" + item, inventoryCriterion(List.of(item)));
                }
            } else {
                criteria.add("has_item", inventoryCriterion(items));
            }

            return criteria;
        }

        private JsonArray requirements() {
            JsonArray requirements = new JsonArray();
            JsonArray group = new JsonArray();

            /*
             * “前途黑暗”使用手动授予条件。
             * 必须和 criteria() 里的 equipped_cursed_ring 名称一致。
             */
            if ("cursedRing".equals(key)) {
                group.add("equipped_cursed_ring");
                requirements.add(group);
                return requirements;
            }

            if ("unholyGrail".equals(key)) {
                group.add("drank_unholy_grail");
                requirements.add(group);
                return requirements;
            }

            if (requireAll) {
                for (String item : items) {
                    group.add("has_" + item);
                }
            } else {
                group.add("has_item");
            }

            requirements.add(group);
            return requirements;
        }

        private static JsonObject inventoryCriterion(List<String> itemIds) {
            JsonObject criterion = new JsonObject();
            criterion.addProperty("trigger", "minecraft:inventory_changed");

            JsonObject conditions = new JsonObject();
            JsonArray items = new JsonArray();
            JsonObject itemPredicate = new JsonObject();
            if (itemIds.size() == 1) {
                itemPredicate.addProperty("items", itemId(itemIds.getFirst()));
            } else {
                JsonArray values = new JsonArray();
                for (String item : itemIds) {
                    values.add(itemId(item));
                }
                itemPredicate.add("items", values);
            }
            items.add(itemPredicate);
            conditions.add("items", items);
            criterion.add("conditions", conditions);

            return criterion;
        }

        private static JsonObject translation(String key) {
            JsonObject text = new JsonObject();
            text.addProperty("translate", key);
            return text;
        }

        private static String normalizeParent(String parent) {
            if (parent.contains(":")) {
                return parent;
            }
            return EnigmaticLegacy.MODID + ":" + parent;
        }

        private static List<String> itemList(String icon, String[] items) {
            if (items.length == 0) {
                return List.of(icon);
            }

            return List.of(items);
        }

        private static String itemId(String path) {
            if (path.contains(":")) {
                return path;
            }
            return EnigmaticLegacy.MODID + ":" + path;
        }
    }
}
