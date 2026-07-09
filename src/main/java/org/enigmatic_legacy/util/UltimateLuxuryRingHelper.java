package org.enigmatic_legacy.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.api.CursedRingApi;
import org.enigmatic_legacy.item.ModItems;

import java.util.Optional;

/**
 * 极尽奢华之戒查询与通用计算。
 *
 * <p>事件类和物品类都可能需要判断“玩家是否真正能使用极尽奢华之戒”。</p>
 * <p>这里统一封装 Curios 查询、七咒资格判断和宝石数量增伤计算，避免各处写出略有差异的判定。</p>
 */
public final class UltimateLuxuryRingHelper {
    /**
     * NeoForge 通用宝石标签。
     *
     * <p>原拓展使用 Forge 的 {@code Tags.Items.GEMS}。</p>
     * <p>1.21.1 NeoForge 数据标签使用 {@code c:gems}，这里直接按标签统计，方便兼容其他模组新增的宝石。</p>
     */
    private static final TagKey<Item> GEMS = commonItemTag("gems");

    /**
     * 原拓展默认增伤系数。
     *
     * <p>最终增伤不是线性叠加，而是 {@code 0.5 * log(1 + 宝石数量) / log(64)}。</p>
     * <p>这意味着携带 64 个宝石时约为 +50% 伤害，继续堆叠仍会增加，但收益逐渐递减。</p>
     */
    private static final double DAMAGE_BOOST_MULTIPLIER = 0.5D;

    private UltimateLuxuryRingHelper() {
    }

    /**
     * 查找实体身上装备的极尽奢华之戒。
     *
     * <p>该方法只负责“是否存在这个物品”的 Curios 查询，不检查七咒资格。</p>
     * <p>如果调用方要判断实际效果是否应该生效，应使用 {@link #hasRing(LivingEntity)}。</p>
     *
     * @param entity 需要查询 Curios 装备的实体
     * @return 找到的戒指 ItemStack；没有佩戴时返回空 Optional
     */
    public static Optional<ItemStack> findRing(LivingEntity entity) {
        return CuriosLookupApi.findFirstStack(entity, ModItems.ULTIMATE_LUXURY_RING.get());
    }

    /**
     * 判断实体是否正在有效佩戴极尽奢华之戒。
     *
     * <p>极尽奢华之戒是七咒限定物品，所以不能只看 Curios 里有没有这个物品。</p>
     * <p>这里通过 {@link CursedRingApi#canUseRestrictedCurio(Player, net.minecraft.world.level.ItemLike)}
     * 同时确认玩家佩戴七咒之戒，并且确实佩戴极尽奢华之戒。</p>
     *
     * @param entity 待检查实体
     * @return 只有玩家满足七咒资格且佩戴戒指时才返回 true
     */
    public static boolean hasRing(LivingEntity entity) {
        return entity instanceof Player player
                && CursedRingApi.canUseRestrictedCurio(player, ModItems.ULTIMATE_LUXURY_RING.get());
    }

    /**
     * 判断玩家是否同时拥有极尽奢华之戒和无尽贪婪契约的有效效果。
     *
     * <p>原拓展中，极尽奢华之戒的“携带宝石提高伤害”只在同时佩戴无尽贪婪契约时启用。</p>
     * <p>这个方法集中表达该联动条件，避免事件类里重复两套 Curios/七咒判断。</p>
     *
     * @param player 待检查玩家
     * @return 两件物品都能生效时返回 true
     */
    public static boolean hasRingAndPact(Player player) {
        return hasRing(player) && PactOfInfiniteAvariceHelper.hasPact(player);
    }

    /**
     * 根据玩家背包中的宝石数量计算极尽奢华之戒提供的伤害加成。
     *
     * <p>该值是倍率增量，例如返回 {@code 0.5F} 表示最终伤害乘以 {@code 1.5}。</p>
     * <p>调用方仍需要先判断联动条件；此方法只负责计算数值，不判断戒指或契约是否佩戴。</p>
     *
     * @param player 用于统计背包宝石数量的玩家
     * @return 伤害倍率增量
     */
    public static float getDamageBoost(Player player) {
        int gemCount = countGems(player);
        return (float) (DAMAGE_BOOST_MULTIPLIER * Math.log1p(gemCount) / Math.log(64.0D));
    }

    /**
     * 统计玩家主背包、快捷栏和装备栏内所有 {@code c:gems} 物品数量。
     *
     * <p>这里使用 {@link Inventory#getContainerSize()} 遍历整个玩家 Inventory。</p>
     * <p>原拓展统计的是玩家 inventory compartments；在当前版本中，这个容器遍历方式覆盖玩家背包中的常规格位。</p>
     *
     * @param player 待统计玩家
     * @return 背包内宝石物品总数量
     */
    private static int countGems(Player player) {
        Inventory inventory = player.getInventory();
        int count = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (stack.is(GEMS)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    /**
     * 构造 NeoForge 通用物品标签。
     *
     * <p>把 {@code c:gems}、{@code c:gems/emerald} 这类兼容标签集中生成，避免调用处手写 ResourceLocation。</p>
     *
     * @param path {@code c} 命名空间下的标签路径
     * @return 对应的物品标签 key
     */
    private static TagKey<Item> commonItemTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
