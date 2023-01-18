/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  java.lang.Object
 */
package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
    private static <T> MapCodec<RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> $$0, MapCodec<T> $$1) {
        return RecordCodecBuilder.mapCodec($$2 -> $$2.group((App)ResourceKey.codec($$0).fieldOf("name").forGetter(RegistryEntry::key), (App)Codec.INT.fieldOf("id").forGetter(RegistryEntry::id), (App)$$1.forGetter(RegistryEntry::value)).apply((Applicative)$$2, RegistryEntry::new));
    }

    public static <T> Codec<Registry<T>> networkCodec(ResourceKey<? extends Registry<T>> $$02, Lifecycle $$1, Codec<T> $$22) {
        return RegistryCodecs.withNameAndId($$02, $$22.fieldOf("element")).codec().listOf().xmap($$2 -> {
            MappedRegistry $$3 = new MappedRegistry($$02, $$1);
            for (RegistryEntry $$4 : $$2) {
                $$3.registerMapping($$4.id(), $$4.key(), $$4.value(), $$1);
            }
            return $$3;
        }, $$0 -> {
            ImmutableList.Builder $$1 = ImmutableList.builder();
            for (Object $$2 : $$0) {
                $$1.add(new RegistryEntry<Object>((ResourceKey)$$0.getResourceKey($$2).get(), $$0.getId($$2), $$2));
            }
            return $$1.build();
        });
    }

    public static <E> Codec<Registry<E>> fullCodec(ResourceKey<? extends Registry<E>> $$02, Lifecycle $$1, Codec<E> $$2) {
        UnboundedMapCodec $$3 = Codec.unboundedMap(ResourceKey.codec($$02), $$2);
        return $$3.xmap($$22 -> {
            MappedRegistry $$32 = new MappedRegistry($$02, $$1);
            $$22.forEach(($$2, $$3) -> $$32.register($$2, $$3, $$1));
            return $$32.freeze();
        }, $$0 -> ImmutableMap.copyOf($$0.entrySet()));
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> $$0, Codec<E> $$1) {
        return RegistryCodecs.homogeneousList($$0, $$1, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> $$0, Codec<E> $$1, boolean $$2) {
        return HolderSetCodec.create($$0, RegistryFileCodec.create($$0, $$1), $$2);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> $$0) {
        return RegistryCodecs.homogeneousList($$0, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> $$0, boolean $$1) {
        return HolderSetCodec.create($$0, RegistryFixedCodec.create($$0), $$1);
    }

    record RegistryEntry<T>(ResourceKey<T> key, int id, T value) {
    }
}