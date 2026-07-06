package org.enigmatic_legacy.compat;

/**
 * Iris 光影兼容逻辑。
 *
 * <p>这里使用反射检测 Iris 是否正在启用光影包，
 * 避免在没有安装 Iris 的环境中直接加载 Iris 类。</p>
 */
public final class IrisCompat {
    private static Object shaderPackInUseMethod;
    private static boolean lookupAttempted;
    private static Object capturedRenderingState;
    private static java.lang.reflect.Method setFogColorMethod;
    private static java.lang.reflect.Method setFogDensityMethod;
    private static boolean fogLookupAttempted;

    private IrisCompat() {
    }

    /**
     * 判断 Iris 当前是否启用了光影包。
     */
    public static boolean isShaderPackInUse() {
        if (!resolveShaderPackHook()) {
            return false;
        }

        try {
            return (boolean) ((java.lang.reflect.Method) shaderPackInUseMethod).invoke(null);
        } catch (ReflectiveOperationException exception) {
            shaderPackInUseMethod = null;
            return false;
        }
    }

    /**
     * 设置 Iris 光影使用的雾颜色和雾密度。
     *
     * <p>部分光影包不完全读取原版雾距离，因此需要同步写入 Iris 捕获的渲染状态。</p>
     */
    public static void setFog(float red, float green, float blue, float density) {
        if (!resolveFogHooks()) {
            return;
        }

        try {
            setFogColorMethod.invoke(capturedRenderingState, red, green, blue);
            setFogDensityMethod.invoke(capturedRenderingState, density);
        } catch (ReflectiveOperationException ignored) {
            capturedRenderingState = null;
            setFogColorMethod = null;
            setFogDensityMethod = null;
        }
    }

    private static boolean resolveShaderPackHook() {
        if (shaderPackInUseMethod != null) {
            return true;
        }

        if (lookupAttempted) {
            return false;
        }

        lookupAttempted = true;

        try {
            Class<?> irisClass = Class.forName("net.irisshaders.iris.Iris");
            shaderPackInUseMethod = irisClass.getMethod("isPackInUseQuick");
            return true;
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    private static boolean resolveFogHooks() {
        if (capturedRenderingState != null
                && setFogColorMethod != null
                && setFogDensityMethod != null) {
            return true;
        }

        if (fogLookupAttempted) {
            return false;
        }

        fogLookupAttempted = true;

        try {
            Class<?> capturedStateClass = Class.forName("net.irisshaders.iris.uniforms.CapturedRenderingState");
            capturedRenderingState = capturedStateClass.getField("INSTANCE").get(null);
            setFogColorMethod = capturedStateClass.getMethod("setFogColor", float.class, float.class, float.class);
            setFogDensityMethod = capturedStateClass.getMethod("setFogDensity", float.class);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
