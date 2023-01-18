/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Stream
 */
package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class RegistrySynchronization {
    private static final Map<ResourceKey<? extends Registry<?>>, NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = (Map)Util.make(() -> {
        ImmutableMap.Builder $$0 = ImmutableMap.builder();
        RegistrySynchronization.put($$0, Registries.BIOME, Biome.NETWORK_CODEC);
        RegistrySynchronization.put($$0, Registries.CHAT_TYPE, ChatType.CODEC);
        RegistrySynchronization.put($$0, Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC);
        return $$0.build();
    });
    public static final Codec<RegistryAccess> NETWORK_CODEC = RegistrySynchronization.makeNetworkCodec();

    private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, NetworkedRegistryData<?>> $$0, ResourceKey<? extends Registry<E>> $$1, Codec<E> $$2) {
        $$0.put($$1, new NetworkedRegistryData<E>($$1, $$2));
    }

    private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess $$02) {
        return $$02.registries().filter($$0 -> NETWORKABLE_REGISTRIES.containsKey($$0.key()));
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> $$02) {
        return (DataResult)Optional.ofNullable((Object)((Object)((NetworkedRegistryData)((Object)NETWORKABLE_REGISTRIES.get($$02))))).map($$0 -> $$0.networkCodec()).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown or not serializable registry: " + $$02)));
    }

    private static <E> Codec<RegistryAccess> makeNetworkCodec() {
        Codec $$02 = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
        Codec $$1 = $$02.partialDispatch("type", $$0 -> DataResult.success($$0.key()), $$0 -> RegistrySynchronization.getNetworkCodec($$0).map($$1 -> RegistryCodecs.networkCodec($$0, Lifecycle.experimental(), $$1)));
        UnboundedMapCodec $$2 = Codec.unboundedMap((Codec)$$02, (Codec)$$1);
        return RegistrySynchronization.captureMap($$2);
    }

    private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> $$0) {
        return $$0.xmap(RegistryAccess.ImmutableRegistryAccess::new, $$02 -> (Map)RegistrySynchronization.ownedNetworkableRegistries($$02).collect(ImmutableMap.toImmutableMap($$0 -> $$0.key(), $$0 -> $$0.value())));
    }

    public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> $$0) {
        return RegistrySynchronization.ownedNetworkableRegistries($$0.getAccessFrom(RegistryLayer.WORLDGEN));
    }

    public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> $$0) {
        Stream $$1 = $$0.getLayer(RegistryLayer.STATIC).registries();
        Stream<RegistryAccess.RegistryEntry<?>> $$2 = RegistrySynchronization.networkedRegistries($$0);
        return Stream.concat($$2, (Stream)$$1);
    }

    record NetworkedRegistryData<E>(ResourceKey<? extends Registry<E>> key, Codec<E> networkCodec) {
    }
}