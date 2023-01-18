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

public class ClientboundSetBorderWarningDelayPacket
implements Packet<ClientGamePacketListener> {
    private final int warningDelay;

    public ClientboundSetBorderWarningDelayPacket(WorldBorder $$0) {
        this.warningDelay = $$0.getWarningTime();
    }

    public ClientboundSetBorderWarningDelayPacket(FriendlyByteBuf $$0) {
        this.warningDelay = $$0.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.warningDelay);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetBorderWarningDelay(this);
    }

    public int getWarningDelay() {
        return this.warningDelay;
    }
}