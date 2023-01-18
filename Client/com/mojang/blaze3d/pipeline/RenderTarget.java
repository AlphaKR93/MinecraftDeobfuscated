/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  org.joml.Matrix4f
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public abstract class RenderTarget {
    private static final int RED_CHANNEL = 0;
    private static final int GREEN_CHANNEL = 1;
    private static final int BLUE_CHANNEL = 2;
    private static final int ALPHA_CHANNEL = 3;
    public int width;
    public int height;
    public int viewWidth;
    public int viewHeight;
    public final boolean useDepth;
    public int frameBufferId;
    protected int colorTextureId;
    protected int depthBufferId;
    private final float[] clearChannels = (float[])Util.make(() -> {
        float[] $$0 = new float[]{1.0f, 1.0f, 1.0f, 0.0f};
        return $$0;
    });
    public int filterMode;

    public RenderTarget(boolean $$0) {
        this.useDepth = $$0;
        this.frameBufferId = -1;
        this.colorTextureId = -1;
        this.depthBufferId = -1;
    }

    public void resize(int $$0, int $$1, boolean $$2) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._resize($$0, $$1, $$2));
        } else {
            this._resize($$0, $$1, $$2);
        }
    }

    private void _resize(int $$0, int $$1, boolean $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._enableDepthTest();
        if (this.frameBufferId >= 0) {
            this.destroyBuffers();
        }
        this.createBuffers($$0, $$1, $$2);
        GlStateManager._glBindFramebuffer(36160, 0);
    }

    public void destroyBuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        this.unbindRead();
        this.unbindWrite();
        if (this.depthBufferId > -1) {
            TextureUtil.releaseTextureId(this.depthBufferId);
            this.depthBufferId = -1;
        }
        if (this.colorTextureId > -1) {
            TextureUtil.releaseTextureId(this.colorTextureId);
            this.colorTextureId = -1;
        }
        if (this.frameBufferId > -1) {
            GlStateManager._glBindFramebuffer(36160, 0);
            GlStateManager._glDeleteFramebuffers(this.frameBufferId);
            this.frameBufferId = -1;
        }
    }

    public void copyDepthFrom(RenderTarget $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer(36008, $$0.frameBufferId);
        GlStateManager._glBindFramebuffer(36009, this.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, $$0.width, $$0.height, 0, 0, this.width, this.height, 256, 9728);
        GlStateManager._glBindFramebuffer(36160, 0);
    }

    public void createBuffers(int $$0, int $$1, boolean $$2) {
        RenderSystem.assertOnRenderThreadOrInit();
        int $$3 = RenderSystem.maxSupportedTextureSize();
        if ($$0 <= 0 || $$0 > $$3 || $$1 <= 0 || $$1 > $$3) {
            throw new IllegalArgumentException("Window " + $$0 + "x" + $$1 + " size out of bounds (max. size: " + $$3 + ")");
        }
        this.viewWidth = $$0;
        this.viewHeight = $$1;
        this.width = $$0;
        this.height = $$1;
        this.frameBufferId = GlStateManager.glGenFramebuffers();
        this.colorTextureId = TextureUtil.generateTextureId();
        if (this.useDepth) {
            this.depthBufferId = TextureUtil.generateTextureId();
            GlStateManager._bindTexture(this.depthBufferId);
            GlStateManager._texParameter(3553, 10241, 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
            GlStateManager._texParameter(3553, 34892, 0);
            GlStateManager._texParameter(3553, 10242, 33071);
            GlStateManager._texParameter(3553, 10243, 33071);
            GlStateManager._texImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, null);
        }
        this.setFilterMode(9728);
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texParameter(3553, 10242, 33071);
        GlStateManager._texParameter(3553, 10243, 33071);
        GlStateManager._texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, null);
        GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
        GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTextureId, 0);
        if (this.useDepth) {
            GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthBufferId, 0);
        }
        this.checkStatus();
        this.clear($$2);
        this.unbindRead();
    }

    public void setFilterMode(int $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.filterMode = $$0;
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texParameter(3553, 10241, $$0);
        GlStateManager._texParameter(3553, 10240, $$0);
        GlStateManager._bindTexture(0);
    }

    public void checkStatus() {
        RenderSystem.assertOnRenderThreadOrInit();
        int $$0 = GlStateManager.glCheckFramebufferStatus(36160);
        if ($$0 == 36053) {
            return;
        }
        if ($$0 == 36054) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        }
        if ($$0 == 36055) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        }
        if ($$0 == 36059) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        }
        if ($$0 == 36060) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        }
        if ($$0 == 36061) {
            throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
        }
        if ($$0 == 1285) {
            throw new RuntimeException("GL_OUT_OF_MEMORY");
        }
        throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + $$0);
    }

    public void bindRead() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._bindTexture(this.colorTextureId);
    }

    public void unbindRead() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._bindTexture(0);
    }

    public void bindWrite(boolean $$0) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._bindWrite($$0));
        } else {
            this._bindWrite($$0);
        }
    }

    private void _bindWrite(boolean $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
        if ($$0) {
            GlStateManager._viewport(0, 0, this.viewWidth, this.viewHeight);
        }
    }

    public void unbindWrite() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._glBindFramebuffer(36160, 0));
        } else {
            GlStateManager._glBindFramebuffer(36160, 0);
        }
    }

    public void setClearColor(float $$0, float $$1, float $$2, float $$3) {
        this.clearChannels[0] = $$0;
        this.clearChannels[1] = $$1;
        this.clearChannels[2] = $$2;
        this.clearChannels[3] = $$3;
    }

    public void blitToScreen(int $$0, int $$1) {
        this.blitToScreen($$0, $$1, true);
    }

    public void blitToScreen(int $$0, int $$1, boolean $$2) {
        RenderSystem.assertOnGameThreadOrInit();
        if (!RenderSystem.isInInitPhase()) {
            RenderSystem.recordRenderCall(() -> this._blitToScreen($$0, $$1, $$2));
        } else {
            this._blitToScreen($$0, $$1, $$2);
        }
    }

    private void _blitToScreen(int $$0, int $$1, boolean $$2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, $$0, $$1);
        if ($$2) {
            GlStateManager._disableBlend();
        }
        Minecraft $$3 = Minecraft.getInstance();
        ShaderInstance $$4 = $$3.gameRenderer.blitShader;
        $$4.setSampler("DiffuseSampler", this.colorTextureId);
        Matrix4f $$5 = new Matrix4f().setOrtho(0.0f, (float)$$0, (float)$$1, 0.0f, 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix($$5);
        if ($$4.MODEL_VIEW_MATRIX != null) {
            $$4.MODEL_VIEW_MATRIX.set(new Matrix4f().translation(0.0f, 0.0f, -2000.0f));
        }
        if ($$4.PROJECTION_MATRIX != null) {
            $$4.PROJECTION_MATRIX.set($$5);
        }
        $$4.apply();
        float $$6 = $$0;
        float $$7 = $$1;
        float $$8 = (float)this.viewWidth / (float)this.width;
        float $$9 = (float)this.viewHeight / (float)this.height;
        Tesselator $$10 = RenderSystem.renderThreadTesselator();
        BufferBuilder $$11 = $$10.getBuilder();
        $$11.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        $$11.vertex(0.0, $$7, 0.0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        $$11.vertex($$6, $$7, 0.0).uv($$8, 0.0f).color(255, 255, 255, 255).endVertex();
        $$11.vertex($$6, 0.0, 0.0).uv($$8, $$9).color(255, 255, 255, 255).endVertex();
        $$11.vertex(0.0, 0.0, 0.0).uv(0.0f, $$9).color(255, 255, 255, 255).endVertex();
        BufferUploader.draw($$11.end());
        $$4.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }

    public void clear(boolean $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.bindWrite(true);
        GlStateManager._clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
        int $$1 = 16384;
        if (this.useDepth) {
            GlStateManager._clearDepth(1.0);
            $$1 |= 0x100;
        }
        GlStateManager._clear($$1, $$0);
        this.unbindWrite();
    }

    public int getColorTextureId() {
        return this.colorTextureId;
    }

    public int getDepthTextureId() {
        return this.depthBufferId;
    }
}