/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.Temporal
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Collectors
 *  jdk.jfr.consumer.RecordedEvent
 */
package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant timestamp, long heapUsed, Timing timing) {
    public static GcHeapStat from(RecordedEvent $$0) {
        return new GcHeapStat($$0.getStartTime(), $$0.getLong("heapUsed"), $$0.getString("when").equalsIgnoreCase("before gc") ? Timing.BEFORE_GC : Timing.AFTER_GC);
    }

    public static Summary summary(Duration $$0, List<GcHeapStat> $$1, Duration $$2, int $$3) {
        return new Summary($$0, $$2, $$3, GcHeapStat.calculateAllocationRatePerSecond($$1));
    }

    private static double calculateAllocationRatePerSecond(List<GcHeapStat> $$02) {
        long $$1 = 0L;
        Map $$2 = (Map)$$02.stream().collect(Collectors.groupingBy($$0 -> $$0.timing));
        List $$3 = (List)$$2.get((Object)Timing.BEFORE_GC);
        List $$4 = (List)$$2.get((Object)Timing.AFTER_GC);
        for (int $$5 = 1; $$5 < $$3.size(); ++$$5) {
            GcHeapStat $$6 = (GcHeapStat)((Object)$$3.get($$5));
            GcHeapStat $$7 = (GcHeapStat)((Object)$$4.get($$5 - 1));
            $$1 += $$6.heapUsed - $$7.heapUsed;
        }
        Duration $$8 = Duration.between((Temporal)((GcHeapStat)((Object)$$02.get((int)1))).timestamp, (Temporal)((GcHeapStat)((Object)$$02.get((int)($$02.size() - 1)))).timestamp);
        return (double)$$1 / (double)$$8.getSeconds();
    }

    static enum Timing {
        BEFORE_GC,
        AFTER_GC;

    }

    public record Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
        public float gcOverHead() {
            return (float)this.gcTotalDuration.toMillis() / (float)this.duration.toMillis();
        }
    }
}