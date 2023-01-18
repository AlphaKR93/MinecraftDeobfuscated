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
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundBlockDestructionPacket
implements Packet<ClientGamePacketListener> {
    private final int id;
    private final BlockPos pos;
    private final int progress;

    public ClientboundBlockDestructionPacket(int $$0, BlockPos $$1, int $$2) {
        this.id = $$0;
        this.pos = $$1;
        this.progress = $$2;
    }

    public ClientboundBlockDestructionPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.pos = $$0.readBlockPos();
        this.progress = $$0.readUnsignedByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeBlockPos(this.pos);
        $$0.writeByte(this.progress);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBlockDestruction(this);
    }

    public int getId() {
        return this.id;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getProgress() {
        return this.progress;
    }
}