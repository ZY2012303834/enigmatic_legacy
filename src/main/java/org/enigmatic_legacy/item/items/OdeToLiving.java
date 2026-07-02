package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.util.AntiqueBookBagHelper;
import org.enigmatic_legacy.util.PlayerInventoryHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 生灵颂词 / Ode to Living Beings。
 *
 * <p>放在快捷栏或古旧书袋中时保护大多数动物，并让被保护动物不会因为七咒之戒主动攻击玩家。
 * 当前实现不再包含攻击例外和黑名单机制。</p>
 */
public class OdeToLiving extends Item {
    public static final int COOLDOWN_TICKS = 1200;

    public OdeToLiving() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    public static Optional<ItemStack> findOde(Player player) {
        return PlayerInventoryHelper.findInHotbar(player, stack -> stack.is(ModItems.ODE_TO_LIVING.get()))
                .or(() -> AntiqueBookBagHelper.findBook(player, ModItems.ODE_TO_LIVING.get()));
    }

    public static boolean hasOde(Player player) {
        return findOde(player).isPresent();
    }

    public static boolean isOdeAnimal(LivingEntity entity) {
        return AnimalGuidebook.isProtectedAnimal(entity)
                || entity instanceof Animal
                || entity instanceof Hoglin;
    }

    public static boolean isProtectedByOde(Player player, LivingEntity entity) {
        return isOdeAnimal(entity) && hasOde(player);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack stack,
            @NotNull Player player,
            @NotNull LivingEntity target,
            @NotNull InteractionHand hand
    ) {
        if (player.getCooldowns().isOnCooldown(this) || !isOdeAnimal(target)) {
            return InteractionResult.PASS;
        }

        appeaseTarget(player, target);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HEART,
                    target.getX(),
                    target.getEyeY(),
                    target.getZ(),
                    5,
                    target.getBbWidth(),
                    0.1D,
                    target.getBbWidth(),
                    0.1D
            );
        }

        if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide());
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
                    .withStyle(ChatFormatting.GOLD));
            return;
        }

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.3")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.void"));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.4")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.5")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.ode_to_living.6")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static void appeaseTarget(Player player, LivingEntity target) {
        if (target instanceof NeutralMob neutralMob) {
            neutralMob.stopBeingAngry();
        }

        if (target instanceof Mob mob) {
            mob.setTarget(null);
            mob.setAggressive(false);
        }
    }
}
