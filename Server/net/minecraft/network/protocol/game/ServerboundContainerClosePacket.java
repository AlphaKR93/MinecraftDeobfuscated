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
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundContainerClosePacket
implements Packet<ServerGamePacketListener> {
    private final int containerId;

    public ServerboundContainerClosePacket(int $$0) {
        this.containerId = $$0;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleContainerClose(this);
    }

    public ServerboundContainerClosePacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
    }

    public int getContainerId() {
        return this.containerId;
    }
}