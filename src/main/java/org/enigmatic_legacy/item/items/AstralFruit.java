package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.util.AstralFruitSlotHelper;
import org.enigmatic_legacy.util.CursedRingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 天体果实。
 * 当前规则：
 * 1. 任何玩家都可以食用并获得药水效果；
 * 2. 只有佩戴七咒之戒并且第一次食用时，才会永久增加 1 个戒指栏位；
 * 3. 未佩戴七咒之戒食用，只获得药水效果；
 * 4. 第二次及以后食用，只获得药水效果；
 * 5. 永久栏位效果通过 ConsumedAstralFruit 标记限制，只能生效一次。
 */
public class AstralFruit extends Item {

    /**
     * 玩家是否已经通过天体果实获得过永久戒指栏位。
     */
    public static final String CONSUMED_FRUIT_TAG = "ConsumedAstralFruit";

    public AstralFruit() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant()
                .food(new FoodProperties.Builder()
                        .nutrition(5)
                        .saturationModifier(20.0F)
                        .alwaysEdible()
                        .build()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        /*
         * 不再阻止未佩戴七咒之戒的玩家食用。
         *
         * 原逻辑：
         * - 未佩戴七咒之戒直接 fail，无法食用。
         *
         * 新逻辑：
         * - 未佩戴七咒之戒也能食用；
         * - 但只获得药水效果；
         * - 不会增加戒指栏位。
         */
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull LivingEntity entity
    ) {
        if (entity instanceof Player player && !level.isClientSide()) {
            boolean hasCursedRing = CursedRingHelper.hasCursedRing(player);
            boolean alreadyConsumed = player.getPersistentData().getBoolean(CONSUMED_FRUIT_TAG);

            /*
             * 天体果实永久戒指栏位效果：
             *
             * 1. 必须佩戴七咒之戒；
             * 2. 必须是第一次触发；
             * 3. 成功后写入 ConsumedAstralFruit = true；
             * 4. 后续再次食用只获得基础药水效果，不再增加栏位；
             * 5. 未佩戴七咒之戒食用也只获得基础药水效果。
             */
            if (hasCursedRing && !alreadyConsumed) {
                boolean granted = AstralFruitSlotHelper.grantPermanentRingSlot(player);

                if (granted) {
                    player.getPersistentData().putBoolean(CONSUMED_FRUIT_TAG, true);

                    level.playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.BEACON_ACTIVATE,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F
                    );
                }
            }

            /*
             * 无论是否佩戴七咒之戒、是否第一次食用，
             * 都会获得天体果实的基础药水效果。
             */
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3000, 3, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3000, 2, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4000, 3, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 5000, 0, false, true));
        }

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.astral_fruit.1")
                    .withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.astral_fruit.2")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.astral_fruit.3")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}