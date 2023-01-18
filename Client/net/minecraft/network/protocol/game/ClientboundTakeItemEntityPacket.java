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

public class ClientboundTakeItemEntityPacket
implements Packet<ClientGamePacketListener> {
    private final int itemId;
    private final int playerId;
    private final int amount;

    public ClientboundTakeItemEntityPacket(int $$0, int $$1, int $$2) {
        this.itemId = $$0;
        this.playerId = $$1;
        this.amount = $$2;
    }

    public ClientboundTakeItemEntityPacket(FriendlyByteBuf $$0) {
        this.itemId = $$0.readVarInt();
        this.playerId = $$0.readVarInt();
        this.amount = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.itemId);
        $$0.writeVarInt(this.playerId);
        $$0.writeVarInt(this.amount);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleTakeItemEntity(this);
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getAmount() {
        return this.amount;
    }
}