/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client;

public class Timer {
    public float partialTick;
    public float tickDelta;
    private long lastMs;
    private final float msPerTick;

    public Timer(float $$0, long $$1) {
        this.msPerTick = 1000.0f / $$0;
        this.lastMs = $$1;
    }

    public int advanceTime(long $$0) {
        this.tickDelta = (float)($$0 - this.lastMs) / this.msPerTick;
        this.lastMs = $$0;
        this.partialTick += this.tickDelta;
        int $$1 = (int)this.partialTick;
        this.partialTick -= (float)$$1;
        return $$1;
    }
}