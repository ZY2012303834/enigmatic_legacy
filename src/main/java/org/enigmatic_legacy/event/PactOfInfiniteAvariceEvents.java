package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.enigmatic_legacy.util.PactOfInfiniteAvariceHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 无尽贪婪契约事件。
 * 实现：
 * 1. +1 时运等级；
 * 2. 猪灵对佩戴者中立；
 * 3. 猪灵交易收益翻倍；
 * 4. 击杀任意生物 15% 概率掉落绿宝石；
 * 5. 村民交易 35% 折扣。
 */
public final class PactOfInfiniteAvariceEvents {
    private static final double EMERALD_DROP_CHANCE = 0.15D;
    private static final double VILLAGER_DISCOUNT = 0.35D;

    /**
     * 记录最近与佩戴者交易的猪灵。
     * 用途：
     * 猪灵完成以物易物并生成物品实体时，给额外一份复制掉落。
     */
    private static final Map<UUID, BarterRecord> RECENT_PIGLIN_BARTERS = new HashMap<>();

    private static final int BARTER_TRACK_TICKS = 20 * 10;
    private static final double BARTER_DUPLICATE_RANGE = 4.0D;
    private static final String DUPLICATED_BARTER_ITEM_TAG = "enigmatic_legacy_avarice_barter_duplicate";

    private PactOfInfiniteAvariceEvents() {
    }

    /**
     * +1 时运等级。
     * 实现方式：
     * 方块掉落已经确定后，使用临时 +1 时运的工具重新计算掉落，
     * 然后替换原本的掉落列表。
     * NeoForge 的 BlockDropsEvent 本来就是用于修改方块掉落和经验的事件。
     */
    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }

        if (!PactOfInfiniteAvariceHelper.hasPact(player)) {
            return;
        }

        if (event.getTool().isEmpty()) {
            return;
        }

        ItemStack boostedTool = copyToolWithExtraFortune(player, event.getTool());

        var boostedDrops = Block.getDrops(
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

            event.getDrops().add(new ItemEntity(
                    event.getLevel(),
                    event.getPos().getX() + 0.5D,
                    event.getPos().getY() + 0.5D,
                    event.getPos().getZ() + 0.5D,
                    drop
            ));
        }
    }

    private static ItemStack copyToolWithExtraFortune(Player player, ItemStack originalTool) {
        ItemStack tool = originalTool.copy();

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup =
                player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        ItemEnchantments enchantments = tool.getOrDefault(
                DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY
        );

        int currentLevel = enchantments.getLevel(fortune);

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        mutable.set(fortune, currentLevel + 1);

        tool.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        return tool;
    }

    /**
     * 猪灵对佩戴无尽贪婪契约的玩家保持中立。
     * NeoForge 1.21.1 当前方法名：
     * getNewAboutToBeSetTarget()
     * 不是 getNewTarget()
     */
    @SubscribeEvent
    public static void onPiglinChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Piglin)) {
            return;
        }

        if (!(event.getNewAboutToBeSetTarget() instanceof Player player)) {
            return;
        }

        if (PactOfInfiniteAvariceHelper.hasPact(player)) {
            event.setCanceled(true);
            event.setNewAboutToBeSetTarget(null);
        }
    }

    /**
     * 记录玩家右键与猪灵以物易物。
     * 原版猪灵以物易物主要使用金锭。
     * 后续猪灵生成交易物品时，onItemJoinLevel 会复制一份。
     */
    @SubscribeEvent
    public static void onPiglinInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Piglin piglin)) {
            return;
        }

        Player player = event.getEntity();

        if (!PactOfInfiniteAvariceHelper.hasPact(player)) {
            return;
        }

        ItemStack heldStack = player.getItemInHand(event.getHand());

        if (!heldStack.is(Items.GOLD_INGOT)) {
            return;
        }

        RECENT_PIGLIN_BARTERS.put(
                piglin.getUUID(),
                new BarterRecord(
                        player.getUUID(),
                        piglin.level().dimension().location().toString(),
                        piglin.level().getGameTime(),
                        piglin.getX(),
                        piglin.getY(),
                        piglin.getZ()
                )
        );
    }

    /**
     * 猪灵交易收益 +100%。
     * 当最近记录过交易的猪灵附近生成非金锭物品时，复制一份。
     * 说明：
     * 这能覆盖正常右键喂金锭的猪灵交易。
     * 如果后续你要做到完全覆盖自动丢金锭机器里的所有情况，
     * 最精确方案是后续加 Mixin 到 PiglinAi 的物品抛出逻辑。
     */
    @SubscribeEvent
    public static void onItemJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (itemEntity.getItem().is(Items.GOLD_INGOT)) {
            return;
        }

        if (itemEntity.getPersistentData().getBoolean(DUPLICATED_BARTER_ITEM_TAG)) {
            return;
        }

        if (itemEntity.getOwner() != null) {
            return;
        }

        long gameTime = level.getGameTime();
        String dimension = level.dimension().location().toString();

        cleanupOldBarterRecords(gameTime);

        Iterator<Map.Entry<UUID, BarterRecord>> iterator = RECENT_PIGLIN_BARTERS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, BarterRecord> entry = iterator.next();
            BarterRecord record = entry.getValue();

            if (!record.dimension().equals(dimension)) {
                continue;
            }

            if (gameTime - record.gameTime() > BARTER_TRACK_TICKS) {
                continue;
            }

            double dx = itemEntity.getX() - record.x();
            double dy = itemEntity.getY() - record.y();
            double dz = itemEntity.getZ() - record.z();

            if (dx * dx + dy * dy + dz * dz > BARTER_DUPLICATE_RANGE * BARTER_DUPLICATE_RANGE) {
                continue;
            }

            ItemEntity extra = new ItemEntity(
                    level,
                    itemEntity.getX(),
                    itemEntity.getY(),
                    itemEntity.getZ(),
                    itemEntity.getItem().copy()
            );

            extra.setDeltaMovement(itemEntity.getDeltaMovement());
            extra.getPersistentData().putBoolean(DUPLICATED_BARTER_ITEM_TAG, true);

            level.addFreshEntity(extra);
            iterator.remove();
            return;
        }
    }

    private static void cleanupOldBarterRecords(long gameTime) {
        Iterator<Map.Entry<UUID, BarterRecord>> iterator = RECENT_PIGLIN_BARTERS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, BarterRecord> entry = iterator.next();

            if (gameTime - entry.getValue().gameTime() > BARTER_TRACK_TICKS) {
                iterator.remove();
            }
        }
    }

    /**
     * 杀死任意生物额外 15% 概率掉落 1 个绿宝石。
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (!PactOfInfiniteAvariceHelper.hasPact(player)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            return;
        }

        if (player.getRandom().nextDouble() > EMERALD_DROP_CHANCE) {
            return;
        }

        event.getDrops().add(new ItemEntity(
                event.getEntity().level(),
                event.getEntity().getX(),
                event.getEntity().getY(),
                event.getEntity().getZ(),
                new ItemStack(Items.EMERALD)
        ));
    }

    /**
     * 可交易生物提供 35% 折扣。
     * 在玩家右键打开交易前，修改当前 offers 的 special price。
     *
     * <p>这里使用原版 Merchant 接口而不是 AbstractVillager，
     * 可以同时覆盖村民和 Iron's Spells 'n Spellbooks 的 IMerchantWizard，
     * 且不会在未安装铁魔法时加载它的类。</p>
     */
    @SubscribeEvent
    public static void onMerchantInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Merchant merchant)) {
            return;
        }

        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        if (!PactOfInfiniteAvariceHelper.hasPact(player)) {
            return;
        }

        applyMerchantDiscount(merchant);
    }

    private static void applyMerchantDiscount(Merchant merchant) {
        for (MerchantOffer offer : merchant.getOffers()) {
            ItemStack baseCost = offer.getBaseCostA();

            if (baseCost.isEmpty()) {
                continue;
            }

            int discount = Math.max(1, (int) Math.floor(baseCost.getCount() * VILLAGER_DISCOUNT));
            int desiredSpecialPrice = -discount;

            /*
             * 避免每次打开交易界面都无限叠加折扣。
             */
            if (offer.getSpecialPriceDiff() > desiredSpecialPrice) {
                offer.addToSpecialPriceDiff(desiredSpecialPrice - offer.getSpecialPriceDiff());
            }
        }
    }

    private record BarterRecord(
            UUID playerId,
            String dimension,
            long gameTime,
            double x,
            double y,
            double z
    ) {
    }
}