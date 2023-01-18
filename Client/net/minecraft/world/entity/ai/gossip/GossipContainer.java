/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.DoublePredicate
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.ai.gossip.GossipType;
import org.slf4j.Logger;

public class GossipContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DISCARD_THRESHOLD = 2;
    private final Map<UUID, EntityGossips> gossips = Maps.newHashMap();

    @VisibleForDebug
    public Map<UUID, Object2IntMap<GossipType>> getGossipEntries() {
        HashMap $$0 = Maps.newHashMap();
        this.gossips.keySet().forEach(arg_0 -> this.lambda$getGossipEntries$0((Map)$$0, arg_0));
        return $$0;
    }

    public void decay() {
        Iterator $$0 = this.gossips.values().iterator();
        while ($$0.hasNext()) {
            EntityGossips $$1 = (EntityGossips)$$0.next();
            $$1.decay();
            if (!$$1.isEmpty()) continue;
            $$0.remove();
        }
    }

    private Stream<GossipEntry> unpack() {
        return this.gossips.entrySet().stream().flatMap($$0 -> ((EntityGossips)$$0.getValue()).unpack((UUID)$$0.getKey()));
    }

    private Collection<GossipEntry> selectGossipsForTransfer(RandomSource $$0, int $$1) {
        List $$2 = this.unpack().toList();
        if ($$2.isEmpty()) {
            return Collections.emptyList();
        }
        int[] $$3 = new int[$$2.size()];
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$2.size(); ++$$5) {
            GossipEntry $$6 = (GossipEntry)((Object)$$2.get($$5));
            $$3[$$5] = ($$4 += Math.abs((int)$$6.weightedValue())) - 1;
        }
        Set $$7 = Sets.newIdentityHashSet();
        for (int $$8 = 0; $$8 < $$1; ++$$8) {
            int $$9 = $$0.nextInt($$4);
            int $$10 = Arrays.binarySearch((int[])$$3, (int)$$9);
            $$7.add((Object)((GossipEntry)((Object)$$2.get($$10 < 0 ? -$$10 - 1 : $$10))));
        }
        return $$7;
    }

    private EntityGossips getOrCreate(UUID $$02) {
        return (EntityGossips)this.gossips.computeIfAbsent((Object)$$02, $$0 -> new EntityGossips());
    }

    public void transferFrom(GossipContainer $$02, RandomSource $$1, int $$2) {
        Collection<GossipEntry> $$3 = $$02.selectGossipsForTransfer($$1, $$2);
        $$3.forEach($$0 -> {
            int $$1 = $$0.value - $$0.type.decayPerTransfer;
            if ($$1 >= 2) {
                this.getOrCreate((UUID)$$0.target).entries.mergeInt((Object)$$0.type, $$1, GossipContainer::mergeValuesForTransfer);
            }
        });
    }

    public int getReputation(UUID $$0, Predicate<GossipType> $$1) {
        EntityGossips $$2 = (EntityGossips)this.gossips.get((Object)$$0);
        return $$2 != null ? $$2.weightedValue($$1) : 0;
    }

    public long getCountForType(GossipType $$0, DoublePredicate $$1) {
        return this.gossips.values().stream().filter($$2 -> $$1.test((double)($$2.entries.getOrDefault((Object)$$0, 0) * $$1.weight))).count();
    }

    public void add(UUID $$0, GossipType $$12, int $$22) {
        EntityGossips $$3 = this.getOrCreate($$0);
        $$3.entries.mergeInt((Object)$$12, $$22, ($$1, $$2) -> this.mergeValuesForAddition($$12, $$1, $$2));
        $$3.makeSureValueIsntTooLowOrTooHigh($$12);
        if ($$3.isEmpty()) {
            this.gossips.remove((Object)$$0);
        }
    }

    public void remove(UUID $$0, GossipType $$1, int $$2) {
        this.add($$0, $$1, -$$2);
    }

    public void remove(UUID $$0, GossipType $$1) {
        EntityGossips $$2 = (EntityGossips)this.gossips.get((Object)$$0);
        if ($$2 != null) {
            $$2.remove($$1);
            if ($$2.isEmpty()) {
                this.gossips.remove((Object)$$0);
            }
        }
    }

    public void remove(GossipType $$0) {
        Iterator $$1 = this.gossips.values().iterator();
        while ($$1.hasNext()) {
            EntityGossips $$2 = (EntityGossips)$$1.next();
            $$2.remove($$0);
            if (!$$2.isEmpty()) continue;
            $$1.remove();
        }
    }

    public <T> T store(DynamicOps<T> $$02) {
        return (T)GossipEntry.LIST_CODEC.encodeStart($$02, (Object)this.unpack().toList()).resultOrPartial($$0 -> LOGGER.warn("Failed to serialize gossips: {}", $$0)).orElseGet(() -> $$02.emptyList());
    }

    public void update(Dynamic<?> $$02) {
        GossipEntry.LIST_CODEC.decode($$02).resultOrPartial($$0 -> LOGGER.warn("Failed to deserialize gossips: {}", $$0)).stream().flatMap($$0 -> ((List)$$0.getFirst()).stream()).forEach($$0 -> this.getOrCreate((UUID)$$0.target).entries.put((Object)$$0.type, $$0.value));
    }

    private static int mergeValuesForTransfer(int $$0, int $$1) {
        return Math.max((int)$$0, (int)$$1);
    }

    private int mergeValuesForAddition(GossipType $$0, int $$1, int $$2) {
        int $$3 = $$1 + $$2;
        return $$3 > $$0.max ? Math.max((int)$$0.max, (int)$$1) : $$3;
    }

    private /* synthetic */ void lambda$getGossipEntries$0(Map $$0, UUID $$1) {
        EntityGossips $$2 = (EntityGossips)this.gossips.get((Object)$$1);
        $$0.put((Object)$$1, $$2.entries);
    }

    static class EntityGossips {
        final Object2IntMap<GossipType> entries = new Object2IntOpenHashMap();

        EntityGossips() {
        }

        public int weightedValue(Predicate<GossipType> $$02) {
            return this.entries.object2IntEntrySet().stream().filter($$1 -> $$02.test((Object)((GossipType)$$1.getKey()))).mapToInt($$0 -> $$0.getIntValue() * ((GossipType)$$0.getKey()).weight).sum();
        }

        public Stream<GossipEntry> unpack(UUID $$0) {
            return this.entries.object2IntEntrySet().stream().map($$1 -> new GossipEntry($$0, (GossipType)$$1.getKey(), $$1.getIntValue()));
        }

        public void decay() {
            ObjectIterator $$0 = this.entries.object2IntEntrySet().iterator();
            while ($$0.hasNext()) {
                Object2IntMap.Entry $$1 = (Object2IntMap.Entry)$$0.next();
                int $$2 = $$1.getIntValue() - ((GossipType)$$1.getKey()).decayPerDay;
                if ($$2 < 2) {
                    $$0.remove();
                    continue;
                }
                $$1.setValue($$2);
            }
        }

        public boolean isEmpty() {
            return this.entries.isEmpty();
        }

        public void makeSureValueIsntTooLowOrTooHigh(GossipType $$0) {
            int $$1 = this.entries.getInt((Object)$$0);
            if ($$1 > $$0.max) {
                this.entries.put((Object)$$0, $$0.max);
            }
            if ($$1 < 2) {
                this.remove($$0);
            }
        }

        public void remove(GossipType $$0) {
            this.entries.removeInt((Object)$$0);
        }
    }

    record GossipEntry(UUID target, GossipType type, int value) {
        public static final Codec<GossipEntry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)UUIDUtil.CODEC.fieldOf("Target").forGetter(GossipEntry::target), (App)GossipType.CODEC.fieldOf("Type").forGetter(GossipEntry::type), (App)ExtraCodecs.POSITIVE_INT.fieldOf("Value").forGetter(GossipEntry::value)).apply((Applicative)$$0, GossipEntry::new));
        public static final Codec<List<GossipEntry>> LIST_CODEC = CODEC.listOf();

        public int weightedValue() {
            return this.value * this.type.weight;
        }
    }
}