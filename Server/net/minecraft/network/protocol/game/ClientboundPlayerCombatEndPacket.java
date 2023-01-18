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
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket
implements Packet<ClientGamePacketListener> {
    private final int killerId;
    private final int duration;

    public ClientboundPlayerCombatEndPacket(CombatTracker $$0) {
        this($$0.getKillerId(), $$0.getCombatDuration());
    }

    public ClientboundPlayerCombatEndPacket(int $$0, int $$1) {
        this.killerId = $$0;
        this.duration = $$1;
    }

    public ClientboundPlayerCombatEndPacket(FriendlyByteBuf $$0) {
        this.duration = $$0.readVarInt();
        this.killerId = $$0.readInt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.duration);
        $$0.writeInt(this.killerId);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerCombatEnd(this);
    }
}