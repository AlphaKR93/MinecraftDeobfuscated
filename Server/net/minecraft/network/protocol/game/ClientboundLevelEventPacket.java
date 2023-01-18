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

public class ClientboundLevelEventPacket
implements Packet<ClientGamePacketListener> {
    private final int type;
    private final BlockPos pos;
    private final int data;
    private final boolean globalEvent;

    public ClientboundLevelEventPacket(int $$0, BlockPos $$1, int $$2, boolean $$3) {
        this.type = $$0;
        this.pos = $$1.immutable();
        this.data = $$2;
        this.globalEvent = $$3;
    }

    public ClientboundLevelEventPacket(FriendlyByteBuf $$0) {
        this.type = $$0.readInt();
        this.pos = $$0.readBlockPos();
        this.data = $$0.readInt();
        this.globalEvent = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.type);
        $$0.writeBlockPos(this.pos);
        $$0.writeInt(this.data);
        $$0.writeBoolean(this.globalEvent);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleLevelEvent(this);
    }

    public boolean isGlobalEvent() {
        return this.globalEvent;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}