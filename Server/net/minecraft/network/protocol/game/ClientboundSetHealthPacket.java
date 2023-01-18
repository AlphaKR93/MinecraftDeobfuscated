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

public class ClientboundSetHealthPacket
implements Packet<ClientGamePacketListener> {
    private final float health;
    private final int food;
    private final float saturation;

    public ClientboundSetHealthPacket(float $$0, int $$1, float $$2) {
        this.health = $$0;
        this.food = $$1;
        this.saturation = $$2;
    }

    public ClientboundSetHealthPacket(FriendlyByteBuf $$0) {
        this.health = $$0.readFloat();
        this.food = $$0.readVarInt();
        this.saturation = $$0.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeFloat(this.health);
        $$0.writeVarInt(this.food);
        $$0.writeFloat(this.saturation);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetHealth(this);
    }

    public float getHealth() {
        return this.health;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }
}