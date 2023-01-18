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
 *  jdk.jfr.StackTrace
 */
package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.obfuscate.DontObfuscate;

@Name(value="minecraft.LoadWorld")
@Label(value="Create/Load World")
@Category(value={"Minecraft", "World Generation"})
@StackTrace(value=false)
@DontObfuscate
public class WorldLoadFinishedEvent
extends Event {
    public static final String EVENT_NAME = "minecraft.LoadWorld";
    public static final EventType TYPE = EventType.getEventType(WorldLoadFinishedEvent.class);
}