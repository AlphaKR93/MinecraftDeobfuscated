/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface DefaultedRegistry<T>
extends Registry<T> {
    @Override
    @Nonnull
    public ResourceLocation getKey(T var1);

    @Override
    @Nonnull
    public T get(@Nullable ResourceLocation var1);

    @Override
    @Nonnull
    public T byId(int var1);

    public ResourceLocation getDefaultKey();
}