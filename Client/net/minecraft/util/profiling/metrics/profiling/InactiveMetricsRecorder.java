/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;

public class InactiveMetricsRecorder
implements MetricsRecorder {
    public static final MetricsRecorder INSTANCE = new InactiveMetricsRecorder();

    @Override
    public void end() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public ProfilerFiller getProfiler() {
        return InactiveProfiler.INSTANCE;
    }

    @Override
    public void endTick() {
    }
}