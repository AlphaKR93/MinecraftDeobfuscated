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

public class ClientboundForgetLevelChunkPacket
implements Packet<ClientGamePacketListener> {
    private final int x;
    private final int z;

    public ClientboundForgetLevelChunkPacket(int $$0, int $$1) {
        this.x = $$0;
        this.z = $$1;
    }

    public ClientboundForgetLevelChunkPacket(FriendlyByteBuf $$0) {
        this.x = $$0.readInt();
        this.z = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.x);
        $$0.writeInt(this.z);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleForgetLevelChunk(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}