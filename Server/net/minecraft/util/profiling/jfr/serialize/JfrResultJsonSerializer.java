/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.LongSerializationPolicy
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.time.Duration
 *  java.util.DoubleSummaryStatistics
 *  java.util.List
 *  java.util.Map
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.stream.DoubleStream
 */
package net.minecraft.util.profiling.jfr.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import net.minecraft.Util;
import net.minecraft.util.profiling.jfr.Percentiles;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.ChunkStatus;

public class JfrResultJsonSerializer {
    private static final String BYTES_PER_SECOND = "bytesPerSecond";
    private static final String COUNT = "count";
    private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
    private static final String TOTAL_BYTES = "totalBytes";
    private static final String COUNT_PER_SECOND = "countPerSecond";
    final Gson gson = new GsonBuilder().setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();

    public String format(JfrStatsResult $$0) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("startedEpoch", (Number)Long.valueOf((long)$$0.recordingStarted().toEpochMilli()));
        $$1.addProperty("endedEpoch", (Number)Long.valueOf((long)$$0.recordingEnded().toEpochMilli()));
        $$1.addProperty("durationMs", (Number)Long.valueOf((long)$$0.recordingDuration().toMillis()));
        Duration $$2 = $$0.worldCreationDuration();
        if ($$2 != null) {
            $$1.addProperty("worldGenDurationMs", (Number)Long.valueOf((long)$$2.toMillis()));
        }
        $$1.add("heap", this.heap($$0.heapSummary()));
        $$1.add("cpuPercent", this.cpu($$0.cpuLoadStats()));
        $$1.add("network", this.network($$0));
        $$1.add("fileIO", this.fileIO($$0));
        $$1.add("serverTick", this.serverTicks($$0.tickTimes()));
        $$1.add("threadAllocation", this.threadAllocations($$0.threadAllocationSummary()));
        $$1.add("chunkGen", this.chunkGen($$0.chunkGenSummary()));
        return this.gson.toJson((JsonElement)$$1);
    }

    private JsonElement heap(GcHeapStat.Summary $$0) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("allocationRateBytesPerSecond", (Number)Double.valueOf((double)$$0.allocationRateBytesPerSecond()));
        $$1.addProperty("gcCount", (Number)Integer.valueOf((int)$$0.totalGCs()));
        $$1.addProperty("gcOverHeadPercent", (Number)Float.valueOf((float)$$0.gcOverHead()));
        $$1.addProperty("gcTotalDurationMs", (Number)Long.valueOf((long)$$0.gcTotalDuration().toMillis()));
        return $$1;
    }

    private JsonElement chunkGen(List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> $$02) {
        JsonObject $$12 = new JsonObject();
        $$12.addProperty(DURATION_NANOS_TOTAL, (Number)Double.valueOf((double)$$02.stream().mapToDouble($$0 -> ((TimedStatSummary)((Object)((Object)$$0.getSecond()))).totalDuration().toNanos()).sum()));
        JsonArray $$22 = Util.make(new JsonArray(), $$1 -> $$12.add("status", (JsonElement)$$1));
        for (Pair $$3 : $$02) {
            TimedStatSummary $$4 = (TimedStatSummary)((Object)$$3.getSecond());
            JsonObject $$5 = Util.make(new JsonObject(), arg_0 -> ((JsonArray)$$22).add(arg_0));
            $$5.addProperty("state", ((ChunkStatus)$$3.getFirst()).getName());
            $$5.addProperty(COUNT, (Number)Integer.valueOf((int)$$4.count()));
            $$5.addProperty(DURATION_NANOS_TOTAL, (Number)Long.valueOf((long)$$4.totalDuration().toNanos()));
            $$5.addProperty("durationNanosAvg", (Number)Long.valueOf((long)($$4.totalDuration().toNanos() / (long)$$4.count())));
            JsonObject $$6 = Util.make(new JsonObject(), $$1 -> $$5.add("durationNanosPercentiles", (JsonElement)$$1));
            $$4.percentilesNanos().forEach(($$1, $$2) -> $$6.addProperty("p" + $$1, (Number)$$2));
            Function $$7 = $$0 -> {
                JsonObject $$1 = new JsonObject();
                $$1.addProperty("durationNanos", (Number)Long.valueOf((long)$$0.duration().toNanos()));
                $$1.addProperty("level", $$0.level());
                $$1.addProperty("chunkPosX", (Number)Integer.valueOf((int)$$0.chunkPos().x));
                $$1.addProperty("chunkPosZ", (Number)Integer.valueOf((int)$$0.chunkPos().z));
                $$1.addProperty("worldPosX", (Number)Integer.valueOf((int)$$0.worldPos().x()));
                $$1.addProperty("worldPosZ", (Number)Integer.valueOf((int)$$0.worldPos().z()));
                return $$1;
            };
            $$5.add("fastest", (JsonElement)$$7.apply((Object)((ChunkGenStat)$$4.fastest())));
            $$5.add("slowest", (JsonElement)$$7.apply((Object)((ChunkGenStat)$$4.slowest())));
            $$5.add("secondSlowest", (JsonElement)($$4.secondSlowest() != null ? (JsonElement)$$7.apply((Object)((ChunkGenStat)$$4.secondSlowest())) : JsonNull.INSTANCE));
        }
        return $$12;
    }

    private JsonElement threadAllocations(ThreadAllocationStat.Summary $$0) {
        JsonArray $$12 = new JsonArray();
        $$0.allocationsPerSecondByThread().forEach(($$1, $$22) -> $$12.add((JsonElement)Util.make(new JsonObject(), $$2 -> {
            $$2.addProperty("thread", $$1);
            $$2.addProperty(BYTES_PER_SECOND, (Number)$$22);
        })));
        return $$12;
    }

    private JsonElement serverTicks(List<TickTimeStat> $$02) {
        if ($$02.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$12 = new JsonObject();
        double[] $$22 = $$02.stream().mapToDouble($$0 -> (double)$$0.currentAverage().toNanos() / 1000000.0).toArray();
        DoubleSummaryStatistics $$3 = DoubleStream.of((double[])$$22).summaryStatistics();
        $$12.addProperty("minMs", (Number)Double.valueOf((double)$$3.getMin()));
        $$12.addProperty("averageMs", (Number)Double.valueOf((double)$$3.getAverage()));
        $$12.addProperty("maxMs", (Number)Double.valueOf((double)$$3.getMax()));
        Map<Integer, Double> $$4 = Percentiles.evaluate($$22);
        $$4.forEach(($$1, $$2) -> $$12.addProperty("p" + $$1, (Number)$$2));
        return $$12;
    }

    private JsonElement fileIO(JfrStatsResult $$0) {
        JsonObject $$1 = new JsonObject();
        $$1.add("write", this.fileIoSummary($$0.fileWrites()));
        $$1.add("read", this.fileIoSummary($$0.fileReads()));
        return $$1;
    }

    private JsonElement fileIoSummary(FileIOStat.Summary $$0) {
        JsonObject $$12 = new JsonObject();
        $$12.addProperty(TOTAL_BYTES, (Number)Long.valueOf((long)$$0.totalBytes()));
        $$12.addProperty(COUNT, (Number)Long.valueOf((long)$$0.counts()));
        $$12.addProperty(BYTES_PER_SECOND, (Number)Double.valueOf((double)$$0.bytesPerSecond()));
        $$12.addProperty(COUNT_PER_SECOND, (Number)Double.valueOf((double)$$0.countsPerSecond()));
        JsonArray $$2 = new JsonArray();
        $$12.add("topContributors", (JsonElement)$$2);
        $$0.topTenContributorsByTotalBytes().forEach($$1 -> {
            JsonObject $$2 = new JsonObject();
            $$2.add((JsonElement)$$2);
            $$2.addProperty("path", (String)$$1.getFirst());
            $$2.addProperty(TOTAL_BYTES, (Number)$$1.getSecond());
        });
        return $$12;
    }

    private JsonElement network(JfrStatsResult $$0) {
        JsonObject $$1 = new JsonObject();
        $$1.add("sent", this.packets($$0.sentPacketsSummary()));
        $$1.add("received", this.packets($$0.receivedPacketsSummary()));
        return $$1;
    }

    private JsonElement packets(NetworkPacketSummary $$0) {
        JsonObject $$12 = new JsonObject();
        $$12.addProperty(TOTAL_BYTES, (Number)Long.valueOf((long)$$0.getTotalSize()));
        $$12.addProperty(COUNT, (Number)Long.valueOf((long)$$0.getTotalCount()));
        $$12.addProperty(BYTES_PER_SECOND, (Number)Double.valueOf((double)$$0.getSizePerSecond()));
        $$12.addProperty(COUNT_PER_SECOND, (Number)Double.valueOf((double)$$0.getCountsPerSecond()));
        JsonArray $$2 = new JsonArray();
        $$12.add("topContributors", (JsonElement)$$2);
        $$0.largestSizeContributors().forEach($$1 -> {
            JsonObject $$2 = new JsonObject();
            $$2.add((JsonElement)$$2);
            NetworkPacketSummary.PacketIdentification $$3 = (NetworkPacketSummary.PacketIdentification)((Object)((Object)$$1.getFirst()));
            NetworkPacketSummary.PacketCountAndSize $$4 = (NetworkPacketSummary.PacketCountAndSize)((Object)((Object)$$1.getSecond()));
            $$2.addProperty("protocolId", (Number)Integer.valueOf((int)$$3.protocolId()));
            $$2.addProperty("packetId", (Number)Integer.valueOf((int)$$3.packetId()));
            $$2.addProperty("packetName", $$3.packetName());
            $$2.addProperty(TOTAL_BYTES, (Number)Long.valueOf((long)$$4.totalSize()));
            $$2.addProperty(COUNT, (Number)Long.valueOf((long)$$4.totalCount()));
        });
        return $$12;
    }

    private JsonElement cpu(List<CpuLoadStat> $$02) {
        JsonObject $$12 = new JsonObject();
        BiFunction $$2 = ($$0, $$1) -> {
            JsonObject $$2 = new JsonObject();
            DoubleSummaryStatistics $$3 = $$0.stream().mapToDouble($$1).summaryStatistics();
            $$2.addProperty("min", (Number)Double.valueOf((double)$$3.getMin()));
            $$2.addProperty("average", (Number)Double.valueOf((double)$$3.getAverage()));
            $$2.addProperty("max", (Number)Double.valueOf((double)$$3.getMax()));
            return $$2;
        };
        $$12.add("jvm", (JsonElement)$$2.apply($$02, CpuLoadStat::jvm));
        $$12.add("userJvm", (JsonElement)$$2.apply($$02, CpuLoadStat::userJvm));
        $$12.add("system", (JsonElement)$$2.apply($$02, CpuLoadStat::system));
        return $$12;
    }
}