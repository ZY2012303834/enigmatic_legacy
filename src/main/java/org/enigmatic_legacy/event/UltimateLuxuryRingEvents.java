package org.enigmatic_legacy.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.enigmatic_legacy.util.PactOfInfiniteAvariceHelper;
import org.enigmatic_legacy.util.UltimateLuxuryRingHelper;

/**
 * 极尽奢华之戒事件逻辑。
 *
 * <p>物品本体只负责 Curios 装备与 tooltip；所有会影响世界状态的行为都集中在这里。</p>
 * <p>该实现复刻 Enigmatic Addons 的 Avarice Ring，并按当前项目已有的事件风格拆分。</p>
 */
public final class UltimateLuxuryRingEvents {
    /**
     * 原拓展中袭击者会在 8 格范围内优先锁定佩戴者。
     *
     * <p>实际判断还会乘以可见度，并保留 5 格保底范围，避免完全隐身时行为过于突兀。</p>
     */
    private static final double RAIDER_TARGET_RANGE = 8.0D;

    private UltimateLuxuryRingEvents() {
    }

    /**
     * 方块掉落时提供额外时运等级。
     *
     * <p>NeoForge 的 {@link BlockDropsEvent} 在方块掉落已经计算完成后触发。</p>
     * <p>为了模拟“工具多 1 级时运”，这里复制当前工具、临时提高时运等级，然后重新计算方块掉落并替换事件掉落列表。</p>
     * <p>优先级设为 LOWEST，是为了在无尽贪婪契约也修改掉落时，最后按总加成重新结算：</p>
     * <p>单独佩戴极尽奢华之戒为 +1；同时佩戴无尽贪婪契约时为 +2。</p>
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }

        if (!UltimateLuxuryRingHelper.hasRing(player)) {
            return;
        }

        if (event.getTool().isEmpty()) {
            return;
        }

        int extraFortune = PactOfInfiniteAvariceHelper.hasPact(player) ? 2 : 1;
        ItemStack boostedTool = copyToolWithExtraFortune(player, event.getTool(), extraFortune);

        var boostedDrops = net.minecraft.world.level.block.Block.getDrops(
                event.getState(),
                event.getLevel(),
                event.getPos(),
                event.getBlockEntity(),
                player,
                boostedTool
        );

        event.getDrops().clear();

        for (ItemStack drop : boostedDrops) {
            if (drop.isEmpty()) {
                continue;
            }

            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                    event.getLevel(),
                    event.getPos().getX() + 0.5D,
                    event.getPos().getY() + 0.5D,
                    event.getPos().getZ() + 0.5D,
                    drop
            ));
        }
    }

    /**
     * 阻止商人类生物主动把佩戴者设为攻击目标。
     *
     * <p>原拓展描述为“猪灵以外的可交易单位不会主动以你为目标”。</p>
     * <p>这里使用原版 {@link Merchant} 接口做弱判断，可以覆盖村民、流浪商人，以及实现 Merchant 的外部模组商人。</p>
     * <p>猪灵相关的中立/交易加成已经由无尽贪婪契约负责，因此这里不处理猪灵。</p>
     */
    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewAboutToBeSetTarget();

        if (!(target instanceof Player player)) {
            return;
        }

        if (!UltimateLuxuryRingHelper.hasRing(player)) {
            return;
        }

        LivingEntity entity = event.getEntity();

        if (entity instanceof Merchant) {
            event.setCanceled(true);
            event.setNewAboutToBeSetTarget(null);
        }
    }

    /**
     * 每 tick 让附近袭击者优先攻击佩戴者。
     *
     * <p>原拓展在 Curios tick 中扫描附近 Raider 并设置目标。</p>
     * <p>当前实现把该行为放到事件类，避免物品类承担世界扫描逻辑，也能让所有戒指行为集中管理。</p>
     * <p>创造模式玩家跳过该效果，保持原拓展逻辑。</p>
     */
    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() || player.isCreative()) {
            return;
        }

        if (!UltimateLuxuryRingHelper.hasRing(player)) {
            return;
        }

        for (Raider raider : player.level().getEntitiesOfClass(Raider.class, player.getBoundingBox().inflate(RAIDER_TARGET_RANGE))) {
            if (!shouldRaiderTarget(player, raider)) {
                continue;
            }

            raider.setLastHurtByMob(player);
            raider.setTarget(player);
        }
    }

    /**
     * 处理极尽奢华之戒的伤害联动。
     *
     * <p>当玩家同时佩戴极尽奢华之戒和无尽贪婪契约时，玩家造成的伤害会根据背包宝石数量提高。</p>
     * <p>作为代价，袭击者攻击该玩家时也会获得一半比例的增伤。</p>
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player player && UltimateLuxuryRingHelper.hasRingAndPact(player)) {
            event.setNewDamage(event.getNewDamage() * (1.0F + UltimateLuxuryRingHelper.getDamageBoost(player)));
        }

        if (event.getSource().getEntity() instanceof Raider && event.getEntity() instanceof Player player && UltimateLuxuryRingHelper.hasRing(player)) {
            event.setNewDamage(event.getNewDamage() * (1.0F + UltimateLuxuryRingHelper.getDamageBoost(player) / 2.0F));
        }
    }

    /**
     * 玩家打开商人交易前，刷新极尽奢华之戒影响下的交易项。
     *
     * <p>原拓展会让村民不再“售罄”，并阻止负价格倍率造成的特殊价格差。</p>
     * <p>NeoForge 没有专门的 TradeWithVillagerEvent 后，这里在右键商人时遍历当前 offers，重置使用次数并清理负倍率下的特殊价格。</p>
     */
    @SubscribeEvent
    public static void onMerchantInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Merchant merchant)) {
            return;
        }

        Player player = event.getEntity();

        if (player.level().isClientSide() || !UltimateLuxuryRingHelper.hasRing(player)) {
            return;
        }

        for (MerchantOffer offer : merchant.getOffers()) {
            offer.resetUses();

            if (offer.getPriceMultiplier() < 0.0F) {
                offer.resetSpecialPriceDiff();
            }
        }
    }

    /**
     * 复制工具并临时提高时运附魔等级。
     *
     * <p>不直接修改玩家手中的真实工具，避免改变耐久、附魔或其它 Data Components。</p>
     * <p>返回值只用于重新计算当前这一次方块掉落。</p>
     *
     * @param player 用于读取注册表上下文的玩家
     * @param originalTool 原始挖掘工具
     * @param extraLevels 需要额外增加的时运等级
     * @return 带有临时时运加成的工具副本
     */
    private static ItemStack copyToolWithExtraFortune(Player player, ItemStack originalTool, int extraLevels) {
        ItemStack tool = originalTool.copy();

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup =
                player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        var fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);
        ItemEnchantments enchantments = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        mutable.set(fortune, enchantments.getLevel(fortune) + extraLevels);
        tool.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        return tool;
    }

    /**
     * 判断某个袭击者当前是否应该改为攻击佩戴者。
     *
     * <p>如果袭击者已有目标，并且目标也是极尽奢华之戒佩戴者，则不抢目标，避免多个佩戴者互相覆盖。</p>
     * <p>可见度越低，触发距离越短；但 5 格内保留保底范围，贴近时仍会被发现。</p>
     *
     * @param player 极尽奢华之戒佩戴者
     * @param raider 待检查袭击者
     * @return 满足视线/距离条件时返回 true
     */
    private static boolean shouldRaiderTarget(Player player, Raider raider) {
        LivingEntity currentTarget = raider.getTarget();

        if (currentTarget != null && currentTarget.isAlive() && UltimateLuxuryRingHelper.hasRing(currentTarget)) {
            return false;
        }

        double visibility = player.getVisibilityPercent(raider);
        double range = Math.max(RAIDER_TARGET_RANGE * visibility, 5.0D);

        if (!player.hasLineOfSight(raider) && player.distanceTo(raider) > 5.0F) {
            return false;
        }

        return raider.distanceToSqr(player) <= range * range;
    }

}
