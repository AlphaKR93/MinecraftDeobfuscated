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
import net.minecraft.world.phys.Vec3;

public class ClientboundTeleportEntityPacket
implements Packet<ClientGamePacketListener> {
    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final byte yRot;
    private final byte xRot;
    private final boolean onGround;

    public ClientboundTeleportEntityPacket(Entity $$0) {
        this.id = $$0.getId();
        Vec3 $$1 = $$0.trackingPosition();
        this.x = $$1.x;
        this.y = $$1.y;
        this.z = $$1.z;
        this.yRot = (byte)($$0.getYRot() * 256.0f / 360.0f);
        this.xRot = (byte)($$0.getXRot() * 256.0f / 360.0f);
        this.onGround = $$0.isOnGround();
    }

    public ClientboundTeleportEntityPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.yRot = $$0.readByte();
        this.xRot = $$0.readByte();
        this.onGround = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeByte(this.yRot);
        $$0.writeByte(this.xRot);
        $$0.writeBoolean(this.onGround);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleTeleportEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}