/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.util.concurrent.Executor
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class AbstractTexture
implements AutoCloseable {
    public static final int NOT_ASSIGNED = -1;
    protected int id = -1;
    protected boolean blur;
    protected boolean mipmap;

    public void setFilter(boolean $$0, boolean $$1) {
        int $$5;
        int $$4;
        RenderSystem.assertOnRenderThreadOrInit();
        this.blur = $$0;
        this.mipmap = $$1;
        if ($$0) {
            int $$2 = $$1 ? 9987 : 9729;
            int $$3 = 9729;
        } else {
            $$4 = $$1 ? 9986 : 9728;
            $$5 = 9728;
        }
        this.bind();
        GlStateManager._texParameter(3553, 10241, $$4);
        GlStateManager._texParameter(3553, 10240, $$5);
    }

    public int getId() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.id == -1) {
            this.id = TextureUtil.generateTextureId();
        }
        return this.id;
    }

    public void releaseId() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                if (this.id != -1) {
                    TextureUtil.releaseTextureId(this.id);
                    this.id = -1;
                }
            });
        } else if (this.id != -1) {
            TextureUtil.releaseTextureId(this.id);
            this.id = -1;
        }
    }

    public abstract void load(ResourceManager var1) throws IOException;

    public void bind() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._bindTexture(this.getId()));
        } else {
            GlStateManager._bindTexture(this.getId());
        }
    }

    public void reset(TextureManager $$0, ResourceManager $$1, ResourceLocation $$2, Executor $$3) {
        $$0.register($$2, this);
    }

    public void close() {
    }
}