/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.UnsupportedOperationException
 */
package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;

public class ServerHandshakePacketListenerImpl
implements ServerHandshakePacketListener {
    private static final Component IGNORE_STATUS_REASON = Component.literal("Ignoring status request");
    private final MinecraftServer server;
    private final Connection connection;

    public ServerHandshakePacketListenerImpl(MinecraftServer $$0, Connection $$1) {
        this.server = $$0;
        this.connection = $$1;
    }

    @Override
    public void handleIntention(ClientIntentionPacket $$0) {
        switch ($$0.getIntention()) {
            case LOGIN: {
                this.connection.setProtocol(ConnectionProtocol.LOGIN);
                if ($$0.getProtocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    MutableComponent $$2;
                    if ($$0.getProtocolVersion() < 754) {
                        MutableComponent $$1 = Component.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
                    } else {
                        $$2 = Component.translatable("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
                    }
                    this.connection.send(new ClientboundLoginDisconnectPacket($$2));
                    this.connection.disconnect($$2);
                    break;
                }
                this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
                break;
            }
            case STATUS: {
                if (this.server.repliesToStatus()) {
                    this.connection.setProtocol(ConnectionProtocol.STATUS);
                    this.connection.setListener(new ServerStatusPacketListenerImpl(this.server, this.connection));
                    break;
                }
                this.connection.disconnect(IGNORE_STATUS_REASON);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Invalid intention " + $$0.getIntention());
            }
        }
    }

    @Override
    public void onDisconnect(Component $$0) {
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }
}