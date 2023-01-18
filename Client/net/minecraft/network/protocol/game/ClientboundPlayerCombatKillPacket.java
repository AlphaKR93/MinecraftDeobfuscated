/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatKillPacket
implements Packet<ClientGamePacketListener> {
    private final int playerId;
    private final int killerId;
    private final Component message;

    public ClientboundPlayerCombatKillPacket(CombatTracker $$0, Component $$1) {
        this($$0.getMob().getId(), $$0.getKillerId(), $$1);
    }

    public ClientboundPlayerCombatKillPacket(int $$0, int $$1, Component $$2) {
        this.playerId = $$0;
        this.killerId = $$1;
        this.message = $$2;
    }

    public ClientboundPlayerCombatKillPacket(FriendlyByteBuf $$0) {
        this.playerId = $$0.readVarInt();
        this.killerId = $$0.readInt();
        this.message = $$0.readComponent();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.playerId);
        $$0.writeInt(this.killerId);
        $$0.writeComponent(this.message);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerCombatKill(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public int getKillerId() {
        return this.killerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public Component getMessage() {
        return this.message;
    }
}