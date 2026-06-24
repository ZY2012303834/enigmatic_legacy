package org.enigmatic_legacy.item.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.event.TeleportParticleEvents;
import org.enigmatic_legacy.util.SpellstoneTooltip;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 非欧立方 / Non-Euclidean Cube。

 * 类型：术石 spellstone。

 * 主动效果：
 * 传送到当前维度的随机结构附近。

 * 被动效果：
 * 1. +35% 疾跑速度；
 * 2. +100% 游泳速度；
 * 3. +60% 挖掘速度；
 * 4. +40% 攻击速度；
 * 5. +1 时运；
 * 6. +1 幸运；
 * 7. 高额伤害阈值免疫；
 * 8. 35% 概率反弹投射物 / 返还伤害；
 * 9. 非投射物伤害给予攻击者负面效果；
 * 10. 击败生物后获得随机正面效果，排除缓降；
 * 11. 不需要在水中呼吸；
 * 12. 免疫负面效果；
 * 13. 免疫挤压、摔落、碰撞、荆棘、传送、火焰、熔岩伤害；
 * 14. 致命伤害保护与濒死主动触发。
 */
public class NonEuclideanCube extends Item implements ICurioItem {
    private static final String SPELLSTONE_SLOT = "spellstone";

    /**
     * 主动技能冷却：120 秒。
     */
    public static final int COOLDOWN_TICKS = 120 * 20;

    /**
     * 主动技能寻找结构的半径，单位是区块。

     * 不建议设置太高，否则容易卡顿。
     */
    public static final int STRUCTURE_SEARCH_RADIUS = 96;

    /**
     * 每次主动最多随机尝试多少个结构类型。
     */
    public static final int STRUCTURE_SEARCH_ATTEMPTS = 16;

    public static final float SPRINT_SPEED_BONUS = 0.35F;
    public static final float SWIM_SPEED_BONUS = 1.00F;
    public static final float DIG_SPEED_BONUS = 0.60F;
    public static final float ATTACK_SPEED_BONUS = 0.40F;
    public static final int FORTUNE_BONUS = 1;
    public static final double LUCK_BONUS = 1.0D;

    public static final float HIGH_DAMAGE_LIMIT_NORMAL = 100.0F;
    public static final float HIGH_DAMAGE_LIMIT_CURSED = 150.0F;

    public static final int REFLECT_CHANCE = 35;

    private static final ResourceLocation SPRINT_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "non_euclidean_cube_sprint_speed"
    );

    private static final ResourceLocation SWIM_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "non_euclidean_cube_swim_speed"
    );

    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "non_euclidean_cube_attack_speed"
    );

    private static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "non_euclidean_cube_luck"
    );

    public NonEuclideanCube() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
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
     * Curios 每 tick 调用。

     * 这里处理属性类加成：
     * - 游泳速度；
     * - 攻击速度；
     * - 幸运；
     * - 疾跑时额外移动速度。

     * 挖掘速度放在 PlayerEvent.BreakSpeed 里处理。
     */
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        entity.getAttributes().removeAttributeModifiers(getBaseModifiers());
        entity.getAttributes().removeAttributeModifiers(getSprintModifiers());

        entity.getAttributes().addTransientAttributeModifiers(getBaseModifiers());

        if (entity.isSprinting()) {
            entity.getAttributes().addTransientAttributeModifiers(getSprintModifiers());
        }

        if (!entity.level().isClientSide()) {
            entity.setAirSupply(entity.getMaxAirSupply());
        }
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();

        entity.getAttributes().removeAttributeModifiers(getBaseModifiers());
        entity.getAttributes().removeAttributeModifiers(getSprintModifiers());

        ICurioItem.super.onUnequip(context, newStack, stack);
    }

    /**
     * 基础常驻属性。
     */
    private static Multimap<Holder<Attribute>, AttributeModifier> getBaseModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                NeoForgeMod.SWIM_SPEED,
                new AttributeModifier(
                        SWIM_SPEED_ID,
                        SWIM_SPEED_BONUS,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );

        modifiers.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ATTACK_SPEED_ID,
                        ATTACK_SPEED_BONUS,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );

        modifiers.put(
                Attributes.LUCK,
                new AttributeModifier(
                        LUCK_ID,
                        LUCK_BONUS,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );

        return modifiers;
    }

    /**
     * 疾跑时额外移动速度。
     */
    private static Multimap<Holder<Attribute>, AttributeModifier> getSprintModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        modifiers.put(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                        SPRINT_SPEED_ID,
                        SPRINT_SPEED_BONUS,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        return modifiers;
    }

    /**
     * Curios 时运加成。
     */
    @Override
    public int getFortuneLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return FORTUNE_BONUS;
    }

    /**
     * 普通快捷键主动效果。
     */
    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        triggerActiveAbility(level, player, stack, false);
    }

    /**
     * 主动效果入口。
     *
     * @param emergency 是否是濒死被动触发。true 时无视冷却，用于救命。
     */
    public boolean triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack, boolean emergency) {
        if (!emergency && player.getCooldowns().isOnCooldown(this)) {
            return false;
        }

        BlockPos destination = findRandomStructurePosition(level, player);

        if (destination == null) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.non_euclidean_cube.no_structure")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return false;
        }

        TeleportParticleEvents.spawnDepartureParticles(player);

        Vec3 pos = new Vec3(
                destination.getX() + 0.5D,
                destination.getY(),
                destination.getZ() + 0.5D
        );

        player.stopRiding();
        player.teleportTo(level, pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
        player.resetFallDistance();

        TeleportParticleEvents.scheduleArrivalParticles(player, 4);

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        player.displayClientMessage(
                Component.translatable("message.enigmatic_legacy.non_euclidean_cube.teleported")
                        .withStyle(ChatFormatting.DARK_PURPLE),
                true
        );

        return true;
    }

    /**
     * 在当前维度随机寻找一个结构并传送过去。

     * 你当前 NeoForge 1.21.1 的 ServerLevel#findNearestMapStructure
     * 返回 BlockPos，不是 Pair<BlockPos, Holder<Structure>>。
     */
    private static BlockPos findRandomStructurePosition(ServerLevel level, ServerPlayer player) {
        List<TagKey<Structure>> structureTags = List.of(
                StructureTags.EYE_OF_ENDER_LOCATED,
                StructureTags.ON_WOODLAND_EXPLORER_MAPS,
                StructureTags.ON_OCEAN_EXPLORER_MAPS,
                StructureTags.ON_TREASURE_MAPS,
                StructureTags.CATS_SPAWN_IN,
                StructureTags.CATS_SPAWN_AS_BLACK,
                StructureTags.VILLAGE,
                StructureTags.MINESHAFT,
                StructureTags.SHIPWRECK,
                StructureTags.RUINED_PORTAL,
                StructureTags.OCEAN_RUIN
        );

        for (int attempt = 0; attempt < STRUCTURE_SEARCH_ATTEMPTS; attempt++) {
            TagKey<Structure> tag = structureTags.get(
                    player.getRandom().nextInt(structureTags.size())
            );

            BlockPos structurePos = level.findNearestMapStructure(
                    tag,
                    player.blockPosition(),
                    STRUCTURE_SEARCH_RADIUS,
                    false
            );

            if (structurePos == null) {
                continue;
            }

            BlockPos surfacePos = level.getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    structurePos
            );

            if (!level.getWorldBorder().isWithinBounds(surfacePos)) {
                continue;
            }

            return surfacePos;
        }

        return null;
    }

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
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.active"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.cooldown", SpellstoneTooltip.number("120.0")));

        tooltip.add(Component.empty());

        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.spellstone.passive"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.passive.1",
                SpellstoneTooltip.number("+35%"),
                SpellstoneTooltip.number("+100%"),
                SpellstoneTooltip.number("+60%"),
                SpellstoneTooltip.number("+40%")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.passive.2",
                SpellstoneTooltip.number("+1"),
                SpellstoneTooltip.number("+1")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.passive.3",
                SpellstoneTooltip.number("100"),
                SpellstoneTooltip.number("150")));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.passive.4",
                SpellstoneTooltip.number("35%")));
        tooltip.add(SpellstoneTooltip.negative("tooltip.enigmatic_legacy.non_euclidean_cube.passive.5"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.passive.6"));
        tooltip.add(SpellstoneTooltip.text("tooltip.enigmatic_legacy.non_euclidean_cube.passive.7"));
    }
}
