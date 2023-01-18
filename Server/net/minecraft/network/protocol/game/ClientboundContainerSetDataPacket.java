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

public class ClientboundContainerSetDataPacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;
    private final int id;
    private final int value;

    public ClientboundContainerSetDataPacket(int $$0, int $$1, int $$2) {
        this.containerId = $$0;
        this.id = $$1;
        this.value = $$2;
    }

    public ClientboundContainerSetDataPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readUnsignedByte();
        this.id = $$0.readShort();
        this.value = $$0.readShort();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeShort(this.id);
        $$0.writeShort(this.value);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleContainerSetData(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getId() {
        return this.id;
    }

    public int getValue() {
        return this.value;
    }
}