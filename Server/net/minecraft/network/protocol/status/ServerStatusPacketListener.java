/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;

public interface ServerStatusPacketListener
extends ServerPacketListener {
    public void handlePingRequest(ServerboundPingRequestPacket var1);

    public void handleStatusRequest(ServerboundStatusRequestPacket var1);
}