/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.nio.FloatBuffer
 *  java.nio.IntBuffer
 *  java.util.List
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@DontObfuscate
public class GlStateManager {
    private static final boolean ON_LINUX = Util.getPlatform() == Util.OS.LINUX;
    public static final int TEXTURE_COUNT = 12;
    private static final BlendState BLEND = new BlendState();
    private static final DepthState DEPTH = new DepthState();
    private static final CullState CULL = new CullState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final ColorLogicState COLOR_LOGIC = new ColorLogicState();
    private static final StencilState STENCIL = new StencilState();
    private static final ScissorState SCISSOR = new ScissorState();
    private static int activeTexture;
    private static final TextureState[] TEXTURES;
    private static final ColorMask COLOR_MASK;

    public static void _disableScissorTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.SCISSOR.mode.disable();
    }

    public static void _enableScissorTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.SCISSOR.mode.enable();
    }

    public static void _scissorBox(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL20.glScissor((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static void _disableDepthTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.DEPTH.mode.disable();
    }

    public static void _enableDepthTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.DEPTH.mode.enable();
    }

    public static void _depthFunc(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        if ($$0 != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = $$0;
            GL11.glDepthFunc((int)$$0);
        }
    }

    public static void _depthMask(boolean $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = $$0;
            GL11.glDepthMask((boolean)$$0);
        }
    }

    public static void _disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.mode.disable();
    }

    public static void _enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.mode.enable();
    }

    public static void _blendFunc(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.BLEND.srcRgb || $$1 != GlStateManager.BLEND.dstRgb) {
            GlStateManager.BLEND.srcRgb = $$0;
            GlStateManager.BLEND.dstRgb = $$1;
            GL11.glBlendFunc((int)$$0, (int)$$1);
        }
    }

    public static void _blendFuncSeparate(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.BLEND.srcRgb || $$1 != GlStateManager.BLEND.dstRgb || $$2 != GlStateManager.BLEND.srcAlpha || $$3 != GlStateManager.BLEND.dstAlpha) {
            GlStateManager.BLEND.srcRgb = $$0;
            GlStateManager.BLEND.dstRgb = $$1;
            GlStateManager.BLEND.srcAlpha = $$2;
            GlStateManager.BLEND.dstAlpha = $$3;
            GlStateManager.glBlendFuncSeparate($$0, $$1, $$2, $$3);
        }
    }

    public static void _blendEquation(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendEquation((int)$$0);
    }

    public static int glGetProgrami(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgrami((int)$$0, (int)$$1);
    }

    public static void glAttachShader(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glAttachShader((int)$$0, (int)$$1);
    }

    public static void glDeleteShader(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteShader((int)$$0);
    }

    public static int glCreateShader(int $$0) {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateShader((int)$$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void glShaderSource(int $$0, List<String> $$1) {
        RenderSystem.assertOnRenderThread();
        StringBuilder $$2 = new StringBuilder();
        for (String $$3 : $$1) {
            $$2.append($$3);
        }
        byte[] $$4 = $$2.toString().getBytes(Charsets.UTF_8);
        ByteBuffer $$5 = MemoryUtil.memAlloc((int)($$4.length + 1));
        $$5.put($$4);
        $$5.put((byte)0);
        $$5.flip();
        try (MemoryStack $$6 = MemoryStack.stackPush();){
            PointerBuffer $$7 = $$6.mallocPointer(1);
            $$7.put($$5);
            GL20C.nglShaderSource((int)$$0, (int)1, (long)$$7.address0(), (long)0L);
        }
        finally {
            MemoryUtil.memFree((Buffer)$$5);
        }
    }

    public static void glCompileShader(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glCompileShader((int)$$0);
    }

    public static int glGetShaderi(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderi((int)$$0, (int)$$1);
    }

    public static void _glUseProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glUseProgram((int)$$0);
    }

    public static int glCreateProgram() {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteProgram((int)$$0);
    }

    public static void glLinkProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glLinkProgram((int)$$0);
    }

    public static int _glGetUniformLocation(int $$0, CharSequence $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetUniformLocation((int)$$0, (CharSequence)$$1);
    }

    public static void _glUniform1(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1iv((int)$$0, (IntBuffer)$$1);
    }

    public static void _glUniform1i(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1i((int)$$0, (int)$$1);
    }

    public static void _glUniform1(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniform2(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2iv((int)$$0, (IntBuffer)$$1);
    }

    public static void _glUniform2(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniform3(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3iv((int)$$0, (IntBuffer)$$1);
    }

    public static void _glUniform3(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniform4(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4iv((int)$$0, (IntBuffer)$$1);
    }

    public static void _glUniform4(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniformMatrix2(int $$0, boolean $$1, FloatBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix2fv((int)$$0, (boolean)$$1, (FloatBuffer)$$2);
    }

    public static void _glUniformMatrix3(int $$0, boolean $$1, FloatBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix3fv((int)$$0, (boolean)$$1, (FloatBuffer)$$2);
    }

    public static void _glUniformMatrix4(int $$0, boolean $$1, FloatBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix4fv((int)$$0, (boolean)$$1, (FloatBuffer)$$2);
    }

    public static int _glGetAttribLocation(int $$0, CharSequence $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetAttribLocation((int)$$0, (CharSequence)$$1);
    }

    public static void _glBindAttribLocation(int $$0, int $$1, CharSequence $$2) {
        RenderSystem.assertOnRenderThread();
        GL20.glBindAttribLocation((int)$$0, (int)$$1, (CharSequence)$$2);
    }

    public static int _glGenBuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL15.glGenBuffers();
    }

    public static int _glGenVertexArrays() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenVertexArrays();
    }

    public static void _glBindBuffer(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glBindBuffer((int)$$0, (int)$$1);
    }

    public static void _glBindVertexArray(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindVertexArray((int)$$0);
    }

    public static void _glBufferData(int $$0, ByteBuffer $$1, int $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glBufferData((int)$$0, (ByteBuffer)$$1, (int)$$2);
    }

    public static void _glBufferData(int $$0, long $$1, int $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glBufferData((int)$$0, (long)$$1, (int)$$2);
    }

    @Nullable
    public static ByteBuffer _glMapBuffer(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL15.glMapBuffer((int)$$0, (int)$$1);
    }

    public static void _glUnmapBuffer(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glUnmapBuffer((int)$$0);
    }

    public static void _glDeleteBuffers(int $$0) {
        RenderSystem.assertOnRenderThread();
        if (ON_LINUX) {
            GL32C.glBindBuffer((int)34962, (int)$$0);
            GL32C.glBufferData((int)34962, (long)0L, (int)35048);
            GL32C.glBindBuffer((int)34962, (int)0);
        }
        GL15.glDeleteBuffers((int)$$0);
    }

    public static void _glCopyTexSubImage2D(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL20.glCopyTexSubImage2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7);
    }

    public static void _glDeleteVertexArrays(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteVertexArrays((int)$$0);
    }

    public static void _glBindFramebuffer(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindFramebuffer((int)$$0, (int)$$1);
    }

    public static void _glBlitFrameBuffer(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBlitFramebuffer((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (int)$$8, (int)$$9);
    }

    public static void _glBindRenderbuffer(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindRenderbuffer((int)$$0, (int)$$1);
    }

    public static void _glDeleteRenderbuffers(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glDeleteRenderbuffers((int)$$0);
    }

    public static void _glDeleteFramebuffers(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glDeleteFramebuffers((int)$$0);
    }

    public static int glGenFramebuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenFramebuffers();
    }

    public static int glGenRenderbuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenRenderbuffers();
    }

    public static void _glRenderbufferStorage(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glRenderbufferStorage((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static void _glFramebufferRenderbuffer(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glFramebufferRenderbuffer((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static int glCheckFramebufferStatus(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glCheckFramebufferStatus((int)$$0);
    }

    public static void _glFramebufferTexture2D(int $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glFramebufferTexture2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4);
    }

    public static int getBoundFramebuffer() {
        RenderSystem.assertOnRenderThread();
        return GlStateManager._getInteger(36006);
    }

    public static void glActiveTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL13.glActiveTexture((int)$$0);
    }

    public static void glBlendFuncSeparate(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendFuncSeparate((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static String glGetShaderInfoLog(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderInfoLog((int)$$0, (int)$$1);
    }

    public static String glGetProgramInfoLog(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgramInfoLog((int)$$0, (int)$$1);
    }

    public static void setupLevelDiffuseLighting(Vector3f $$0, Vector3f $$1, Matrix4f $$2) {
        RenderSystem.assertOnRenderThread();
        Vector4f $$3 = $$2.transform(new Vector4f((Vector3fc)$$0, 1.0f));
        Vector4f $$4 = $$2.transform(new Vector4f((Vector3fc)$$1, 1.0f));
        RenderSystem.setShaderLights(new Vector3f($$3.x(), $$3.y(), $$3.z()), new Vector3f($$4.x(), $$4.y(), $$4.z()));
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f $$0, Vector3f $$1) {
        RenderSystem.assertOnRenderThread();
        Matrix4f $$2 = new Matrix4f().scaling(1.0f, -1.0f, 1.0f).rotateY(-0.3926991f).rotateX(2.3561945f);
        GlStateManager.setupLevelDiffuseLighting($$0, $$1, $$2);
    }

    public static void setupGui3DDiffuseLighting(Vector3f $$0, Vector3f $$1) {
        RenderSystem.assertOnRenderThread();
        Matrix4f $$2 = new Matrix4f().rotationYXZ(1.0821041f, 3.2375858f, 0.0f).rotateYXZ(-0.3926991f, 2.3561945f, 0.0f);
        GlStateManager.setupLevelDiffuseLighting($$0, $$1, $$2);
    }

    public static void _enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.enable.enable();
    }

    public static void _disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.enable.disable();
    }

    public static void _polygonMode(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL11.glPolygonMode((int)$$0, (int)$$1);
    }

    public static void _enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.fill.enable();
    }

    public static void _disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.fill.disable();
    }

    public static void _polygonOffset(float $$0, float $$1) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.POLY_OFFSET.factor || $$1 != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = $$0;
            GlStateManager.POLY_OFFSET.units = $$1;
            GL11.glPolygonOffset((float)$$0, (float)$$1);
        }
    }

    public static void _enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.enable.enable();
    }

    public static void _disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.enable.disable();
    }

    public static void _logicOp(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = $$0;
            GL11.glLogicOp((int)$$0);
        }
    }

    public static void _activeTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        if (activeTexture != $$0 - 33984) {
            activeTexture = $$0 - 33984;
            GlStateManager.glActiveTexture($$0);
        }
    }

    public static void _texParameter(int $$0, int $$1, float $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexParameterf((int)$$0, (int)$$1, (float)$$2);
    }

    public static void _texParameter(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexParameteri((int)$$0, (int)$$1, (int)$$2);
    }

    public static int _getTexLevelParameter(int $$0, int $$1, int $$2) {
        RenderSystem.assertInInitPhase();
        return GL11.glGetTexLevelParameteri((int)$$0, (int)$$1, (int)$$2);
    }

    public static int _genTexture() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL11.glGenTextures();
    }

    public static void _genTextures(int[] $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glGenTextures((int[])$$0);
    }

    public static void _deleteTexture(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glDeleteTextures((int)$$0);
        for (TextureState $$1 : TEXTURES) {
            if ($$1.binding != $$0) continue;
            $$1.binding = -1;
        }
    }

    public static void _deleteTextures(int[] $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        for (TextureState $$1 : TEXTURES) {
            for (int $$2 : $$0) {
                if ($$1.binding != $$2) continue;
                $$1.binding = -1;
            }
        }
        GL11.glDeleteTextures((int[])$$0);
    }

    public static void _bindTexture(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        if ($$0 != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = $$0;
            GL11.glBindTexture((int)3553, (int)$$0);
        }
    }

    public static int _getActiveTexture() {
        return activeTexture + 33984;
    }

    public static void _texImage2D(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, @Nullable IntBuffer $$8) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexImage2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (IntBuffer)$$8);
    }

    public static void _texSubImage2D(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, long $$8) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexSubImage2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (long)$$8);
    }

    public static void _getTexImage(int $$0, int $$1, int $$2, int $$3, long $$4) {
        RenderSystem.assertOnRenderThread();
        GL11.glGetTexImage((int)$$0, (int)$$1, (int)$$2, (int)$$3, (long)$$4);
    }

    public static void _viewport(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThreadOrInit();
        Viewport.INSTANCE.x = $$0;
        Viewport.INSTANCE.y = $$1;
        Viewport.INSTANCE.width = $$2;
        Viewport.INSTANCE.height = $$3;
        GL11.glViewport((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static void _colorMask(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.COLOR_MASK.red || $$1 != GlStateManager.COLOR_MASK.green || $$2 != GlStateManager.COLOR_MASK.blue || $$3 != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = $$0;
            GlStateManager.COLOR_MASK.green = $$1;
            GlStateManager.COLOR_MASK.blue = $$2;
            GlStateManager.COLOR_MASK.alpha = $$3;
            GL11.glColorMask((boolean)$$0, (boolean)$$1, (boolean)$$2, (boolean)$$3);
        }
    }

    public static void _stencilFunc(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.STENCIL.func.func || $$0 != GlStateManager.STENCIL.func.ref || $$0 != GlStateManager.STENCIL.func.mask) {
            GlStateManager.STENCIL.func.func = $$0;
            GlStateManager.STENCIL.func.ref = $$1;
            GlStateManager.STENCIL.func.mask = $$2;
            GL11.glStencilFunc((int)$$0, (int)$$1, (int)$$2);
        }
    }

    public static void _stencilMask(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = $$0;
            GL11.glStencilMask((int)$$0);
        }
    }

    public static void _stencilOp(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.STENCIL.fail || $$1 != GlStateManager.STENCIL.zfail || $$2 != GlStateManager.STENCIL.zpass) {
            GlStateManager.STENCIL.fail = $$0;
            GlStateManager.STENCIL.zfail = $$1;
            GlStateManager.STENCIL.zpass = $$2;
            GL11.glStencilOp((int)$$0, (int)$$1, (int)$$2);
        }
    }

    public static void _clearDepth(double $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glClearDepth((double)$$0);
    }

    public static void _clearColor(float $$0, float $$1, float $$2, float $$3) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glClearColor((float)$$0, (float)$$1, (float)$$2, (float)$$3);
    }

    public static void _clearStencil(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL11.glClearStencil((int)$$0);
    }

    public static void _clear(int $$0, boolean $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glClear((int)$$0);
        if ($$1) {
            GlStateManager._getError();
        }
    }

    public static void _glDrawPixels(int $$0, int $$1, int $$2, int $$3, long $$4) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawPixels((int)$$0, (int)$$1, (int)$$2, (int)$$3, (long)$$4);
    }

    public static void _vertexAttribPointer(int $$0, int $$1, int $$2, boolean $$3, int $$4, long $$5) {
        RenderSystem.assertOnRenderThread();
        GL20.glVertexAttribPointer((int)$$0, (int)$$1, (int)$$2, (boolean)$$3, (int)$$4, (long)$$5);
    }

    public static void _vertexAttribIPointer(int $$0, int $$1, int $$2, int $$3, long $$4) {
        RenderSystem.assertOnRenderThread();
        GL30.glVertexAttribIPointer((int)$$0, (int)$$1, (int)$$2, (int)$$3, (long)$$4);
    }

    public static void _enableVertexAttribArray(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glEnableVertexAttribArray((int)$$0);
    }

    public static void _disableVertexAttribArray(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glDisableVertexAttribArray((int)$$0);
    }

    public static void _drawElements(int $$0, int $$1, int $$2, long $$3) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawElements((int)$$0, (int)$$1, (int)$$2, (long)$$3);
    }

    public static void _pixelStore(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glPixelStorei((int)$$0, (int)$$1);
    }

    public static void _readPixels(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, ByteBuffer $$6) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (ByteBuffer)$$6);
    }

    public static void _readPixels(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, long $$6) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (long)$$6);
    }

    public static int _getError() {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetError();
    }

    public static String _getString(int $$0) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetString((int)$$0);
    }

    public static int _getInteger(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL11.glGetInteger((int)$$0);
    }

    static {
        TEXTURES = (TextureState[])IntStream.range((int)0, (int)12).mapToObj($$0 -> new TextureState()).toArray(TextureState[]::new);
        COLOR_MASK = new ColorMask();
    }

    static class ScissorState {
        public final BooleanState mode = new BooleanState(3089);

        ScissorState() {
        }
    }

    static class BooleanState {
        private final int state;
        private boolean enabled;

        public BooleanState(int $$0) {
            this.state = $$0;
        }

        public void disable() {
            this.setEnabled(false);
        }

        public void enable() {
            this.setEnabled(true);
        }

        public void setEnabled(boolean $$0) {
            RenderSystem.assertOnRenderThreadOrInit();
            if ($$0 != this.enabled) {
                this.enabled = $$0;
                if ($$0) {
                    GL11.glEnable((int)this.state);
                } else {
                    GL11.glDisable((int)this.state);
                }
            }
        }
    }

    static class DepthState {
        public final BooleanState mode = new BooleanState(2929);
        public boolean mask = true;
        public int func = 513;

        DepthState() {
        }
    }

    static class BlendState {
        public final BooleanState mode = new BooleanState(3042);
        public int srcRgb = 1;
        public int dstRgb = 0;
        public int srcAlpha = 1;
        public int dstAlpha = 0;

        BlendState() {
        }
    }

    static class CullState {
        public final BooleanState enable = new BooleanState(2884);
        public int mode = 1029;

        CullState() {
        }
    }

    static class PolygonOffsetState {
        public final BooleanState fill = new BooleanState(32823);
        public final BooleanState line = new BooleanState(10754);
        public float factor;
        public float units;

        PolygonOffsetState() {
        }
    }

    static class ColorLogicState {
        public final BooleanState enable = new BooleanState(3058);
        public int op = 5379;

        ColorLogicState() {
        }
    }

    static class TextureState {
        public int binding;

        TextureState() {
        }
    }

    public static enum Viewport {
        INSTANCE;

        protected int x;
        protected int y;
        protected int width;
        protected int height;

        public static int x() {
            return Viewport.INSTANCE.x;
        }

        public static int y() {
            return Viewport.INSTANCE.y;
        }

        public static int width() {
            return Viewport.INSTANCE.width;
        }

        public static int height() {
            return Viewport.INSTANCE.height;
        }
    }

    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        ColorMask() {
        }
    }

    static class StencilState {
        public final StencilFunc func = new StencilFunc();
        public int mask = -1;
        public int fail = 7680;
        public int zfail = 7680;
        public int zpass = 7680;

        StencilState() {
        }
    }

    static class StencilFunc {
        public int func = 519;
        public int ref;
        public int mask = -1;

        StencilFunc() {
        }
    }

    @DontObfuscate
    public static enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        private DestFactor(int $$0) {
            this.value = $$0;
        }
    }

    @DontObfuscate
    public static enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        private SourceFactor(int $$0) {
            this.value = $$0;
        }
    }

    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int value;

        private LogicOp(int $$0) {
            this.value = $$0;
        }
    }
}