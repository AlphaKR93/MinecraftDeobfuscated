/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.nio.ByteBuffer
 *  javax.annotation.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class VertexBuffer
implements AutoCloseable {
    private int vertexBufferId;
    private int indexBufferId;
    private int arrayObjectId;
    @Nullable
    private VertexFormat format;
    @Nullable
    private RenderSystem.AutoStorageIndexBuffer sequentialIndices;
    private VertexFormat.IndexType indexType;
    private int indexCount;
    private VertexFormat.Mode mode;

    public VertexBuffer() {
        RenderSystem.assertOnRenderThread();
        this.vertexBufferId = GlStateManager._glGenBuffers();
        this.indexBufferId = GlStateManager._glGenBuffers();
        this.arrayObjectId = GlStateManager._glGenVertexArrays();
    }

    public void upload(BufferBuilder.RenderedBuffer $$0) {
        if (this.isInvalid()) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        try {
            BufferBuilder.DrawState $$1 = $$0.drawState();
            this.format = this.uploadVertexBuffer($$1, $$0.vertexBuffer());
            this.sequentialIndices = this.uploadIndexBuffer($$1, $$0.indexBuffer());
            this.indexCount = $$1.indexCount();
            this.indexType = $$1.indexType();
            this.mode = $$1.mode();
        }
        finally {
            $$0.release();
        }
    }

    private VertexFormat uploadVertexBuffer(BufferBuilder.DrawState $$0, ByteBuffer $$1) {
        boolean $$2 = false;
        if (!$$0.format().equals(this.format)) {
            if (this.format != null) {
                this.format.clearBufferState();
            }
            GlStateManager._glBindBuffer(34962, this.vertexBufferId);
            $$0.format().setupBufferState();
            $$2 = true;
        }
        if (!$$0.indexOnly()) {
            if (!$$2) {
                GlStateManager._glBindBuffer(34962, this.vertexBufferId);
            }
            RenderSystem.glBufferData(34962, $$1, 35044);
        }
        return $$0.format();
    }

    @Nullable
    private RenderSystem.AutoStorageIndexBuffer uploadIndexBuffer(BufferBuilder.DrawState $$0, ByteBuffer $$1) {
        if ($$0.sequentialIndex()) {
            RenderSystem.AutoStorageIndexBuffer $$2 = RenderSystem.getSequentialBuffer($$0.mode());
            if ($$2 != this.sequentialIndices || !$$2.hasStorage($$0.indexCount())) {
                $$2.bind($$0.indexCount());
            }
            return $$2;
        }
        GlStateManager._glBindBuffer(34963, this.indexBufferId);
        RenderSystem.glBufferData(34963, $$1, 35044);
        return null;
    }

    public void bind() {
        BufferUploader.invalidate();
        GlStateManager._glBindVertexArray(this.arrayObjectId);
    }

    public static void unbind() {
        BufferUploader.invalidate();
        GlStateManager._glBindVertexArray(0);
    }

    public void draw() {
        RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.getIndexType().asGLType);
    }

    private VertexFormat.IndexType getIndexType() {
        RenderSystem.AutoStorageIndexBuffer $$0 = this.sequentialIndices;
        return $$0 != null ? $$0.type() : this.indexType;
    }

    public void drawWithShader(Matrix4f $$0, Matrix4f $$1, ShaderInstance $$2) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._drawWithShader(new Matrix4f((Matrix4fc)$$0), new Matrix4f((Matrix4fc)$$1), $$2));
        } else {
            this._drawWithShader($$0, $$1, $$2);
        }
    }

    private void _drawWithShader(Matrix4f $$0, Matrix4f $$1, ShaderInstance $$2) {
        for (int $$3 = 0; $$3 < 12; ++$$3) {
            int $$4 = RenderSystem.getShaderTexture($$3);
            $$2.setSampler("Sampler" + $$3, $$4);
        }
        if ($$2.MODEL_VIEW_MATRIX != null) {
            $$2.MODEL_VIEW_MATRIX.set($$0);
        }
        if ($$2.PROJECTION_MATRIX != null) {
            $$2.PROJECTION_MATRIX.set($$1);
        }
        if ($$2.INVERSE_VIEW_ROTATION_MATRIX != null) {
            $$2.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if ($$2.COLOR_MODULATOR != null) {
            $$2.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if ($$2.GLINT_ALPHA != null) {
            $$2.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
        }
        if ($$2.FOG_START != null) {
            $$2.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if ($$2.FOG_END != null) {
            $$2.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if ($$2.FOG_COLOR != null) {
            $$2.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if ($$2.FOG_SHAPE != null) {
            $$2.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        if ($$2.TEXTURE_MATRIX != null) {
            $$2.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }
        if ($$2.GAME_TIME != null) {
            $$2.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        if ($$2.SCREEN_SIZE != null) {
            Window $$5 = Minecraft.getInstance().getWindow();
            $$2.SCREEN_SIZE.set((float)$$5.getWidth(), (float)$$5.getHeight());
        }
        if ($$2.LINE_WIDTH != null && (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP)) {
            $$2.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights($$2);
        $$2.apply();
        this.draw();
        $$2.clear();
    }

    public void close() {
        if (this.vertexBufferId >= 0) {
            RenderSystem.glDeleteBuffers(this.vertexBufferId);
            this.vertexBufferId = -1;
        }
        if (this.indexBufferId >= 0) {
            RenderSystem.glDeleteBuffers(this.indexBufferId);
            this.indexBufferId = -1;
        }
        if (this.arrayObjectId >= 0) {
            RenderSystem.glDeleteVertexArrays(this.arrayObjectId);
            this.arrayObjectId = -1;
        }
    }

    public VertexFormat getFormat() {
        return this.format;
    }

    public boolean isInvalid() {
        return this.arrayObjectId == -1;
    }
}