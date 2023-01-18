/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T>
extends DelegatingOps<T> {
    private final RegistryInfoLookup lookupProvider;

    private static RegistryInfoLookup memoizeLookup(final RegistryInfoLookup $$0) {
        return new RegistryInfoLookup(){
            private final Map<ResourceKey<? extends Registry<?>>, Optional<? extends RegistryInfo<?>>> lookups = new HashMap();

            @Override
            public <T> Optional<RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$02) {
                return (Optional)this.lookups.computeIfAbsent($$02, $$0::lookup);
            }
        };
    }

    public static <T> RegistryOps<T> create(DynamicOps<T> $$0, final HolderLookup.Provider $$1) {
        return RegistryOps.create($$0, RegistryOps.memoizeLookup(new RegistryInfoLookup(){

            public <E> Optional<RegistryInfo<E>> lookup(ResourceKey<? extends Registry<? extends E>> $$02) {
                return $$1.lookup($$02).map($$0 -> new RegistryInfo($$0, $$0, $$0.registryLifecycle()));
            }
        }));
    }

    public static <T> RegistryOps<T> create(DynamicOps<T> $$0, RegistryInfoLookup $$1) {
        return new RegistryOps<T>($$0, $$1);
    }

    private RegistryOps(DynamicOps<T> $$0, RegistryInfoLookup $$1) {
        super($$0);
        this.lookupProvider = $$1;
    }

    public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends Registry<? extends E>> $$0) {
        return this.lookupProvider.lookup($$0).map(RegistryInfo::owner);
    }

    public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends Registry<? extends E>> $$0) {
        return this.lookupProvider.lookup($$0).map(RegistryInfo::getter);
    }

    public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends Registry<? extends E>> $$02) {
        return ExtraCodecs.retrieveContext($$1 -> {
            if ($$1 instanceof RegistryOps) {
                RegistryOps $$2 = (RegistryOps)$$1;
                return (DataResult)$$2.lookupProvider.lookup($$02).map($$0 -> DataResult.success($$0.getter(), (Lifecycle)$$0.elementsLifecycle())).orElseGet(() -> DataResult.error((String)("Unknown registry: " + $$02)));
            }
            return DataResult.error((String)"Not a registry ops");
        }).forGetter($$0 -> null);
    }

    public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(ResourceKey<E> $$02) {
        ResourceKey $$1 = ResourceKey.createRegistryKey($$02.registry());
        return ExtraCodecs.retrieveContext($$2 -> {
            if ($$2 instanceof RegistryOps) {
                RegistryOps $$3 = (RegistryOps)$$2;
                return (DataResult)$$3.lookupProvider.lookup($$1).flatMap($$1 -> $$1.getter().get($$02)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Can't find value: " + $$02)));
            }
            return DataResult.error((String)"Not a registry ops");
        }).forGetter($$0 -> null);
    }

    public static interface RegistryInfoLookup {
        public <T> Optional<RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);
    }

    public record RegistryInfo<T>(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
    }
}