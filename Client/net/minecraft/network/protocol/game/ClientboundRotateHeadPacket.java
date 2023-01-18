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
import net.minecraft.world.level.Level;

public class ClientboundRotateHeadPacket
implements Packet<ClientGamePacketListener> {
    private final int entityId;
    private final byte yHeadRot;

    public ClientboundRotateHeadPacket(Entity $$0, byte $$1) {
        this.entityId = $$0.getId();
        this.yHeadRot = $$1;
    }

    public ClientboundRotateHeadPacket(FriendlyByteBuf $$0) {
        this.entityId = $$0.readVarInt();
        this.yHeadRot = $$0.readByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entityId);
        $$0.writeByte(this.yHeadRot);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRotateMob(this);
    }

    public Entity getEntity(Level $$0) {
        return $$0.getEntity(this.entityId);
    }

    public byte getYHeadRot() {
        return this.yHeadRot;
    }
}