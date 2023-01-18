/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.core;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface HolderGetter<T> {
    public Optional<Holder.Reference<T>> get(ResourceKey<T> var1);

    default public Holder.Reference<T> getOrThrow(ResourceKey<T> $$0) {
        return (Holder.Reference)this.get($$0).orElseThrow(() -> new IllegalStateException("Missing element " + $$0));
    }

    public Optional<HolderSet.Named<T>> get(TagKey<T> var1);

    default public HolderSet.Named<T> getOrThrow(TagKey<T> $$0) {
        return (HolderSet.Named)this.get($$0).orElseThrow(() -> new IllegalStateException("Missing tag " + $$0));
    }

    public static interface Provider {
        public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

        default public <T> HolderGetter<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> $$0) {
            return (HolderGetter)this.lookup($$0).orElseThrow(() -> new IllegalStateException("Registry " + $$0.location() + " not found"));
        }
    }
}