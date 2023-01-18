/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.time.Instant
 */
package net.minecraft.util.profiling.jfr.stats;

import java.time.Instant;

public interface TimeStamped {
    public Instant getTimestamp();
}