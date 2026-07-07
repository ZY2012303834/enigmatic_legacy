package org.enigmatic_legacy.item.items.scroll;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.util.ExperienceHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 天堂之礼 / Gift of the Heaven。
 * 类型：奥秘卷轴 scroll。
 * 效果：
 * 1. 在激活信标范围内给予飞行能力；
 * 2. 飞行时缓慢消耗经验；
 * 3. 飞行时增加挖掘速度；
 * 4. 离开信标范围后移除飞行，并给予 8 秒缓降；
 * 5. 在信标范围内且飞行能力未丧失时免疫摔落伤害。
 */
public class GiftOfTheHeaven extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    /**
     * 记录飞行能力是否由天堂之礼授予。
     * 避免取下卷轴后误删创造模式 / 旁观模式飞行。
     */
    public static final String GRANTED_FLIGHT_TAG = "enigmatic_legacy_heaven_scroll_granted_flight";

    /**
     * 记录上一 tick 是否拥有信标飞行资格。
     * 用于离开范围时只触发一次缓降。
     */
    private static final String HAD_BEACON_POWER_TAG = "enigmatic_legacy_heaven_scroll_had_beacon_power";

    /**
     * 最大信标搜索半径。
     * 原版满级信标范围是 50 格，所以这里扫描 50 格内的信标。
     */
    private static final int MAX_BEACON_SEARCH_RADIUS = 50;

    /**
     * 失去信标范围后给予 8 秒缓降。
     */
    private static final int SLOW_FALLING_TICKS = 8 * 20;

    /**
     * 飞行时每秒消耗经验。
     */
    private static final int XP_COST_INTERVAL = 20;
    private static final int XP_COST_AMOUNT = 1;

    public GiftOfTheHeaven() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());
    }

    /**
     * 普通右键允许直接装备到奥秘卷轴栏。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    /**
     * 只能放进 scroll 奥秘卷轴栏。
     * 奥秘卷轴栏可以有 3 个槽位，
     * 但天堂之礼最多只能装备 1 个。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context == null || !SCROLL_SLOT.equals(context.identifier())) {
            return false;
        }

        LivingEntity entity = context.entity();

        if (entity == null) {
            return true;
        }

        return CuriosLookupApi.findFirstSlot(entity, equippedStack ->
                        equippedStack != stack
                                && (
                                equippedStack.getItem() instanceof GiftOfTheHeaven
                                        || equippedStack.getItem() instanceof GraceOfTheCreator
                        ))
                .isEmpty();
    }

    /**
     * 每 tick 由事件类调用。
     * 注意：
     * 不直接依赖 curioTick 做移除逻辑。
     * 因为玩家取下卷轴后 curioTick 不再执行，
     * 如果只靠 curioTick，飞行能力可能不会被正确移除。
     */
    public static void serverTick(ServerPlayer player, ItemStack stack) {
        boolean hasBeaconPower = hasBeaconPower(player);
        boolean hasExperience = ExperienceHelper.getPlayerXP(player) > 0;

        if (hasBeaconPower && hasExperience) {
            grantFlight(player);
            player.getPersistentData().putBoolean(HAD_BEACON_POWER_TAG, true);

            if (player.getAbilities().flying && player.tickCount % XP_COST_INTERVAL == 0) {
                consumeFlightExperience(player);
            }

            return;
        }

        boolean hadBeaconPower = player.getPersistentData().getBoolean(HAD_BEACON_POWER_TAG);

        revokeFlightIfGranted(player);

        if (hadBeaconPower && !player.onGround()) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.SLOW_FALLING,
                    SLOW_FALLING_TICKS,
                    0,
                    false,
                    true,
                    true
            ));
        }

        player.getPersistentData().putBoolean(HAD_BEACON_POWER_TAG, false);
    }

    /**
     * 玩家是否仍然拥有天堂之礼授予的安全飞行资格。
     * 用于摔落伤害免疫判断。
     */
    public static boolean hasSafeBeaconFlight(Player player) {
        return player.getPersistentData().getBoolean(GRANTED_FLIGHT_TAG)
                && player.getPersistentData().getBoolean(HAD_BEACON_POWER_TAG);
    }

    /**
     * 玩家没有装备天堂之礼时调用。
     * 作用：
     * 取下卷轴后立刻移除由天堂之礼授予的飞行能力。
     */
    public static void revokeWhenMissing(ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(GRANTED_FLIGHT_TAG)) {
            return;
        }

        revokeFlightIfGranted(player);

        if (!player.onGround()) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.SLOW_FALLING,
                    SLOW_FALLING_TICKS,
                    0,
                    false,
                    true,
                    true
            ));
        }

        player.getPersistentData().putBoolean(HAD_BEACON_POWER_TAG, false);
    }

    @SuppressWarnings("deprecation")
    private static void grantFlight(ServerPlayer player) {
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        player.getPersistentData().putBoolean(GRANTED_FLIGHT_TAG, true);
    }

    @SuppressWarnings("deprecation")
    private static void revokeFlightIfGranted(ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(GRANTED_FLIGHT_TAG)) {
            return;
        }

        player.getPersistentData().remove(GRANTED_FLIGHT_TAG);

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    private static void consumeFlightExperience(ServerPlayer player) {
        int currentExperience = ExperienceHelper.getPlayerXP(player);

        if (currentExperience <= 0) {
            revokeFlightIfGranted(player);
            return;
        }

        ExperienceHelper.drainPlayerXP(player, Math.min(XP_COST_AMOUNT, currentExperience));
    }

    /**
     * 检查玩家是否在激活信标范围内。
     * 实现方式：
     * 扫描玩家附近已加载区块里的 BeaconBlockEntity。
     * 不强制加载新区块，避免卡顿。
     * 信标范围按照原版规则：
     * level 1 = 20
     * level 2 = 30
     * level 3 = 40
     * level 4 = 50
     */
    private static boolean hasBeaconPower(ServerPlayer player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;
        int chunkRadius = (MAX_BEACON_SEARCH_RADIUS >> 4) + 1;

        for (int chunkX = playerChunkX - chunkRadius; chunkX <= playerChunkX + chunkRadius; chunkX++) {
            for (int chunkZ = playerChunkZ - chunkRadius; chunkZ <= playerChunkZ + chunkRadius; chunkZ++) {
                if (!(level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) instanceof LevelChunk chunk)) {
                    continue;
                }

                for (var blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof BeaconBlockEntity beacon)) {
                        continue;
                    }

                    int beaconLevels = getBeaconLevels(level, beacon.getBlockPos());

                    if (beaconLevels <= 0) {
                        continue;
                    }

                    BlockPos beaconPos = beacon.getBlockPos();

                    double range = beaconLevels * 10.0D + 10.0D;
                    double dx = playerPos.getX() - beaconPos.getX();
                    double dz = playerPos.getZ() - beaconPos.getZ();

                    if (dx * dx + dz * dz <= range * range) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static int getBeaconLevels(Level level, BlockPos beaconPos) {
        for (int beaconLevel = 1; beaconLevel <= 4; beaconLevel++) {
            int y = beaconPos.getY() - beaconLevel;
            if (y < level.getMinBuildHeight()) {
                return beaconLevel - 1;
            }

            for (int x = beaconPos.getX() - beaconLevel; x <= beaconPos.getX() + beaconLevel; x++) {
                for (int z = beaconPos.getZ() - beaconLevel; z <= beaconPos.getZ() + beaconLevel; z++) {
                    if (!level.getBlockState(new BlockPos(x, y, z)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        return beaconLevel - 1;
                    }
                }
            }
        }

        return 4;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.heaven_scroll.1"));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.heaven_scroll.2"));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.heaven_scroll.3", SpellstoneTooltip.number("8")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.heaven_scroll.4"));
    }
}
