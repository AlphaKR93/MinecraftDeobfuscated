/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public class ServerboundPingRequestPacket
implements Packet<ServerStatusPacketListener> {
    private final long time;

    public ServerboundPingRequestPacket(long $$0) {
        this.time = $$0;
    }

    public ServerboundPingRequestPacket(FriendlyByteBuf $$0) {
        this.time = $$0.readLong();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeLong(this.time);
    }

    @Override
    public void handle(ServerStatusPacketListener $$0) {
        $$0.handlePingRequest(this);
    }

    public long getTime() {
        return this.time;
    }
}