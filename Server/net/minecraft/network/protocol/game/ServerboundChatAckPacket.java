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

public record ServerboundChatAckPacket(int offset) implements Packet<ServerGamePacketListener>
{
    public ServerboundChatAckPacket(FriendlyByteBuf $$0) {
        this($$0.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.offset);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleChatAck(this);
    }
}