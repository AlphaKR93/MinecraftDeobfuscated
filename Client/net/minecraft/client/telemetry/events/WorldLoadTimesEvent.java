/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.time.Duration
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.telemetry.events;

import java.time.Duration;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;

public class WorldLoadTimesEvent {
    private final boolean newWorld;
    @Nullable
    private final Duration worldLoadDuration;

    public WorldLoadTimesEvent(boolean $$0, @Nullable Duration $$1) {
        this.worldLoadDuration = $$1;
        this.newWorld = $$0;
    }

    public void send(TelemetryEventSender $$02) {
        if (this.worldLoadDuration != null) {
            $$02.send(TelemetryEventType.WORLD_LOAD_TIMES, (Consumer<TelemetryPropertyMap.Builder>)((Consumer)$$0 -> {
                $$0.put(TelemetryProperty.WORLD_LOAD_TIME_MS, (int)this.worldLoadDuration.toMillis());
                $$0.put(TelemetryProperty.NEW_WORLD, this.newWorld);
            }));
        }
    }
}