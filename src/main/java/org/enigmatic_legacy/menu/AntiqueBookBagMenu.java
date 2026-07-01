package org.enigmatic_legacy.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.enigmatic_legacy.item.items.AntiqueBookBag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AntiqueBookBagMenu extends AbstractContainerMenu {
    private static final int BAG_SLOT_COUNT = AntiqueBookBag.SLOT_COUNT;

    private final Inventory playerInventory;
    private final Container bagContainer;

    public AntiqueBookBagMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, inventory);
    }

    public AntiqueBookBagMenu(int containerId, Inventory inventory) {
        super(ModMenus.ANTIQUE_BOOK_BAG.get(), containerId);
        this.playerInventory = inventory;
        this.bagContainer = createContainer(inventory);

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 6; column++) {
                int slot = column + row * 6;
                this.addSlot(new BookSlot(this.bagContainer, slot, 35 + column * 18, 24 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (slotIndex < BAG_SLOT_COUNT) {
            if (!this.moveItemStackTo(original, BAG_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (AntiqueBookBag.canStore(original)) {
            if (!this.moveItemStackTo(original, 0, BAG_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (original.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (original.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, original);
        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    private static Container createContainer(Inventory inventory) {
        SimpleContainer container = new SimpleContainer(BAG_SLOT_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                saveToBag(this, inventory);
            }
        };

        List<ItemStack> books = AntiqueBookBag.getStoredBooks(inventory.player);
        for (int slot = 0; slot < Math.min(BAG_SLOT_COUNT, books.size()); slot++) {
            container.setItem(slot, books.get(slot));
        }

        return container;
    }

    private static void saveToBag(Container container, Inventory inventory) {
        List<ItemStack> books = new java.util.ArrayList<>(BAG_SLOT_COUNT);
        for (int slot = 0; slot < BAG_SLOT_COUNT; slot++) {
            books.add(container.getItem(slot));
        }

        AntiqueBookBag.setStoredBooks(inventory.player, books);
    }

    public static class BookSlot extends Slot {
        public BookSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return AntiqueBookBag.canStore(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
