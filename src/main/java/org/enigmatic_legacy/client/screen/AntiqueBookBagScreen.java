package org.enigmatic_legacy.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.menu.AntiqueBookBagMenu;
import org.jetbrains.annotations.NotNull;

public class AntiqueBookBagScreen extends AbstractContainerScreen<AntiqueBookBagMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            EnigmaticLegacy.MODID,
            "textures/gui/antique_book_bag.png"
    );

    public AntiqueBookBagScreen(AntiqueBookBagMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
