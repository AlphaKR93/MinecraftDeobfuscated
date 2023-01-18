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

public class ClientboundPingPacket
implements Packet<ClientGamePacketListener> {
    private final int id;

    public ClientboundPingPacket(int $$0) {
        this.id = $$0;
    }

    public ClientboundPingPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.id);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePing(this);
    }

    public int getId() {
        return this.id;
    }
}