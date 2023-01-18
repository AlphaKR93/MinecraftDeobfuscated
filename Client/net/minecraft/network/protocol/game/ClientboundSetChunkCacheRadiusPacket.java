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

public class ClientboundSetChunkCacheRadiusPacket
implements Packet<ClientGamePacketListener> {
    private final int radius;

    public ClientboundSetChunkCacheRadiusPacket(int $$0) {
        this.radius = $$0;
    }

    public ClientboundSetChunkCacheRadiusPacket(FriendlyByteBuf $$0) {
        this.radius = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.radius);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetChunkCacheRadius(this);
    }

    public int getRadius() {
        return this.radius;
    }
}