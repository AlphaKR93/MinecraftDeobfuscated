/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.AutoCloseable
 *  java.lang.Object
 *  java.lang.Void
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AtlasSet
implements AutoCloseable {
    private final Map<ResourceLocation, AtlasEntry> atlases;

    public AtlasSet(Map<ResourceLocation, ResourceLocation> $$0, TextureManager $$12) {
        this.atlases = (Map)$$0.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, $$1 -> {
            TextureAtlas $$2 = new TextureAtlas((ResourceLocation)$$1.getKey());
            $$12.register((ResourceLocation)$$1.getKey(), $$2);
            return new AtlasEntry($$2, (ResourceLocation)$$1.getValue());
        }));
    }

    public TextureAtlas getAtlas(ResourceLocation $$0) {
        return ((AtlasEntry)((Object)this.atlases.get((Object)$$0))).atlas();
    }

    public void close() {
        this.atlases.values().forEach(AtlasEntry::close);
        this.atlases.clear();
    }

    public Map<ResourceLocation, CompletableFuture<StitchResult>> scheduleLoad(ResourceManager $$0, int $$1, Executor $$2) {
        return (Map)this.atlases.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, $$3 -> {
            AtlasEntry $$4 = (AtlasEntry)((Object)((Object)$$3.getValue()));
            return SpriteLoader.create($$4.atlas).loadAndStitch($$0, $$4.atlasInfoLocation, $$1, $$2).thenApply($$1 -> new StitchResult($$0.atlas, (SpriteLoader.Preparations)((Object)((Object)$$1))));
        }));
    }

    record AtlasEntry(TextureAtlas atlas, ResourceLocation atlasInfoLocation) implements AutoCloseable
    {
        public void close() {
            this.atlas.clearTextureData();
        }
    }

    public static class StitchResult {
        private final TextureAtlas atlas;
        private final SpriteLoader.Preparations preparations;

        public StitchResult(TextureAtlas $$0, SpriteLoader.Preparations $$1) {
            this.atlas = $$0;
            this.preparations = $$1;
        }

        @Nullable
        public TextureAtlasSprite getSprite(ResourceLocation $$0) {
            return (TextureAtlasSprite)this.preparations.regions().get((Object)$$0);
        }

        public TextureAtlasSprite missing() {
            return this.preparations.missing();
        }

        public CompletableFuture<Void> readyForUpload() {
            return this.preparations.readyForUpload();
        }

        public void upload() {
            this.atlas.upload(this.preparations);
        }
    }
}