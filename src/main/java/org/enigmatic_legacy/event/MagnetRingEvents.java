package org.enigmatic_legacy.event;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.enigmatic_legacy.util.MagnetRingHelper;

import java.util.Optional;

/**
 * 磁力之戒服务端事件。
 * 这里不处理 UI 渲染。
 * UI 只在客户端负责显示按钮；
 * 真正的开关切换必须放在服务端，防止客户端伪造状态。
 */
public final class MagnetRingEvents {
    private MagnetRingEvents() {
    }

    /**
     * 磁力戒指会临时指定掉落物拾取目标，避免被其他玩家蹭到。
     * 这里负责过期清理，防止公共掉落物永久绑定给某个玩家。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity item) || item.level().isClientSide) {
            return;
        }

        MagnetRingHelper.clearExpiredMagnetPickupReservation(item);
    }

    /**
     * 注册隐藏命令：
     * /enigmatic_legacy_toggle_magnet_ring
     * 客户端按钮点击后会发送这个命令。
     * 服务端收到后检查玩家是否真的佩戴磁力之戒，
     * 然后切换那枚戒指 ItemStack 里的 MagnetEnabled 状态。
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal(MagnetRingHelper.TOGGLE_COMMAND)
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            toggleMagnetRing(player);
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }

    /**
     * 服务端切换磁力之戒状态。
     */
    private static void toggleMagnetRing(ServerPlayer player) {
        // 查找磁力之戒或转位之戒。
        // 如果玩家只佩戴转位之戒，也允许使用同一个磁力开关按钮。
        Optional<ItemStack> ring = MagnetRingHelper.findEquippedMagnetControlRing(player);

        if (ring.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.enigmatic_legacy.magnet_ring.no_ring"),
                    true
            );
            return;
        }

        boolean enabled = MagnetRingHelper.toggleMagnet(ring.get());

        // 让当前容器尽快同步物品数据。
        // Curios 槽位本身也会同步，但这里主动广播一次，按钮反馈会更及时。
        player.containerMenu.broadcastChanges();

        player.displayClientMessage(
                Component.translatable(
                        enabled
                                ? "message.enigmatic_legacy.magnet_control.enabled"
                                : "message.enigmatic_legacy.magnet_control.disabled",
                        Component.translatable(MagnetRingHelper.getMagnetControlRingNameKey(ring.get()))
                ),
                true
        );
    }
}
