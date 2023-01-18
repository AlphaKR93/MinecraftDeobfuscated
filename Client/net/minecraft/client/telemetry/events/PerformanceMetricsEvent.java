/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runtime
 *  java.util.function.Consumer
 */
package net.minecraft.client.telemetry.events;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.client.telemetry.events.AggregatedTelemetryEvent;

public final class PerformanceMetricsEvent
extends AggregatedTelemetryEvent {
    private static final long DEDICATED_MEMORY_KB = PerformanceMetricsEvent.toKilobytes(Runtime.getRuntime().maxMemory());
    private final LongList fpsSamples = new LongArrayList();
    private final LongList frameTimeSamples = new LongArrayList();
    private final LongList usedMemorySamples = new LongArrayList();

    @Override
    public void tick(TelemetryEventSender $$0) {
        if (Minecraft.getInstance().telemetryOptInExtra()) {
            super.tick($$0);
        }
    }

    private void resetValues() {
        this.fpsSamples.clear();
        this.frameTimeSamples.clear();
        this.usedMemorySamples.clear();
    }

    @Override
    public void takeSample() {
        this.fpsSamples.add((long)Minecraft.getInstance().getFps());
        this.takeUsedMemorySample();
        this.frameTimeSamples.add(Minecraft.getInstance().getFrameTimeNs());
    }

    private void takeUsedMemorySample() {
        long $$0 = Runtime.getRuntime().totalMemory();
        long $$1 = Runtime.getRuntime().freeMemory();
        long $$2 = $$0 - $$1;
        this.usedMemorySamples.add(PerformanceMetricsEvent.toKilobytes($$2));
    }

    @Override
    public void sendEvent(TelemetryEventSender $$02) {
        $$02.send(TelemetryEventType.PERFORMANCE_METRICS, (Consumer<TelemetryPropertyMap.Builder>)((Consumer)$$0 -> {
            $$0.put(TelemetryProperty.FRAME_RATE_SAMPLES, new LongArrayList(this.fpsSamples));
            $$0.put(TelemetryProperty.RENDER_TIME_SAMPLES, new LongArrayList(this.frameTimeSamples));
            $$0.put(TelemetryProperty.USED_MEMORY_SAMPLES, new LongArrayList(this.usedMemorySamples));
            $$0.put(TelemetryProperty.NUMBER_OF_SAMPLES, this.getSampleCount());
            $$0.put(TelemetryProperty.RENDER_DISTANCE, Minecraft.getInstance().options.getEffectiveRenderDistance());
            $$0.put(TelemetryProperty.DEDICATED_MEMORY_KB, (int)DEDICATED_MEMORY_KB);
        }));
        this.resetValues();
    }

    private static long toKilobytes(long $$0) {
        return $$0 / 1000L;
    }
}