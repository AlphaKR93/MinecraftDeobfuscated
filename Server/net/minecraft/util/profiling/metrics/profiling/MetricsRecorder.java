/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.ProfilerFiller;

public interface MetricsRecorder {
    public void end();

    public void cancel();

    public void startTick();

    public boolean isRecording();

    public ProfilerFiller getProfiler();

    public void endTick();
}