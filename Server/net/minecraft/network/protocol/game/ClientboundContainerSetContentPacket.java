/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentPacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;
    private final int stateId;
    private final List<ItemStack> items;
    private final ItemStack carriedItem;

    public ClientboundContainerSetContentPacket(int $$0, int $$1, NonNullList<ItemStack> $$2, ItemStack $$3) {
        this.containerId = $$0;
        this.stateId = $$1;
        this.items = NonNullList.withSize($$2.size(), ItemStack.EMPTY);
        for (int $$4 = 0; $$4 < $$2.size(); ++$$4) {
            this.items.set($$4, (Object)$$2.get($$4).copy());
        }
        this.carriedItem = $$3.copy();
    }

    public ClientboundContainerSetContentPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readUnsignedByte();
        this.stateId = $$0.readVarInt();
        this.items = (List)$$0.readCollection(NonNullList::createWithCapacity, FriendlyByteBuf::readItem);
        this.carriedItem = $$0.readItem();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeVarInt(this.stateId);
        $$0.writeCollection(this.items, FriendlyByteBuf::writeItem);
        $$0.writeItem(this.carriedItem);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleContainerContent(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public int getStateId() {
        return this.stateId;
    }
}