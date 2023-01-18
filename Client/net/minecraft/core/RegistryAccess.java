/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess
extends HolderLookup.Provider {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Frozen EMPTY = new ImmutableRegistryAccess(Map.of()).freeze();

    public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1);

    @Override
    default public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
        return this.registry($$0).map(Registry::asLookup);
    }

    default public <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> $$0) {
        return (Registry)this.registry($$0).orElseThrow(() -> new IllegalStateException("Missing registry: " + $$0));
    }

    public Stream<RegistryEntry<?>> registries();

    public static Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> $$0) {
        return new Frozen(){

            public <T> Optional<Registry<T>> registry(ResourceKey<? extends Registry<? extends T>> $$02) {
                Registry $$1 = $$0;
                return $$1.getOptional($$02);
            }

            @Override
            public Stream<RegistryEntry<?>> registries() {
                return $$0.entrySet().stream().map(RegistryEntry::fromMapEntry);
            }

            @Override
            public Frozen freeze() {
                return this;
            }
        };
    }

    default public Frozen freeze() {
        class FrozenAccess
        extends ImmutableRegistryAccess
        implements Frozen {
            protected FrozenAccess(Stream<RegistryEntry<?>> $$1) {
                super($$1);
            }
        }
        return new FrozenAccess(this.registries().map(RegistryEntry::freeze));
    }

    default public Lifecycle allRegistriesLifecycle() {
        return (Lifecycle)this.registries().map($$0 -> $$0.value.registryLifecycle()).reduce((Object)Lifecycle.stable(), Lifecycle::add);
    }

    public record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
        private static <T, R extends Registry<? extends T>> RegistryEntry<T> fromMapEntry(Map.Entry<? extends ResourceKey<? extends Registry<?>>, R> $$0) {
            return RegistryEntry.fromUntyped((ResourceKey)$$0.getKey(), (Registry)$$0.getValue());
        }

        private static <T> RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> $$0, Registry<?> $$1) {
            return new RegistryEntry($$0, $$1);
        }

        private RegistryEntry<T> freeze() {
            return new RegistryEntry<T>(this.key, this.value.freeze());
        }
    }

    public static class ImmutableRegistryAccess
    implements RegistryAccess {
        private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

        public ImmutableRegistryAccess(List<? extends Registry<?>> $$02) {
            this.registries = (Map)$$02.stream().collect(Collectors.toUnmodifiableMap(Registry::key, $$0 -> $$0));
        }

        public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> $$0) {
            this.registries = Map.copyOf($$0);
        }

        public ImmutableRegistryAccess(Stream<RegistryEntry<?>> $$0) {
            this.registries = (Map)$$0.collect(ImmutableMap.toImmutableMap(RegistryEntry::key, RegistryEntry::value));
        }

        @Override
        public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> $$02) {
            return Optional.ofNullable((Object)((Registry)this.registries.get($$02))).map($$0 -> $$0);
        }

        @Override
        public Stream<RegistryEntry<?>> registries() {
            return this.registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
        }
    }

    public static interface Frozen
    extends RegistryAccess {
    }
}