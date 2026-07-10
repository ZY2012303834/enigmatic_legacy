package org.enigmatic_legacy.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.enigmatic_legacy.EnigmaticLegacy;

/**
 * 本模组的全局战利品修饰器序列化器注册入口。
 *
 * <p>Global Loot Modifier，简称 GLM，是 NeoForge 提供的“战利品表结果后处理”机制。
 * 它不会替换原版战利品表，而是在某张目标战利品表生成结果之后，
 * 根据条件对结果列表进行追加、删除或修改。</p>
 *
 * <p>这里注册的不是某一条具体的战利品注入规则，
 * 而是“如何读取/写出某一种 GLM JSON”的 {@link MapCodec}。
 * 具体每个箱子注入哪张附加表，仍然由
 * {@code data/enigmatic_legacy/loot_modifiers/*.json} 决定。</p>
 */
public final class ModLootModifiers {
    /**
     * NeoForge 的 GLM 序列化器注册表。
     *
     * <p>注册到这个表中的值会成为 JSON 里的 {@code type} 可选项。
     * 例如本类下面注册的 {@code configurable_add_table}，
     * 在数据文件里会写成：</p>
     *
     * <pre>
     * "type": "enigmatic_legacy:configurable_add_table"
     * </pre>
     */
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnigmaticLegacy.MODID);

    /**
     * 可被 {@code CustomDungeonLootEnabled} 控制的 add_table 修饰器。
     *
     * <p>它对应 {@link ConfigurableAddTableLootModifier#CODEC}，
     * 功能上类似 NeoForge 原生的 {@code neoforge:add_table}：
     * 当 {@code conditions} 匹配目标战利品表时，额外掷一张 {@code table}
     * 指定的附加战利品表，并把结果追加进原战利品结果。</p>
     *
     * <p>区别在于：
     * {@link ConfigurableAddTableLootModifier} 会在执行追加前读取
     * {@code ConfigCommon.CUSTOM_DUNGEON_LOOT_ENABLED}。
     * 配置关闭时，所有使用此类型的箱子注入都会原样跳过。</p>
     */
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ConfigurableAddTableLootModifier>> CONFIGURABLE_ADD_TABLE =
            LOOT_MODIFIER_SERIALIZERS.register("configurable_add_table", () -> ConfigurableAddTableLootModifier.CODEC);

    private ModLootModifiers() {
    }

    /**
     * 把本类的 DeferredRegister 挂到模组事件总线上。
     *
     * <p>必须在主模组构造函数中调用，否则游戏加载
     * {@code enigmatic_legacy:configurable_add_table} 类型的 loot modifier JSON 时，
     * 找不到对应 codec，会导致数据包加载失败。</p>
     */
    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
