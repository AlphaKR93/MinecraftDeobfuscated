/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.util.profiling.metrics;

import java.util.List;
import net.minecraft.util.profiling.metrics.MetricSampler;

public interface ProfilerMeasured {
    public List<MetricSampler> profiledMetrics();
}