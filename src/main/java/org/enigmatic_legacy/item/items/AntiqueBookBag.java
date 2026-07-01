package org.enigmatic_legacy.item.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.menu.AntiqueBookBagMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 古旧书袋 / Antique Book Bag。
 *
 * <p>复刻 Enigmatic Addons 的书袋定位：
 * 它是一个只能收纳书类物品的便携容器。放入其中的书会被本项目的书类判定视作有效，
 * 并且书袋本身放在玩家末影箱里时也会继续提供这些书的效果。</p>
 */
public class AntiqueBookBag extends Item {
    public static final int SLOT_COUNT = 12;

    public static final String ROOT_TAG = "enigmatic_legacy_antique_book_bag";
    private static final String BOOKS_TAG = "Books";
    private static final String SLOT_TAG = "Slot";
    private static final String ITEM_TAG = "Item";

    public AntiqueBookBag() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    /**
     * 只有书架书籍标签内的物品可以放入书袋；书袋本身禁止嵌套。
     */
    public static boolean canStore(ItemStack stack) {
        return !stack.isEmpty()
                && !stack.is(ModItems.ANTIQUE_BOOK_BAG.get())
                && stack.is(ItemTags.BOOKSHELF_BOOKS);
    }

    public static List<ItemStack> getStoredBooks(Player player) {
        List<ItemStack> books = new ArrayList<>(SLOT_COUNT);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            books.add(ItemStack.EMPTY);
        }

        CompoundTag tag = player.getPersistentData().getCompound(ROOT_TAG);
        ListTag list = tag.getList(BOOKS_TAG, Tag.TAG_COMPOUND);
        for (int index = 0; index < list.size(); index++) {
            CompoundTag entry = list.getCompound(index);
            int slot = entry.getInt(SLOT_TAG);

            if (slot >= 0 && slot < SLOT_COUNT) {
                books.set(slot, ItemStack.parseOptional(player.registryAccess(), entry.getCompound(ITEM_TAG)));
            }
        }

        return books;
    }

    public static void setStoredBooks(Player player, List<ItemStack> books) {
        CompoundTag tag = player.getPersistentData().getCompound(ROOT_TAG);
        ListTag list = new ListTag();
        for (int slot = 0; slot < Math.min(SLOT_COUNT, books.size()); slot++) {
            ItemStack book = books.get(slot);

            if (book.isEmpty()) {
                continue;
            }

            CompoundTag entry = new CompoundTag();
            entry.putInt(SLOT_TAG, slot);
            entry.put(ITEM_TAG, book.save(player.registryAccess()));
            list.add(entry);
        }

        tag.put(BOOKS_TAG, list);
        player.getPersistentData().put(ROOT_TAG, tag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, opener) -> new AntiqueBookBagMenu(containerId, inventory),
                    Component.translatable("container.enigmatic_legacy.antique_book_bag")
            ));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.antique_book_bag.1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.antique_book_bag.2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.antique_book_bag.3")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.enigmatic_legacy.antique_book_bag.4")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

}
