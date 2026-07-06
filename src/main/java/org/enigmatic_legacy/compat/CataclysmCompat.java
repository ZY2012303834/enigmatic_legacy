package org.enigmatic_legacy.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.util.ScorchedCharmHelper;

import java.lang.reflect.Method;

/**
 * L_Ender's Cataclysm 兼容逻辑。
 *
 * <p>灾变 1.21 分支中，腾炎靴子（cataclysm:ignitium_boots）的“岩浆行走”
 * 不是通过放置临时方块实现，而是在 LionfishAPI 的 {@code StandOnFluidEvent}
 * 中取消玩家下沉到岩浆的行为。因此这里不能用方块放置事件拦截，必须处理同一个事件。</p>
 *
 * <p>本项目不把灾变或 LionfishAPI 作为编译期依赖。兼容逻辑通过反射查找事件类和访问器方法，
 * 只有运行时安装了灾变并且 LionfishAPI 的事件存在时才注册监听器。这样没有安装灾变的整合包
 * 不会因为类加载缺失而崩溃。</p>
 */
public final class CataclysmCompat {
    public static final String MODID = "cataclysm";

    private static final String STAND_ON_FLUID_EVENT_CLASS =
            "com.github.L_Ender.lionfishapi.server.event.StandOnFluidEvent";
    private static final ResourceLocation IGNITIUM_BOOTS_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "ignitium_boots");

    private static Method getEntityMethod;
    private static Method getFluidStateMethod;
    private static boolean registered;

    private CataclysmCompat() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerEventHandlers() {
        // 只在灾变已加载时尝试注册；如果没有灾变，后续反射查找没有意义。
        if (registered || !ModList.get().isLoaded(MODID)) {
            return;
        }

        try {
            Class<?> rawEventClass = Class.forName(STAND_ON_FLUID_EVENT_CLASS);
            Class<? extends Event> eventClass = rawEventClass.asSubclass(Event.class);
            getEntityMethod = rawEventClass.getMethod("getEntity");
            getFluidStateMethod = rawEventClass.getMethod("getFluidState");

            /*
             * 灾变自己的监听器会先把 StandOnFluidEvent 设置为 canceled=true，
             * 表示允许腾炎靴子站在岩浆表面。这里需要在最低优先级接收已取消事件，
             * 才能在“同时佩戴阳灼护符和腾炎靴子”时撤销这一次取消。
             */
            NeoForge.EVENT_BUS.addListener(
                    EventPriority.LOWEST,
                    true,
                    (Class) eventClass,
                    CataclysmCompat::onStandOnFluid
            );
            registered = true;
        } catch (ReflectiveOperationException | LinkageError exception) {
            EnigmaticLegacy.LOGGER.warn("Cataclysm lava-walking compatibility was not registered.", exception);
        }
    }

    private static void onStandOnFluid(Event event) {
        // 只有灾变已经取消的流体站立事件才需要处理；普通事件保持原样。
        if (!(event instanceof ICancellableEvent cancellable) || !cancellable.isCanceled()) {
            return;
        }

        LivingEntity entity = getEntity(event);
        if (entity == null || !isLava(event)) {
            return;
        }

        /*
         * 兼容目标很窄：
         * - 必须佩戴阳灼护符；
         * - 脚部装备必须正好是灾变的腾炎靴子；
         * - 流体必须是岩浆或流动岩浆。
         *
         * 满足这些条件时，将 canceled 改回 false，使 LionfishAPI/灾变不再把岩浆当作可站立流体。
         * 其他灾变装备、其他流体、以及没有阳灼护符的情况都不受影响。
         */
        if (ScorchedCharmHelper.hasScorchedCharm(entity) && isIgnitiumBoots(entity.getItemBySlot(EquipmentSlot.FEET))) {
            cancellable.setCanceled(false);
        }
    }

    private static LivingEntity getEntity(Event event) {
        try {
            // StandOnFluidEvent#getEntity() 来自 LionfishAPI；反射调用避免编译期硬依赖。
            Object entity = getEntityMethod.invoke(event);
            return entity instanceof LivingEntity livingEntity ? livingEntity : null;
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private static boolean isLava(Event event) {
        try {
            // 只处理岩浆相关的流体站立行为，避免影响其他 mod 可能扩展的流体站立能力。
            Object fluidState = getFluidStateMethod.invoke(event);
            return fluidState instanceof FluidState state
                    && (state.is(Fluids.LAVA) || state.is(Fluids.FLOWING_LAVA));
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    private static boolean isIgnitiumBoots(ItemStack stack) {
        // 用注册名判断，避免直接引用灾变的 ModItems 或物品类。
        return !stack.isEmpty() && IGNITIUM_BOOTS_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}
