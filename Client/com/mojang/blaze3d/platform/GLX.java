/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.LongSupplier
 *  java.util.function.Supplier
 *  org.lwjgl.Version
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.slf4j.Logger
 *  oshi.SystemInfo
 *  oshi.hardware.CentralProcessor
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@DontObfuscate
public class GLX {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static String cpuInfo;

    public static String getOpenGLVersionString() {
        RenderSystem.assertOnRenderThread();
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager._getString(7937) + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
    }

    public static int _getRefreshRate(Window $$0) {
        RenderSystem.assertOnRenderThread();
        long $$1 = GLFW.glfwGetWindowMonitor((long)$$0.getWindow());
        if ($$1 == 0L) {
            $$1 = GLFW.glfwGetPrimaryMonitor();
        }
        GLFWVidMode $$2 = $$1 == 0L ? null : GLFW.glfwGetVideoMode((long)$$1);
        return $$2 == null ? 0 : $$2.refreshRate();
    }

    public static String _getLWJGLVersion() {
        RenderSystem.assertInInitPhase();
        return Version.getVersion();
    }

    /*
     * WARNING - void declaration
     */
    public static LongSupplier _initGlfw() {
        void $$4;
        RenderSystem.assertInInitPhase();
        Window.checkGlfwError((BiConsumer<Integer, String>)((BiConsumer)($$0, $$1) -> {
            throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"GLFW error before init: [0x%X]%s", (Object[])new Object[]{$$0, $$1}));
        }));
        ArrayList $$02 = Lists.newArrayList();
        GLFWErrorCallback $$12 = GLFW.glfwSetErrorCallback((arg_0, arg_1) -> GLX.lambda$_initGlfw$1((List)$$02, arg_0, arg_1));
        if (GLFW.glfwInit()) {
            LongSupplier $$2 = () -> (long)(GLFW.glfwGetTime() * 1.0E9);
            for (String $$3 : $$02) {
                LOGGER.error("GLFW error collected during initialization: {}", (Object)$$3);
            }
        } else {
            throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on((String)",").join((Iterable)$$02));
        }
        RenderSystem.setErrorCallback((GLFWErrorCallbackI)$$12);
        return $$4;
    }

    public static void _setGlfwErrorCallback(GLFWErrorCallbackI $$0) {
        RenderSystem.assertInInitPhase();
        GLFWErrorCallback $$1 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)$$0);
        if ($$1 != null) {
            $$1.free();
        }
    }

    public static boolean _shouldClose(Window $$0) {
        return GLFW.glfwWindowShouldClose((long)$$0.getWindow());
    }

    public static void _init(int $$0, boolean $$1) {
        RenderSystem.assertInInitPhase();
        try {
            CentralProcessor $$2 = new SystemInfo().getHardware().getProcessor();
            cpuInfo = String.format((Locale)Locale.ROOT, (String)"%dx %s", (Object[])new Object[]{$$2.getLogicalProcessorCount(), $$2.getProcessorIdentifier().getName()}).replaceAll("\\s+", " ");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        GlDebug.enableDebugCallback($$0, $$1);
    }

    public static String _getCpuInfo() {
        return cpuInfo == null ? "<unknown>" : cpuInfo;
    }

    public static void _renderCrosshair(int $$0, boolean $$1, boolean $$2, boolean $$3) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableTexture();
        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLinesShader));
        Tesselator $$4 = RenderSystem.renderThreadTesselator();
        BufferBuilder $$5 = $$4.getBuilder();
        RenderSystem.lineWidth(4.0f);
        $$5.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        if ($$1) {
            $$5.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).normal(1.0f, 0.0f, 0.0f).endVertex();
            $$5.vertex($$0, 0.0, 0.0).color(0, 0, 0, 255).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        if ($$2) {
            $$5.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).normal(0.0f, 1.0f, 0.0f).endVertex();
            $$5.vertex(0.0, $$0, 0.0).color(0, 0, 0, 255).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        if ($$3) {
            $$5.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).normal(0.0f, 0.0f, 1.0f).endVertex();
            $$5.vertex(0.0, 0.0, $$0).color(0, 0, 0, 255).normal(0.0f, 0.0f, 1.0f).endVertex();
        }
        $$4.end();
        RenderSystem.lineWidth(2.0f);
        $$5.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        if ($$1) {
            $$5.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).normal(1.0f, 0.0f, 0.0f).endVertex();
            $$5.vertex($$0, 0.0, 0.0).color(255, 0, 0, 255).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        if ($$2) {
            $$5.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).normal(0.0f, 1.0f, 0.0f).endVertex();
            $$5.vertex(0.0, $$0, 0.0).color(0, 255, 0, 255).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        if ($$3) {
            $$5.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).normal(0.0f, 0.0f, 1.0f).endVertex();
            $$5.vertex(0.0, 0.0, $$0).color(127, 127, 255, 255).normal(0.0f, 0.0f, 1.0f).endVertex();
        }
        $$4.end();
        RenderSystem.lineWidth(1.0f);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
        GlStateManager._enableTexture();
    }

    public static <T> T make(Supplier<T> $$0) {
        return (T)$$0.get();
    }

    public static <T> T make(T $$0, Consumer<T> $$1) {
        $$1.accept($$0);
        return $$0;
    }

    private static /* synthetic */ void lambda$_initGlfw$1(List $$0, int $$1, long $$2) {
        $$0.add((Object)String.format((Locale)Locale.ROOT, (String)"GLFW error during init: [0x%X]%s", (Object[])new Object[]{$$1, $$2}));
    }
}