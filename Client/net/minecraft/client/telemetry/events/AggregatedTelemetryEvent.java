/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.Temporal
 *  javax.annotation.Nullable
 */
package net.minecraft.client.telemetry.events;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import javax.annotation.Nullable;
import net.minecraft.client.telemetry.TelemetryEventSender;

public abstract class AggregatedTelemetryEvent {
    private static final int SAMPLE_INTERVAL_MS = 60000;
    private static final int SAMPLES_PER_EVENT = 10;
    private int sampleCount;
    private boolean ticking = false;
    @Nullable
    private Instant lastSampleTime;

    public void start() {
        this.ticking = true;
        this.lastSampleTime = Instant.now();
        this.sampleCount = 0;
    }

    public void tick(TelemetryEventSender $$0) {
        if (this.shouldTakeSample()) {
            this.takeSample();
            ++this.sampleCount;
            this.lastSampleTime = Instant.now();
        }
        if (this.shouldSentEvent()) {
            this.sendEvent($$0);
            this.sampleCount = 0;
        }
    }

    public boolean shouldTakeSample() {
        return this.ticking && this.lastSampleTime != null && Duration.between((Temporal)this.lastSampleTime, (Temporal)Instant.now()).toMillis() > 60000L;
    }

    public boolean shouldSentEvent() {
        return this.sampleCount >= 10;
    }

    public void stop() {
        this.ticking = false;
    }

    protected int getSampleCount() {
        return this.sampleCount;
    }

    public abstract void takeSample();

    public abstract void sendEvent(TelemetryEventSender var1);
}