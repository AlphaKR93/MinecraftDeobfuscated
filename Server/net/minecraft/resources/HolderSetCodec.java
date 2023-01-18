/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.resources;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

public class HolderSetCodec<E>
implements Codec<HolderSet<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final Codec<Holder<E>> elementCodec;
    private final Codec<List<Holder<E>>> homogenousListCodec;
    private final Codec<Either<TagKey<E>, List<Holder<E>>>> registryAwareCodec;

    private static <E> Codec<List<Holder<E>>> homogenousList(Codec<Holder<E>> $$03, boolean $$1) {
        Codec $$2 = ExtraCodecs.validate($$03.listOf(), ExtraCodecs.ensureHomogenous(Holder::kind));
        if ($$1) {
            return $$2;
        }
        return Codec.either($$2, $$03).xmap($$02 -> (List)$$02.map($$0 -> $$0, List::of), $$0 -> $$0.size() == 1 ? Either.right((Object)((Holder)$$0.get(0))) : Either.left((Object)$$0));
    }

    public static <E> Codec<HolderSet<E>> create(ResourceKey<? extends Registry<E>> $$0, Codec<Holder<E>> $$1, boolean $$2) {
        return new HolderSetCodec<E>($$0, $$1, $$2);
    }

    private HolderSetCodec(ResourceKey<? extends Registry<E>> $$0, Codec<Holder<E>> $$1, boolean $$2) {
        this.registryKey = $$0;
        this.elementCodec = $$1;
        this.homogenousListCodec = HolderSetCodec.homogenousList($$1, $$2);
        this.registryAwareCodec = Codec.either(TagKey.hashedCodec($$0), this.homogenousListCodec);
    }

    public <T> DataResult<Pair<HolderSet<E>, T>> decode(DynamicOps<T> $$0, T $$1) {
        RegistryOps $$2;
        Optional $$3;
        if ($$0 instanceof RegistryOps && ($$3 = ($$2 = (RegistryOps)$$0).getter(this.registryKey)).isPresent()) {
            HolderGetter $$4 = (HolderGetter)$$3.get();
            return this.registryAwareCodec.decode($$0, $$1).map($$12 -> $$12.mapFirst($$1 -> (HolderSet)$$1.map($$4::getOrThrow, HolderSet::direct)));
        }
        return this.decodeWithoutRegistry($$0, $$1);
    }

    public <T> DataResult<T> encode(HolderSet<E> $$0, DynamicOps<T> $$1, T $$2) {
        RegistryOps $$3;
        Optional $$4;
        if ($$1 instanceof RegistryOps && ($$4 = ($$3 = (RegistryOps)$$1).owner(this.registryKey)).isPresent()) {
            if (!$$0.canSerializeIn((HolderOwner)$$4.get())) {
                return DataResult.error((String)("HolderSet " + $$0 + " is not valid in current registry set"));
            }
            return this.registryAwareCodec.encode((Object)$$0.unwrap().mapRight(List::copyOf), $$1, $$2);
        }
        return this.encodeWithoutRegistry($$0, $$1, $$2);
    }

    private <T> DataResult<Pair<HolderSet<E>, T>> decodeWithoutRegistry(DynamicOps<T> $$02, T $$1) {
        return this.elementCodec.listOf().decode($$02, $$1).flatMap($$0 -> {
            ArrayList $$1 = new ArrayList();
            for (Holder $$2 : (List)$$0.getFirst()) {
                if ($$2 instanceof Holder.Direct) {
                    Holder.Direct $$3 = (Holder.Direct)$$2;
                    $$1.add((Object)$$3);
                    continue;
                }
                return DataResult.error((String)("Can't decode element " + $$2 + " without registry"));
            }
            return DataResult.success((Object)new Pair(HolderSet.direct($$1), $$0.getSecond()));
        });
    }

    private <T> DataResult<T> encodeWithoutRegistry(HolderSet<E> $$0, DynamicOps<T> $$1, T $$2) {
        return this.homogenousListCodec.encode((Object)$$0.stream().toList(), $$1, $$2);
    }
}