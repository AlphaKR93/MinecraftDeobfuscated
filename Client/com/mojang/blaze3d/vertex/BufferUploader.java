/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import javax.annotation.Nullable;

public class BufferUploader {
    @Nullable
    private static VertexBuffer lastImmediateBuffer;

    public static void reset() {
        if (lastImmediateBuffer != null) {
            BufferUploader.invalidate();
            VertexBuffer.unbind();
        }
    }

    public static void invalidate() {
        lastImmediateBuffer = null;
    }

    public static void drawWithShader(BufferBuilder.RenderedBuffer $$0) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> BufferUploader._drawWithShader($$0));
        } else {
            BufferUploader._drawWithShader($$0);
        }
    }

    private static void _drawWithShader(BufferBuilder.RenderedBuffer $$0) {
        VertexBuffer $$1 = BufferUploader.upload($$0);
        if ($$1 != null) {
            $$1.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }
    }

    public static void draw(BufferBuilder.RenderedBuffer $$0) {
        VertexBuffer $$1 = BufferUploader.upload($$0);
        if ($$1 != null) {
            $$1.draw();
        }
    }

    @Nullable
    private static VertexBuffer upload(BufferBuilder.RenderedBuffer $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0.isEmpty()) {
            $$0.release();
            return null;
        }
        VertexBuffer $$1 = BufferUploader.bindImmediateBuffer($$0.drawState().format());
        $$1.upload($$0);
        return $$1;
    }

    private static VertexBuffer bindImmediateBuffer(VertexFormat $$0) {
        VertexBuffer $$1 = $$0.getImmediateDrawVertexBuffer();
        BufferUploader.bindImmediateBuffer($$1);
        return $$1;
    }

    private static void bindImmediateBuffer(VertexBuffer $$0) {
        if ($$0 != lastImmediateBuffer) {
            $$0.bind();
            lastImmediateBuffer = $$0;
        }
    }
}