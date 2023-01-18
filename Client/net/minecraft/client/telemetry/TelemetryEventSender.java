/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.function.Consumer
 */
package net.minecraft.client.telemetry;

import java.util.function.Consumer;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryPropertyMap;

@FunctionalInterface
public interface TelemetryEventSender {
    public static final TelemetryEventSender DISABLED = ($$0, $$1) -> {};

    default public TelemetryEventSender decorate(Consumer<TelemetryPropertyMap.Builder> $$0) {
        return ($$1, $$22) -> this.send($$1, (Consumer<TelemetryPropertyMap.Builder>)((Consumer)$$2 -> {
            $$22.accept($$2);
            $$0.accept($$2);
        }));
    }

    public void send(TelemetryEventType var1, Consumer<TelemetryPropertyMap.Builder> var2);
}