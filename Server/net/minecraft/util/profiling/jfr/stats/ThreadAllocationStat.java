/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  java.lang.Double
 *  java.lang.Object
 *  java.lang.String
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.Temporal
 *  java.util.List
 *  java.util.Map
 *  java.util.TreeMap
 *  java.util.stream.Collectors
 *  jdk.jfr.consumer.RecordedEvent
 *  jdk.jfr.consumer.RecordedThread
 */
package net.minecraft.util.profiling.jfr.stats;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public record ThreadAllocationStat(Instant timestamp, String threadName, long totalBytes) {
    private static final String UNKNOWN_THREAD = "unknown";

    public static ThreadAllocationStat from(RecordedEvent $$0) {
        RecordedThread $$1 = $$0.getThread("thread");
        String $$2 = $$1 == null ? UNKNOWN_THREAD : (String)MoreObjects.firstNonNull((Object)$$1.getJavaName(), (Object)UNKNOWN_THREAD);
        return new ThreadAllocationStat($$0.getStartTime(), $$2, $$0.getLong("allocated"));
    }

    public static Summary summary(List<ThreadAllocationStat> $$02) {
        TreeMap $$1 = new TreeMap();
        Map $$2 = (Map)$$02.stream().collect(Collectors.groupingBy($$0 -> $$0.threadName));
        $$2.forEach((arg_0, arg_1) -> ThreadAllocationStat.lambda$summary$1((Map)$$1, arg_0, arg_1));
        return new Summary((Map<String, Double>)$$1);
    }

    private static /* synthetic */ void lambda$summary$1(Map $$0, String $$1, List $$2) {
        if ($$2.size() < 2) {
            return;
        }
        ThreadAllocationStat $$3 = (ThreadAllocationStat)((Object)$$2.get(0));
        ThreadAllocationStat $$4 = (ThreadAllocationStat)((Object)$$2.get($$2.size() - 1));
        long $$5 = Duration.between((Temporal)$$3.timestamp, (Temporal)$$4.timestamp).getSeconds();
        long $$6 = $$4.totalBytes - $$3.totalBytes;
        $$0.put((Object)$$1, (Object)((double)$$6 / (double)$$5));
    }

    public record Summary(Map<String, Double> allocationsPerSecondByThread) {
    }
}