/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundRemoveEntitiesPacket
implements Packet<ClientGamePacketListener> {
    private final IntList entityIds;

    public ClientboundRemoveEntitiesPacket(IntList $$0) {
        this.entityIds = new IntArrayList($$0);
    }

    public ClientboundRemoveEntitiesPacket(int ... $$0) {
        this.entityIds = new IntArrayList($$0);
    }

    public ClientboundRemoveEntitiesPacket(FriendlyByteBuf $$0) {
        this.entityIds = $$0.readIntIdList();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeIntIdList(this.entityIds);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRemoveEntities(this);
    }

    public IntList getEntityIds() {
        return this.entityIds;
    }
}