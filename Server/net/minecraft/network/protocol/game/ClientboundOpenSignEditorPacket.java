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

public class ClientboundOpenSignEditorPacket
implements Packet<ClientGamePacketListener> {
    private final BlockPos pos;

    public ClientboundOpenSignEditorPacket(BlockPos $$0) {
        this.pos = $$0;
    }

    public ClientboundOpenSignEditorPacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleOpenSignEditor(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }
}