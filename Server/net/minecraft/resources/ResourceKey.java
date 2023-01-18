/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.concurrent.ConcurrentMap
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraft.resources;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ResourceKey<T> {
    private static final ConcurrentMap<InternKey, ResourceKey<?>> VALUES = new MapMaker().weakValues().makeMap();
    private final ResourceLocation registryName;
    private final ResourceLocation location;

    public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> $$0) {
        return ResourceLocation.CODEC.xmap($$1 -> ResourceKey.create($$0, $$1), ResourceKey::location);
    }

    public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> $$0, ResourceLocation $$1) {
        return ResourceKey.create($$0.location, $$1);
    }

    public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation $$0) {
        return ResourceKey.create(BuiltInRegistries.ROOT_REGISTRY_NAME, $$0);
    }

    private static <T> ResourceKey<T> create(ResourceLocation $$02, ResourceLocation $$1) {
        return (ResourceKey)VALUES.computeIfAbsent((Object)new InternKey($$02, $$1), $$0 -> new ResourceKey($$0.registry, $$0.location));
    }

    private ResourceKey(ResourceLocation $$0, ResourceLocation $$1) {
        this.registryName = $$0;
        this.location = $$1;
    }

    public String toString() {
        return "ResourceKey[" + this.registryName + " / " + this.location + "]";
    }

    public boolean isFor(ResourceKey<? extends Registry<?>> $$0) {
        return this.registryName.equals((Object)$$0.location());
    }

    public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
        return this.isFor($$0) ? Optional.of((Object)this) : Optional.empty();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public ResourceLocation registry() {
        return this.registryName;
    }

    record InternKey(ResourceLocation registry, ResourceLocation location) {
    }
}