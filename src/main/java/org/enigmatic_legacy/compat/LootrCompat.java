package org.enigmatic_legacy.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.item.items.charm.EnigmaticEye;

import java.util.List;
import java.util.Map;

/**
 * Lootr 兼容逻辑。
 *
 * <p>Lootr 会把原版箱子替换成玩家独立战利品箱，
 * 原本基于原版战利品表注入的物品不会自动进入 Lootr 容器。
 * 这里在玩家打开 Lootr 容器时手动追加本项目的注入战利品。</p>
 */
@SuppressWarnings("UnstableApiUsage")
public final class LootrCompat {
    public static final String MODID = "lootr";

    private static final String HAS_GENERATED_DORMANT_EYE_TAG =
            "EnigmaticLegacyHasGeneratedDormantEye";
    private static final String LOOTR_INJECTED_TAG =
            "EnigmaticLegacyLootrInjected";

    private static final ResourceLocation LOOTR_ELYTRA_TABLE =
            ResourceLocation.fromNamespaceAndPath("lootr", "chests/elytra");

    private static final Map<ResourceLocation, List<String>> INJECT_TABLES = Map.ofEntries(
            entry("minecraft", "chests/end_city_treasure",
                    "inject/chests/mending_mixture/end_city_treasure",
                    "inject/chests/etherium_ore/end_city_treasure",
                    "inject/chests/majestic_elytra/end_city_treasure",
                    "inject/chests/astral_fruit/end_city_treasure",
                    "inject/chests/astral_dust/end_city_treasure",
                    "inject/chests/spellstones/ender"),
            Map.entry(LOOTR_ELYTRA_TABLE, List.of(
                    "inject/chests/mending_mixture/end_city_treasure",
                    "inject/chests/etherium_ore/end_city_treasure",
                    "inject/chests/majestic_elytra/end_city_treasure",
                    "inject/chests/astral_fruit/end_city_treasure",
                    "inject/chests/astral_dust/end_city_treasure",
                    "inject/chests/spellstones/ender")),
            entry("minecraft", "chests/bastion_bridge",
                    "inject/chests/forbidden_fruit/bastion_common",
                    "inject/chests/spellstones/nether"),
            entry("minecraft", "chests/bastion_hoglin_stable",
                    "inject/chests/forbidden_fruit/bastion_common",
                    "inject/chests/spellstones/nether"),
            entry("minecraft", "chests/bastion_other",
                    "inject/chests/forbidden_fruit/bastion_common",
                    "inject/chests/spellstones/nether"),
            entry("minecraft", "chests/bastion_treasure",
                    "inject/chests/darkest_scroll/bastion_treasure",
                    "inject/chests/spellstones/nether"),
            entry("minecraft", "chests/desert_pyramid",
                    "inject/chests/spellstones/air_earthen",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/jungle_temple",
                    "inject/chests/spellstones/air_earthen",
                    "inject/chests/revival_leaf/jungle_temple",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/stronghold_corridor",
                    "inject/chests/spellstones/ender_earthen",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/stronghold_crossing",
                    "inject/chests/spellstones/ender_earthen",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/village/village_temple",
                    "inject/chests/spellstones/air",
                    "inject/chests/redemption_potion/village"),
            entry("minecraft", "chests/village/village_armorer",
                    "inject/chests/spellstones/earthen",
                    "inject/chests/redemption_potion/village"),
            entry("minecraft", "chests/simple_dungeon",
                    "inject/chests/spellstones/earthen",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/abandoned_mineshaft",
                    "inject/chests/spellstones/earthen",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/nether_bridge",
                    "inject/chests/spellstones/nether"),
            entry("minecraft", "chests/ruined_portal",
                    "inject/chests/spellstones/nether"),
            entry("minecraft", "chests/underwater_ruin_big",
                    "inject/chests/spellstones/water",
                    "inject/chests/unholy_grail/overworld_epic_without_earth_heart"),
            entry("minecraft", "chests/underwater_ruin_small",
                    "inject/chests/spellstones/water",
                    "inject/chests/unholy_grail/overworld_epic_without_earth_heart"),
            entry("minecraft", "chests/shipwreck_treasure",
                    "inject/chests/spellstones/water"),
            entry("minecraft", "chests/buried_treasure",
                    "inject/chests/spellstones/water"),
            entry("minecraft", "chests/igloo_chest",
                    "inject/chests/spellstones/ice",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/ancient_city_ice_box",
                    "inject/chests/spellstones/ice"),
            entry("minecraft", "chests/woodland_mansion",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/shipwreck_supply",
                    "inject/chests/earth_heart/overworld_epic",
                    "inject/chests/earth_heart_fragment/overworld_epic",
                    "inject/chests/unholy_grail/overworld_epic"),
            entry("minecraft", "chests/pillager_outpost",
                    "inject/chests/unholy_grail/overworld_epic_without_earth_heart"),
            dungeonsAriseHeavenlyEntry(0),
            dungeonsAriseHeavenlyEntry(1),
            dungeonsAriseHeavenlyEntry(2),
            dungeonsAriseFoundryNormalEntry(0),
            dungeonsAriseFoundryNormalEntry(1),
            dungeonsAriseFoundryTreasureEntry(0)
    );

    private LootrCompat() {
    }

    /**
     * 仅在 Lootr 已加载时注册兼容事件。
     */
    public static void registerEventHandlers() {
        if (ModList.get().isLoaded(MODID)) {
            NeoForge.EVENT_BUS.register(LootrCompat.class);
        }
    }

    /**
     * 判断方块实体是否来自 Lootr。
     *
     * <p>用于避免本项目的原版战利品箱逻辑和 Lootr 独立箱子逻辑重复处理同一个容器。</p>
     */
    public static boolean isLootrBlockEntity(BlockEntity blockEntity) {
        return blockEntity != null
                && blockEntity.getClass().getName().startsWith("noobanidus.mods.lootr.");
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getContainer() instanceof ChestMenu menu)
                || !(menu.getContainer() instanceof ILootrInventory inventory)) {
            return;
        }

        addConfiguredLoot(player, inventory);
        addDormantEye(player, inventory);

        inventory.setChanged();
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }

    private static void addConfiguredLoot(ServerPlayer player, ILootrInventory inventory) {
        ResourceKey<LootTable> sourceTable = inventory.getInfo().getInfoLootTable();

        if (sourceTable == null || hasInjectedLootrContainer(player, inventory)) {
            return;
        }

        List<String> injectTables = INJECT_TABLES.get(sourceTable.location());
        if (injectTables == null || injectTables.isEmpty()) {
            return;
        }

        ServerLevel level = player.serverLevel();
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, inventory.getInfo().getInfoVec())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);

        for (String injectPath : injectTables) {
            ResourceKey<LootTable> injectKey = ResourceKey.create(
                    Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, injectPath)
            );
            LootTable table = level.getServer().reloadableRegistries().getLootTable(injectKey);

            if (table != LootTable.EMPTY) {
                table.getRandomItems(params, player.getRandom().nextLong(), stack -> insertOrReplace(player, inventory, stack));
            }
        }

        markLootrContainerInjected(player, inventory);
    }

    private static void addDormantEye(ServerPlayer player, ILootrInventory inventory) {
        if (player.getPersistentData().getBoolean(HAS_GENERATED_DORMANT_EYE_TAG)) {
            return;
        }

        ItemStack eye = new ItemStack(ModItems.ENIGMATIC_EYE.get());
        EnigmaticEye.setDormant(eye, true);
        insertOrReplace(player, inventory, eye);
        player.getPersistentData().putBoolean(HAS_GENERATED_DORMANT_EYE_TAG, true);
    }

    private static void insertOrReplace(Player player, Container container, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).isEmpty()) {
                container.setItem(slot, stack.copy());
                return;
            }
        }

        int slot = player.getRandom().nextInt(container.getContainerSize());
        container.setItem(slot, stack.copy());
    }

    private static boolean hasInjectedLootrContainer(ServerPlayer player, ILootrInventory inventory) {
        return player.getPersistentData()
                .getCompound(LOOTR_INJECTED_TAG)
                .getBoolean(inventory.getInfo().getInfoUUID().toString());
    }

    private static void markLootrContainerInjected(ServerPlayer player, ILootrInventory inventory) {
        CompoundTag data = player.getPersistentData();
        CompoundTag injected = data.getCompound(LOOTR_INJECTED_TAG);
        injected.putBoolean(inventory.getInfo().getInfoUUID().toString(), true);
        data.put(LOOTR_INJECTED_TAG, injected);
    }

    private static Map.Entry<ResourceLocation, List<String>> entry(String namespace, String path, String... injectPaths) {
        return Map.entry(ResourceLocation.fromNamespaceAndPath(namespace, path), List.of(injectPaths));
    }

    private static Map.Entry<ResourceLocation, List<String>> dungeonsAriseHeavenlyEntry(int index) {
        return Map.entry(
                DungeonsAriseCompat.HEAVENLY_ANGEL_BLESSING_CHEST_TABLES.get(index),
                List.of(DungeonsAriseCompat.HEAVENLY_CHALLENGER_ANGEL_BLESSING_INJECT)
        );
    }

    private static Map.Entry<ResourceLocation, List<String>> dungeonsAriseFoundryNormalEntry(int index) {
        return Map.entry(
                DungeonsAriseCompat.FOUNDRY_NORMAL_CHEST_TABLES.get(index),
                DungeonsAriseCompat.FOUNDRY_NORMAL_INJECTS
        );
    }

    private static Map.Entry<ResourceLocation, List<String>> dungeonsAriseFoundryTreasureEntry(int index) {
        return Map.entry(
                DungeonsAriseCompat.FOUNDRY_TREASURE_CHEST_TABLES.get(index),
                DungeonsAriseCompat.FOUNDRY_TREASURE_INJECTS
        );
    }
}
