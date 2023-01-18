/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.net.SocketAddress
 *  jdk.jfr.EventType
 *  jdk.jfr.Label
 *  jdk.jfr.Name
 */
package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.util.profiling.jfr.event.PacketEvent;

@Name(value="minecraft.PacketReceived")
@Label(value="Network Packet Received")
@DontObfuscate
public class PacketReceivedEvent
extends PacketEvent {
    public static final String NAME = "minecraft.PacketReceived";
    public static final EventType TYPE = EventType.getEventType(PacketReceivedEvent.class);

    public PacketReceivedEvent(int $$0, int $$1, SocketAddress $$2, int $$3) {
        super($$0, $$1, $$2, $$3);
    }
}