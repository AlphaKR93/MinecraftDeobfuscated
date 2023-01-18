/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.time.Duration
 *  java.time.Instant
 *  jdk.jfr.consumer.RecordedEvent
 */
package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant timestamp, Duration currentAverage) {
    public static TickTimeStat from(RecordedEvent $$0) {
        return new TickTimeStat($$0.getStartTime(), $$0.getDuration("averageTickDuration"));
    }
}