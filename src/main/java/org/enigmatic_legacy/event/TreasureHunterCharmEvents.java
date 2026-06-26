package org.enigmatic_legacy.event;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.items.charm.TreasureHunterCharm;
import org.enigmatic_legacy.util.TreasureHunterCharmHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 猎宝者护符事件。
 * 实现：
 * 1. 夜视；
 * 2. 挖掘速度加成；
 * 3. 临时 +1 时运。
 */
public class TreasureHunterCharmEvents {
    /**
     * 等待还原的临时时运数据。
     */
    private static final Map<UUID, PendingFortuneRestore> PENDING_FORTUNE_RESTORES = new HashMap<>();

    /**
     * 提供夜视。
     * 原项目后续版本不再要求低亮度才工作，
     * 所以这里只要佩戴并开启夜视，就持续刷新 Night Vision。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        restoreTemporaryFortune(player);

        if (player.level().isClientSide) {
            return;
        }

        if (!ConfigCommon.TREASURE_HUNTER_CHARM_NIGHT_VISION_ENABLED.get()) {
            return;
        }

        Optional<ItemStack> charm = TreasureHunterCharmHelper.findEquippedTreasureHunterCharm(player);

        if (charm.isEmpty()) {
            return;
        }

        if (!TreasureHunterCharm.isNightVisionEnabled(charm.get())) {
            return;
        }

        // 每秒检查一次即可，不需要每 tick 刷新。
        if (player.tickCount % 20 != 0) {
            return;
        }

        int duration = ConfigCommon.TREASURE_HUNTER_CHARM_NIGHT_VISION_DURATION.get();

        MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);

        if (current == null || current.getDuration() < 220) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    duration,
                    0,
                    true,
                    false,
                    true
            ));
        }
    }

    /**
     * 挖掘速度加成。
     * NeoForge 的 BreakSpeed 事件会在玩家计算挖掘速度时触发。:contentReference[oaicite:1]{index=1}
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (!TreasureHunterCharmHelper.hasTreasureHunterCharm(player)) {
            return;
        }

        double bonus = ConfigCommon.TREASURE_HUNTER_CHARM_MINING_SPEED_BONUS.get() / 100.0D;

        if (bonus <= 0.0D) {
            return;
        }

        event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + bonus)));
    }

    /**
     * 临时给主手工具 +1 时运。
     * BlockEvent.BreakEvent 在服务端方块破坏流程中触发，位于真正处理掉落之前。:contentReference[oaicite:2]{index=2}
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!ConfigCommon.TREASURE_HUNTER_CHARM_FORTUNE_ENABLED.get()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.level().isClientSide) {
            return;
        }

        if (!TreasureHunterCharmHelper.hasTreasureHunterCharm(player)) {
            return;
        }

        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty()) {
            return;
        }

        UUID playerId = player.getUUID();

        // 防止同一 tick 内重复写入。
        if (PENDING_FORTUNE_RESTORES.containsKey(playerId)) {
            return;
        }

        Holder<Enchantment> fortune = player.level()
                .registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FORTUNE);

        ItemEnchantments originalEnchantments = tool.getOrDefault(
                DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY
        );

        int originalFortuneLevel = tool.getEnchantmentLevel(fortune);

        EnchantmentHelper.updateEnchantments(tool, mutable -> {
            mutable.set(fortune, originalFortuneLevel + 1);
        });

        PENDING_FORTUNE_RESTORES.put(
                playerId,
                new PendingFortuneRestore(tool, originalEnchantments)
        );
    }

    /**
     * 还原临时时运。
     */
    private static void restoreTemporaryFortune(Player player) {
        PendingFortuneRestore pending = PENDING_FORTUNE_RESTORES.remove(player.getUUID());

        if (pending == null) {
            return;
        }

        if (pending.tool().isEmpty()) {
            return;
        }

        EnchantmentHelper.setEnchantments(pending.tool(), pending.originalEnchantments());
    }

    /**
     * 临时时运还原数据。
     */
    private record PendingFortuneRestore(
            ItemStack tool,
            ItemEnchantments originalEnchantments
    ) {
    }
}
