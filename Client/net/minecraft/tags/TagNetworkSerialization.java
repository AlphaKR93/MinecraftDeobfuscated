/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Collectors
 */
package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.tags.TagKey;

public class TagNetworkSerialization {
    public static Map<ResourceKey<? extends Registry<?>>, NetworkPayload> serializeTagsToNetwork(LayeredRegistryAccess<RegistryLayer> $$02) {
        return (Map)RegistrySynchronization.networkSafeRegistries($$02).map($$0 -> Pair.of($$0.key(), (Object)TagNetworkSerialization.serializeToNetwork($$0.value()))).filter($$0 -> !((NetworkPayload)$$0.getSecond()).isEmpty()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static <T> NetworkPayload serializeToNetwork(Registry<T> $$0) {
        HashMap $$1 = new HashMap();
        $$0.getTags().forEach(arg_0 -> TagNetworkSerialization.lambda$serializeToNetwork$2($$0, (Map)$$1, arg_0));
        return new NetworkPayload((Map<ResourceLocation, IntList>)$$1);
    }

    public static <T> void deserializeTagsFromNetwork(ResourceKey<? extends Registry<T>> $$0, Registry<T> $$1, NetworkPayload $$2, TagOutput<T> $$32) {
        $$2.tags.forEach(($$3, $$4) -> {
            TagKey $$5 = TagKey.create($$0, $$3);
            List $$6 = (List)$$4.intStream().mapToObj($$1::getHolder).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
            $$32.accept($$5, $$6);
        });
    }

    private static /* synthetic */ void lambda$serializeToNetwork$2(Registry $$0, Map $$1, Pair $$2) {
        HolderSet $$3 = (HolderSet)$$2.getSecond();
        IntArrayList $$4 = new IntArrayList($$3.size());
        for (Holder $$5 : $$3) {
            if ($$5.kind() != Holder.Kind.REFERENCE) {
                throw new IllegalStateException("Can't serialize unregistered value " + $$5);
            }
            $$4.add($$0.getId($$5.value()));
        }
        $$1.put((Object)((TagKey)((Object)$$2.getFirst())).location(), (Object)$$4);
    }

    public static final class NetworkPayload {
        final Map<ResourceLocation, IntList> tags;

        NetworkPayload(Map<ResourceLocation, IntList> $$0) {
            this.tags = $$0;
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
        }

        public static NetworkPayload read(FriendlyByteBuf $$0) {
            return new NetworkPayload($$0.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
        }

        public boolean isEmpty() {
            return this.tags.isEmpty();
        }
    }

    @FunctionalInterface
    public static interface TagOutput<T> {
        public void accept(TagKey<T> var1, List<Holder<T>> var2);
    }
}