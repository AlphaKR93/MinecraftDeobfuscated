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
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket
implements Packet<ClientGamePacketListener> {
    public static final int CARRIED_ITEM = -1;
    public static final int PLAYER_INVENTORY = -2;
    private final int containerId;
    private final int stateId;
    private final int slot;
    private final ItemStack itemStack;

    public ClientboundContainerSetSlotPacket(int $$0, int $$1, int $$2, ItemStack $$3) {
        this.containerId = $$0;
        this.stateId = $$1;
        this.slot = $$2;
        this.itemStack = $$3.copy();
    }

    public ClientboundContainerSetSlotPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readByte();
        this.stateId = $$0.readVarInt();
        this.slot = $$0.readShort();
        this.itemStack = $$0.readItem();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeVarInt(this.stateId);
        $$0.writeShort(this.slot);
        $$0.writeItem(this.itemStack);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleContainerSetSlot(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public int getStateId() {
        return this.stateId;
    }
}