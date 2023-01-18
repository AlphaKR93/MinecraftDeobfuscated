/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundLoginCompressionPacket
implements Packet<ClientLoginPacketListener> {
    private final int compressionThreshold;

    public ClientboundLoginCompressionPacket(int $$0) {
        this.compressionThreshold = $$0;
    }

    public ClientboundLoginCompressionPacket(FriendlyByteBuf $$0) {
        this.compressionThreshold = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.compressionThreshold);
    }

    @Override
    public void handle(ClientLoginPacketListener $$0) {
        $$0.handleCompression(this);
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}