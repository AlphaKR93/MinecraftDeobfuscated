/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameSession
 *  java.lang.Object
 *  java.lang.String
 *  java.util.UUID
 */
package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;

public class Session
implements GameSession {
    private final int players;
    private final boolean isRemoteServer;
    private final String difficulty;
    private final String gameMode;
    private final UUID id;

    public Session(ClientLevel $$0, LocalPlayer $$1, ClientPacketListener $$2) {
        this.players = $$2.getOnlinePlayers().size();
        this.isRemoteServer = !$$2.getConnection().isMemoryConnection();
        this.difficulty = $$0.getDifficulty().getKey();
        PlayerInfo $$3 = $$2.getPlayerInfo($$1.getUUID());
        this.gameMode = $$3 != null ? $$3.getGameMode().getName() : "unknown";
        this.id = $$2.getId();
    }

    public int getPlayerCount() {
        return this.players;
    }

    public boolean isRemoteServer() {
        return this.isRemoteServer;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public UUID getSessionId() {
        return this.id;
    }
}