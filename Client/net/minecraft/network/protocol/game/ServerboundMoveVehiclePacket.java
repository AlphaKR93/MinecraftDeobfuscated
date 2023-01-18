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
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.Entity;

public class ServerboundMoveVehiclePacket
implements Packet<ServerGamePacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;

    public ServerboundMoveVehiclePacket(Entity $$0) {
        this.x = $$0.getX();
        this.y = $$0.getY();
        this.z = $$0.getZ();
        this.yRot = $$0.getYRot();
        this.xRot = $$0.getXRot();
    }

    public ServerboundMoveVehiclePacket(FriendlyByteBuf $$0) {
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.yRot = $$0.readFloat();
        this.xRot = $$0.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeFloat(this.yRot);
        $$0.writeFloat(this.xRot);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleMoveVehicle(this);
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

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }
}