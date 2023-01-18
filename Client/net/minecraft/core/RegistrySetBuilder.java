/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Lifecycle
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class RegistrySetBuilder {
    private final List<RegistryStub<?>> entries = new ArrayList();

    static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> $$0) {
        return new EmptyTagLookup<T>($$0){

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> $$02) {
                return $$0.get($$02);
            }
        };
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> $$0, Lifecycle $$1, RegistryBootstrap<T> $$2) {
        this.entries.add(new RegistryStub<T>($$0, $$1, $$2));
        return this;
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> $$0, RegistryBootstrap<T> $$1) {
        return this.add($$0, Lifecycle.stable(), $$1);
    }

    private BuildState createState(RegistryAccess $$0) {
        BuildState $$12 = BuildState.create($$0, this.entries.stream().map(RegistryStub::key));
        this.entries.forEach($$1 -> $$1.apply($$12));
        return $$12;
    }

    public HolderLookup.Provider build(RegistryAccess $$02) {
        BuildState $$12 = this.createState($$02);
        Stream $$2 = $$02.registries().map($$0 -> $$0.value().asLookup());
        Stream $$3 = this.entries.stream().map($$1 -> $$1.collectChanges($$12).buildAsLookup());
        HolderLookup.Provider $$4 = HolderLookup.Provider.create(Stream.concat((Stream)$$2, (Stream)$$3.peek($$12::addOwner)));
        $$12.reportRemainingUnreferencedValues();
        $$12.throwOnError();
        return $$4;
    }

    public HolderLookup.Provider buildPatch(RegistryAccess $$02, HolderLookup.Provider $$12) {
        BuildState $$2 = this.createState($$02);
        Stream $$3 = $$02.registries().map($$0 -> $$0.value().asLookup());
        Stream $$4 = this.entries.stream().map($$1 -> $$1.collectChanges($$2).buildAsLookup());
        HolderLookup.Provider $$5 = HolderLookup.Provider.create(Stream.concat((Stream)$$3, (Stream)$$4.peek($$2::addOwner)));
        $$2.fillMissingHolders($$12);
        $$2.reportRemainingUnreferencedValues();
        $$2.throwOnError();
        return $$5;
    }

    record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBootstrap<T> bootstrap) {
        void apply(BuildState $$0) {
            this.bootstrap.run($$0.bootstapContext());
        }

        public RegistryContents<T> collectChanges(BuildState $$0) {
            HashMap $$1 = new HashMap();
            Iterator $$2 = $$0.registeredValues.entrySet().iterator();
            while ($$2.hasNext()) {
                Map.Entry $$3 = (Map.Entry)$$2.next();
                ResourceKey $$4 = (ResourceKey)$$3.getKey();
                if (!$$4.isFor(this.key)) continue;
                ResourceKey $$5 = $$4;
                RegisteredValue $$6 = (RegisteredValue)((Object)$$3.getValue());
                Holder.Reference $$7 = (Holder.Reference)$$0.lookup.holders.remove((Object)$$4);
                $$1.put((Object)$$5, new ValueAndHolder($$6, Optional.ofNullable((Object)$$7)));
                $$2.remove();
            }
            return new RegistryContents(this, $$1);
        }
    }

    @FunctionalInterface
    public static interface RegistryBootstrap<T> {
        public void run(BootstapContext<T> var1);
    }

    record BuildState(CompositeOwner owner, UniversalLookup lookup, Map<ResourceLocation, HolderGetter<?>> registries, Map<ResourceKey<?>, RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
        public static BuildState create(RegistryAccess $$0, Stream<ResourceKey<? extends Registry<?>>> $$12) {
            CompositeOwner $$22 = new CompositeOwner();
            ArrayList $$3 = new ArrayList();
            UniversalLookup $$4 = new UniversalLookup($$22);
            ImmutableMap.Builder $$5 = ImmutableMap.builder();
            $$0.registries().forEach($$1 -> $$5.put((Object)$$1.key().location(), RegistrySetBuilder.wrapContextLookup($$1.value().asLookup())));
            $$12.forEach($$2 -> $$5.put((Object)$$2.location(), (Object)$$4));
            return new BuildState($$22, $$4, (Map<ResourceLocation, HolderGetter<?>>)$$5.build(), (Map<ResourceKey<?>, RegisteredValue<?>>)new HashMap(), (List<RuntimeException>)$$3);
        }

        public <T> BootstapContext<T> bootstapContext() {
            return new BootstapContext<T>(){

                @Override
                public Holder.Reference<T> register(ResourceKey<T> $$0, T $$1, Lifecycle $$2) {
                    RegisteredValue $$3 = (RegisteredValue)((Object)registeredValues.put($$0, new RegisteredValue($$1, $$2)));
                    if ($$3 != null) {
                        errors.add((Object)new IllegalStateException("Duplicate registration for " + $$0 + ", new=" + $$1 + ", old=" + $$3.value));
                    }
                    return lookup.getOrCreate($$0);
                }

                @Override
                public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> $$0) {
                    return (HolderGetter)registries.getOrDefault((Object)$$0.location(), (Object)lookup);
                }
            };
        }

        public void reportRemainingUnreferencedValues() {
            for (ResourceKey $$02 : this.lookup.holders.keySet()) {
                this.errors.add((Object)new IllegalStateException("Unreferenced key: " + $$02));
            }
            this.registeredValues.forEach(($$0, $$1) -> this.errors.add((Object)new IllegalStateException("Orpaned value " + $$1.value + " for key " + $$0)));
        }

        public void throwOnError() {
            if (!this.errors.isEmpty()) {
                IllegalStateException $$0 = new IllegalStateException("Errors during registry creation");
                for (RuntimeException $$1 : this.errors) {
                    $$0.addSuppressed((Throwable)$$1);
                }
                throw $$0;
            }
        }

        public void addOwner(HolderOwner<?> $$0) {
            this.owner.add($$0);
        }

        public void fillMissingHolders(HolderLookup.Provider $$0) {
            HashMap $$12 = new HashMap();
            Iterator $$22 = this.lookup.holders.entrySet().iterator();
            while ($$22.hasNext()) {
                Map.Entry $$3 = (Map.Entry)$$22.next();
                ResourceKey $$4 = (ResourceKey)$$3.getKey();
                Holder.Reference $$5 = (Holder.Reference)$$3.getValue();
                ((Optional)$$12.computeIfAbsent((Object)$$4.registry(), $$1 -> $$0.lookup(ResourceKey.createRegistryKey($$1)))).flatMap($$1 -> $$1.get($$4)).ifPresent($$2 -> {
                    $$5.bindValue($$2.value());
                    $$22.remove();
                });
            }
        }
    }

    record RegistryContents<T>(RegistryStub<T> stub, Map<ResourceKey<T>, ValueAndHolder<T>> values) {
        public HolderLookup.RegistryLookup<T> buildAsLookup() {
            return new HolderLookup.RegistryLookup<T>(){
                private final Map<ResourceKey<T>, Holder.Reference<T>> entries;
                {
                    this.entries = (Map)values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, $$0 -> {
                        ValueAndHolder $$1 = (ValueAndHolder)((Object)((Object)$$0.getValue()));
                        Holder.Reference $$2 = (Holder.Reference)$$1.holder().orElseGet(() -> Holder.Reference.createStandAlone(this, (ResourceKey)$$0.getKey()));
                        $$2.bindValue($$1.value().value());
                        return $$2;
                    }));
                }

                @Override
                public ResourceKey<? extends Registry<? extends T>> key() {
                    return stub.key();
                }

                @Override
                public Lifecycle registryLifecycle() {
                    return stub.lifecycle();
                }

                @Override
                public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
                    return Optional.ofNullable((Object)((Holder.Reference)this.entries.get($$0)));
                }

                @Override
                public Stream<Holder.Reference<T>> listElements() {
                    return this.entries.values().stream();
                }

                @Override
                public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
                    return Optional.empty();
                }

                @Override
                public Stream<HolderSet.Named<T>> listTags() {
                    return Stream.empty();
                }
            };
        }
    }

    record ValueAndHolder<T>(RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
    }

    record RegisteredValue<T>(T value, Lifecycle lifecycle) {
    }

    static class UniversalLookup
    extends EmptyTagLookup<Object> {
        final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();

        public UniversalLookup(HolderOwner<Object> $$0) {
            super($$0);
        }

        @Override
        public Optional<Holder.Reference<Object>> get(ResourceKey<Object> $$0) {
            return Optional.of(this.getOrCreate($$0));
        }

        <T> Holder.Reference<T> getOrCreate(ResourceKey<T> $$02) {
            return (Holder.Reference)this.holders.computeIfAbsent($$02, $$0 -> Holder.Reference.createStandAlone(this.owner, $$0));
        }
    }

    static class CompositeOwner
    implements HolderOwner<Object> {
        private final Set<HolderOwner<?>> owners = Sets.newIdentityHashSet();

        CompositeOwner() {
        }

        @Override
        public boolean canSerializeIn(HolderOwner<Object> $$0) {
            return this.owners.contains($$0);
        }

        public void add(HolderOwner<?> $$0) {
            this.owners.add($$0);
        }
    }

    static abstract class EmptyTagLookup<T>
    implements HolderGetter<T> {
        protected final HolderOwner<T> owner;

        protected EmptyTagLookup(HolderOwner<T> $$0) {
            this.owner = $$0;
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
            return Optional.of(HolderSet.emptyNamed(this.owner, $$0));
        }
    }
}