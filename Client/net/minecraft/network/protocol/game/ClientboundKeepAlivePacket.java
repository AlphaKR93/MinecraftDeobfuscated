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

public class ClientboundKeepAlivePacket
implements Packet<ClientGamePacketListener> {
    private final long id;

    public ClientboundKeepAlivePacket(long $$0) {
        this.id = $$0;
    }

    public ClientboundKeepAlivePacket(FriendlyByteBuf $$0) {
        this.id = $$0.readLong();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeLong(this.id);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleKeepAlive(this);
    }

    public long getId() {
        return this.id;
    }
}