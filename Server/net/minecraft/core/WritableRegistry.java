/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Object
 */
package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface WritableRegistry<T>
extends Registry<T> {
    public Holder<T> registerMapping(int var1, ResourceKey<T> var2, T var3, Lifecycle var4);

    public Holder.Reference<T> register(ResourceKey<T> var1, T var2, Lifecycle var3);

    public boolean isEmpty();

    public HolderGetter<T> createRegistrationLookup();
}