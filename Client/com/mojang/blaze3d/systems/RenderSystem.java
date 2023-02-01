/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Thread
 *  java.nio.ByteBuffer
 *  java.nio.FloatBuffer
 *  java.nio.IntBuffer
 *  java.util.Locale
 *  java.util.concurrent.ConcurrentLinkedQueue
 *  java.util.function.Consumer
 *  java.util.function.IntConsumer
 *  java.util.function.IntSupplier
 *  java.util.function.LongSupplier
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeSource;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.slf4j.Logger;

@DontObfuscate
public class RenderSystem {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
    private static final Tesselator RENDER_THREAD_TESSELATOR = new Tesselator();
    private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
    private static boolean isReplayingQueue;
    @Nullable
    private static Thread gameThread;
    @Nullable
    private static Thread renderThread;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private static boolean isInInit;
    private static double lastDrawTime;
    private static final AutoStorageIndexBuffer sharedSequential;
    private static final AutoStorageIndexBuffer sharedSequentialQuad;
    private static final AutoStorageIndexBuffer sharedSequentialLines;
    private static Matrix3f inverseViewRotationMatrix;
    private static Matrix4f projectionMatrix;
    private static Matrix4f savedProjectionMatrix;
    private static final PoseStack modelViewStack;
    private static Matrix4f modelViewMatrix;
    private static Matrix4f textureMatrix;
    private static final int[] shaderTextures;
    private static final float[] shaderColor;
    private static float shaderGlintAlpha;
    private static float shaderFogStart;
    private static float shaderFogEnd;
    private static final float[] shaderFogColor;
    private static FogShape shaderFogShape;
    private static final Vector3f[] shaderLightDirections;
    private static float shaderGameTime;
    private static float shaderLineWidth;
    private static String apiDescription;
    @Nullable
    private static ShaderInstance shader;

    public static void initRenderThread() {
        if (renderThread != null || gameThread == Thread.currentThread()) {
            throw new IllegalStateException("Could not initialize render thread");
        }
        renderThread = Thread.currentThread();
    }

    public static boolean isOnRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public static boolean isOnRenderThreadOrInit() {
        return isInInit || RenderSystem.isOnRenderThread();
    }

    public static void initGameThread(boolean $$0) {
        boolean $$1;
        boolean bl = $$1 = renderThread == Thread.currentThread();
        if (gameThread != null || renderThread == null || $$1 == $$0) {
            throw new IllegalStateException("Could not initialize tick thread");
        }
        gameThread = Thread.currentThread();
    }

    public static boolean isOnGameThread() {
        return true;
    }

    public static void assertInInitPhase() {
        if (!RenderSystem.isInInitPhase()) {
            throw RenderSystem.constructThreadException();
        }
    }

    public static void assertOnGameThreadOrInit() {
        if (isInInit || RenderSystem.isOnGameThread()) {
            return;
        }
        throw RenderSystem.constructThreadException();
    }

    public static void assertOnRenderThreadOrInit() {
        if (isInInit || RenderSystem.isOnRenderThread()) {
            return;
        }
        throw RenderSystem.constructThreadException();
    }

    public static void assertOnRenderThread() {
        if (!RenderSystem.isOnRenderThread()) {
            throw RenderSystem.constructThreadException();
        }
    }

    public static void assertOnGameThread() {
        if (!RenderSystem.isOnGameThread()) {
            throw RenderSystem.constructThreadException();
        }
    }

    private static IllegalStateException constructThreadException() {
        return new IllegalStateException("Rendersystem called from wrong thread");
    }

    public static boolean isInInitPhase() {
        return true;
    }

    public static void recordRenderCall(RenderCall $$0) {
        recordingQueue.add((Object)$$0);
    }

    public static void flipFrame(long $$0) {
        GLFW.glfwPollEvents();
        RenderSystem.replayQueue();
        Tesselator.getInstance().getBuilder().clear();
        GLFW.glfwSwapBuffers((long)$$0);
        GLFW.glfwPollEvents();
    }

    public static void replayQueue() {
        isReplayingQueue = true;
        while (!recordingQueue.isEmpty()) {
            RenderCall $$0 = (RenderCall)recordingQueue.poll();
            $$0.execute();
        }
        isReplayingQueue = false;
    }

    public static void limitDisplayFPS(int $$0) {
        double $$1 = lastDrawTime + 1.0 / (double)$$0;
        double $$2 = GLFW.glfwGetTime();
        while ($$2 < $$1) {
            GLFW.glfwWaitEventsTimeout((double)($$1 - $$2));
            $$2 = GLFW.glfwGetTime();
        }
        lastDrawTime = $$2;
    }

    public static void disableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest() {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._enableDepthTest();
    }

    public static void enableScissor(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox($$0, $$1, $$2, $$3);
    }

    public static void disableScissor() {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._disableScissorTest();
    }

    public static void depthFunc(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._depthFunc($$0);
    }

    public static void depthMask(boolean $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._depthMask($$0);
    }

    public static void enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableBlend();
    }

    public static void disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableBlend();
    }

    public static void blendFunc(GlStateManager.SourceFactor $$0, GlStateManager.DestFactor $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFunc($$0.value, $$1.value);
    }

    public static void blendFunc(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFunc($$0, $$1);
    }

    public static void blendFuncSeparate(GlStateManager.SourceFactor $$0, GlStateManager.DestFactor $$1, GlStateManager.SourceFactor $$2, GlStateManager.DestFactor $$3) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFuncSeparate($$0.value, $$1.value, $$2.value, $$3.value);
    }

    public static void blendFuncSeparate(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFuncSeparate($$0, $$1, $$2, $$3);
    }

    public static void blendEquation(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendEquation($$0);
    }

    public static void enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableCull();
    }

    public static void polygonMode(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._polygonMode($$0, $$1);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enablePolygonOffset();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disablePolygonOffset();
    }

    public static void polygonOffset(float $$0, float $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._polygonOffset($$0, $$1);
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableColorLogicOp();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableColorLogicOp();
    }

    public static void logicOp(GlStateManager.LogicOp $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._logicOp($$0.value);
    }

    public static void activeTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._activeTexture($$0);
    }

    public static void texParameter(int $$0, int $$1, int $$2) {
        GlStateManager._texParameter($$0, $$1, $$2);
    }

    public static void deleteTexture(int $$0) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._deleteTexture($$0);
    }

    public static void bindTextureForSetup(int $$0) {
        RenderSystem.bindTexture($$0);
    }

    public static void bindTexture(int $$0) {
        GlStateManager._bindTexture($$0);
    }

    public static void viewport(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._viewport($$0, $$1, $$2, $$3);
    }

    public static void colorMask(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask($$0, $$1, $$2, $$3);
    }

    public static void stencilFunc(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._stencilFunc($$0, $$1, $$2);
    }

    public static void stencilMask(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._stencilMask($$0);
    }

    public static void stencilOp(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._stencilOp($$0, $$1, $$2);
    }

    public static void clearDepth(double $$0) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._clearDepth($$0);
    }

    public static void clearColor(float $$0, float $$1, float $$2, float $$3) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._clearColor($$0, $$1, $$2, $$3);
    }

    public static void clearStencil(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._clearStencil($$0);
    }

    public static void clear(int $$0, boolean $$1) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._clear($$0, $$1);
    }

    public static void setShaderFogStart(float $$0) {
        RenderSystem.assertOnRenderThread();
        RenderSystem._setShaderFogStart($$0);
    }

    private static void _setShaderFogStart(float $$0) {
        shaderFogStart = $$0;
    }

    public static float getShaderFogStart() {
        RenderSystem.assertOnRenderThread();
        return shaderFogStart;
    }

    public static void setShaderGlintAlpha(double $$0) {
        RenderSystem.setShaderGlintAlpha((float)$$0);
    }

    public static void setShaderGlintAlpha(float $$0) {
        RenderSystem.assertOnRenderThread();
        RenderSystem._setShaderGlintAlpha($$0);
    }

    private static void _setShaderGlintAlpha(float $$0) {
        shaderGlintAlpha = $$0;
    }

    public static float getShaderGlintAlpha() {
        RenderSystem.assertOnRenderThread();
        return shaderGlintAlpha;
    }

    public static void setShaderFogEnd(float $$0) {
        RenderSystem.assertOnRenderThread();
        RenderSystem._setShaderFogEnd($$0);
    }

    private static void _setShaderFogEnd(float $$0) {
        shaderFogEnd = $$0;
    }

    public static float getShaderFogEnd() {
        RenderSystem.assertOnRenderThread();
        return shaderFogEnd;
    }

    public static void setShaderFogColor(float $$0, float $$1, float $$2, float $$3) {
        RenderSystem.assertOnRenderThread();
        RenderSystem._setShaderFogColor($$0, $$1, $$2, $$3);
    }

    public static void setShaderFogColor(float $$0, float $$1, float $$2) {
        RenderSystem.setShaderFogColor($$0, $$1, $$2, 1.0f);
    }

    private static void _setShaderFogColor(float $$0, float $$1, float $$2, float $$3) {
        RenderSystem.shaderFogColor[0] = $$0;
        RenderSystem.shaderFogColor[1] = $$1;
        RenderSystem.shaderFogColor[2] = $$2;
        RenderSystem.shaderFogColor[3] = $$3;
    }

    public static float[] getShaderFogColor() {
        RenderSystem.assertOnRenderThread();
        return shaderFogColor;
    }

    public static void setShaderFogShape(FogShape $$0) {
        RenderSystem.assertOnRenderThread();
        RenderSystem._setShaderFogShape($$0);
    }

    private static void _setShaderFogShape(FogShape $$0) {
        shaderFogShape = $$0;
    }

    public static FogShape getShaderFogShape() {
        RenderSystem.assertOnRenderThread();
        return shaderFogShape;
    }

    public static void setShaderLights(Vector3f $$0, Vector3f $$1) {
        RenderSystem.assertOnRenderThread();
        RenderSystem._setShaderLights($$0, $$1);
    }

    public static void _setShaderLights(Vector3f $$0, Vector3f $$1) {
        RenderSystem.shaderLightDirections[0] = $$0;
        RenderSystem.shaderLightDirections[1] = $$1;
    }

    public static void setupShaderLights(ShaderInstance $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0.LIGHT0_DIRECTION != null) {
            $$0.LIGHT0_DIRECTION.set(shaderLightDirections[0]);
        }
        if ($$0.LIGHT1_DIRECTION != null) {
            $$0.LIGHT1_DIRECTION.set(shaderLightDirections[1]);
        }
    }

    public static void setShaderColor(float $$0, float $$1, float $$2, float $$3) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._setShaderColor($$0, $$1, $$2, $$3));
        } else {
            RenderSystem._setShaderColor($$0, $$1, $$2, $$3);
        }
    }

    private static void _setShaderColor(float $$0, float $$1, float $$2, float $$3) {
        RenderSystem.shaderColor[0] = $$0;
        RenderSystem.shaderColor[1] = $$1;
        RenderSystem.shaderColor[2] = $$2;
        RenderSystem.shaderColor[3] = $$3;
    }

    public static float[] getShaderColor() {
        RenderSystem.assertOnRenderThread();
        return shaderColor;
    }

    public static void drawElements(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._drawElements($$0, $$1, $$2, 0L);
    }

    public static void lineWidth(float $$0) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                shaderLineWidth = $$0;
            });
        } else {
            shaderLineWidth = $$0;
        }
    }

    public static float getShaderLineWidth() {
        RenderSystem.assertOnRenderThread();
        return shaderLineWidth;
    }

    public static void pixelStore(int $$0, int $$1) {
        RenderSystem.assertOnGameThreadOrInit();
        GlStateManager._pixelStore($$0, $$1);
    }

    public static void readPixels(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, ByteBuffer $$6) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._readPixels($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public static void getString(int $$0, Consumer<String> $$1) {
        RenderSystem.assertOnRenderThread();
        $$1.accept((Object)GlStateManager._getString($$0));
    }

    public static String getBackendDescription() {
        RenderSystem.assertInInitPhase();
        return String.format((Locale)Locale.ROOT, (String)"LWJGL version %s", (Object[])new Object[]{GLX._getLWJGLVersion()});
    }

    public static String getApiDescription() {
        return apiDescription;
    }

    public static TimeSource.NanoTimeSource initBackendSystem() {
        RenderSystem.assertInInitPhase();
        return () -> ((LongSupplier)GLX._initGlfw()).getAsLong();
    }

    public static void initRenderer(int $$0, boolean $$1) {
        RenderSystem.assertInInitPhase();
        GLX._init($$0, $$1);
        apiDescription = GLX.getOpenGLVersionString();
    }

    public static void setErrorCallback(GLFWErrorCallbackI $$0) {
        RenderSystem.assertInInitPhase();
        GLX._setGlfwErrorCallback($$0);
    }

    public static void renderCrosshair(int $$0) {
        RenderSystem.assertOnRenderThread();
        GLX._renderCrosshair($$0, true, true, true);
    }

    public static String getCapsString() {
        RenderSystem.assertOnRenderThread();
        return "Using framebuffer using OpenGL 3.2";
    }

    public static void setupDefaultState(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertInInitPhase();
        GlStateManager._clearDepth(1.0);
        GlStateManager._enableDepthTest();
        GlStateManager._depthFunc(515);
        projectionMatrix.identity();
        savedProjectionMatrix.identity();
        modelViewMatrix.identity();
        textureMatrix.identity();
        GlStateManager._viewport($$0, $$1, $$2, $$3);
    }

    public static int maxSupportedTextureSize() {
        if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
            RenderSystem.assertOnRenderThreadOrInit();
            int $$0 = GlStateManager._getInteger(3379);
            for (int $$1 = Math.max((int)32768, (int)$$0); $$1 >= 1024; $$1 >>= 1) {
                GlStateManager._texImage2D(32868, 0, 6408, $$1, $$1, 0, 6408, 5121, null);
                int $$2 = GlStateManager._getTexLevelParameter(32868, 0, 4096);
                if ($$2 == 0) continue;
                MAX_SUPPORTED_TEXTURE_SIZE = $$1;
                return $$1;
            }
            MAX_SUPPORTED_TEXTURE_SIZE = Math.max((int)$$0, (int)1024);
            LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)MAX_SUPPORTED_TEXTURE_SIZE);
        }
        return MAX_SUPPORTED_TEXTURE_SIZE;
    }

    public static void glBindBuffer(int $$0, IntSupplier $$1) {
        GlStateManager._glBindBuffer($$0, $$1.getAsInt());
    }

    public static void glBindVertexArray(Supplier<Integer> $$0) {
        GlStateManager._glBindVertexArray((Integer)$$0.get());
    }

    public static void glBufferData(int $$0, ByteBuffer $$1, int $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBufferData($$0, $$1, $$2);
    }

    public static void glDeleteBuffers(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glDeleteBuffers($$0);
    }

    public static void glDeleteVertexArrays(int $$0) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glDeleteVertexArrays($$0);
    }

    public static void glUniform1i(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform1i($$0, $$1);
    }

    public static void glUniform1(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform1($$0, $$1);
    }

    public static void glUniform2(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform2($$0, $$1);
    }

    public static void glUniform3(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform3($$0, $$1);
    }

    public static void glUniform4(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform4($$0, $$1);
    }

    public static void glUniform1(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform1($$0, $$1);
    }

    public static void glUniform2(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform2($$0, $$1);
    }

    public static void glUniform3(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform3($$0, $$1);
    }

    public static void glUniform4(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniform4($$0, $$1);
    }

    public static void glUniformMatrix2(int $$0, boolean $$1, FloatBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniformMatrix2($$0, $$1, $$2);
    }

    public static void glUniformMatrix3(int $$0, boolean $$1, FloatBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniformMatrix3($$0, $$1, $$2);
    }

    public static void glUniformMatrix4(int $$0, boolean $$1, FloatBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUniformMatrix4($$0, $$1, $$2);
    }

    public static void setupOverlayColor(IntSupplier $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        int $$2 = $$0.getAsInt();
        RenderSystem.setShaderTexture(1, $$2);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, 0);
    }

    public static void setupLevelDiffuseLighting(Vector3f $$0, Vector3f $$1, Matrix4f $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.setupLevelDiffuseLighting($$0, $$1, $$2);
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f $$0, Vector3f $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.setupGuiFlatDiffuseLighting($$0, $$1);
    }

    public static void setupGui3DDiffuseLighting(Vector3f $$0, Vector3f $$1) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.setupGui3DDiffuseLighting($$0, $$1);
    }

    public static void beginInitialization() {
        isInInit = true;
    }

    public static void finishInitialization() {
        isInInit = false;
        if (!recordingQueue.isEmpty()) {
            RenderSystem.replayQueue();
        }
        if (!recordingQueue.isEmpty()) {
            throw new IllegalStateException("Recorded to render queue during initialization");
        }
    }

    public static void glGenBuffers(Consumer<Integer> $$0) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> $$0.accept((Object)GlStateManager._glGenBuffers()));
        } else {
            $$0.accept((Object)GlStateManager._glGenBuffers());
        }
    }

    public static void glGenVertexArrays(Consumer<Integer> $$0) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> $$0.accept((Object)GlStateManager._glGenVertexArrays()));
        } else {
            $$0.accept((Object)GlStateManager._glGenVertexArrays());
        }
    }

    public static Tesselator renderThreadTesselator() {
        RenderSystem.assertOnRenderThread();
        return RENDER_THREAD_TESSELATOR;
    }

    public static void defaultBlendFunc() {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    @Deprecated
    public static void runAsFancy(Runnable $$0) {
        boolean $$1 = Minecraft.useShaderTransparency();
        if (!$$1) {
            $$0.run();
            return;
        }
        OptionInstance<GraphicsStatus> $$2 = Minecraft.getInstance().options.graphicsMode();
        GraphicsStatus $$3 = $$2.get();
        $$2.set(GraphicsStatus.FANCY);
        $$0.run();
        $$2.set($$3);
    }

    public static void setShader(Supplier<ShaderInstance> $$0) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                shader = (ShaderInstance)$$0.get();
            });
        } else {
            shader = (ShaderInstance)$$0.get();
        }
    }

    @Nullable
    public static ShaderInstance getShader() {
        RenderSystem.assertOnRenderThread();
        return shader;
    }

    public static void setShaderTexture(int $$0, ResourceLocation $$1) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._setShaderTexture($$0, $$1));
        } else {
            RenderSystem._setShaderTexture($$0, $$1);
        }
    }

    public static void _setShaderTexture(int $$0, ResourceLocation $$1) {
        if ($$0 >= 0 && $$0 < shaderTextures.length) {
            TextureManager $$2 = Minecraft.getInstance().getTextureManager();
            AbstractTexture $$3 = $$2.getTexture($$1);
            RenderSystem.shaderTextures[$$0] = $$3.getId();
        }
    }

    public static void setShaderTexture(int $$0, int $$1) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._setShaderTexture($$0, $$1));
        } else {
            RenderSystem._setShaderTexture($$0, $$1);
        }
    }

    public static void _setShaderTexture(int $$0, int $$1) {
        if ($$0 >= 0 && $$0 < shaderTextures.length) {
            RenderSystem.shaderTextures[$$0] = $$1;
        }
    }

    public static int getShaderTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 >= 0 && $$0 < shaderTextures.length) {
            return shaderTextures[$$0];
        }
        return 0;
    }

    public static void setProjectionMatrix(Matrix4f $$0) {
        Matrix4f $$1 = new Matrix4f((Matrix4fc)$$0);
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                projectionMatrix = $$1;
            });
        } else {
            projectionMatrix = $$1;
        }
    }

    public static void setInverseViewRotationMatrix(Matrix3f $$0) {
        Matrix3f $$1 = new Matrix3f((Matrix3fc)$$0);
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                inverseViewRotationMatrix = $$1;
            });
        } else {
            inverseViewRotationMatrix = $$1;
        }
    }

    public static void setTextureMatrix(Matrix4f $$0) {
        Matrix4f $$1 = new Matrix4f((Matrix4fc)$$0);
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                textureMatrix = $$1;
            });
        } else {
            textureMatrix = $$1;
        }
    }

    public static void resetTextureMatrix() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> textureMatrix.identity());
        } else {
            textureMatrix.identity();
        }
    }

    public static void applyModelViewMatrix() {
        Matrix4f $$0 = new Matrix4f((Matrix4fc)modelViewStack.last().pose());
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                modelViewMatrix = $$0;
            });
        } else {
            modelViewMatrix = $$0;
        }
    }

    public static void backupProjectionMatrix() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._backupProjectionMatrix());
        } else {
            RenderSystem._backupProjectionMatrix();
        }
    }

    private static void _backupProjectionMatrix() {
        savedProjectionMatrix = projectionMatrix;
    }

    public static void restoreProjectionMatrix() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> RenderSystem._restoreProjectionMatrix());
        } else {
            RenderSystem._restoreProjectionMatrix();
        }
    }

    private static void _restoreProjectionMatrix() {
        projectionMatrix = savedProjectionMatrix;
    }

    public static Matrix4f getProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        return projectionMatrix;
    }

    public static Matrix3f getInverseViewRotationMatrix() {
        RenderSystem.assertOnRenderThread();
        return inverseViewRotationMatrix;
    }

    public static Matrix4f getModelViewMatrix() {
        RenderSystem.assertOnRenderThread();
        return modelViewMatrix;
    }

    public static PoseStack getModelViewStack() {
        return modelViewStack;
    }

    public static Matrix4f getTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        return textureMatrix;
    }

    public static AutoStorageIndexBuffer getSequentialBuffer(VertexFormat.Mode $$0) {
        RenderSystem.assertOnRenderThread();
        return switch ($$0) {
            case VertexFormat.Mode.QUADS -> sharedSequentialQuad;
            case VertexFormat.Mode.LINES -> sharedSequentialLines;
            default -> sharedSequential;
        };
    }

    public static void setShaderGameTime(long $$0, float $$1) {
        float $$2 = ((float)($$0 % 24000L) + $$1) / 24000.0f;
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                shaderGameTime = $$2;
            });
        } else {
            shaderGameTime = $$2;
        }
    }

    public static float getShaderGameTime() {
        RenderSystem.assertOnRenderThread();
        return shaderGameTime;
    }

    private static /* synthetic */ void lambda$setupGui3DDiffuseLighting$59(Vector3f $$0, Vector3f $$1) {
        GlStateManager.setupGui3DDiffuseLighting($$0, $$1);
    }

    private static /* synthetic */ void lambda$setupGuiFlatDiffuseLighting$58(Vector3f $$0, Vector3f $$1) {
        GlStateManager.setupGuiFlatDiffuseLighting($$0, $$1);
    }

    private static /* synthetic */ void lambda$setupLevelDiffuseLighting$57(Vector3f $$0, Vector3f $$1, Matrix4f $$2) {
        GlStateManager.setupLevelDiffuseLighting($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$teardownOverlayColor$56() {
        RenderSystem.setShaderTexture(1, 0);
    }

    private static /* synthetic */ void lambda$setupOverlayColor$55(IntSupplier $$0) {
        int $$1 = $$0.getAsInt();
        RenderSystem.setShaderTexture(1, $$1);
    }

    private static /* synthetic */ void lambda$glUniformMatrix4$54(int $$0, boolean $$1, FloatBuffer $$2) {
        GlStateManager._glUniformMatrix4($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$glUniformMatrix3$53(int $$0, boolean $$1, FloatBuffer $$2) {
        GlStateManager._glUniformMatrix3($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$glUniformMatrix2$52(int $$0, boolean $$1, FloatBuffer $$2) {
        GlStateManager._glUniformMatrix2($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$glUniform4$51(int $$0, FloatBuffer $$1) {
        GlStateManager._glUniform4($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform3$50(int $$0, FloatBuffer $$1) {
        GlStateManager._glUniform3($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform2$49(int $$0, FloatBuffer $$1) {
        GlStateManager._glUniform2($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform1$48(int $$0, FloatBuffer $$1) {
        GlStateManager._glUniform1($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform4$47(int $$0, IntBuffer $$1) {
        GlStateManager._glUniform4($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform3$46(int $$0, IntBuffer $$1) {
        GlStateManager._glUniform3($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform2$45(int $$0, IntBuffer $$1) {
        GlStateManager._glUniform2($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform1$44(int $$0, IntBuffer $$1) {
        GlStateManager._glUniform1($$0, $$1);
    }

    private static /* synthetic */ void lambda$glUniform1i$43(int $$0, int $$1) {
        GlStateManager._glUniform1i($$0, $$1);
    }

    private static /* synthetic */ void lambda$glDeleteVertexArrays$42(int $$0) {
        GlStateManager._glDeleteVertexArrays($$0);
    }

    private static /* synthetic */ void lambda$glDeleteBuffers$41(int $$0) {
        GlStateManager._glDeleteBuffers($$0);
    }

    private static /* synthetic */ void lambda$glBindVertexArray$40(Supplier $$0) {
        GlStateManager._glBindVertexArray((Integer)$$0.get());
    }

    private static /* synthetic */ void lambda$glBindBuffer$39(int $$0, IntSupplier $$1) {
        GlStateManager._glBindBuffer($$0, $$1.getAsInt());
    }

    private static /* synthetic */ void lambda$renderCrosshair$38(int $$0) {
        GLX._renderCrosshair($$0, true, true, true);
    }

    private static /* synthetic */ void lambda$getString$37(int $$0, Consumer $$1) {
        String $$2 = GlStateManager._getString($$0);
        $$1.accept((Object)$$2);
    }

    private static /* synthetic */ void lambda$readPixels$36(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, ByteBuffer $$6) {
        GlStateManager._readPixels($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    private static /* synthetic */ void lambda$pixelStore$35(int $$0, int $$1) {
        GlStateManager._pixelStore($$0, $$1);
    }

    private static /* synthetic */ void lambda$drawElements$33(int $$0, int $$1, int $$2) {
        GlStateManager._drawElements($$0, $$1, $$2, 0L);
    }

    private static /* synthetic */ void lambda$setShaderLights$31(Vector3f $$0, Vector3f $$1) {
        RenderSystem._setShaderLights($$0, $$1);
    }

    private static /* synthetic */ void lambda$setShaderFogShape$30(FogShape $$0) {
        RenderSystem._setShaderFogShape($$0);
    }

    private static /* synthetic */ void lambda$setShaderFogColor$29(float $$0, float $$1, float $$2, float $$3) {
        RenderSystem._setShaderFogColor($$0, $$1, $$2, $$3);
    }

    private static /* synthetic */ void lambda$setShaderFogEnd$28(float $$0) {
        RenderSystem._setShaderFogEnd($$0);
    }

    private static /* synthetic */ void lambda$setShaderGlintAlpha$27(float $$0) {
        RenderSystem._setShaderGlintAlpha($$0);
    }

    private static /* synthetic */ void lambda$setShaderFogStart$26(float $$0) {
        RenderSystem._setShaderFogStart($$0);
    }

    private static /* synthetic */ void lambda$clear$25(int $$0, boolean $$1) {
        GlStateManager._clear($$0, $$1);
    }

    private static /* synthetic */ void lambda$clearStencil$24(int $$0) {
        GlStateManager._clearStencil($$0);
    }

    private static /* synthetic */ void lambda$clearColor$23(float $$0, float $$1, float $$2, float $$3) {
        GlStateManager._clearColor($$0, $$1, $$2, $$3);
    }

    private static /* synthetic */ void lambda$clearDepth$22(double $$0) {
        GlStateManager._clearDepth($$0);
    }

    private static /* synthetic */ void lambda$stencilOp$21(int $$0, int $$1, int $$2) {
        GlStateManager._stencilOp($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$stencilMask$20(int $$0) {
        GlStateManager._stencilMask($$0);
    }

    private static /* synthetic */ void lambda$stencilFunc$19(int $$0, int $$1, int $$2) {
        GlStateManager._stencilFunc($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$colorMask$18(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
        GlStateManager._colorMask($$0, $$1, $$2, $$3);
    }

    private static /* synthetic */ void lambda$viewport$17(int $$0, int $$1, int $$2, int $$3) {
        GlStateManager._viewport($$0, $$1, $$2, $$3);
    }

    private static /* synthetic */ void lambda$bindTexture$16(int $$0) {
        GlStateManager._bindTexture($$0);
    }

    private static /* synthetic */ void lambda$deleteTexture$15(int $$0) {
        GlStateManager._deleteTexture($$0);
    }

    private static /* synthetic */ void lambda$texParameter$14(int $$0, int $$1, int $$2) {
        GlStateManager._texParameter($$0, $$1, $$2);
    }

    private static /* synthetic */ void lambda$activeTexture$13(int $$0) {
        GlStateManager._activeTexture($$0);
    }

    private static /* synthetic */ void lambda$logicOp$12(GlStateManager.LogicOp $$0) {
        GlStateManager._logicOp($$0.value);
    }

    private static /* synthetic */ void lambda$polygonOffset$11(float $$0, float $$1) {
        GlStateManager._polygonOffset($$0, $$1);
    }

    private static /* synthetic */ void lambda$polygonMode$10(int $$0, int $$1) {
        GlStateManager._polygonMode($$0, $$1);
    }

    private static /* synthetic */ void lambda$blendEquation$9(int $$0) {
        GlStateManager._blendEquation($$0);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$8(int $$0, int $$1, int $$2, int $$3) {
        GlStateManager._blendFuncSeparate($$0, $$1, $$2, $$3);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$7(GlStateManager.SourceFactor $$0, GlStateManager.DestFactor $$1, GlStateManager.SourceFactor $$2, GlStateManager.DestFactor $$3) {
        GlStateManager._blendFuncSeparate($$0.value, $$1.value, $$2.value, $$3.value);
    }

    private static /* synthetic */ void lambda$blendFunc$6(int $$0, int $$1) {
        GlStateManager._blendFunc($$0, $$1);
    }

    private static /* synthetic */ void lambda$blendFunc$5(GlStateManager.SourceFactor $$0, GlStateManager.DestFactor $$1) {
        GlStateManager._blendFunc($$0.value, $$1.value);
    }

    private static /* synthetic */ void lambda$depthMask$4(boolean $$0) {
        GlStateManager._depthMask($$0);
    }

    private static /* synthetic */ void lambda$depthFunc$3(int $$0) {
        GlStateManager._depthFunc($$0);
    }

    private static /* synthetic */ void lambda$enableScissor$2(int $$0, int $$1, int $$2, int $$3) {
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox($$0, $$1, $$2, $$3);
    }

    static {
        MAX_SUPPORTED_TEXTURE_SIZE = -1;
        lastDrawTime = Double.MIN_VALUE;
        sharedSequential = new AutoStorageIndexBuffer(1, 1, java.util.function.IntConsumer::accept);
        sharedSequentialQuad = new AutoStorageIndexBuffer(4, 6, ($$0, $$1) -> {
            $$0.accept($$1 + 0);
            $$0.accept($$1 + 1);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 3);
            $$0.accept($$1 + 0);
        });
        sharedSequentialLines = new AutoStorageIndexBuffer(4, 6, ($$0, $$1) -> {
            $$0.accept($$1 + 0);
            $$0.accept($$1 + 1);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 3);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 1);
        });
        inverseViewRotationMatrix = new Matrix3f().zero();
        projectionMatrix = new Matrix4f();
        savedProjectionMatrix = new Matrix4f();
        modelViewStack = new PoseStack();
        modelViewMatrix = new Matrix4f();
        textureMatrix = new Matrix4f();
        shaderTextures = new int[12];
        shaderColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        shaderGlintAlpha = 1.0f;
        shaderFogEnd = 1.0f;
        shaderFogColor = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        shaderFogShape = FogShape.SPHERE;
        shaderLightDirections = new Vector3f[2];
        shaderLineWidth = 1.0f;
        apiDescription = "Unknown";
    }

    public static final class AutoStorageIndexBuffer {
        private final int vertexStride;
        private final int indexStride;
        private final IndexGenerator generator;
        private int name;
        private VertexFormat.IndexType type = VertexFormat.IndexType.BYTE;
        private int indexCount;

        AutoStorageIndexBuffer(int $$0, int $$1, IndexGenerator $$2) {
            this.vertexStride = $$0;
            this.indexStride = $$1;
            this.generator = $$2;
        }

        public boolean hasStorage(int $$0) {
            return $$0 <= this.indexCount;
        }

        public void bind(int $$0) {
            if (this.name == 0) {
                this.name = GlStateManager._glGenBuffers();
            }
            GlStateManager._glBindBuffer(34963, this.name);
            this.ensureStorage($$0);
        }

        private void ensureStorage(int $$0) {
            if (this.hasStorage($$0)) {
                return;
            }
            $$0 = Mth.roundToward($$0 * 2, this.indexStride);
            LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", (Object)this.indexCount, (Object)$$0);
            VertexFormat.IndexType $$1 = VertexFormat.IndexType.least($$0);
            int $$2 = Mth.roundToward($$0 * $$1.bytes, 4);
            GlStateManager._glBufferData(34963, $$2, 35048);
            ByteBuffer $$3 = GlStateManager._glMapBuffer(34963, 35001);
            if ($$3 == null) {
                throw new RuntimeException("Failed to map GL buffer");
            }
            this.type = $$1;
            IntConsumer $$4 = this.intConsumer($$3);
            for (int $$5 = 0; $$5 < $$0; $$5 += this.indexStride) {
                this.generator.accept($$4, $$5 * this.vertexStride / this.indexStride);
            }
            GlStateManager._glUnmapBuffer(34963);
            this.indexCount = $$0;
        }

        private IntConsumer intConsumer(ByteBuffer $$0) {
            switch (this.type) {
                case BYTE: {
                    return $$1 -> $$0.put((byte)$$1);
                }
                case SHORT: {
                    return $$1 -> $$0.putShort((short)$$1);
                }
            }
            return arg_0 -> ((ByteBuffer)$$0).putInt(arg_0);
        }

        public VertexFormat.IndexType type() {
            return this.type;
        }

        static interface IndexGenerator {
            public void accept(IntConsumer var1, int var2);
        }
    }
}