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

public class ServerboundPongPacket
implements Packet<ServerGamePacketListener> {
    private final int id;

    public ServerboundPongPacket(int $$0) {
        this.id = $$0;
    }

    public ServerboundPongPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.id);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePong(this);
    }

    public int getId() {
        return this.id;
    }
}