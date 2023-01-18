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

public class ServerboundPickItemPacket
implements Packet<ServerGamePacketListener> {
    private final int slot;

    public ServerboundPickItemPacket(int $$0) {
        this.slot = $$0;
    }

    public ServerboundPickItemPacket(FriendlyByteBuf $$0) {
        this.slot = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.slot);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePickItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}