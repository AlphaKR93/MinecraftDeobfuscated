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

public final class RegistryFixedCodec<E>
implements Codec<Holder<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;

    public static <E> RegistryFixedCodec<E> create(ResourceKey<? extends Registry<E>> $$0) {
        return new RegistryFixedCodec<E>($$0);
    }

    private RegistryFixedCodec(ResourceKey<? extends Registry<E>> $$0) {
        this.registryKey = $$0;
    }

    public <T> DataResult<T> encode(Holder<E> $$02, DynamicOps<T> $$1, T $$22) {
        RegistryOps $$3;
        Optional $$4;
        if ($$1 instanceof RegistryOps && ($$4 = ($$3 = (RegistryOps)$$1).owner(this.registryKey)).isPresent()) {
            if (!$$02.canSerializeIn((HolderOwner)$$4.get())) {
                return DataResult.error((String)("Element " + $$02 + " is not valid in current registry set"));
            }
            return (DataResult)$$02.unwrap().map($$2 -> ResourceLocation.CODEC.encode((Object)$$2.location(), $$1, $$22), $$0 -> DataResult.error((String)("Elements from registry " + this.registryKey + " can't be serialized to a value")));
        }
        return DataResult.error((String)("Can't access registry " + this.registryKey));
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> $$0, T $$1) {
        RegistryOps $$2;
        Optional $$3;
        if ($$0 instanceof RegistryOps && ($$3 = ($$2 = (RegistryOps)$$0).getter(this.registryKey)).isPresent()) {
            return ResourceLocation.CODEC.decode($$0, $$1).flatMap($$12 -> {
                ResourceLocation $$2 = (ResourceLocation)$$12.getFirst();
                return ((DataResult)((HolderGetter)$$3.get()).get(ResourceKey.create(this.registryKey, $$2)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Failed to get element " + $$2)))).map($$1 -> Pair.of((Object)$$1, (Object)$$12.getSecond())).setLifecycle(Lifecycle.stable());
            });
        }
        return DataResult.error((String)("Can't access registry " + this.registryKey));
    }

    public String toString() {
        return "RegistryFixedCodec[" + this.registryKey + "]";
    }
}