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

public class ServerboundEntityTagQuery
implements Packet<ServerGamePacketListener> {
    private final int transactionId;
    private final int entityId;

    public ServerboundEntityTagQuery(int $$0, int $$1) {
        this.transactionId = $$0;
        this.entityId = $$1;
    }

    public ServerboundEntityTagQuery(FriendlyByteBuf $$0) {
        this.transactionId = $$0.readVarInt();
        this.entityId = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.transactionId);
        $$0.writeVarInt(this.entityId);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleEntityTagQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public int getEntityId() {
        return this.entityId;
    }
}