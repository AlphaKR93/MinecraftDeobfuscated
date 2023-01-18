/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket
implements Packet<ClientGamePacketListener> {
    private final int entityId;
    private final byte eventId;

    public ClientboundEntityEventPacket(Entity $$0, byte $$1) {
        this.entityId = $$0.getId();
        this.eventId = $$1;
    }

    public ClientboundEntityEventPacket(FriendlyByteBuf $$0) {
        this.entityId = $$0.readInt();
        this.eventId = $$0.readByte();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.entityId);
        $$0.writeByte(this.eventId);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleEntityEvent(this);
    }

    @Nullable
    public Entity getEntity(Level $$0) {
        return $$0.getEntity(this.entityId);
    }

    public byte getEventId() {
        return this.eventId;
    }
}