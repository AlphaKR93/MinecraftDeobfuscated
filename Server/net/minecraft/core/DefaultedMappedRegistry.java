/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DefaultedMappedRegistry<T>
extends MappedRegistry<T>
implements DefaultedRegistry<T> {
    private final ResourceLocation defaultKey;
    private Holder.Reference<T> defaultValue;

    public DefaultedMappedRegistry(String $$0, ResourceKey<? extends Registry<T>> $$1, Lifecycle $$2, boolean $$3) {
        super($$1, $$2, $$3);
        this.defaultKey = new ResourceLocation($$0);
    }

    @Override
    public Holder.Reference<T> registerMapping(int $$0, ResourceKey<T> $$1, T $$2, Lifecycle $$3) {
        Holder $$4 = super.registerMapping($$0, (ResourceKey)$$1, (Object)$$2, $$3);
        if (this.defaultKey.equals($$1.location())) {
            this.defaultValue = $$4;
        }
        return $$4;
    }

    @Override
    public int getId(@Nullable T $$0) {
        int $$1 = super.getId($$0);
        return $$1 == -1 ? super.getId(this.defaultValue.value()) : $$1;
    }

    @Override
    @Nonnull
    public ResourceLocation getKey(T $$0) {
        ResourceLocation $$1 = super.getKey($$0);
        return $$1 == null ? this.defaultKey : $$1;
    }

    @Override
    @Nonnull
    public T get(@Nullable ResourceLocation $$0) {
        Object $$1 = super.get($$0);
        return $$1 == null ? this.defaultValue.value() : $$1;
    }

    @Override
    public Optional<T> getOptional(@Nullable ResourceLocation $$0) {
        return Optional.ofNullable(super.get($$0));
    }

    @Override
    @Nonnull
    public T byId(int $$0) {
        Object $$1 = super.byId($$0);
        return $$1 == null ? this.defaultValue.value() : $$1;
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource $$0) {
        return super.getRandom($$0).or(() -> Optional.of(this.defaultValue));
    }

    @Override
    public ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }
}