/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class RegistryFileCodec<E>
implements Codec<Holder<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final Codec<E> elementCodec;
    private final boolean allowInline;

    public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> $$0, Codec<E> $$1) {
        return RegistryFileCodec.create($$0, $$1, true);
    }

    public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> $$0, Codec<E> $$1, boolean $$2) {
        return new RegistryFileCodec<E>($$0, $$1, $$2);
    }

    private RegistryFileCodec(ResourceKey<? extends Registry<E>> $$0, Codec<E> $$1, boolean $$2) {
        this.registryKey = $$0;
        this.elementCodec = $$1;
        this.allowInline = $$2;
    }

    public <T> DataResult<T> encode(Holder<E> $$0, DynamicOps<T> $$1, T $$22) {
        RegistryOps $$3;
        Optional $$4;
        if ($$1 instanceof RegistryOps && ($$4 = ($$3 = (RegistryOps)$$1).owner(this.registryKey)).isPresent()) {
            if (!$$0.canSerializeIn((HolderOwner)$$4.get())) {
                return DataResult.error((String)("Element " + $$0 + " is not valid in current registry set"));
            }
            return (DataResult)$$0.unwrap().map($$2 -> ResourceLocation.CODEC.encode((Object)$$2.location(), $$1, $$22), $$2 -> this.elementCodec.encode($$2, $$1, $$22));
        }
        return this.elementCodec.encode($$0.value(), $$1, $$22);
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> $$02, T $$12) {
        if ($$02 instanceof RegistryOps) {
            RegistryOps $$2 = (RegistryOps)$$02;
            Optional $$3 = $$2.getter(this.registryKey);
            if ($$3.isEmpty()) {
                return DataResult.error((String)("Registry does not exist: " + this.registryKey));
            }
            HolderGetter $$4 = (HolderGetter)$$3.get();
            DataResult $$5 = ResourceLocation.CODEC.decode($$02, $$12);
            if ($$5.result().isEmpty()) {
                if (!this.allowInline) {
                    return DataResult.error((String)"Inline definitions not allowed here");
                }
                return this.elementCodec.decode($$02, $$12).map($$0 -> $$0.mapFirst(Holder::direct));
            }
            Pair $$6 = (Pair)$$5.result().get();
            ResourceKey $$7 = ResourceKey.create(this.registryKey, (ResourceLocation)$$6.getFirst());
            return ((DataResult)$$4.get($$7).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Failed to get element " + $$7)))).map($$1 -> Pair.of((Object)$$1, (Object)$$6.getSecond())).setLifecycle(Lifecycle.stable());
        }
        return this.elementCodec.decode($$02, $$12).map($$0 -> $$0.mapFirst(Holder::direct));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryKey + " " + this.elementCodec + "]";
    }
}