package org.enigmatic_legacy.client.util;

import net.minecraft.client.Camera;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.enigmatic_legacy.item.items.charm.ScorchedCharm;
import org.enigmatic_legacy.item.items.spellstone.BlazingCore;
import org.enigmatic_legacy.util.BlazingCoreHelper;
import org.enigmatic_legacy.util.ScorchedCharmHelper;

public final class ClientLavaVisionHelper {
    // 熔岩雾起始距离。原版默认值：普通岩浆 0.25F，火焰抗性 0.0F。
    public static final float LAVA_FOG_START = 0.25F;

    // 熔岩雾结束距离。原版默认值：普通岩浆 1.0F，火焰抗性 5.0F。数值越大，在岩浆中看得越远；推荐范围 16-96。
    public static final float LAVA_FOG_END = 5.0F;

    // 熔岩雾颜色。原版默认值：0.6F, 0.1F, 0.0F。提高红色和绿色会让岩浆视野更明亮。
    public static final float LAVA_FOG_RED = 0.95F;
    public static final float LAVA_FOG_GREEN = 0.42F;
    public static final float LAVA_FOG_BLUE = 0.12F;

    // 清屏颜色透明度。原版默认值：0.0F。保持 0，避免额外覆盖整屏颜色。
    public static final float LAVA_CLEAR_ALPHA = 0.0F;

    // Iris 光影雾密度。原版没有这个值；ittrp 未兼容时会用 isEyeInWater 的岩浆路径套强雾。数值越低越清晰。
    public static final float IRIS_LAVA_FOG_DENSITY = 0.25F;

    private ClientLavaVisionHelper() {
    }

    public static boolean hasLavaVision(Camera camera) {
        Entity entity = camera.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        if (!hasLavaVisionSource(livingEntity)) {
            return false;
        }

        return camera.getFluidInCamera() == FogType.LAVA
                || livingEntity.isInLava()
                || livingEntity.level().getFluidState(camera.getBlockPosition()).is(FluidTags.LAVA)
                || livingEntity.level().getFluidState(livingEntity.blockPosition()).is(FluidTags.LAVA);
    }

    public static boolean hasLavaVisionSource(LivingEntity entity) {
        return BlazingCoreHelper.hasBlazingCore(entity)
                || ScorchedCharmHelper.hasScorchedCharm(entity)
                || entity.hasEffect(MobEffects.FIRE_RESISTANCE)
                || entity.getPersistentData().getInt(BlazingCore.CLIENT_TICK_TAG) >= entity.tickCount - 2
                || entity.getPersistentData().getInt(ScorchedCharm.CLIENT_TICK_TAG) >= entity.tickCount - 2;
    }
}
