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
import net.minecraft.world.entity.ExperienceOrb;

public class ClientboundAddExperienceOrbPacket
implements Packet<ClientGamePacketListener> {
    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final int value;

    public ClientboundAddExperienceOrbPacket(ExperienceOrb $$0) {
        this.id = $$0.getId();
        this.x = $$0.getX();
        this.y = $$0.getY();
        this.z = $$0.getZ();
        this.value = $$0.getValue();
    }

    public ClientboundAddExperienceOrbPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.value = $$0.readShort();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeShort(this.value);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAddExperienceOrb(this);
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

    public int getValue() {
        return this.value;
    }
}