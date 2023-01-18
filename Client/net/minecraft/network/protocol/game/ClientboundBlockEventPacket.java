/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.Block;

public class ClientboundBlockEventPacket
implements Packet<ClientGamePacketListener> {
    private final BlockPos pos;
    private final int b0;
    private final int b1;
    private final Block block;

    public ClientboundBlockEventPacket(BlockPos $$0, Block $$1, int $$2, int $$3) {
        this.pos = $$0;
        this.block = $$1;
        this.b0 = $$2;
        this.b1 = $$3;
    }

    public ClientboundBlockEventPacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
        this.b0 = $$0.readUnsignedByte();
        this.b1 = $$0.readUnsignedByte();
        this.block = $$0.readById(BuiltInRegistries.BLOCK);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeByte(this.b0);
        $$0.writeByte(this.b1);
        $$0.writeId(BuiltInRegistries.BLOCK, this.block);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBlockEvent(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getB0() {
        return this.b0;
    }

    public int getB1() {
        return this.b1;
    }

    public Block getBlock() {
        return this.block;
    }
}