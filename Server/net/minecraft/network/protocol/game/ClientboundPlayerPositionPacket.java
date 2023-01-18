/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.network.protocol.game;

import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.RelativeMovement;

public class ClientboundPlayerPositionPacket
implements Packet<ClientGamePacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;
    private final Set<RelativeMovement> relativeArguments;
    private final int id;
    private final boolean dismountVehicle;

    public ClientboundPlayerPositionPacket(double $$0, double $$1, double $$2, float $$3, float $$4, Set<RelativeMovement> $$5, int $$6, boolean $$7) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.yRot = $$3;
        this.xRot = $$4;
        this.relativeArguments = $$5;
        this.id = $$6;
        this.dismountVehicle = $$7;
    }

    public ClientboundPlayerPositionPacket(FriendlyByteBuf $$0) {
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.yRot = $$0.readFloat();
        this.xRot = $$0.readFloat();
        this.relativeArguments = RelativeMovement.unpack($$0.readUnsignedByte());
        this.id = $$0.readVarInt();
        this.dismountVehicle = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeFloat(this.yRot);
        $$0.writeFloat(this.xRot);
        $$0.writeByte(RelativeMovement.pack(this.relativeArguments));
        $$0.writeVarInt(this.id);
        $$0.writeBoolean(this.dismountVehicle);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleMovePlayer(this);
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

    public int getId() {
        return this.id;
    }

    public boolean requestDismountVehicle() {
        return this.dismountVehicle;
    }

    public Set<RelativeMovement> getRelativeArguments() {
        return this.relativeArguments;
    }
}