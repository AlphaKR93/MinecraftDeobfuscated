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

public class ServerboundSelectTradePacket
implements Packet<ServerGamePacketListener> {
    private final int item;

    public ServerboundSelectTradePacket(int $$0) {
        this.item = $$0;
    }

    public ServerboundSelectTradePacket(FriendlyByteBuf $$0) {
        this.item = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.item);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSelectTrade(this);
    }

    public int getItem() {
        return this.item;
    }
}