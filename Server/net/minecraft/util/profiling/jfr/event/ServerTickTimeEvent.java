/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  jdk.jfr.Category
 *  jdk.jfr.Event
 *  jdk.jfr.EventType
 *  jdk.jfr.Label
 *  jdk.jfr.Name
 *  jdk.jfr.Period
 *  jdk.jfr.StackTrace
 *  jdk.jfr.Timespan
 */
package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Period;
import jdk.jfr.StackTrace;
import jdk.jfr.Timespan;
import net.minecraft.obfuscate.DontObfuscate;

@Name(value="minecraft.ServerTickTime")
@Label(value="Server Tick Time")
@Category(value={"Minecraft", "Ticking"})
@StackTrace(value=false)
@Period(value="1 s")
@DontObfuscate
public class ServerTickTimeEvent
extends Event {
    public static final String EVENT_NAME = "minecraft.ServerTickTime";
    public static final EventType TYPE = EventType.getEventType(ServerTickTimeEvent.class);
    @Name(value="averageTickDuration")
    @Label(value="Average Server Tick Duration")
    @Timespan
    public final long averageTickDurationNanos;

    public ServerTickTimeEvent(float $$0) {
        this.averageTickDurationNanos = (long)(1000000.0f * $$0);
    }

    public static class Fields {
        public static final String AVERAGE_TICK_DURATION = "averageTickDuration";

        private Fields() {
        }
    }
}