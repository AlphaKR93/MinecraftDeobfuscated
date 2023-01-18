/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.Lifecycle
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.Spliterator
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface Registry<T>
extends Keyable,
IdMap<T> {
    public ResourceKey<? extends Registry<T>> key();

    default public Codec<T> byNameCodec() {
        Codec $$02 = ResourceLocation.CODEC.flatXmap($$0 -> (DataResult)Optional.ofNullable(this.get((ResourceLocation)$$0)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown registry key in " + this.key() + ": " + $$0))), $$0 -> (DataResult)this.getResourceKey($$0).map(ResourceKey::location).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown registry element in " + this.key() + ":" + $$0))));
        Codec $$1 = ExtraCodecs.idResolverCodec($$0 -> this.getResourceKey($$0).isPresent() ? this.getId($$0) : -1, this::byId, -1);
        return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed($$02, $$1), this::lifecycle, this::lifecycle);
    }

    default public Codec<Holder<T>> holderByNameCodec() {
        Codec $$02 = ResourceLocation.CODEC.flatXmap($$0 -> (DataResult)this.getHolder(ResourceKey.create(this.key(), $$0)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown registry key in " + this.key() + ": " + $$0))), $$0 -> (DataResult)$$0.unwrapKey().map(ResourceKey::location).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown registry element in " + this.key() + ":" + $$0))));
        return ExtraCodecs.overrideLifecycle($$02, $$0 -> this.lifecycle($$0.value()), $$0 -> this.lifecycle($$0.value()));
    }

    default public <U> Stream<U> keys(DynamicOps<U> $$0) {
        return this.keySet().stream().map($$1 -> $$0.createString($$1.toString()));
    }

    @Nullable
    public ResourceLocation getKey(T var1);

    public Optional<ResourceKey<T>> getResourceKey(T var1);

    @Override
    public int getId(@Nullable T var1);

    @Nullable
    public T get(@Nullable ResourceKey<T> var1);

    @Nullable
    public T get(@Nullable ResourceLocation var1);

    public Lifecycle lifecycle(T var1);

    public Lifecycle registryLifecycle();

    default public Optional<T> getOptional(@Nullable ResourceLocation $$0) {
        return Optional.ofNullable(this.get($$0));
    }

    default public Optional<T> getOptional(@Nullable ResourceKey<T> $$0) {
        return Optional.ofNullable(this.get($$0));
    }

    default public T getOrThrow(ResourceKey<T> $$0) {
        T $$1 = this.get($$0);
        if ($$1 == null) {
            throw new IllegalStateException("Missing key in " + this.key() + ": " + $$0);
        }
        return $$1;
    }

    public Set<ResourceLocation> keySet();

    public Set<Map.Entry<ResourceKey<T>, T>> entrySet();

    public Set<ResourceKey<T>> registryKeySet();

    public Optional<Holder.Reference<T>> getRandom(RandomSource var1);

    default public Stream<T> stream() {
        return StreamSupport.stream((Spliterator)this.spliterator(), (boolean)false);
    }

    public boolean containsKey(ResourceLocation var1);

    public boolean containsKey(ResourceKey<T> var1);

    public static <T> T register(Registry<? super T> $$0, String $$1, T $$2) {
        return Registry.register($$0, new ResourceLocation($$1), $$2);
    }

    public static <V, T extends V> T register(Registry<V> $$0, ResourceLocation $$1, T $$2) {
        return Registry.register($$0, ResourceKey.create($$0.key(), $$1), $$2);
    }

    public static <V, T extends V> T register(Registry<V> $$0, ResourceKey<V> $$1, T $$2) {
        ((WritableRegistry)$$0).register($$1, $$2, Lifecycle.stable());
        return $$2;
    }

    public static <T> Holder.Reference<T> registerForHolder(Registry<T> $$0, ResourceKey<T> $$1, T $$2) {
        return ((WritableRegistry)$$0).register($$1, $$2, Lifecycle.stable());
    }

    public static <T> Holder.Reference<T> registerForHolder(Registry<T> $$0, ResourceLocation $$1, T $$2) {
        return Registry.registerForHolder($$0, ResourceKey.create($$0.key(), $$1), $$2);
    }

    public static <V, T extends V> T registerMapping(Registry<V> $$0, int $$1, String $$2, T $$3) {
        ((WritableRegistry)$$0).registerMapping($$1, ResourceKey.create($$0.key(), new ResourceLocation($$2)), $$3, Lifecycle.stable());
        return $$3;
    }

    public Registry<T> freeze();

    public Holder.Reference<T> createIntrusiveHolder(T var1);

    public Optional<Holder.Reference<T>> getHolder(int var1);

    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> var1);

    public Holder<T> wrapAsHolder(T var1);

    default public Holder.Reference<T> getHolderOrThrow(ResourceKey<T> $$0) {
        return (Holder.Reference)this.getHolder($$0).orElseThrow(() -> new IllegalStateException("Missing key in " + this.key() + ": " + $$0));
    }

    public Stream<Holder.Reference<T>> holders();

    public Optional<HolderSet.Named<T>> getTag(TagKey<T> var1);

    default public Iterable<Holder<T>> getTagOrEmpty(TagKey<T> $$0) {
        return (Iterable)DataFixUtils.orElse(this.getTag($$0), (Object)List.of());
    }

    public HolderSet.Named<T> getOrCreateTag(TagKey<T> var1);

    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

    public Stream<TagKey<T>> getTagNames();

    public void resetTags();

    public void bindTags(Map<TagKey<T>, List<Holder<T>>> var1);

    default public IdMap<Holder<T>> asHolderIdMap() {
        return new IdMap<Holder<T>>(){

            @Override
            public int getId(Holder<T> $$0) {
                return Registry.this.getId($$0.value());
            }

            @Override
            @Nullable
            public Holder<T> byId(int $$0) {
                return (Holder)Registry.this.getHolder($$0).orElse(null);
            }

            @Override
            public int size() {
                return Registry.this.size();
            }

            public Iterator<Holder<T>> iterator() {
                return Registry.this.holders().map($$0 -> $$0).iterator();
            }
        };
    }

    public HolderOwner<T> holderOwner();

    public HolderLookup.RegistryLookup<T> asLookup();

    default public HolderLookup.RegistryLookup<T> asTagAddingLookup() {
        return new HolderLookup.RegistryLookup.Delegate<T>(){

            @Override
            protected HolderLookup.RegistryLookup<T> parent() {
                return Registry.this.asLookup();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
                return Optional.of(this.getOrThrow($$0));
            }

            @Override
            public HolderSet.Named<T> getOrThrow(TagKey<T> $$0) {
                return Registry.this.getOrCreateTag($$0);
            }
        };
    }
}