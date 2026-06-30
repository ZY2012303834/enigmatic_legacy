package org.enigmatic_legacy.item.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * 灵魂水晶 / Soul Crystal。
 *
 * <p>玩家死亡时可能损失一枚灵魂水晶，每枚会让最大生命值降低 10%。
 * 取回死亡时生成的灵魂水晶可以恢复一枚已损失的灵魂碎片。
 */
public class SoulCrystal extends Item {

    public static final String LOST_SOUL_TAG = "enigmatic_legacy.lostsoulfragments";
    private static final String OWNER_TAG = "Owner";
    public static final ResourceLocation LOST_SOUL_HEALTH_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "lost_soul_health");

    private final Map<Player, Multimap<Attribute, AttributeModifier>> attributeDispatcher = new WeakHashMap<>();

    public SoulCrystal() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.soulCrystal1").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.soulCrystal2").withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.translatable("tooltip.enigmatic_legacy.hold_shift").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    public ItemStack createCrystalFrom(Player player) {
        setLostCrystals(player, getLostCrystals(player) + 1);

        ItemStack stack = new ItemStack(this);
        setOwner(stack, player.getUUID());
        return stack;
    }

    public boolean retrieveSoulFromCrystal(Player player, ItemStack stack) {
        if (!hasOwner(stack) || !canRetrieveCrystal(player, stack)) {
            return false;
        }

        int lostFragments = getLostCrystals(player);

        if (lostFragments <= 0) {
            return false;
        }

        setLostCrystals(player, lostFragments - 1);

        if (!player.level().isClientSide) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return true;
    }

    private static boolean canRetrieveCrystal(Player player, ItemStack stack) {
        return player.getUUID().equals(getOwnerId(stack));
    }

    public static boolean hasOwner(ItemStack stack) {
        return getTag(stack).hasUUID(OWNER_TAG);
    }

    public static UUID getOwnerId(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
    }

    private static void setOwner(ItemStack stack, UUID owner) {
        CompoundTag tag = getTag(stack);
        tag.putUUID(OWNER_TAG, owner);
        setTag(stack, tag);
    }

    public void setLostCrystals(Player player, int lost) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.putInt(LOST_SOUL_TAG, Math.max(0, lost));
        this.updatePlayerSoulMap(player);
    }

    public int getLostCrystals(Player player) {
        return Math.max(0, player.getPersistentData().getInt(LOST_SOUL_TAG));
    }

    public void copyLostCrystals(Player oldPlayer, Player newPlayer) {
        int lostCrystals = getLostCrystals(oldPlayer);

        if (lostCrystals > 0) {
            newPlayer.getPersistentData().putInt(LOST_SOUL_TAG, lostCrystals);
        } else {
            newPlayer.getPersistentData().remove(LOST_SOUL_TAG);
        }
    }

    public void updatePlayerSoulMap(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        // 先移除旧 modifier，再按最新损失数量重建，避免重复叠加。
        maxHealth.removeModifier(LOST_SOUL_HEALTH_MODIFIER);

        int lostFragments = getLostCrystals(player);

        if (lostFragments > 0) {
            AttributeModifier modifier = new AttributeModifier(
                    LOST_SOUL_HEALTH_MODIFIER,
                    -0.1D * lostFragments,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
            maxHealth.addTransientModifier(modifier);
        }

        Multimap<Attribute, AttributeModifier> soulMap = HashMultimap.create();
        this.attributeDispatcher.put(player, soulMap);
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
