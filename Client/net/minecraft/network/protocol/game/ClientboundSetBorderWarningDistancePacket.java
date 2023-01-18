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
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDistancePacket
implements Packet<ClientGamePacketListener> {
    private final int warningBlocks;

    public ClientboundSetBorderWarningDistancePacket(WorldBorder $$0) {
        this.warningBlocks = $$0.getWarningBlocks();
    }

    public ClientboundSetBorderWarningDistancePacket(FriendlyByteBuf $$0) {
        this.warningBlocks = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.warningBlocks);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetBorderWarningDistance(this);
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }
}