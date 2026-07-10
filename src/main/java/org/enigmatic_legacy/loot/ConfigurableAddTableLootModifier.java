package org.enigmatic_legacy.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.enigmatic_legacy.config.ConfigCommon;

/**
 * 可被配置开关控制的 AddTable 全局战利品修饰器。
 *
 * <p>NeoForge 自带的 {@code neoforge:add_table} 可以把一张“附加战利品表”
 * 掷骰后追加到目标战利品表结果中，但它不会读取本项目的配置。
 * 本类复刻同样的追加逻辑，并在真正追加前检查
 * {@link ConfigCommon#CUSTOM_DUNGEON_LOOT_ENABLED}。</p>
 *
 * <p>用途：
 * 本项目向地牢、神殿、村庄、末地城、堡垒、第三方结构箱子等战利品表注入
 * 术石、材料、卷轴、特殊饰品等物品时，统一使用这个 modifier。
 * 当配置项关闭时，这些额外箱子战利品不会生成。</p>
 */
public class ConfigurableAddTableLootModifier extends LootModifier {
    /**
     * JSON 编解码器。
     *
     * <p>生成出来的 JSON 结构与 NeoForge 的 add_table 基本一致：</p>
     *
     * <pre>
     * {
     *   "type": "enigmatic_legacy:configurable_add_table",
     *   "conditions": [
     *     {
     *       "condition": "neoforge:loot_table_id",
     *       "loot_table_id": "minecraft:chests/simple_dungeon"
     *     }
     *   ],
     *   "table": "enigmatic_legacy:inject/chests/spellstones/earthen"
     * }
     * </pre>
     *
     * <p>{@code conditions} 由 {@link LootModifier} 基类负责匹配。
     * 只有条件全部通过时，才会进入 {@link #doApply(ObjectArrayList, LootContext)}。
     * {@code table} 是要额外掷骰并追加到结果里的附加战利品表。</p>
     */
    public static final MapCodec<ConfigurableAddTableLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IGlobalLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(modifier -> modifier.conditions),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("table").forGetter(ConfigurableAddTableLootModifier::table)
    ).apply(instance, ConfigurableAddTableLootModifier::new));

    /**
     * 要追加的附加战利品表。
     *
     * <p>它不是目标箱子的表，而是本项目生成在
     * {@code data/enigmatic_legacy/loot_table/inject/chests/...}
     * 下的注入表。</p>
     */
    private final ResourceKey<LootTable> table;

    public ConfigurableAddTableLootModifier(LootItemCondition[] conditions, ResourceKey<LootTable> table) {
        super(conditions);
        this.table = table;
    }

    public ResourceKey<LootTable> table() {
        return this.table;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        /*
         * 配置开关的实际生效点。
         *
         * 如果 CustomDungeonLootEnabled = false：
         * - 原版/其它模组战利品表仍照常生成；
         * - 本 modifier 不再掷本项目的附加表；
         * - 已经由目标表生成出的原始 generatedLoot 原样返回。
         */
        if (!ConfigCommon.CUSTOM_DUNGEON_LOOT_ENABLED.get()) {
            return generatedLoot;
        }

        /*
         * 从当前 loot context 的解析器中取出附加战利品表。
         *
         * 这里使用 getRandomItemsRaw，而不是 getRandomItems：
         * - getRandomItems 会再次触发全局战利品修饰器；
         * - 附加表如果再次被其它 GLM 处理，可能导致重复修改或递归式追加；
         * - NeoForge 自带 AddTableLootModifier 也是使用 raw 方式处理子表。
         *
         * LootTable.createStackSplitter 会正确处理堆叠拆分，然后把结果加入
         * generatedLoot。也就是说，本方法直接修改并返回同一个战利品列表。
         */
        context.getResolver().get(Registries.LOOT_TABLE, this.table).ifPresent(extraTable ->
                extraTable.value().getRandomItemsRaw(
                        context,
                        LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)
                )
        );

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        /*
         * 告诉 NeoForge：序列化/反序列化本 modifier 时使用哪个注册的 codec。
         * 对应 JSON 中的 type = enigmatic_legacy:configurable_add_table。
         */
        return ModLootModifiers.CONFIGURABLE_ADD_TABLE.get();
    }
}
