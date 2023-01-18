/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundContainerClosePacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;

    public ClientboundContainerClosePacket(int $$0) {
        this.containerId = $$0;
    }

    public ClientboundContainerClosePacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readUnsignedByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleContainerClose(this);
    }

    public int getContainerId() {
        return this.containerId;
    }
}