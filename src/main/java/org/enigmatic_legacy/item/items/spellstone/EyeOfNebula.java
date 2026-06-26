package org.enigmatic_legacy.item.items.spellstone;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.event.TeleportParticleEvents;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 星云之眼 / Eye of the Nebula。

 * 类型：术石 spellstone。

 * 主动效果：
 * 将玩家传送到正在注视的生物背后。

 * 被动效果：
 * 1. 增加 40% 魔法伤害；
 * 2. 增加 65% 魔法抗性；
 * 3. 受到攻击时有 15% 概率闪现到别处，并取消本次伤害；
 * 4. 使用主动技能后，下一次攻击额外造成 +150% 伤害；
 * 5. 星云之眼自身传送后免疫短时间摔落型传送伤害；
 * 6. 在水中时，受到的所有伤害翻倍。
 */
public class EyeOfNebula extends Item implements ICurioItem {
    /**
     * Curios 槽位 ID。
     * 你的项目里术石槽位名就是 spellstone。
     */
    private static final String SPELLSTONE_SLOT = "spellstone";

    /**
     * 主动技能冷却。
     * Minecraft 20 tick = 1 秒，所以 60 tick = 3 秒。
     */
    public static final int COOLDOWN_TICKS = 60;

    /**
     * 主动技能最大锁定距离。
     * 玩家必须看着 32 格内的生物才会触发传送。
     */
    public static final double ACTIVE_RANGE = 32.0D;

    /**
     * 被攻击时随机闪现的最大范围。
     */
    public static final double DODGE_RANGE = 16.0D;

    /**
     * 魔法伤害增加 40%。
     * 计算方式：最终魔法伤害 * 1.4。
     */
    public static final float MAGIC_DAMAGE_BONUS = 0.40F;

    /**
     * 魔法抗性 65%。
     * 计算方式：受到的魔法伤害 * 0.35。
     */
    public static final float MAGIC_RESISTANCE = 0.65F;

    /**
     * 受到攻击时 15% 概率传送躲避。
     */
    public static final int DODGE_CHANCE = 15;

    /**
     * 主动技能后下一次攻击 +150%。
     * 计算方式：最终伤害 * 2.5。
     */
    public static final float EMPOWERED_ATTACK_BONUS = 1.50F;

    /**
     * 主动技能后，用这个 NBT 标记玩家下一次攻击需要增伤。
     * 事件类 EyeOfNebulaEvents 会读取并清除这个标记。
     */
    public static final String EMPOWERED_ATTACK_TAG =
            "enigmatic_legacy_eye_of_nebula_empowered";

    /**
     * 传送后短时间免疫摔落型传送伤害。
     * 这里存的是游戏时间 gameTime，到期后自动失效。
     */
    public static final String TELEPORT_PROTECTION_UNTIL_TAG =
            "enigmatic_legacy_eye_of_nebula_teleport_protection_until";

    public EyeOfNebula() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    /**
     * 允许玩家右键物品时直接装备到 Curios 的 spellstone 槽。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 限制该物品只能放进 spellstone 槽。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 判断当前 Curios 槽位是否为术石槽。
     */
    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * 主动技能入口。

     * 这个方法会被 SpellstoneUsePayload 调用，
     * 也就是玩家按下“使用术石”快捷键时触发。
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        // 如果物品还在冷却中，直接返回。
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        // 获取玩家当前正在注视的生物。
        LivingEntity target = getObservedEntity(player, ACTIVE_RANGE);

        // 没有找到目标时，给玩家一个屏幕提示。
        if (target == null) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.eye_of_nebula.no_target")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return;
        }

        // 计算目标背后的安全传送位置。
        Vec3 destination = findPositionBehindTarget(level, target);

        // 计算玩家传送后面对目标的视角。
        float[] rotation = getRotationToFaceTarget(destination, target);

        // 出发点粒子和音效。
        TeleportParticleEvents.spawnDepartureParticles(player);
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0F,
                0.9F
        );

        // 传送前脱离坐骑，避免骑乘状态导致位置异常。
        player.stopRiding();

        // 执行传送。
        player.teleportTo(
                level,
                destination.x,
                destination.y,
                destination.z,
                rotation[0],
                rotation[1]
        );

        // 清理摔落距离，避免传送后产生摔落伤害。
        player.resetFallDistance();

        // 给玩家短暂传送保护，避免极端情况下立刻吃到摔落型传送伤害。
        markTeleportProtected(player, 20);

        // 到达点粒子和音效。
        TeleportParticleEvents.scheduleArrivalParticles(player, 4);
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0F,
                1.1F
        );

        // 添加 3 秒冷却。
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // 标记下一次攻击获得 +150% 伤害。
        player.getPersistentData().putBoolean(EMPOWERED_ATTACK_TAG, true);
    }

    /**
     * 给实体添加短时间“传送保护”。
     *
     * @param entity 被保护实体
     * @param ticks  保护持续 tick 数
     */
    public static void markTeleportProtected(LivingEntity entity, int ticks) {
        long until = entity.level().getGameTime() + ticks;
        entity.getPersistentData().putLong(TELEPORT_PROTECTION_UNTIL_TAG, until);
    }

    /**
     * 判断传送保护是否仍然有效。
     */
    public static boolean hasTeleportProtection(LivingEntity entity) {
        long until = entity.getPersistentData().getLong(TELEPORT_PROTECTION_UNTIL_TAG);
        return entity.level().getGameTime() <= until;
    }

    /**
     * 获取玩家正在注视的生物。

     * 这里会先进行方块射线检测，
     * 如果方块挡住视线，则不会穿墙锁定目标。
     */
    private static LivingEntity getObservedEntity(ServerPlayer player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(lookVector.scale(range));

        // 检测视线中是否先撞到方块。
        HitResult blockHit = player.level().clip(new ClipContext(
                eyePosition,
                endPosition,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        double maxDistanceSqr = range * range;

        // 如果方块更近，就把最大锁定距离缩短到方块位置。
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            maxDistanceSqr = blockHit.getLocation().distanceToSqr(eyePosition);
        }

        // 搜索玩家视线方向上的实体。
        AABB searchBox = player.getBoundingBox()
                .expandTowards(lookVector.scale(range))
                .inflate(1.0D);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                eyePosition,
                endPosition,
                searchBox,
                entity -> entity instanceof LivingEntity
                        && entity != player
                        && EntitySelector.NO_SPECTATORS.test(entity)
                        && entity.isPickable()
        );

        if (entityHit == null) {
            return null;
        }

        Entity entity = entityHit.getEntity();

        // 如果实体在方块后面，不允许穿墙锁定。
        if (entityHit.getLocation().distanceToSqr(eyePosition) > maxDistanceSqr) {
            return null;
        }

        return entity instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    /**
     * 计算目标背后的位置。

     * 背后方向 = 目标当前位置 - 目标面朝方向。
     */
    private static Vec3 findPositionBehindTarget(ServerLevel level, LivingEntity target) {
        Vec3 behind = target.position()
                .subtract(target.getLookAngle().normalize().scale(1.65D));

        BlockPos basePos = BlockPos.containing(behind.x, target.getY(), behind.z);

        // 尝试目标高度附近几个位置，找一个脚下有方块、身体不卡墙的位置。
        for (int yOffset = 1; yOffset >= -2; yOffset--) {
            BlockPos pos = basePos.offset(0, yOffset, 0);

            if (isSafeTeleportPosition(level, pos)) {
                return new Vec3(
                        pos.getX() + 0.5D,
                        pos.getY(),
                        pos.getZ() + 0.5D
                );
            }
        }

        // 如果没找到完美安全点，就退回到目标背后的原始位置。
        return new Vec3(behind.x, target.getY() + 0.1D, behind.z);
    }

    /**
     * 判断位置是否适合传送。

     * 注意：
     * 不使用 hasChunksAt(...)，因为 1.21.x 里它已经被标记为弃用。

     * 这里使用 getChunk(chunkX, chunkZ, ChunkStatus.FULL, false)：
     * 1. 不会强制生成新区块；
     * 2. 可以判断目标区块是否已经完整加载；
     * 3. 避免传送到未加载区块导致卡顿或异常。
     */
    private static boolean isSafeTeleportPosition(ServerLevel level, BlockPos pos) {
        // 如果位置超出世界高度，直接不允许传送。
        if (level.isOutsideBuildHeight(pos) || level.isOutsideBuildHeight(pos.above())) {
            return false;
        }

        // 检查目标位置所在区块是否已经加载。
        if (!isChunkLoaded(level, pos)) {
            return false;
        }

        // 脚部位置必须为空，避免玩家卡进方块。
        boolean feetClear = level.getBlockState(pos)
                .getCollisionShape(level, pos)
                .isEmpty();

        // 头部位置必须为空，避免玩家窒息。
        boolean headClear = level.getBlockState(pos.above())
                .getCollisionShape(level, pos.above())
                .isEmpty();

        // 脚下必须有实体方块，避免直接传送到空中。
        boolean hasGround = !level.getBlockState(pos.below())
                .getCollisionShape(level, pos.below())
                .isEmpty();

        return feetClear && headClear && hasGround;
    }

    /**
     * 判断某个 BlockPos 所在区块是否已经完整加载。
     *
     * 这个方法替代已弃用的 hasChunksAt(...)。
     */
    private static boolean isChunkLoaded(ServerLevel level, BlockPos pos) {
        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());

        return level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
    }

    /**
     * 计算玩家传送后看向目标需要的 yaw 和 pitch。
     */
    private static float[] getRotationToFaceTarget(Vec3 from, LivingEntity target) {
        double dx = target.getX() - from.x;
        double dy = target.getEyeY() - from.y;
        double dz = target.getZ() - from.z;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(dy, horizontalDistance) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }

    /**
     * 物品提示文本。

     * 你的项目里已有类似风格：
     * 不按 Shift 只显示“按住 Shift 查看详情”；
     * 按住 Shift 后显示完整主动 / 被动说明。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.void"));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift"));
            return;
        }

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.active"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eye_of_nebula.active"));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.cooldown",
                SpellstoneTooltip.number(String.format("%.1f", COOLDOWN_TICKS / 20.0F))
        ));

        tooltip.add(Component.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.passive"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eye_of_nebula.passive.1", SpellstoneTooltip.number("+40%")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eye_of_nebula.passive.2", SpellstoneTooltip.number("65%")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eye_of_nebula.passive.3", SpellstoneTooltip.number("15%")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eye_of_nebula.passive.4", SpellstoneTooltip.number("+150%")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.eye_of_nebula.passive.5"));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.eye_of_nebula.passive.6"));
    }
}
