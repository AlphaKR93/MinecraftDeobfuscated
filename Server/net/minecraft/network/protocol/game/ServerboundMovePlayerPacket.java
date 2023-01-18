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

public abstract class ServerboundMovePlayerPacket
implements Packet<ServerGamePacketListener> {
    protected final double x;
    protected final double y;
    protected final double z;
    protected final float yRot;
    protected final float xRot;
    protected final boolean onGround;
    protected final boolean hasPos;
    protected final boolean hasRot;

    protected ServerboundMovePlayerPacket(double $$0, double $$1, double $$2, float $$3, float $$4, boolean $$5, boolean $$6, boolean $$7) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.yRot = $$3;
        this.xRot = $$4;
        this.onGround = $$5;
        this.hasPos = $$6;
        this.hasRot = $$7;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleMovePlayer(this);
    }

    public double getX(double $$0) {
        return this.hasPos ? this.x : $$0;
    }

    public double getY(double $$0) {
        return this.hasPos ? this.y : $$0;
    }

    public double getZ(double $$0) {
        return this.hasPos ? this.z : $$0;
    }

    public float getYRot(float $$0) {
        return this.hasRot ? this.yRot : $$0;
    }

    public float getXRot(float $$0) {
        return this.hasRot ? this.xRot : $$0;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean hasPosition() {
        return this.hasPos;
    }

    public boolean hasRotation() {
        return this.hasRot;
    }

    public static class StatusOnly
    extends ServerboundMovePlayerPacket {
        public StatusOnly(boolean $$0) {
            super(0.0, 0.0, 0.0, 0.0f, 0.0f, $$0, false, false);
        }

        public static StatusOnly read(FriendlyByteBuf $$0) {
            boolean $$1 = $$0.readUnsignedByte() != 0;
            return new StatusOnly($$1);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeByte(this.onGround ? 1 : 0);
        }
    }

    public static class Rot
    extends ServerboundMovePlayerPacket {
        public Rot(float $$0, float $$1, boolean $$2) {
            super(0.0, 0.0, 0.0, $$0, $$1, $$2, false, true);
        }

        public static Rot read(FriendlyByteBuf $$0) {
            float $$1 = $$0.readFloat();
            float $$2 = $$0.readFloat();
            boolean $$3 = $$0.readUnsignedByte() != 0;
            return new Rot($$1, $$2, $$3);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeFloat(this.yRot);
            $$0.writeFloat(this.xRot);
            $$0.writeByte(this.onGround ? 1 : 0);
        }
    }

    public static class Pos
    extends ServerboundMovePlayerPacket {
        public Pos(double $$0, double $$1, double $$2, boolean $$3) {
            super($$0, $$1, $$2, 0.0f, 0.0f, $$3, true, false);
        }

        public static Pos read(FriendlyByteBuf $$0) {
            double $$1 = $$0.readDouble();
            double $$2 = $$0.readDouble();
            double $$3 = $$0.readDouble();
            boolean $$4 = $$0.readUnsignedByte() != 0;
            return new Pos($$1, $$2, $$3, $$4);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeDouble(this.x);
            $$0.writeDouble(this.y);
            $$0.writeDouble(this.z);
            $$0.writeByte(this.onGround ? 1 : 0);
        }
    }

    public static class PosRot
    extends ServerboundMovePlayerPacket {
        public PosRot(double $$0, double $$1, double $$2, float $$3, float $$4, boolean $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5, true, true);
        }

        public static PosRot read(FriendlyByteBuf $$0) {
            double $$1 = $$0.readDouble();
            double $$2 = $$0.readDouble();
            double $$3 = $$0.readDouble();
            float $$4 = $$0.readFloat();
            float $$5 = $$0.readFloat();
            boolean $$6 = $$0.readUnsignedByte() != 0;
            return new PosRot($$1, $$2, $$3, $$4, $$5, $$6);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeDouble(this.x);
            $$0.writeDouble(this.y);
            $$0.writeDouble(this.z);
            $$0.writeFloat(this.yRot);
            $$0.writeFloat(this.xRot);
            $$0.writeByte(this.onGround ? 1 : 0);
        }
    }
}