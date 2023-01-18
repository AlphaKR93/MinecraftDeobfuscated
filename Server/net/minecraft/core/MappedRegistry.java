/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  java.lang.AssertionError
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.IdentityHashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class MappedRegistry<T>
implements WritableRegistry<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final ResourceKey<? extends Registry<T>> key;
    private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList(256);
    private final Object2IntMap<T> toId = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityStrategy()), $$0 -> $$0.defaultReturnValue(-1));
    private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap();
    private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap();
    private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap();
    private final Map<T, Lifecycle> lifecycles = new IdentityHashMap();
    private Lifecycle registryLifecycle;
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap();
    private boolean frozen;
    @Nullable
    private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;
    @Nullable
    private List<Holder.Reference<T>> holdersInOrder;
    private int nextId;
    private final HolderLookup.RegistryLookup<T> lookup = new HolderLookup.RegistryLookup<T>(){

        @Override
        public ResourceKey<? extends Registry<? extends T>> key() {
            return MappedRegistry.this.key;
        }

        @Override
        public Lifecycle registryLifecycle() {
            return MappedRegistry.this.registryLifecycle();
        }

        @Override
        public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
            return MappedRegistry.this.getHolder($$0);
        }

        @Override
        public Stream<Holder.Reference<T>> listElements() {
            return MappedRegistry.this.holders();
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
            return MappedRegistry.this.getTag($$0);
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            return MappedRegistry.this.getTags().map(Pair::getSecond);
        }
    };

    public MappedRegistry(ResourceKey<? extends Registry<T>> $$0, Lifecycle $$1) {
        this($$0, $$1, false);
    }

    public MappedRegistry(ResourceKey<? extends Registry<T>> $$02, Lifecycle $$1, boolean $$2) {
        Bootstrap.checkBootstrapCalled((Supplier<String>)((Supplier)() -> "registry " + $$02));
        this.key = $$02;
        this.registryLifecycle = $$1;
        if ($$2) {
            this.unregisteredIntrusiveHolders = new IdentityHashMap();
        }
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.key;
    }

    public String toString() {
        return "Registry[" + this.key + " (" + this.registryLifecycle + ")]";
    }

    private List<Holder.Reference<T>> holdersInOrder() {
        if (this.holdersInOrder == null) {
            this.holdersInOrder = this.byId.stream().filter(Objects::nonNull).toList();
        }
        return this.holdersInOrder;
    }

    private void validateWrite() {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen");
        }
    }

    private void validateWrite(ResourceKey<T> $$0) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + $$0 + ")");
        }
    }

    @Override
    public Holder.Reference<T> registerMapping(int $$02, ResourceKey<T> $$1, T $$2, Lifecycle $$3) {
        Holder.Reference $$5;
        this.validateWrite($$1);
        Validate.notNull($$1);
        Validate.notNull($$2);
        if (this.byLocation.containsKey((Object)$$1.location())) {
            Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + $$1 + "' to registry"));
        }
        if (this.byValue.containsKey($$2)) {
            Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + $$2 + "' to registry"));
        }
        if (this.unregisteredIntrusiveHolders != null) {
            Holder.Reference $$4 = (Holder.Reference)this.unregisteredIntrusiveHolders.remove($$2);
            if ($$4 == null) {
                throw new AssertionError((Object)("Missing intrusive holder for " + $$1 + ":" + $$2));
            }
            $$4.bindKey($$1);
        } else {
            $$5 = (Holder.Reference)this.byKey.computeIfAbsent($$1, $$0 -> Holder.Reference.createStandAlone(this.holderOwner(), $$0));
        }
        this.byKey.put($$1, (Object)$$5);
        this.byLocation.put((Object)$$1.location(), (Object)$$5);
        this.byValue.put($$2, (Object)$$5);
        this.byId.size(Math.max((int)this.byId.size(), (int)($$02 + 1)));
        this.byId.set($$02, (Object)$$5);
        this.toId.put($$2, $$02);
        if (this.nextId <= $$02) {
            this.nextId = $$02 + 1;
        }
        this.lifecycles.put($$2, (Object)$$3);
        this.registryLifecycle = this.registryLifecycle.add($$3);
        this.holdersInOrder = null;
        return $$5;
    }

    @Override
    public Holder.Reference<T> register(ResourceKey<T> $$0, T $$1, Lifecycle $$2) {
        return this.registerMapping(this.nextId, (ResourceKey)$$0, (Object)$$1, $$2);
    }

    @Override
    @Nullable
    public ResourceLocation getKey(T $$0) {
        Holder.Reference $$1 = (Holder.Reference)this.byValue.get($$0);
        return $$1 != null ? $$1.key().location() : null;
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T $$0) {
        return Optional.ofNullable((Object)((Holder.Reference)this.byValue.get($$0))).map(Holder.Reference::key);
    }

    @Override
    public int getId(@Nullable T $$0) {
        return this.toId.getInt($$0);
    }

    @Override
    @Nullable
    public T get(@Nullable ResourceKey<T> $$0) {
        return MappedRegistry.getValueFromNullable((Holder.Reference)this.byKey.get($$0));
    }

    @Override
    @Nullable
    public T byId(int $$0) {
        if ($$0 < 0 || $$0 >= this.byId.size()) {
            return null;
        }
        return MappedRegistry.getValueFromNullable((Holder.Reference)this.byId.get($$0));
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(int $$0) {
        if ($$0 < 0 || $$0 >= this.byId.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable((Object)((Holder.Reference)this.byId.get($$0)));
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> $$0) {
        return Optional.ofNullable((Object)((Holder.Reference)this.byKey.get($$0)));
    }

    @Override
    public Holder<T> wrapAsHolder(T $$0) {
        Holder.Reference $$1 = (Holder.Reference)this.byValue.get($$0);
        return $$1 != null ? $$1 : Holder.direct($$0);
    }

    Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> $$02) {
        return (Holder.Reference)this.byKey.computeIfAbsent($$02, $$0 -> {
            if (this.unregisteredIntrusiveHolders != null) {
                throw new IllegalStateException("This registry can't create new holders without value");
            }
            this.validateWrite((ResourceKey<T>)$$0);
            return Holder.Reference.createStandAlone(this.holderOwner(), $$0);
        });
    }

    @Override
    public int size() {
        return this.byKey.size();
    }

    @Override
    public Lifecycle lifecycle(T $$0) {
        return (Lifecycle)this.lifecycles.get($$0);
    }

    @Override
    public Lifecycle registryLifecycle() {
        return this.registryLifecycle;
    }

    public Iterator<T> iterator() {
        return Iterators.transform((Iterator)this.holdersInOrder().iterator(), Holder::value);
    }

    @Override
    @Nullable
    public T get(@Nullable ResourceLocation $$0) {
        Holder.Reference $$1 = (Holder.Reference)this.byLocation.get((Object)$$0);
        return MappedRegistry.getValueFromNullable($$1);
    }

    @Nullable
    private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> $$0) {
        return $$0 != null ? (T)$$0.value() : null;
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return Collections.unmodifiableSet((Set)this.byLocation.keySet());
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return Collections.unmodifiableSet((Set)this.byKey.keySet());
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return Collections.unmodifiableSet((Set)Maps.transformValues(this.byKey, Holder::value).entrySet());
    }

    @Override
    public Stream<Holder.Reference<T>> holders() {
        return this.holdersInOrder().stream();
    }

    @Override
    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return this.tags.entrySet().stream().map($$0 -> Pair.of((Object)((Object)((TagKey)((Object)((Object)$$0.getKey())))), (Object)((HolderSet.Named)$$0.getValue())));
    }

    @Override
    public HolderSet.Named<T> getOrCreateTag(TagKey<T> $$0) {
        HolderSet.Named<T> $$1 = (HolderSet.Named<T>)this.tags.get($$0);
        if ($$1 == null) {
            $$1 = this.createTag($$0);
            IdentityHashMap $$2 = new IdentityHashMap(this.tags);
            $$2.put($$0, $$1);
            this.tags = $$2;
        }
        return $$1;
    }

    private HolderSet.Named<T> createTag(TagKey<T> $$0) {
        return new HolderSet.Named<T>(this.holderOwner(), $$0);
    }

    @Override
    public Stream<TagKey<T>> getTagNames() {
        return this.tags.keySet().stream();
    }

    @Override
    public boolean isEmpty() {
        return this.byKey.isEmpty();
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource $$0) {
        return Util.getRandomSafe(this.holdersInOrder(), $$0);
    }

    @Override
    public boolean containsKey(ResourceLocation $$0) {
        return this.byLocation.containsKey((Object)$$0);
    }

    @Override
    public boolean containsKey(ResourceKey<T> $$0) {
        return this.byKey.containsKey($$0);
    }

    @Override
    public Registry<T> freeze() {
        if (this.frozen) {
            return this;
        }
        this.frozen = true;
        this.byValue.forEach(($$0, $$1) -> $$1.bindValue($$0));
        List $$02 = this.byKey.entrySet().stream().filter($$0 -> !((Holder.Reference)$$0.getValue()).isBound()).map($$0 -> ((ResourceKey)$$0.getKey()).location()).sorted().toList();
        if (!$$02.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + $$02);
        }
        if (this.unregisteredIntrusiveHolders != null) {
            if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values());
            }
            this.unregisteredIntrusiveHolders = null;
        }
        return this;
    }

    @Override
    public Holder.Reference<T> createIntrusiveHolder(T $$02) {
        if (this.unregisteredIntrusiveHolders == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        }
        this.validateWrite();
        return (Holder.Reference)this.unregisteredIntrusiveHolders.computeIfAbsent($$02, $$0 -> Holder.Reference.createIntrusive(this.asLookup(), $$0));
    }

    @Override
    public Optional<HolderSet.Named<T>> getTag(TagKey<T> $$0) {
        return Optional.ofNullable((Object)((HolderSet.Named)this.tags.get($$0)));
    }

    @Override
    public void bindTags(Map<TagKey<T>, List<Holder<T>>> $$02) {
        IdentityHashMap $$1 = new IdentityHashMap();
        this.byKey.values().forEach(arg_0 -> MappedRegistry.lambda$bindTags$9((Map)$$1, arg_0));
        $$02.forEach((arg_0, arg_1) -> this.lambda$bindTags$10((Map)$$1, arg_0, arg_1));
        Sets.SetView $$2 = Sets.difference((Set)this.tags.keySet(), (Set)$$02.keySet());
        if (!$$2.isEmpty()) {
            LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.key(), $$2.stream().map($$0 -> $$0.location().toString()).sorted().collect(Collectors.joining((CharSequence)", ")));
        }
        IdentityHashMap $$3 = new IdentityHashMap(this.tags);
        $$02.forEach((arg_0, arg_1) -> this.lambda$bindTags$12((Map)$$3, arg_0, arg_1));
        $$1.forEach(Holder.Reference::bindTags);
        this.tags = $$3;
    }

    @Override
    public void resetTags() {
        this.tags.values().forEach($$0 -> $$0.bind(List.of()));
        this.byKey.values().forEach($$0 -> $$0.bindTags(Set.of()));
    }

    @Override
    public HolderGetter<T> createRegistrationLookup() {
        this.validateWrite();
        return new HolderGetter<T>(){

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
                return Optional.of(this.getOrThrow($$0));
            }

            @Override
            public Holder.Reference<T> getOrThrow(ResourceKey<T> $$0) {
                return MappedRegistry.this.getOrCreateHolderOrThrow($$0);
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
                return Optional.of(this.getOrThrow($$0));
            }

            @Override
            public HolderSet.Named<T> getOrThrow(TagKey<T> $$0) {
                return MappedRegistry.this.getOrCreateTag($$0);
            }
        };
    }

    @Override
    public HolderOwner<T> holderOwner() {
        return this.lookup;
    }

    @Override
    public HolderLookup.RegistryLookup<T> asLookup() {
        return this.lookup;
    }

    private /* synthetic */ void lambda$bindTags$12(Map $$0, TagKey $$1, List $$2) {
        ((HolderSet.Named)$$0.computeIfAbsent((Object)$$1, this::createTag)).bind($$2);
    }

    private /* synthetic */ void lambda$bindTags$10(Map $$0, TagKey $$1, List $$2) {
        for (Holder $$3 : $$2) {
            if (!$$3.canSerializeIn(this.asLookup())) {
                throw new IllegalStateException("Can't create named set " + $$1 + " containing value " + $$3 + " from outside registry " + this);
            }
            if ($$3 instanceof Holder.Reference) {
                Holder.Reference $$4 = (Holder.Reference)$$3;
                ((List)$$0.get((Object)$$4)).add((Object)$$1);
                continue;
            }
            throw new IllegalStateException("Found direct holder " + $$3 + " value in tag " + $$1);
        }
    }

    private static /* synthetic */ void lambda$bindTags$9(Map $$0, Holder.Reference $$1) {
        $$0.put((Object)$$1, (Object)new ArrayList());
    }
}