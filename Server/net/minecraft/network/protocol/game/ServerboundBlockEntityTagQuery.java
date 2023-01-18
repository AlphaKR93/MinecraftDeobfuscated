/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundBlockEntityTagQuery
implements Packet<ServerGamePacketListener> {
    private final int transactionId;
    private final BlockPos pos;

    public ServerboundBlockEntityTagQuery(int $$0, BlockPos $$1) {
        this.transactionId = $$0;
        this.pos = $$1;
    }

    public ServerboundBlockEntityTagQuery(FriendlyByteBuf $$0) {
        this.transactionId = $$0.readVarInt();
        this.pos = $$0.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.transactionId);
        $$0.writeBlockPos(this.pos);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleBlockEntityTagQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}