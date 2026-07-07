package org.enigmatic_legacy.item.items.spellstone;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.enigmatic_legacy.api.CuriosLookupApi;
import org.enigmatic_legacy.config.ConfigCommon;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.ExperienceHelper;
import org.enigmatic_legacy.util.HeartOfCreationHelper;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 复苏之叶 / Revival Leaves。
 *
 * <p>自然系术石。复刻 Enigmatic Addons 的复苏之叶，并按本项目 1.21.1 NeoForge 结构重写。</p>
 *
 * <p>主动能力：</p>
 * <ul>
 *     <li>消耗玩家经验；</li>
 *     <li>治疗周围生物；</li>
 *     <li>给范围内生物施加生命恢复；</li>
 *     <li>净化附近凋零玫瑰，将其替换为虞美人；</li>
 *     <li>触发配置冷却。</li>
 * </ul>
 * <p>被动能力：</p>
 * <ul>
 *     <li>只能装备在 Curios 的 spellstone 槽；</li>
 *     <li>周期性自然恢复生命；</li>
 *     <li>清除饥饿、中毒、凋零；</li>
 *     <li>加速附近作物和瓜苗生长；</li>
 *     <li>附近有植物时授予飞行能力；</li>
 *     <li>离开植物后短暂宽限，之后撤销飞行；</li>
 *     <li>卸下术石时撤销本物品授予的飞行。</li>
 * </ul>
 *
 * <p>战斗相关逻辑不在本类，而在 RevivalLeavesEvents 中处理：</p>
 * <ul>
 *     <li>Wither 伤害免疫；</li>
 *     <li>火焰和弹射物伤害变得更危险；</li>
 *     <li>攻击目标施加中毒；</li>
 *     <li>中毒目标治疗量降低；</li>
 *     <li>状态效果持续时间倍率调整。</li>
 * </ul>
 */
public class RevivalLeaves extends Item implements ICurioItem {
    /**
     * Curios 槽位 ID。
     * 复苏之叶是术石，所以只能装备到 spellstone 槽。
     */
    private static final String SPELLSTONE_SLOT = "spellstone";

    /**
     * PersistentData 标记：玩家当前的飞行能力是否由复苏之叶授予。
     * 这个标记很重要：
     * 撤销飞行时只撤销复苏之叶自己授予的飞行，避免误关创造模式、旁观模式、
     * 创造之心、天堂之礼、创造者的恩赐等其它来源的飞行。
     */
    private static final String FLIGHT_GRANTED_TAG = "enigmatic_legacy.revival_leaves_flight_granted";

    /**
     * PersistentData 标记：离开植物范围后的飞行宽限 tick。
     * 如果没有宽限，玩家飞过植物边缘时 mayfly 会频繁开关，
     * 体验会很差，也可能导致玩家突然下坠。
     */
    private static final String FLIGHT_GRACE_TAG = "enigmatic_legacy.revival_leaves_flight_grace";

    /**
     * PersistentData 标记：缓存最近一次找到的有效植物坐标。
     * 作用：
     * 1. 服务端不用每 tick 都完整扫描附近区域；
     * 2. 客户端可以围绕这个植物位置生成提示粒子。
     */
    private static final String FLIGHT_PLANT_POS_TAG = "enigmatic_legacy.revival_leaves_plant_pos";

    /**
     * 搜索可维持飞行植物的半径。
     */
    private static final int PLANT_SEARCH_RADIUS = 5;

    /**
     * 离开植物范围后的飞行宽限时间。
     * 5 tick = 0.25 秒。
     */
    private static final int PLANT_FLIGHT_GRACE_TICKS = 5;

    /**
     * 允许触发复苏之叶飞行的植物标签。
     * FLOWERS：花；
     * SAPLINGS：树苗；
     * REPLACEABLE_BY_TREES：树木生成时可替换的自然植物。
     */
    private static final List<TagKey<Block>> PLANT_TAGS = List.of(
            BlockTags.FLOWERS,
            BlockTags.SAPLINGS,
            BlockTags.REPLACEABLE_BY_TREES
    );

    public RevivalLeaves() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    /**
     * 允许玩家右键把物品直接装备到 Curios 槽。
     * 但只有目标槽位是 spellstone 时才允许。
     */
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 限制物品只能被放进 spellstone 槽。
     */
    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    /**
     * 判断当前 Curios 槽位是否是术石槽。
     */
    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * 主动能力入口。
     * 注意：
     * 这个方法本身不会自动触发，必须由你的术石主动技能系统调用。
     * 例如快捷键、网络包、或统一 spellstone active handler。
     * 执行流程：
     * 1. 检查冷却；
     * 2. 检查玩家经验是否足够；
     * 3. 消耗经验；
     * 4. 播放音效；
     * 5. 治疗范围内生物；
     * 6. 净化附近凋零玫瑰；
     * 7. 添加冷却。
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        int totalXp = ExperienceHelper.getPlayerXP(player);
        if (totalXp <= 10) {
            return;
        }

        int cost = Math.min(Mth.ceil(5.0F * player.getRandom().nextFloat()) + player.experienceLevel, totalXp);
        ExperienceHelper.drainPlayerXP(player, cost);

        BlockPos origin = player.blockPosition();
        level.playSound(
                null,
                origin,
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.2F
        );

        applyRegenerationPulse(level, player, totalXp);
        cleanseWitherRoses(level, origin);

        player.getCooldowns().addCooldown(this, ConfigCommon.REVIVAL_LEAVES_COOLDOWN.get());
    }

    /**
     * 主动能力的治疗脉冲。
     * 范围由配置 REVIVAL_LEAVES_ABILITY_RADIUS 控制。
     * 当前逻辑：
     * - 范围内所有存活 LivingEntity 都会获得生命恢复；
     * - 玩家等级超过 25 时，会额外按目标最大生命值立刻治疗；
     * - 生命恢复持续时间根据玩家总经验和等级增加，但额外值最多等于基础时间。
     * 注意：
     * 这里会治疗所有生物，包括敌对生物。
     * 如果想只治疗玩家、友方或宠物，需要在这里加过滤条件。
     */
    private static void applyRegenerationPulse(ServerLevel level, ServerPlayer player, int totalXp) {
        double radius = ConfigCommon.REVIVAL_LEAVES_ABILITY_RADIUS.get();
        AABB area = player.getBoundingBox().inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (player.experienceLevel > 25) {
                float modifier = Math.min(0.2F, (player.experienceLevel - 25) * 0.01F);
                entity.heal(modifier * entity.getMaxHealth());
            }

            int duration = ConfigCommon.REVIVAL_LEAVES_REGENERATION_TIME.get()
                    + Math.min(totalXp * Math.max(1, player.experienceLevel) / 2, ConfigCommon.REVIVAL_LEAVES_REGENERATION_TIME.get());

            entity.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,
                    duration,
                    ConfigCommon.REVIVAL_LEAVES_REGENERATION_LEVEL.get(),
                    false,
                    true
            ), player);
        }
    }

    /**
     * 净化附近凋零玫瑰。
     * 当前范围是玩家周围 11x11x11 立方体。
     * 找到凋零玫瑰后：
     * 1. 破坏原方块，不掉落；
     * 2. 替换为虞美人。
     */
    private static void cleanseWitherRoses(ServerLevel level, BlockPos origin) {
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(5, 5, 5), origin.offset(-5, -5, -5))) {
            if (level.getBlockState(pos).is(Blocks.WITHER_ROSE)) {
                level.destroyBlock(pos, false);
                level.setBlock(pos, Blocks.POPPY.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    /**
     * Curios 每 tick 被动逻辑。
     * 只要装备在 spellstone 槽里，就会持续：
     * 1. 清除指定负面效果；
     * 2. 周期性自然恢复；
     * 3. 如果佩戴者是玩家，则处理植物飞行；
     * 4. 服务端加速附近作物生长。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        removeForbiddenEffects(entity);
        handleNaturalRegeneration(entity);

        if (entity instanceof Player player) {
            handlePlantFlight(player);

            if (!player.level().isClientSide()) {
                accelerateNearbyCrops((ServerLevel) player.level(), player);
            }
        }
    }

    /**
     * 清除复苏之叶免疫的负面效果。
     * 当前清除：
     * - 饥饿；
     * - 中毒；
     * - 凋零。
     * 这里先收集再移除，避免遍历活动效果集合时直接修改集合。
     */
    private static void removeForbiddenEffects(LivingEntity entity) {
        List<Holder<MobEffect>> toRemove = new ArrayList<>();

        for (MobEffectInstance effect : entity.getActiveEffects()) {
            if (effect.is(MobEffects.HUNGER) || effect.is(MobEffects.POISON) || effect.is(MobEffects.WITHER)) {
                toRemove.add(effect.getEffect());
            }
        }

        for (Holder<MobEffect> effect : toRemove) {
            entity.removeEffect(effect);
        }
    }

    /**
     * 被动自然恢复。
     * 恢复间隔由 REVIVAL_LEAVES_NATURAL_REGENERATION_TICK 控制，最低 5 tick。
     * 每次恢复量：
     * - 至少 0.5 点；
     * - 或最大生命值的 1%；
     * 二者取较大值。
     */
    private static void handleNaturalRegeneration(LivingEntity entity) {
        int interval = Math.max(5, ConfigCommon.REVIVAL_LEAVES_NATURAL_REGENERATION_TICK.get());

        if (entity.tickCount % interval == 0 && entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(Math.max(0.5F, entity.getMaxHealth() / 100.0F));
        }
    }

    /**
     * 加速附近作物生长。
     * 注意：
     * 在 1.21.1 中，CropBlock#randomTick 和 StemBlock#randomTick 是 protected，
     * 不能从物品类中直接调用。
     * 正确做法是调用 BlockState#randomTick，
     * 让方块状态自己分发到对应方块的随机刻逻辑。
     */
    private static void accelerateNearbyCrops(ServerLevel level, Player player) {
        BlockPos origin = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-3, -1, -3), origin.offset(3, 1, 3))) {
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof CropBlock crop) {
                if (crop.getAge(state) < crop.getMaxAge() && player.getRandom().nextInt(16) == 0) {
                    state.randomTick(level, pos, player.getRandom());
                    sendHappyParticles(level, player, pos, 12);
                }
            } else if (state.getBlock() instanceof StemBlock && player.getRandom().nextInt(16) == 0) {
                state.randomTick(level, pos, player.getRandom());
                sendHappyParticles(level, player, pos, 12);
            }
        }
    }

    /**
     * 随机发送快乐村民粒子。
     * chance 越大，触发概率越低。
     * 当前 chance = 12 时，概率是 1/12。
     */
    private static void sendHappyParticles(ServerLevel level, Player player, BlockPos pos, int chance) {
        if (player.getRandom().nextInt(chance) != 0) {
            return;
        }

        Vec3 center = pos.getCenter();
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 1, 0.2D, 0.2D, 0.2D, 0.0D);
    }

    /**
     * 周围存在植物方块时授予飞行能力。
     * 服务端负责真正授予/撤销飞行；
     * 客户端只负责播放粒子。
     * 如果玩家已经有更强飞行遗物，例如天堂之礼或创造者的恩赐，
     * 复苏之叶不会覆盖它们的飞行逻辑。
     */
    private static void handlePlantFlight(Player player) {
        if (player.level().isClientSide()) {
            spawnFlightParticles(player);
            return;
        }

        boolean blockedByOtherFlightRelic = CuriosLookupApi.hasCurio(player, ModItems.HEAVEN_SCROLL.get())
                || CuriosLookupApi.hasCurio(player, ModItems.FABULOUS_SCROLL.get());

        if (player.isCreative() || player.isSpectator() || blockedByOtherFlightRelic) {
            revokeFlight(player);
            return;
        }

        boolean hasPlant = hasPlantNearby(player);

        if (hasPlant) {
            grantFlight(player);
            player.getPersistentData().putInt(FLIGHT_GRACE_TAG, PLANT_FLIGHT_GRACE_TICKS);
            return;
        }

        int grace = player.getPersistentData().getInt(FLIGHT_GRACE_TAG);
        if (grace > 1) {
            player.getPersistentData().putInt(FLIGHT_GRACE_TAG, grace - 1);
            return;
        }

        revokeFlight(player);
    }

    /**
     * 授予飞行能力，并记录这个飞行来自复苏之叶。
     *
     * <p>这里不直接在业务逻辑里访问 mayfly 字段，而是统一走 setMayflySafely，
     * 这样弃用字段只集中在一个小方法里，后续版本如果有正式替代 API，只需要改 helper 方法。</p>
     */
    private static void grantFlight(Player player) {
        if (!canMayflySafely(player)) {
            setMayflySafely(player, true);
            player.onUpdateAbilities();
        }

        player.getPersistentData().putBoolean(FLIGHT_GRANTED_TAG, true);
    }

    /**
     * 撤销复苏之叶授予的飞行。
     *
     * <p>只有存在 FLIGHT_GRANTED_TAG 时才撤销，避免误关其它来源提供的飞行。</p>
     */
    private static void revokeFlight(Player player) {
        if (!player.getPersistentData().getBoolean(FLIGHT_GRANTED_TAG)) {
            return;
        }

        player.getPersistentData().remove(FLIGHT_GRANTED_TAG);
        player.getPersistentData().remove(FLIGHT_GRACE_TAG);
        player.getPersistentData().remove(FLIGHT_PLANT_POS_TAG);

        if (!player.isCreative() && !player.isSpectator() && !HeartOfCreationHelper.hasHeartOfCreationEquipped(player)) {
            setMayflySafely(player, false);
            setFlyingSafely(player, false);
            player.onUpdateAbilities();
        }
    }

    /**
     * 判断玩家当前是否允许飞行。
     *
     * <p>1.21.1 里 Abilities 没有公开的 setMayfly/getMayfly 方法，
     * 因此这里集中访问 mayfly 字段，并把弃用警告限制在这个 helper 内。</p>
     */
    @SuppressWarnings("deprecation")
    private static boolean canMayflySafely(Player player) {
        return player.getAbilities().mayfly;
    }

    /**
     * 设置玩家是否允许飞行。
     *
     * <p>集中封装 mayfly 字段访问，避免业务逻辑里到处出现弃用警告。</p>
     */
    @SuppressWarnings("deprecation")
    private static void setMayflySafely(Player player, boolean value) {
        player.getAbilities().mayfly = value;
    }

    /**
     * 判断玩家当前是否正在飞行。
     *
     * <p>集中封装 flying 字段访问。</p>
     */
    private static boolean isFlyingSafely(Player player) {
        return player.getAbilities().flying;
    }

    /**
     * 设置玩家当前是否正在飞行。
     *
     * <p>关闭 mayfly 时也要关闭 flying，否则玩家可能保留飞行状态。</p>
     */
    private static void setFlyingSafely(Player player, boolean value) {
        player.getAbilities().flying = value;
    }

    /**
     * 判断附近是否有可维持飞行的植物。
     * 优先检查缓存位置；
     * 如果缓存无效，再扫描玩家周围区域；
     * 找到新植物后缓存它的位置。
     */
    private static boolean hasPlantNearby(Player player) {
        BlockPos cached = getCachedPlantPos(player);
        if (cached != null && isValidPlantForFlight(player, cached)) {
            return true;
        }

        BlockPos origin = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-PLANT_SEARCH_RADIUS, -PLANT_SEARCH_RADIUS, -PLANT_SEARCH_RADIUS),
                origin.offset(PLANT_SEARCH_RADIUS, PLANT_SEARCH_RADIUS, PLANT_SEARCH_RADIUS))) {
            if (isValidPlantForFlight(player, pos)) {
                player.getPersistentData().putLong(FLIGHT_PLANT_POS_TAG, pos.asLong());
                return true;
            }
        }

        return false;
    }

    /**
     * 读取缓存植物坐标。
     */
    private static BlockPos getCachedPlantPos(Player player) {
        if (!player.getPersistentData().contains(FLIGHT_PLANT_POS_TAG)) {
            return null;
        }

        return BlockPos.of(player.getPersistentData().getLong(FLIGHT_PLANT_POS_TAG));
    }

    /**
     * 判断指定方块是否是有效植物。
     * 条件：
     * 1. 不能是水；
     * 2. 必须属于允许的植物标签；
     * 3. 与玩家距离不能太远。
     */
    private static boolean isValidPlantForFlight(Player player, BlockPos pos) {
        BlockState state = player.level().getBlockState(pos);
        return !state.is(Blocks.WATER)
                && PLANT_TAGS.stream().anyMatch(state::is)
                && pos.distToCenterSqr(player.position()) <= 36.0D;
    }

    /**
     * 客户端飞行粒子。
     * 玩家飞行时周期性生成 HAPPY_VILLAGER 粒子。
     * 如果存在缓存植物坐标，也会在植物附近显示粒子，
     * 用来提示飞行能力来自植物。
     */
    private static void spawnFlightParticles(Player player) {
        if (!isFlyingSafely(player) || player.tickCount % 12 != 0) {
            return;
        }

        player.level().addParticle(
                ParticleTypes.HAPPY_VILLAGER,
                player.getRandomX(0.5D),
                player.getY(),
                player.getRandomZ(0.5D),
                0.0D,
                0.0D,
                0.0D
        );

        BlockPos cached = getCachedPlantPos(player);
        if (cached == null) {
            return;
        }

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    player.level().addParticle(
                            ParticleTypes.HAPPY_VILLAGER,
                            cached.getX() + x,
                            cached.getY() + y,
                            cached.getZ() + z,
                            0.0D,
                            0.0D,
                            0.0D
                    );
                }
            }
        }
    }

    /**
     * 卸下术石时撤销复苏之叶授予的飞行。
     */
    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (context.entity() instanceof Player player) {
            revokeFlight(player);
        }
    }

    /**
     * 物品 tooltip。
     * 统一样式：
     * - 普通介绍：紫色；
     * - 数字 / 冷却：金色；
     * - 负面代价：红色。
     * 这里在国际化 key 后方写中文注释，方便后续维护语言文件。
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift()); // 按住 Shift 查看详情。
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.active")
                .withStyle(ChatFormatting.DARK_PURPLE)); // 主动能力
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.active.1" // 消耗经验治疗附近生物，并净化凋零玫瑰。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.cooldown", // 冷却时间：%s 秒。
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.REVIVAL_LEAVES_COOLDOWN.get() / 20.0F))
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.passive")
                .withStyle(ChatFormatting.DARK_PURPLE)); // 被动能力
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.1" // 随时间缓慢恢复生命值。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.2" // 清除饥饿、中毒与凋零效果。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.3" // 加速附近作物生长。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.4" // 附近存在植物时允许飞行。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.5" // 离开植物范围后，飞行能力会很快消失。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.6" // 不会覆盖更强遗物提供的飞行能力。
        ));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.revival_leaves.passive.7" // 自然祝福粒子会标记它的力量。
        ));
        tooltip.add(SpellstoneTooltip.negative(
                "tooltip.enigmatic_legacy.revival_leaves.passive.8" // 火焰与弹射物伤害会变得更加危险。
        ));
    }
}