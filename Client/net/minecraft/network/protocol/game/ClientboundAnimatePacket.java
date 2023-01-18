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
import net.minecraft.world.entity.Entity;

public class ClientboundAnimatePacket
implements Packet<ClientGamePacketListener> {
    public static final int SWING_MAIN_HAND = 0;
    public static final int WAKE_UP = 2;
    public static final int SWING_OFF_HAND = 3;
    public static final int CRITICAL_HIT = 4;
    public static final int MAGIC_CRITICAL_HIT = 5;
    private final int id;
    private final int action;

    public ClientboundAnimatePacket(Entity $$0, int $$1) {
        this.id = $$0.getId();
        this.action = $$1;
    }

    public ClientboundAnimatePacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.action = $$0.readUnsignedByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeByte(this.action);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAnimate(this);
    }

    public int getId() {
        return this.id;
    }

    public int getAction() {
        return this.action;
    }
}