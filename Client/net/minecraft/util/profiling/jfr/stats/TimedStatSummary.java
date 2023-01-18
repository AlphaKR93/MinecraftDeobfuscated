/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.time.Duration
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.Percentiles;
import net.minecraft.util.profiling.jfr.stats.TimedStat;

public record TimedStatSummary<T extends TimedStat>(T fastest, T slowest, @Nullable T secondSlowest, int count, Map<Integer, Double> percentilesNanos, Duration totalDuration) {
    public static <T extends TimedStat> TimedStatSummary<T> summary(List<T> $$02) {
        if ($$02.isEmpty()) {
            throw new IllegalArgumentException("No values");
        }
        List $$1 = $$02.stream().sorted(Comparator.comparing(TimedStat::duration)).toList();
        Duration $$2 = (Duration)$$1.stream().map(TimedStat::duration).reduce(Duration::plus).orElse((Object)Duration.ZERO);
        TimedStat $$3 = (TimedStat)$$1.get(0);
        TimedStat $$4 = (TimedStat)$$1.get($$1.size() - 1);
        TimedStat $$5 = $$1.size() > 1 ? (TimedStat)$$1.get($$1.size() - 2) : null;
        int $$6 = $$1.size();
        Map<Integer, Double> $$7 = Percentiles.evaluate($$1.stream().mapToLong($$0 -> $$0.duration().toNanos()).toArray());
        return new TimedStatSummary<TimedStat>($$3, $$4, $$5, $$6, $$7, $$2);
    }
}