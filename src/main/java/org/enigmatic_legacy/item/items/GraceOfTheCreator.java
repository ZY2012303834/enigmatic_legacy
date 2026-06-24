package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
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
import org.enigmatic_legacy.util.ExperienceHelper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 创造者的恩赐 / Grace of the Creator。
 * 栏位：奥秘卷轴 scroll。
 * 效果：
 * 1. 给予飞行能力；
 * 2. 飞行会快速消耗经验；
 * 3. 在激活信标范围内飞行不消耗经验；
 * 4. 补偿飞行时挖掘速度损失；
 * 5. 在未丧失飞行能力时免疫摔落伤害。
 */
public class GraceOfTheCreator extends Item implements ICurioItem {
    private static final String SCROLL_SLOT = "scroll";

    /**
     * 记录飞行能力是否由创造者的恩赐授予。
     * 避免取下卷轴后误删创造模式 / 旁观模式飞行。
     */
    public static final String GRANTED_FLIGHT_TAG = "enigmatic_legacy_grace_scroll_granted_flight";

    /**
     * 创造之心的飞行标记。
     * 这里用来避免玩家同时拥有创造之心时，
     * 取下创造者的恩赐错误关闭飞行。
     */
    private static final String HEART_OF_CREATION_FLIGHT_TAG = "enigmatic_legacy_heart_of_creation_granted_flight";

    /**
     * 最大信标搜索半径。
     * 原版满级信标范围是 50 格。
     */
    private static final int MAX_BEACON_SEARCH_RADIUS = 50;

    /**
     * 创造者的恩赐比天堂之礼消耗更快。
     * 这里设置为：
     * 每 5 tick 消耗 1 点经验。
     * 也就是每秒大约 4 点经验。
     */
    private static final int XP_COST_INTERVAL = 5;
    private static final int XP_COST_AMOUNT = 1;

    public GraceOfTheCreator() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
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
     * 但创造者的恩赐最多只能装备 1 个。
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

        return CuriosApi.getCuriosInventory(entity)
                .flatMap(handler -> handler.findFirstCurio(
                        equippedStack -> equippedStack.getItem() instanceof GraceOfTheCreator && equippedStack != stack
                ))
                .isEmpty();
    }

    /**
     * 每 tick 由事件类调用。
     * 逻辑：
     * 1. 装备后直接给予飞行能力；
     * 2. 玩家正在飞行时，如果不在信标范围内，快速消耗经验；
     * 3. 如果没有经验且不在信标范围内，失去飞行能力；
     * 4. 在信标范围内飞行不消耗经验。
     */
    public static void serverTick(ServerPlayer player, ItemStack stack) {
        boolean hasBeaconPower = hasBeaconPower(player);

        if (hasBeaconPower) {
            grantFlight(player);
            return;
        }

        int playerExperience = ExperienceHelper.getPlayerXP(player);

        if (playerExperience <= 0) {
            revokeFlightIfGranted(player);
            return;
        }

        grantFlight(player);

        if (player.getAbilities().flying && player.tickCount % XP_COST_INTERVAL == 0) {
            consumeFlightExperience(player);
        }
    }

    /**
     * 玩家没有装备创造者的恩赐时调用。
     * 取下卷轴后移除由创造者的恩赐授予的飞行能力。
     */
    public static void revokeWhenMissing(ServerPlayer player) {
        revokeFlightIfGranted(player);
    }

    /**
     * 玩家是否仍然拥有创造者的恩赐授予的飞行能力。
     * 用于免疫摔落伤害。
     */
    public static boolean hasGraceFlight(Player player) {
        return player.getPersistentData().getBoolean(GRANTED_FLIGHT_TAG);
    }

    private static void grantFlight(ServerPlayer player) {
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        player.getPersistentData().putBoolean(GRANTED_FLIGHT_TAG, true);
    }

    private static void revokeFlightIfGranted(ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(GRANTED_FLIGHT_TAG)) {
            return;
        }

        player.getPersistentData().remove(GRANTED_FLIGHT_TAG);

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        /*
         * 如果其它遗物仍然授予飞行，不要关闭 mayfly。
         * 例如：天堂之礼、创造之心。
         */
        if (player.getPersistentData().getBoolean(GiftOfTheHeaven.GRANTED_FLIGHT_TAG)
                || player.getPersistentData().getBoolean(HEART_OF_CREATION_FLIGHT_TAG)) {
            return;
        }

        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
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

    /**
     * 计算信标底座层数。
     * 不直接依赖 BeaconBlockEntity#getLevels，
     * 避免不同映射环境中访问问题。
     */
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
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.fabulous_scroll.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.fabulous_scroll.2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.fabulous_scroll.3")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.fabulous_scroll.4")
                .withStyle(ChatFormatting.GRAY));
    }
}