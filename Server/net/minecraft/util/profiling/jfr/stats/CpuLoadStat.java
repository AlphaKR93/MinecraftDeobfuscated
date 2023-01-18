/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  jdk.jfr.consumer.RecordedEvent
 */
package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double jvm, double userJvm, double system) {
    public static CpuLoadStat from(RecordedEvent $$0) {
        return new CpuLoadStat($$0.getFloat("jvmSystem"), $$0.getFloat("jvmUser"), $$0.getFloat("machineTotal"));
    }
}