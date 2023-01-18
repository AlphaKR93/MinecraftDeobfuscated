/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.handshake;

import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;

public class ClientIntentionPacket
implements Packet<ServerHandshakePacketListener> {
    private static final int MAX_HOST_LENGTH = 255;
    private final int protocolVersion;
    private final String hostName;
    private final int port;
    private final ConnectionProtocol intention;

    public ClientIntentionPacket(String $$0, int $$1, ConnectionProtocol $$2) {
        this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
        this.hostName = $$0;
        this.port = $$1;
        this.intention = $$2;
    }

    public ClientIntentionPacket(FriendlyByteBuf $$0) {
        this.protocolVersion = $$0.readVarInt();
        this.hostName = $$0.readUtf(255);
        this.port = $$0.readUnsignedShort();
        this.intention = ConnectionProtocol.getById($$0.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.protocolVersion);
        $$0.writeUtf(this.hostName);
        $$0.writeShort(this.port);
        $$0.writeVarInt(this.intention.getId());
    }

    @Override
    public void handle(ServerHandshakePacketListener $$0) {
        $$0.handleIntention(this);
    }

    public ConnectionProtocol getIntention() {
        return this.intention;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPort() {
        return this.port;
    }
}