/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Object
 */
package net.minecraft.data.worldgen;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface BootstapContext<T> {
    public Holder.Reference<T> register(ResourceKey<T> var1, T var2, Lifecycle var3);

    default public Holder.Reference<T> register(ResourceKey<T> $$0, T $$1) {
        return this.register($$0, $$1, Lifecycle.stable());
    }

    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> var1);
}