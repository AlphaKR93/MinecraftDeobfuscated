/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Void
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.stream.Collectors
 */
package net.minecraft.tags;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;

public class TagManager
implements PreparableReloadListener {
    private static final Map<ResourceKey<? extends Registry<?>>, String> CUSTOM_REGISTRY_DIRECTORIES = Map.of(Registries.BLOCK, (Object)"tags/blocks", Registries.ENTITY_TYPE, (Object)"tags/entity_types", Registries.FLUID, (Object)"tags/fluids", Registries.GAME_EVENT, (Object)"tags/game_events", Registries.ITEM, (Object)"tags/items");
    private final RegistryAccess registryAccess;
    private List<LoadResult<?>> results = List.of();

    public TagManager(RegistryAccess $$0) {
        this.registryAccess = $$0;
    }

    public List<LoadResult<?>> getResult() {
        return this.results;
    }

    public static String getTagDir(ResourceKey<? extends Registry<?>> $$0) {
        String $$1 = (String)CUSTOM_REGISTRY_DIRECTORIES.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return "tags/" + $$0.location().getPath();
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$12, ProfilerFiller $$22, ProfilerFiller $$3, Executor $$4, Executor $$5) {
        List $$6 = this.registryAccess.registries().map($$2 -> this.createLoader($$12, $$4, (RegistryAccess.RegistryEntry)((Object)$$2))).toList();
        return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$6.toArray(CompletableFuture[]::new))).thenCompose($$0::wait).thenAcceptAsync($$1 -> {
            this.results = (List)$$6.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());
        }, $$5);
    }

    private <T> CompletableFuture<LoadResult<T>> createLoader(ResourceManager $$0, Executor $$1, RegistryAccess.RegistryEntry<T> $$22) {
        ResourceKey $$3 = $$22.key();
        Registry $$4 = $$22.value();
        TagLoader $$5 = new TagLoader($$2 -> $$4.getHolder(ResourceKey.create($$3, $$2)), TagManager.getTagDir($$3));
        return CompletableFuture.supplyAsync(() -> new LoadResult($$3, $$5.loadAndBuild($$0)), (Executor)$$1);
    }

    public record LoadResult<T>(ResourceKey<? extends Registry<T>> key, Map<ResourceLocation, Collection<Holder<T>>> tags) {
    }
}