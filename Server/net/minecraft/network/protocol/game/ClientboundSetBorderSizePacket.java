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
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket
implements Packet<ClientGamePacketListener> {
    private final double size;

    public ClientboundSetBorderSizePacket(WorldBorder $$0) {
        this.size = $$0.getLerpTarget();
    }

    public ClientboundSetBorderSizePacket(FriendlyByteBuf $$0) {
        this.size = $$0.readDouble();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeDouble(this.size);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetBorderSize(this);
    }

    public double getSize() {
        return this.size;
    }
}