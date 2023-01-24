/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.network.FriendlyByteBuf
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundDisconnectPacket
implements Packet<ClientGamePacketListener> {
    private final Component reason;

    public ClientboundDisconnectPacket(Component $$0) {
        this.reason = $$0;
    }

    public ClientboundDisconnectPacket(FriendlyByteBuf $$0) {
        this.reason = $$0.readComponent();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeComponent(this.reason);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleDisconnect(this);
    }

    public Component getReason() {
        return this.reason;
    }
}