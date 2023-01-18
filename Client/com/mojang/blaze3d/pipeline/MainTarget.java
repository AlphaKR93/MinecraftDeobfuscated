/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.List
 *  java.util.Objects
 */
package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;

public class MainTarget
extends RenderTarget {
    public static final int DEFAULT_WIDTH = 854;
    public static final int DEFAULT_HEIGHT = 480;
    static final Dimension DEFAULT_DIMENSIONS = new Dimension(854, 480);

    public MainTarget(int $$0, int $$1) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.createFrameBuffer($$0, $$1));
        } else {
            this.createFrameBuffer($$0, $$1);
        }
    }

    private void createFrameBuffer(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        Dimension $$2 = this.allocateAttachments($$0, $$1);
        this.frameBufferId = GlStateManager.glGenFramebuffers();
        GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texParameter(3553, 10241, 9728);
        GlStateManager._texParameter(3553, 10240, 9728);
        GlStateManager._texParameter(3553, 10242, 33071);
        GlStateManager._texParameter(3553, 10243, 33071);
        GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTextureId, 0);
        GlStateManager._bindTexture(this.depthBufferId);
        GlStateManager._texParameter(3553, 34892, 0);
        GlStateManager._texParameter(3553, 10241, 9728);
        GlStateManager._texParameter(3553, 10240, 9728);
        GlStateManager._texParameter(3553, 10242, 33071);
        GlStateManager._texParameter(3553, 10243, 33071);
        GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthBufferId, 0);
        GlStateManager._bindTexture(0);
        this.viewWidth = $$2.width;
        this.viewHeight = $$2.height;
        this.width = $$2.width;
        this.height = $$2.height;
        this.checkStatus();
        GlStateManager._glBindFramebuffer(36160, 0);
    }

    private Dimension allocateAttachments(int $$0, int $$1) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.colorTextureId = TextureUtil.generateTextureId();
        this.depthBufferId = TextureUtil.generateTextureId();
        AttachmentState $$2 = AttachmentState.NONE;
        for (Dimension $$3 : Dimension.listWithFallback($$0, $$1)) {
            $$2 = AttachmentState.NONE;
            if (this.allocateColorAttachment($$3)) {
                $$2 = $$2.with(AttachmentState.COLOR);
            }
            if (this.allocateDepthAttachment($$3)) {
                $$2 = $$2.with(AttachmentState.DEPTH);
            }
            if ($$2 != AttachmentState.COLOR_DEPTH) continue;
            return $$3;
        }
        throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (allocated attachments = " + $$2.name() + ")");
    }

    private boolean allocateColorAttachment(Dimension $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texImage2D(3553, 0, 32856, $$0.width, $$0.height, 0, 6408, 5121, null);
        return GlStateManager._getError() != 1285;
    }

    private boolean allocateDepthAttachment(Dimension $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        GlStateManager._bindTexture(this.depthBufferId);
        GlStateManager._texImage2D(3553, 0, 6402, $$0.width, $$0.height, 0, 6402, 5126, null);
        return GlStateManager._getError() != 1285;
    }

    static class Dimension {
        public final int width;
        public final int height;

        Dimension(int $$0, int $$1) {
            this.width = $$0;
            this.height = $$1;
        }

        static List<Dimension> listWithFallback(int $$0, int $$1) {
            RenderSystem.assertOnRenderThreadOrInit();
            int $$2 = RenderSystem.maxSupportedTextureSize();
            if ($$0 <= 0 || $$0 > $$2 || $$1 <= 0 || $$1 > $$2) {
                return ImmutableList.of((Object)DEFAULT_DIMENSIONS);
            }
            return ImmutableList.of((Object)new Dimension($$0, $$1), (Object)DEFAULT_DIMENSIONS);
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            Dimension $$1 = (Dimension)$$0;
            return this.width == $$1.width && this.height == $$1.height;
        }

        public int hashCode() {
            return Objects.hash((Object[])new Object[]{this.width, this.height});
        }

        public String toString() {
            return this.width + "x" + this.height;
        }
    }

    static enum AttachmentState {
        NONE,
        COLOR,
        DEPTH,
        COLOR_DEPTH;

        private static final AttachmentState[] VALUES;

        AttachmentState with(AttachmentState $$0) {
            return VALUES[this.ordinal() | $$0.ordinal()];
        }

        static {
            VALUES = AttachmentState.values();
        }
    }
}