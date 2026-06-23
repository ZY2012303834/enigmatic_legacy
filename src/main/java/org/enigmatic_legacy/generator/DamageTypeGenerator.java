package org.enigmatic_legacy.generator;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * 自定义 DamageType 数据生成器。

 * 作用：
 * 生成：
 * data/enigmatic_legacy/damage_type/darkness.json

 * 注意：
 * 不需要手写 JSON。
 */
public class DamageTypeGenerator implements DataProvider {
    private final PackOutput output;

    public DamageTypeGenerator(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return generateDarknessDamageType(cachedOutput);
    }

    /**
     * 生成虚空珍珠的 darkness 伤害类型。
     */
    private CompletableFuture<?> generateDarknessDamageType(CachedOutput cachedOutput) {
        JsonObject json = new JsonObject();

        /*
         * message_id 对应语言 key：
         * death.attack.enigmatic_legacy.darkness
         * death.attack.enigmatic_legacy.darkness.player
         */
        json.addProperty("message_id", "enigmatic_legacy.darkness");

        /*
         * scaling：
         * when_caused_by_living_non_player 是原版常用写法之一。
         * 这里伤害来源通常是玩家，但黑暗光环伤害本身是固定值。
         */
        json.addProperty("scaling", "when_caused_by_living_non_player");

        /*
         * exhaustion：
         * 影响玩家受伤后的饥饿消耗。
         */
        json.addProperty("exhaustion", 0.1F);

        /*
         * effects：
         * hurt = 普通受伤表现。
         */
        json.addProperty("effects", "hurt");

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(EnigmaticLegacy.MODID)
                .resolve("damage_type")
                .resolve("darkness.json");

        return DataProvider.saveStable(cachedOutput, json, path);
    }

    @Override
    public @NotNull String getName() {
        return "Enigmatic Legacy Damage Types";
    }

    /**
     * 注册到 runData。
     */
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new DamageTypeGenerator(event.getGenerator().getPackOutput())
        );
    }
}