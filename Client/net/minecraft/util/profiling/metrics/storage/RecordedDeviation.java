/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.time.Instant
 */
package net.minecraft.util.profiling.metrics.storage;

import java.time.Instant;
import net.minecraft.util.profiling.ProfileResults;

public final class RecordedDeviation {
    public final Instant timestamp;
    public final int tick;
    public final ProfileResults profilerResultAtTick;

    public RecordedDeviation(Instant $$0, int $$1, ProfileResults $$2) {
        this.timestamp = $$0;
        this.tick = $$1;
        this.profilerResultAtTick = $$2;
    }
}