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

public record ClientboundSetSimulationDistancePacket(int simulationDistance) implements Packet<ClientGamePacketListener>
{
    public ClientboundSetSimulationDistancePacket(FriendlyByteBuf $$0) {
        this($$0.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.simulationDistance);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetSimulationDistance(this);
    }
}