package org.enigmatic_legacy.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.config.ConfigClient;
import org.enigmatic_legacy.item.items.ForbiddenFruit;
import org.enigmatic_legacy.potion.ModEffects;

/**
 * 禁忌之果客户端 HUD。
 *
 * <p>已食用禁忌之果时，原版饱食度不再代表真实玩法状态，因此替换为原版
 * Enigmatic Legacy 使用的空心图标栏。
 */
public final class ForbiddenFruitClientEvents {

    private static final ResourceLocation GENERIC_ICONS =
            ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "textures/gui/generic_icons.png");

    private ForbiddenFruitClientEvents() {
    }

    @SubscribeEvent
    public static void onRenderFoodLayer(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.FOOD_LEVEL.equals(event.getName())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || !hasConsumedForbiddenFruit(player)) {
            return;
        }

        if (!ConfigClient.FORBIDDEN_FRUIT_RENDER_HUNGER_BAR.get()) {
            event.setCanceled(true);
            return;
        }

        if (player.isCreative() || player.isSpectator()
                || !ConfigClient.FORBIDDEN_FRUIT_REPLACE_HUNGER_BAR.get()) {
            return;
        }

        event.setCanceled(true);
        renderForbiddenFoodBar(event.getGuiGraphics(), player);
    }

    private static boolean hasConsumedForbiddenFruit(LocalPlayer player) {
        return ForbiddenFruit.hasConsumedFruit(player) || player.hasEffect(ModEffects.FORBIDDEN_FRUIT);
    }

    private static void renderForbiddenFoodBar(GuiGraphics guiGraphics, LocalPlayer player) {
        int right = guiGraphics.guiWidth() / 2 + 91;
        int top = guiGraphics.guiHeight() - 39;

        for (int index = 0; index < 10; index++) {
            int x = right - index * 8 - 9;
            int y = top;

            if (player.getFoodData().getSaturationLevel() <= 0.0F) {
                y += Mth.floor(Mth.sin((player.tickCount + index * 13) * 0.55F));
            }

            guiGraphics.blit(GENERIC_ICONS, x, y, 0, 0, 9, 9, 9, 9);
        }
    }
}
