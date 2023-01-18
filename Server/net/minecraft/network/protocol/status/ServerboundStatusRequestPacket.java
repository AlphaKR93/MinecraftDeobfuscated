/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public class ServerboundStatusRequestPacket
implements Packet<ServerStatusPacketListener> {
    public ServerboundStatusRequestPacket() {
    }

    public ServerboundStatusRequestPacket(FriendlyByteBuf $$0) {
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
    }

    @Override
    public void handle(ServerStatusPacketListener $$0) {
        $$0.handleStatusRequest(this);
    }
}