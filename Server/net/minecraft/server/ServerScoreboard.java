/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;

public class ServerScoreboard
extends Scoreboard {
    private final MinecraftServer server;
    private final Set<Objective> trackedObjectives = Sets.newHashSet();
    private final List<Runnable> dirtyListeners = Lists.newArrayList();

    public ServerScoreboard(MinecraftServer $$0) {
        this.server = $$0;
    }

    @Override
    public void onScoreChanged(Score $$0) {
        super.onScoreChanged($$0);
        if (this.trackedObjectives.contains((Object)$$0.getObjective())) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.CHANGE, $$0.getObjective().getName(), $$0.getOwner(), $$0.getScore()));
        }
        this.setDirty();
    }

    @Override
    public void onPlayerRemoved(String $$0) {
        super.onPlayerRemoved($$0);
        this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.REMOVE, null, $$0, 0));
        this.setDirty();
    }

    @Override
    public void onPlayerScoreRemoved(String $$0, Objective $$1) {
        super.onPlayerScoreRemoved($$0, $$1);
        if (this.trackedObjectives.contains((Object)$$1)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.REMOVE, $$1.getName(), $$0, 0));
        }
        this.setDirty();
    }

    @Override
    public void setDisplayObjective(int $$0, @Nullable Objective $$1) {
        Objective $$2 = this.getDisplayObjective($$0);
        super.setDisplayObjective($$0, $$1);
        if ($$2 != $$1 && $$2 != null) {
            if (this.getObjectiveDisplaySlotCount($$2) > 0) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket($$0, $$1));
            } else {
                this.stopTrackingObjective($$2);
            }
        }
        if ($$1 != null) {
            if (this.trackedObjectives.contains((Object)$$1)) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket($$0, $$1));
            } else {
                this.startTrackingObjective($$1);
            }
        }
        this.setDirty();
    }

    @Override
    public boolean addPlayerToTeam(String $$0, PlayerTeam $$1) {
        if (super.addPlayerToTeam($$0, $$1)) {
            this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket($$1, $$0, ClientboundSetPlayerTeamPacket.Action.ADD));
            this.setDirty();
            return true;
        }
        return false;
    }

    @Override
    public void removePlayerFromTeam(String $$0, PlayerTeam $$1) {
        super.removePlayerFromTeam($$0, $$1);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket($$1, $$0, ClientboundSetPlayerTeamPacket.Action.REMOVE));
        this.setDirty();
    }

    @Override
    public void onObjectiveAdded(Objective $$0) {
        super.onObjectiveAdded($$0);
        this.setDirty();
    }

    @Override
    public void onObjectiveChanged(Objective $$0) {
        super.onObjectiveChanged($$0);
        if (this.trackedObjectives.contains((Object)$$0)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket($$0, 2));
        }
        this.setDirty();
    }

    @Override
    public void onObjectiveRemoved(Objective $$0) {
        super.onObjectiveRemoved($$0);
        if (this.trackedObjectives.contains((Object)$$0)) {
            this.stopTrackingObjective($$0);
        }
        this.setDirty();
    }

    @Override
    public void onTeamAdded(PlayerTeam $$0) {
        super.onTeamAdded($$0);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket($$0, true));
        this.setDirty();
    }

    @Override
    public void onTeamChanged(PlayerTeam $$0) {
        super.onTeamChanged($$0);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket($$0, false));
        this.setDirty();
    }

    @Override
    public void onTeamRemoved(PlayerTeam $$0) {
        super.onTeamRemoved($$0);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createRemovePacket($$0));
        this.setDirty();
    }

    public void addDirtyListener(Runnable $$0) {
        this.dirtyListeners.add((Object)$$0);
    }

    protected void setDirty() {
        for (Runnable $$0 : this.dirtyListeners) {
            $$0.run();
        }
    }

    public List<Packet<?>> getStartTrackingPackets(Objective $$0) {
        ArrayList $$1 = Lists.newArrayList();
        $$1.add((Object)new ClientboundSetObjectivePacket($$0, 0));
        for (int $$2 = 0; $$2 < 19; ++$$2) {
            if (this.getDisplayObjective($$2) != $$0) continue;
            $$1.add((Object)new ClientboundSetDisplayObjectivePacket($$2, $$0));
        }
        for (Score $$3 : this.getPlayerScores($$0)) {
            $$1.add((Object)new ClientboundSetScorePacket(Method.CHANGE, $$3.getObjective().getName(), $$3.getOwner(), $$3.getScore()));
        }
        return $$1;
    }

    public void startTrackingObjective(Objective $$0) {
        List<Packet<?>> $$1 = this.getStartTrackingPackets($$0);
        for (ServerPlayer $$2 : this.server.getPlayerList().getPlayers()) {
            for (Packet $$3 : $$1) {
                $$2.connection.send($$3);
            }
        }
        this.trackedObjectives.add((Object)$$0);
    }

    public List<Packet<?>> getStopTrackingPackets(Objective $$0) {
        ArrayList $$1 = Lists.newArrayList();
        $$1.add((Object)new ClientboundSetObjectivePacket($$0, 1));
        for (int $$2 = 0; $$2 < 19; ++$$2) {
            if (this.getDisplayObjective($$2) != $$0) continue;
            $$1.add((Object)new ClientboundSetDisplayObjectivePacket($$2, $$0));
        }
        return $$1;
    }

    public void stopTrackingObjective(Objective $$0) {
        List<Packet<?>> $$1 = this.getStopTrackingPackets($$0);
        for (ServerPlayer $$2 : this.server.getPlayerList().getPlayers()) {
            for (Packet $$3 : $$1) {
                $$2.connection.send($$3);
            }
        }
        this.trackedObjectives.remove((Object)$$0);
    }

    public int getObjectiveDisplaySlotCount(Objective $$0) {
        int $$1 = 0;
        for (int $$2 = 0; $$2 < 19; ++$$2) {
            if (this.getDisplayObjective($$2) != $$0) continue;
            ++$$1;
        }
        return $$1;
    }

    public ScoreboardSaveData createData() {
        ScoreboardSaveData $$0 = new ScoreboardSaveData(this);
        this.addDirtyListener($$0::setDirty);
        return $$0;
    }

    public ScoreboardSaveData createData(CompoundTag $$0) {
        return this.createData().load($$0);
    }

    public static enum Method {
        CHANGE,
        REMOVE;

    }
}