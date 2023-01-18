/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.time.Duration
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Map
 *  jdk.jfr.consumer.RecordedEvent
 */
package net.minecraft.util.profiling.jfr.stats;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public final class NetworkPacketSummary {
    private final PacketCountAndSize totalPacketCountAndSize;
    private final List<Pair<PacketIdentification, PacketCountAndSize>> largestSizeContributors;
    private final Duration recordingDuration;

    public NetworkPacketSummary(Duration $$0, List<Pair<PacketIdentification, PacketCountAndSize>> $$1) {
        this.recordingDuration = $$0;
        this.totalPacketCountAndSize = (PacketCountAndSize)((Object)$$1.stream().map(Pair::getSecond).reduce(PacketCountAndSize::add).orElseGet(() -> new PacketCountAndSize(0L, 0L)));
        this.largestSizeContributors = $$1.stream().sorted(Comparator.comparing(Pair::getSecond, PacketCountAndSize.SIZE_THEN_COUNT)).limit(10L).toList();
    }

    public double getCountsPerSecond() {
        return (double)this.totalPacketCountAndSize.totalCount / (double)this.recordingDuration.getSeconds();
    }

    public double getSizePerSecond() {
        return (double)this.totalPacketCountAndSize.totalSize / (double)this.recordingDuration.getSeconds();
    }

    public long getTotalCount() {
        return this.totalPacketCountAndSize.totalCount;
    }

    public long getTotalSize() {
        return this.totalPacketCountAndSize.totalSize;
    }

    public List<Pair<PacketIdentification, PacketCountAndSize>> largestSizeContributors() {
        return this.largestSizeContributors;
    }

    public record PacketCountAndSize(long totalCount, long totalSize) {
        static final Comparator<PacketCountAndSize> SIZE_THEN_COUNT = Comparator.comparing(PacketCountAndSize::totalSize).thenComparing(PacketCountAndSize::totalCount).reversed();

        PacketCountAndSize add(PacketCountAndSize $$0) {
            return new PacketCountAndSize(this.totalCount + $$0.totalCount, this.totalSize + $$0.totalSize);
        }
    }

    public record PacketIdentification(PacketFlow direction, int protocolId, int packetId) {
        private static final Map<PacketIdentification, String> PACKET_NAME_BY_ID;

        public String packetName() {
            return (String)PACKET_NAME_BY_ID.getOrDefault((Object)this, (Object)"unknown");
        }

        public static PacketIdentification from(RecordedEvent $$0) {
            return new PacketIdentification($$0.getEventType().getName().equals((Object)"minecraft.PacketSent") ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND, $$0.getInt("protocolId"), $$0.getInt("packetId"));
        }

        static {
            ImmutableMap.Builder $$0 = ImmutableMap.builder();
            for (ConnectionProtocol $$1 : ConnectionProtocol.values()) {
                for (PacketFlow $$2 : PacketFlow.values()) {
                    Int2ObjectMap<Class<? extends Packet<?>>> $$32 = $$1.getPacketsByIds($$2);
                    $$32.forEach(($$3, $$4) -> $$0.put((Object)new PacketIdentification($$2, $$1.getId(), (int)$$3), (Object)$$4.getSimpleName()));
                }
            }
            PACKET_NAME_BY_ID = $$0.build();
        }
    }
}