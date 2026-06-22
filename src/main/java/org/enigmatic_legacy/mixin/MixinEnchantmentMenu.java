package org.enigmatic_legacy.mixin;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.enigmatic_legacy.util.EnchanterPearlHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {
    @Shadow
    @Final
    private Container enchantSlots;

    @Shadow
    @Final
    private ContainerLevelAccess access;

    @Shadow
    @Final
    private DataSlot enchantmentSeed;

    @Shadow
    @Final
    public int[] costs;

    @Shadow
    private List<EnchantmentInstance> getEnchantmentList(
            RegistryAccess registryAccess,
            ItemStack stack,
            int enchantSlot,
            int level
    ) {
        throw new AssertionError();
    }

    protected MixinEnchantmentMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$enchantWithPearl(
            Player player,
            int id,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!EnchanterPearlHelper.canUseEnchanterPearl(player)) {
            return;
        }

        if (id < 0 || id >= this.costs.length) {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + id);
            callback.setReturnValue(false);
            return;
        }

        ItemStack input = this.enchantSlots.getItem(0);
        int levelsRequired = id + 1;

        if (this.costs[id] <= 0
                || input.isEmpty()
                || (player.experienceLevel < levelsRequired || player.experienceLevel < this.costs[id])
                && !player.getAbilities().instabuild) {
            callback.setReturnValue(false);
            return;
        }

        this.access.execute((level, pos) -> {
            ItemStack enchantedItem = input;
            List<EnchantmentInstance> rolledEnchantments = this.getEnchantmentList(
                    level.registryAccess(),
                    input,
                    id,
                    this.costs[id]
            );

            if (rolledEnchantments.isEmpty()) {
                return;
            }

            ItemStack doubleRoll = EnchantmentHelper.enchantItem(
                    player.getRandom(),
                    enchantedItem.copy(),
                    Math.min(this.costs[id] + 7, 40),
                    level.registryAccess(),
                    Optional.empty()
            );

            player.onEnchantmentPerformed(input, levelsRequired);

            if (input.is(Items.BOOK)) {
                enchantedItem = input.transmuteCopy(Items.ENCHANTED_BOOK, 1);
                this.enchantSlots.setItem(0, enchantedItem);
            }

            for (EnchantmentInstance enchantment : rolledEnchantments) {
                enchantedItem.enchant(enchantment.enchantment, enchantment.level);
            }

            enchantedItem = EnchanterPearlHelper.mergeEnchantments(enchantedItem, doubleRoll);
            this.enchantSlots.setItem(0, enchantedItem);

            player.awardStat(Stats.ENCHANT_ITEM);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayer, enchantedItem, levelsRequired);
            }

            this.enchantSlots.setChanged();
            this.enchantmentSeed.set(player.getEnchantmentSeed());
            this.slotsChanged(this.enchantSlots);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.random.nextFloat() * 0.1F + 0.9F
            );
        });

        callback.setReturnValue(true);
    }

    @Inject(method = "getGoldCount", at = @At("HEAD"), cancellable = true)
    private void enigmaticLegacy$getLapisCount(CallbackInfoReturnable<Integer> callback) {
        Player player = null;

        for (Slot slot : this.slots) {
            if (slot.container instanceof Inventory inventory) {
                player = inventory.player;
                break;
            }
        }

        if (player != null && EnchanterPearlHelper.canUseEnchanterPearl(player)) {
            callback.setReturnValue(64);
        }
    }
}
