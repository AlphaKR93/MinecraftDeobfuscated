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

public class ClientboundSetBorderLerpSizePacket
implements Packet<ClientGamePacketListener> {
    private final double oldSize;
    private final double newSize;
    private final long lerpTime;

    public ClientboundSetBorderLerpSizePacket(WorldBorder $$0) {
        this.oldSize = $$0.getSize();
        this.newSize = $$0.getLerpTarget();
        this.lerpTime = $$0.getLerpRemainingTime();
    }

    public ClientboundSetBorderLerpSizePacket(FriendlyByteBuf $$0) {
        this.oldSize = $$0.readDouble();
        this.newSize = $$0.readDouble();
        this.lerpTime = $$0.readVarLong();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeDouble(this.oldSize);
        $$0.writeDouble(this.newSize);
        $$0.writeVarLong(this.lerpTime);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetBorderLerpSize(this);
    }

    public double getOldSize() {
        return this.oldSize;
    }

    public double getNewSize() {
        return this.newSize;
    }

    public long getLerpTime() {
        return this.lerpTime;
    }
}