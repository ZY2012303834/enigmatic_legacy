package org.enigmatic_legacy.event;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import org.enigmatic_legacy.util.EnderRingHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 末影之戒服务端事件。
 */
public final class EnderRingEvents {

    public static final String OPEN_ENDER_CHEST_COMMAND = "enigmatic_legacy_ender_chest";

    private static final Set<UUID> OPENED_BY_ENDER_RING = new HashSet<>();

    private EnderRingEvents() {
    }

    /**
     * 注册隐藏用途命令。
     *
     * <p>客户端按键和 UI 按钮会发送这个命令。
     * 真正的权限判断在服务端完成。
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal(OPEN_ENDER_CHEST_COMMAND)
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            openEnderChest(player);
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }

    private static void openEnderChest(ServerPlayer player) {
        if (!EnderRingHelper.hasEnderChestAccess(player)) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.ender_ring.no_access"),
                    true
            );
            return;
        }

        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, owner) -> ChestMenu.threeRows(
                        containerId,
                        inventory,
                        player.getEnderChestInventory()
                ),
                Component.translatable("container.enderchest")
        ));

        OPENED_BY_ENDER_RING.add(player.getUUID());

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENDER_CHEST_OPEN,
                SoundSource.BLOCKS,
                0.5F,
                player.level().random.nextFloat() * 0.1F + 0.9F
        );
    }

    /**
     * 关闭由末影之戒打开的末影箱时播放关闭音效。
     */
    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!OPENED_BY_ENDER_RING.remove(player.getUUID())) {
            return;
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENDER_CHEST_CLOSE,
                SoundSource.BLOCKS,
                0.5F,
                player.level().random.nextFloat() * 0.1F + 0.9F
        );
    }
}