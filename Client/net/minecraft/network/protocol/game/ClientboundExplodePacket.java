/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket
implements Packet<ClientGamePacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final List<BlockPos> toBlow;
    private final float knockbackX;
    private final float knockbackY;
    private final float knockbackZ;

    public ClientboundExplodePacket(double $$0, double $$1, double $$2, float $$3, List<BlockPos> $$4, @Nullable Vec3 $$5) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.power = $$3;
        this.toBlow = Lists.newArrayList($$4);
        if ($$5 != null) {
            this.knockbackX = (float)$$5.x;
            this.knockbackY = (float)$$5.y;
            this.knockbackZ = (float)$$5.z;
        } else {
            this.knockbackX = 0.0f;
            this.knockbackY = 0.0f;
            this.knockbackZ = 0.0f;
        }
    }

    public ClientboundExplodePacket(FriendlyByteBuf $$0) {
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.power = $$0.readFloat();
        int $$1 = Mth.floor(this.x);
        int $$2 = Mth.floor(this.y);
        int $$32 = Mth.floor(this.z);
        this.toBlow = $$0.readList($$3 -> {
            int $$4 = $$3.readByte() + $$1;
            int $$5 = $$3.readByte() + $$2;
            int $$6 = $$3.readByte() + $$32;
            return new BlockPos($$4, $$5, $$6);
        });
        this.knockbackX = $$0.readFloat();
        this.knockbackY = $$0.readFloat();
        this.knockbackZ = $$0.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeFloat(this.power);
        int $$1 = Mth.floor(this.x);
        int $$2 = Mth.floor(this.y);
        int $$32 = Mth.floor(this.z);
        $$0.writeCollection(this.toBlow, ($$3, $$4) -> {
            int $$5 = $$4.getX() - $$1;
            int $$6 = $$4.getY() - $$2;
            int $$7 = $$4.getZ() - $$32;
            $$3.writeByte($$5);
            $$3.writeByte($$6);
            $$3.writeByte($$7);
        });
        $$0.writeFloat(this.knockbackX);
        $$0.writeFloat(this.knockbackY);
        $$0.writeFloat(this.knockbackZ);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleExplosion(this);
    }

    public float getKnockbackX() {
        return this.knockbackX;
    }

    public float getKnockbackY() {
        return this.knockbackY;
    }

    public float getKnockbackZ() {
        return this.knockbackZ;
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

    public float getPower() {
        return this.power;
    }

    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }
}