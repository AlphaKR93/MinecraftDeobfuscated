/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.MinecraftServer;

public class ServerStatusPacketListenerImpl
implements ServerStatusPacketListener {
    private static final Component DISCONNECT_REASON = Component.translatable("multiplayer.status.request_handled");
    private final MinecraftServer server;
    private final Connection connection;
    private boolean hasRequestedStatus;

    public ServerStatusPacketListenerImpl(MinecraftServer $$0, Connection $$1) {
        this.server = $$0;
        this.connection = $$1;
    }

    @Override
    public void onDisconnect(Component $$0) {
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public void handleStatusRequest(ServerboundStatusRequestPacket $$0) {
        if (this.hasRequestedStatus) {
            this.connection.disconnect(DISCONNECT_REASON);
            return;
        }
        this.hasRequestedStatus = true;
        this.connection.send(new ClientboundStatusResponsePacket(this.server.getStatus()));
    }

    @Override
    public void handlePingRequest(ServerboundPingRequestPacket $$0) {
        this.connection.send(new ClientboundPongResponsePacket($$0.getTime()));
        this.connection.disconnect(DISCONNECT_REASON);
    }
}