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

public class ClientboundHorseScreenOpenPacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;
    private final int size;
    private final int entityId;

    public ClientboundHorseScreenOpenPacket(int $$0, int $$1, int $$2) {
        this.containerId = $$0;
        this.size = $$1;
        this.entityId = $$2;
    }

    public ClientboundHorseScreenOpenPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readUnsignedByte();
        this.size = $$0.readVarInt();
        this.entityId = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeVarInt(this.size);
        $$0.writeInt(this.entityId);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleHorseScreenOpen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSize() {
        return this.size;
    }

    public int getEntityId() {
        return this.entityId;
    }
}