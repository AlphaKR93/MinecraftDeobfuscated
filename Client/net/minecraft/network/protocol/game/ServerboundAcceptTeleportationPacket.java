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
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundAcceptTeleportationPacket
implements Packet<ServerGamePacketListener> {
    private final int id;

    public ServerboundAcceptTeleportationPacket(int $$0) {
        this.id = $$0;
    }

    public ServerboundAcceptTeleportationPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleAcceptTeleportPacket(this);
    }

    public int getId() {
        return this.id;
    }
}