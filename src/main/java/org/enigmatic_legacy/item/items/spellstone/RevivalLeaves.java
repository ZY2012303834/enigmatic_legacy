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
 * <p>复刻 Enigmatic Addons 的自然系术石：
 * 主动消耗经验，给周围生物生命恢复并净化凋零玫瑰；
 * 被动提供缓慢自然恢复、清除饥饿/中毒/凋零、加速作物生长；
 * 周围有植物方块时允许短暂飞行，但火焰和弹射物伤害会更危险。</p>
 */
public class RevivalLeaves extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";
    private static final String FLIGHT_GRANTED_TAG = "enigmatic_legacy.revival_leaves_flight_granted";
    private static final String FLIGHT_GRACE_TAG = "enigmatic_legacy.revival_leaves_flight_grace";
    private static final String FLIGHT_PLANT_POS_TAG = "enigmatic_legacy.revival_leaves_plant_pos";
    private static final int PLANT_SEARCH_RADIUS = 5;
    private static final int PLANT_FLIGHT_GRACE_TICKS = 5;

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

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return isSpellstoneSlot(context);
    }

    private static boolean isSpellstoneSlot(SlotContext context) {
        return context != null && SPELLSTONE_SLOT.equals(context.identifier());
    }

    /**
     * 主动能力：消耗经验，为周围生物施加生命恢复，并把附近凋零玫瑰净化为虞美人。
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

    private static void cleanseWitherRoses(ServerLevel level, BlockPos origin) {
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(5, 5, 5), origin.offset(-5, -5, -5))) {
            if (level.getBlockState(pos).is(Blocks.WITHER_ROSE)) {
                level.destroyBlock(pos, false);
                level.setBlock(pos, Blocks.POPPY.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

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

    private static void handleNaturalRegeneration(LivingEntity entity) {
        int interval = Math.max(5, ConfigCommon.REVIVAL_LEAVES_NATURAL_REGENERATION_TICK.get());

        if (entity.tickCount % interval == 0 && entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(Math.max(0.5F, entity.getMaxHealth() / 100.0F));
        }
    }

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

    private static void sendHappyParticles(ServerLevel level, Player player, BlockPos pos, int chance) {
        if (player.getRandom().nextInt(chance) != 0) {
            return;
        }

        Vec3 center = pos.getCenter();
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 1, 0.2D, 0.2D, 0.2D, 0.0D);
    }

    /**
     * 周围存在植物方块时授予飞行能力。
     *
     * <p>这里用 PersistentData 记录“由复苏之叶授予的飞行”，
     * 取下或离开植物范围时只撤销自己授予的能力，尽量不影响创造模式、旁观模式或创造之心等其它飞行来源。</p>
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

    private static void grantFlight(Player player) {
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        player.getPersistentData().putBoolean(FLIGHT_GRANTED_TAG, true);
    }

    private static void revokeFlight(Player player) {
        if (!player.getPersistentData().getBoolean(FLIGHT_GRANTED_TAG)) {
            return;
        }

        player.getPersistentData().remove(FLIGHT_GRANTED_TAG);
        player.getPersistentData().remove(FLIGHT_GRACE_TAG);
        player.getPersistentData().remove(FLIGHT_PLANT_POS_TAG);

        if (!player.isCreative() && !player.isSpectator() && !HeartOfCreationHelper.hasHeartOfCreationEquipped(player)) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

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

    private static BlockPos getCachedPlantPos(Player player) {
        if (!player.getPersistentData().contains(FLIGHT_PLANT_POS_TAG)) {
            return null;
        }

        return BlockPos.of(player.getPersistentData().getLong(FLIGHT_PLANT_POS_TAG));
    }

    private static boolean isValidPlantForFlight(Player player, BlockPos pos) {
        BlockState state = player.level().getBlockState(pos);
        return !state.is(Blocks.WATER)
                && PLANT_TAGS.stream().anyMatch(state::is)
                && pos.distToCenterSqr(player.position()) <= 36.0D;
    }

    private static void spawnFlightParticles(Player player) {
        if (!player.getAbilities().flying || player.tickCount % 12 != 0) {
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

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (context.entity() instanceof Player player) {
            revokeFlight(player);
        }
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(SpellstoneTooltip.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(SpellstoneTooltip.holdShift());
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.active")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.active.1"));
        tooltip.add(SpellstoneTooltip.text(
                "tooltip.enigmatic_legacy.spellstone.cooldown",
                SpellstoneTooltip.number(String.format("%.1f", ConfigCommon.REVIVAL_LEAVES_COOLDOWN.get() / 20.0F))
        ));

        tooltip.add(SpellstoneTooltip.empty());

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.spellstone.passive")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.1"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.2"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.3"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.4"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.5"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.6"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.revival_leaves.passive.7"));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.revival_leaves.passive.8"));
    }
}
