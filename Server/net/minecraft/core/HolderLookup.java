/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T>
extends HolderGetter<T> {
    public Stream<Holder.Reference<T>> listElements();

    default public Stream<ResourceKey<T>> listElementIds() {
        return this.listElements().map(Holder.Reference::key);
    }

    public Stream<HolderSet.Named<T>> listTags();

    default public Stream<TagKey<T>> listTagIds() {
        return this.listTags().map(HolderSet.Named::key);
    }

    default public HolderLookup<T> filterElements(final Predicate<T> $$0) {
        return new Delegate<T>(this){

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> $$02) {
                return this.parent.get($$02).filter($$1 -> $$0.test($$1.value()));
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return this.parent.listElements().filter($$1 -> $$0.test($$1.value()));
            }
        };
    }

    public static interface Provider {
        public <T> Optional<RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

        default public <T> RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> $$0) {
            return (RegistryLookup)this.lookup($$0).orElseThrow(() -> new IllegalStateException("Registry " + $$0.location() + " not found"));
        }

        default public HolderGetter.Provider asGetterLookup() {
            return new HolderGetter.Provider(){

                @Override
                public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$02) {
                    return this.lookup($$02).map($$0 -> $$0);
                }
            };
        }

        public static Provider create(Stream<RegistryLookup<?>> $$02) {
            final Map $$1 = (Map)$$02.collect(Collectors.toUnmodifiableMap(RegistryLookup::key, $$0 -> $$0));
            return new Provider(){

                @Override
                public <T> Optional<RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
                    return Optional.ofNullable((Object)((RegistryLookup)$$1.get($$0)));
                }
            };
        }
    }

    public static class Delegate<T>
    implements HolderLookup<T> {
        protected final HolderLookup<T> parent;

        public Delegate(HolderLookup<T> $$0) {
            this.parent = $$0;
        }

        @Override
        public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
            return this.parent.get($$0);
        }

        @Override
        public Stream<Holder.Reference<T>> listElements() {
            return this.parent.listElements();
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
            return this.parent.get($$0);
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            return this.parent.listTags();
        }
    }

    public static interface RegistryLookup<T>
    extends HolderLookup<T>,
    HolderOwner<T> {
        public ResourceKey<? extends Registry<? extends T>> key();

        public Lifecycle registryLifecycle();

        default public HolderLookup<T> filterFeatures(FeatureFlagSet $$0) {
            if (FeatureElement.FILTERED_REGISTRIES.contains(this.key())) {
                return this.filterElements($$1 -> ((FeatureElement)$$1).isEnabled($$0));
            }
            return this;
        }

        public static abstract class Delegate<T>
        implements RegistryLookup<T> {
            protected abstract RegistryLookup<T> parent();

            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return this.parent().key();
            }

            @Override
            public Lifecycle registryLifecycle() {
                return this.parent().registryLifecycle();
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
                return this.parent().get($$0);
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return this.parent().listElements();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
                return this.parent().get($$0);
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return this.parent().listTags();
            }
        }
    }
}