package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.util.ExperienceHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 超维容器 / Extradimensional Vessel。
 *
 * <p>原版注册名为 {@code storage_crystal}。它负责保存玩家死亡时掉落的物品和部分经验；
 * 在世界中实际由 {@link org.enigmatic_legacy.entity.PermanentItemEntity} 承载。
 */
public class StorageCrystal extends Item {

    private static final String STORED_STACKS_TAG = "storedStacks";
    private static final String STORED_XP_TAG = "storedXP";
    private static final String STORED_FLAG_TAG = "isStored";
    private static final String EMBEDDED_SOUL_TAG = "embeddedSoul";

    public StorageCrystal() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    public ItemStack storeDropsOnCrystal(Collection<ItemEntity> drops, Player player, ItemStack embeddedSoulCrystal) {
        ItemStack crystal = new ItemStack(this);
        CompoundTag crystalTag = getTag(crystal);
        ListTag storedStacks = new ListTag();

        for (ItemEntity drop : drops) {
            ItemStack dropStack = drop.getItem();

            if (!dropStack.isEmpty()) {
                storedStacks.add(dropStack.save(player.registryAccess()));
                dropStack.setCount(0);
                drop.discard();
            }
        }

        crystalTag.put(STORED_STACKS_TAG, storedStacks);

        if (!embeddedSoulCrystal.isEmpty()) {
            crystalTag.put(EMBEDDED_SOUL_TAG, embeddedSoulCrystal.save(player.registryAccess()));
        }

        int exp = ExperienceHelper.getPlayerXP(player);
        ExperienceHelper.drainPlayerXP(player, exp);

        // 当前项目还没有原版 Enigmatic Amulet 配置，先按原版默认用途保存全部死亡经验。
        crystalTag.putInt(STORED_XP_TAG, exp);
        crystalTag.putBoolean(STORED_FLAG_TAG, true);
        setTag(crystal, crystalTag);

        return crystal;
    }

    public boolean canRetrieveDropsFromCrystal(ItemStack crystal, Player player) {
        ItemStack embeddedSoulCrystal = getEmbeddedSoulCrystal(crystal, player);
        UUID owner = SoulCrystal.getOwnerId(embeddedSoulCrystal);

        return owner == null || player.getUUID().equals(owner);
    }

    public boolean retrieveDropsFromCrystal(ItemStack crystal, Player player) {
        if (!canRetrieveDropsFromCrystal(crystal, player)) {
            return false;
        }

        CompoundTag crystalTag = getTag(crystal);
        ListTag storedStacks = crystalTag.getList(STORED_STACKS_TAG, Tag.TAG_COMPOUND);

        for (int index = storedStacks.size() - 1; index >= 0; index--) {
            ItemStack stack = ItemStack.parseOptional(player.registryAccess(), storedStacks.getCompound(index));

            if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }

        ExperienceHelper.addPlayerXP(player, crystalTag.getInt(STORED_XP_TAG));

        ItemStack soulCrystal = getEmbeddedSoulCrystal(crystal, player);

        if (!soulCrystal.isEmpty() && soulCrystal.getItem() instanceof SoulCrystal soulCrystalItem) {
            soulCrystalItem.retrieveSoulFromCrystal(player, soulCrystal);
        } else {
            player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        crystalTag.remove(STORED_STACKS_TAG);
        crystalTag.remove(EMBEDDED_SOUL_TAG);
        crystalTag.putInt(STORED_XP_TAG, 0);
        crystalTag.putBoolean(STORED_FLAG_TAG, false);
        setTag(crystal, crystalTag);
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        CompoundTag tag = getTag(stack);
        int storedStacks = tag.getList(STORED_STACKS_TAG, Tag.TAG_COMPOUND).size();
        int storedXP = tag.getInt(STORED_XP_TAG);

        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.storageCrystal1").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.storageCrystal2", storedStacks).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.storageCrystal3", storedXP).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static UUID getEmbeddedSoulOwner(ItemStack crystal, Player player) {
        ItemStack embeddedSoulCrystal = getEmbeddedSoulCrystal(crystal, player);
        return SoulCrystal.getOwnerId(embeddedSoulCrystal);
    }

    private static ItemStack getEmbeddedSoulCrystal(ItemStack crystal, Player player) {
        CompoundTag crystalTag = getTag(crystal);

        if (!crystalTag.contains(EMBEDDED_SOUL_TAG)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.parseOptional(player.registryAccess(), crystalTag.getCompound(EMBEDDED_SOUL_TAG));
    }
}
