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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public class ClientboundSetPassengersPacket
implements Packet<ClientGamePacketListener> {
    private final int vehicle;
    private final int[] passengers;

    public ClientboundSetPassengersPacket(Entity $$0) {
        this.vehicle = $$0.getId();
        List<Entity> $$1 = $$0.getPassengers();
        this.passengers = new int[$$1.size()];
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            this.passengers[$$2] = ((Entity)$$1.get($$2)).getId();
        }
    }

    public ClientboundSetPassengersPacket(FriendlyByteBuf $$0) {
        this.vehicle = $$0.readVarInt();
        this.passengers = $$0.readVarIntArray();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.vehicle);
        $$0.writeVarIntArray(this.passengers);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetEntityPassengersPacket(this);
    }

    public int[] getPassengers() {
        return this.passengers;
    }

    public int getVehicle() {
        return this.vehicle;
    }
}