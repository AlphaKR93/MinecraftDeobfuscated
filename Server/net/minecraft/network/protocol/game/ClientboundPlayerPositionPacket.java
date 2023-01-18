/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.Set
 */
package net.minecraft.network.protocol.game;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundPlayerPositionPacket
implements Packet<ClientGamePacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;
    private final Set<RelativeArgument> relativeArguments;
    private final int id;
    private final boolean dismountVehicle;

    public ClientboundPlayerPositionPacket(double $$0, double $$1, double $$2, float $$3, float $$4, Set<RelativeArgument> $$5, int $$6, boolean $$7) {
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
        this.relativeArguments = RelativeArgument.unpack($$0.readUnsignedByte());
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
        $$0.writeByte(RelativeArgument.pack(this.relativeArguments));
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

    public Set<RelativeArgument> getRelativeArguments() {
        return this.relativeArguments;
    }

    public static enum RelativeArgument {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        public static final Set<RelativeArgument> ALL;
        public static final Set<RelativeArgument> ROTATION;
        private final int bit;

        private RelativeArgument(int $$0) {
            this.bit = $$0;
        }

        private int getMask() {
            return 1 << this.bit;
        }

        private boolean isSet(int $$0) {
            return ($$0 & this.getMask()) == this.getMask();
        }

        public static Set<RelativeArgument> unpack(int $$0) {
            EnumSet $$1 = EnumSet.noneOf(RelativeArgument.class);
            for (RelativeArgument $$2 : RelativeArgument.values()) {
                if (!$$2.isSet($$0)) continue;
                $$1.add((Object)$$2);
            }
            return $$1;
        }

        public static int pack(Set<RelativeArgument> $$0) {
            int $$1 = 0;
            for (RelativeArgument $$2 : $$0) {
                $$1 |= $$2.getMask();
            }
            return $$1;
        }

        static {
            ALL = Set.of((Object[])RelativeArgument.values());
            ROTATION = Set.of((Object)((Object)X_ROT), (Object)((Object)Y_ROT));
        }
    }
}