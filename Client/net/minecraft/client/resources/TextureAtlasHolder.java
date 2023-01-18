/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Void
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 */
package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class TextureAtlasHolder
implements PreparableReloadListener,
AutoCloseable {
    private final TextureAtlas textureAtlas;
    private final ResourceLocation atlasInfoLocation;

    public TextureAtlasHolder(TextureManager $$0, ResourceLocation $$1, ResourceLocation $$2) {
        this.atlasInfoLocation = $$2;
        this.textureAtlas = new TextureAtlas($$1);
        $$0.register(this.textureAtlas.location(), this.textureAtlas);
    }

    protected TextureAtlasSprite getSprite(ResourceLocation $$0) {
        return this.textureAtlas.getSprite($$0);
    }

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$12, ProfilerFiller $$2, ProfilerFiller $$3, Executor $$4, Executor $$5) {
        return SpriteLoader.create(this.textureAtlas).loadAndStitch($$12, this.atlasInfoLocation, 0, $$4).thenCompose(SpriteLoader.Preparations::waitForUpload).thenCompose($$0::wait).thenAcceptAsync($$1 -> this.apply((SpriteLoader.Preparations)((Object)$$1), $$3), $$5);
    }

    private void apply(SpriteLoader.Preparations $$0, ProfilerFiller $$1) {
        $$1.startTick();
        $$1.push("upload");
        this.textureAtlas.upload($$0);
        $$1.pop();
        $$1.endTick();
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }
}