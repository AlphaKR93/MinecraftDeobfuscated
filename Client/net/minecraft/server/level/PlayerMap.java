/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  java.lang.Object
 *  java.util.Set
 */
package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerMap {
    private final Object2BooleanMap<ServerPlayer> players = new Object2BooleanOpenHashMap();

    public Set<ServerPlayer> getPlayers(long $$0) {
        return this.players.keySet();
    }

    public void addPlayer(long $$0, ServerPlayer $$1, boolean $$2) {
        this.players.put((Object)$$1, $$2);
    }

    public void removePlayer(long $$0, ServerPlayer $$1) {
        this.players.removeBoolean((Object)$$1);
    }

    public void ignorePlayer(ServerPlayer $$0) {
        this.players.replace((Object)$$0, true);
    }

    public void unIgnorePlayer(ServerPlayer $$0) {
        this.players.replace((Object)$$0, false);
    }

    public boolean ignoredOrUnknown(ServerPlayer $$0) {
        return this.players.getOrDefault((Object)$$0, true);
    }

    public boolean ignored(ServerPlayer $$0) {
        return this.players.getBoolean((Object)$$0);
    }

    public void updatePlayer(long $$0, long $$1, ServerPlayer $$2) {
    }
}