package org.enigmatic_legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.item.ModItems;
import org.enigmatic_legacy.network.ThunderScrollSweepPayload;

/**
 * 万钧之护卷轴客户端输入监听。
 *
 * <p>只在玩家左键“空点”时发包，命中方块或实体时交给原版攻击流程。
 * 客户端不判断 Curios 中是否真的有卷轴，最终效果由服务端包处理再次校验。</p>
 */
@EventBusSubscriber(
        modid = EnigmaticLegacy.MODID,
        value = Dist.CLIENT
)
public final class ClientThunderScrollEvents {
    private ClientThunderScrollEvents() {
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        if (!minecraft.player.onGround()) {
            return;
        }

        if (minecraft.player.getCooldowns().isOnCooldown(ModItems.THUNDER_SCROLL.get())) {
            return;
        }

        if (!minecraft.player.getMainHandItem().canPerformAction(ItemAbilities.SWORD_SWEEP)) {
            return;
        }

        if (minecraft.hitResult != null && minecraft.hitResult.getType() != HitResult.Type.MISS) {
            return;
        }

        PacketDistributor.sendToServer(new ThunderScrollSweepPayload());

        /*
         * 空点时取消后续原版处理，并要求客户端播放挥手动画。
         * 命中方块或实体时上面的 hitResult 检查已经 return，不会影响正常攻击/破坏。
         */
        event.setSwingHand(true);
        event.setCanceled(true);
    }
}
