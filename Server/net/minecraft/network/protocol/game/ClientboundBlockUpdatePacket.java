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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket
implements Packet<ClientGamePacketListener> {
    private final BlockPos pos;
    private final BlockState blockState;

    public ClientboundBlockUpdatePacket(BlockPos $$0, BlockState $$1) {
        this.pos = $$0;
        this.blockState = $$1;
    }

    public ClientboundBlockUpdatePacket(BlockGetter $$0, BlockPos $$1) {
        this($$1, $$0.getBlockState($$1));
    }

    public ClientboundBlockUpdatePacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
        this.blockState = $$0.readById(Block.BLOCK_STATE_REGISTRY);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeId(Block.BLOCK_STATE_REGISTRY, this.blockState);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBlockUpdate(this);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}