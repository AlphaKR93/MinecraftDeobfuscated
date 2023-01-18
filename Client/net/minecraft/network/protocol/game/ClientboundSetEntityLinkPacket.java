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

public class ClientboundSetEntityLinkPacket
implements Packet<ClientGamePacketListener> {
    private final int sourceId;
    private final int destId;

    public ClientboundSetEntityLinkPacket(Entity $$0, @Nullable Entity $$1) {
        this.sourceId = $$0.getId();
        this.destId = $$1 != null ? $$1.getId() : 0;
    }

    public ClientboundSetEntityLinkPacket(FriendlyByteBuf $$0) {
        this.sourceId = $$0.readInt();
        this.destId = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.sourceId);
        $$0.writeInt(this.destId);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleEntityLinkPacket(this);
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getDestId() {
        return this.destId;
    }
}