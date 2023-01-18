/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.net.SocketAddress
 *  jdk.jfr.Category
 *  jdk.jfr.DataAmount
 *  jdk.jfr.Enabled
 *  jdk.jfr.Event
 *  jdk.jfr.Label
 *  jdk.jfr.Name
 *  jdk.jfr.StackTrace
 */
package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Category(value={"Minecraft", "Network"})
@StackTrace(value=false)
@Enabled(value=false)
public abstract class PacketEvent
extends Event {
    @Name(value="protocolId")
    @Label(value="Protocol Id")
    public final int protocolId;
    @Name(value="packetId")
    @Label(value="Packet Id")
    public final int packetId;
    @Name(value="remoteAddress")
    @Label(value="Remote Address")
    public final String remoteAddress;
    @Name(value="bytes")
    @Label(value="Bytes")
    @DataAmount
    public final int bytes;

    public PacketEvent(int $$0, int $$1, SocketAddress $$2, int $$3) {
        this.protocolId = $$0;
        this.packetId = $$1;
        this.remoteAddress = $$2.toString();
        this.bytes = $$3;
    }

    public static final class Fields {
        public static final String REMOTE_ADDRESS = "remoteAddress";
        public static final String PROTOCOL_ID = "protocolId";
        public static final String PACKET_ID = "packetId";
        public static final String BYTES = "bytes";

        private Fields() {
        }
    }
}