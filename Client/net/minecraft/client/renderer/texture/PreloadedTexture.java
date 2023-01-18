/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Void
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class PreloadedTexture
extends SimpleTexture {
    @Nullable
    private CompletableFuture<SimpleTexture.TextureImage> future;

    public PreloadedTexture(ResourceManager $$0, ResourceLocation $$1, Executor $$2) {
        super($$1);
        this.future = CompletableFuture.supplyAsync(() -> SimpleTexture.TextureImage.load($$0, $$1), (Executor)$$2);
    }

    @Override
    protected SimpleTexture.TextureImage getTextureImage(ResourceManager $$0) {
        if (this.future != null) {
            SimpleTexture.TextureImage $$1 = (SimpleTexture.TextureImage)this.future.join();
            this.future = null;
            return $$1;
        }
        return SimpleTexture.TextureImage.load($$0, this.location);
    }

    public CompletableFuture<Void> getFuture() {
        return this.future == null ? CompletableFuture.completedFuture(null) : this.future.thenApply($$0 -> null);
    }

    @Override
    public void reset(TextureManager $$0, ResourceManager $$1, ResourceLocation $$2, Executor $$3) {
        this.future = CompletableFuture.supplyAsync(() -> SimpleTexture.TextureImage.load($$1, this.location), (Executor)Util.backgroundExecutor());
        this.future.thenRunAsync(() -> $$0.register(this.location, this), PreloadedTexture.executor($$3));
    }

    private static Executor executor(Executor $$0) {
        return $$1 -> $$0.execute(() -> RenderSystem.recordRenderCall(() -> ((Runnable)$$1).run()));
    }
}