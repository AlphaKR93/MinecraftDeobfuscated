/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class Scoreboard {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DISPLAY_SLOT_LIST = 0;
    public static final int DISPLAY_SLOT_SIDEBAR = 1;
    public static final int DISPLAY_SLOT_BELOW_NAME = 2;
    public static final int DISPLAY_SLOT_TEAMS_SIDEBAR_START = 3;
    public static final int DISPLAY_SLOT_TEAMS_SIDEBAR_END = 18;
    public static final int DISPLAY_SLOTS = 19;
    private final Map<String, Objective> objectivesByName = Maps.newHashMap();
    private final Map<ObjectiveCriteria, List<Objective>> objectivesByCriteria = Maps.newHashMap();
    private final Map<String, Map<Objective, Score>> playerScores = Maps.newHashMap();
    private final Objective[] displayObjectives = new Objective[19];
    private final Map<String, PlayerTeam> teamsByName = Maps.newHashMap();
    private final Map<String, PlayerTeam> teamsByPlayer = Maps.newHashMap();
    @Nullable
    private static String[] displaySlotNames;

    public boolean hasObjective(String $$0) {
        return this.objectivesByName.containsKey((Object)$$0);
    }

    public Objective getOrCreateObjective(String $$0) {
        return (Objective)this.objectivesByName.get((Object)$$0);
    }

    @Nullable
    public Objective getObjective(@Nullable String $$0) {
        return (Objective)this.objectivesByName.get((Object)$$0);
    }

    public Objective addObjective(String $$02, ObjectiveCriteria $$1, Component $$2, ObjectiveCriteria.RenderType $$3) {
        if (this.objectivesByName.containsKey((Object)$$02)) {
            throw new IllegalArgumentException("An objective with the name '" + $$02 + "' already exists!");
        }
        Objective $$4 = new Objective(this, $$02, $$1, $$2, $$3);
        ((List)this.objectivesByCriteria.computeIfAbsent((Object)$$1, $$0 -> Lists.newArrayList())).add((Object)$$4);
        this.objectivesByName.put((Object)$$02, (Object)$$4);
        this.onObjectiveAdded($$4);
        return $$4;
    }

    public final void forAllObjectives(ObjectiveCriteria $$0, String $$1, Consumer<Score> $$22) {
        ((List)this.objectivesByCriteria.getOrDefault((Object)$$0, (Object)Collections.emptyList())).forEach($$2 -> $$22.accept((Object)this.getOrCreatePlayerScore($$1, (Objective)$$2)));
    }

    public boolean hasPlayerScore(String $$0, Objective $$1) {
        Map $$2 = (Map)this.playerScores.get((Object)$$0);
        if ($$2 == null) {
            return false;
        }
        Score $$3 = (Score)$$2.get((Object)$$1);
        return $$3 != null;
    }

    public Score getOrCreatePlayerScore(String $$02, Objective $$12) {
        Map $$2 = (Map)this.playerScores.computeIfAbsent((Object)$$02, $$0 -> Maps.newHashMap());
        return (Score)$$2.computeIfAbsent((Object)$$12, $$1 -> {
            Score $$2 = new Score(this, (Objective)$$1, $$02);
            $$2.setScore(0);
            return $$2;
        });
    }

    public Collection<Score> getPlayerScores(Objective $$0) {
        ArrayList $$1 = Lists.newArrayList();
        for (Map $$2 : this.playerScores.values()) {
            Score $$3 = (Score)$$2.get((Object)$$0);
            if ($$3 == null) continue;
            $$1.add((Object)$$3);
        }
        $$1.sort(Score.SCORE_COMPARATOR);
        return $$1;
    }

    public Collection<Objective> getObjectives() {
        return this.objectivesByName.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectivesByName.keySet();
    }

    public Collection<String> getTrackedPlayers() {
        return Lists.newArrayList((Iterable)this.playerScores.keySet());
    }

    public void resetPlayerScore(String $$0, @Nullable Objective $$1) {
        if ($$1 == null) {
            Map $$2 = (Map)this.playerScores.remove((Object)$$0);
            if ($$2 != null) {
                this.onPlayerRemoved($$0);
            }
        } else {
            Map $$3 = (Map)this.playerScores.get((Object)$$0);
            if ($$3 != null) {
                Score $$4 = (Score)$$3.remove((Object)$$1);
                if ($$3.size() < 1) {
                    Map $$5 = (Map)this.playerScores.remove((Object)$$0);
                    if ($$5 != null) {
                        this.onPlayerRemoved($$0);
                    }
                } else if ($$4 != null) {
                    this.onPlayerScoreRemoved($$0, $$1);
                }
            }
        }
    }

    public Map<Objective, Score> getPlayerScores(String $$0) {
        Map $$1 = (Map)this.playerScores.get((Object)$$0);
        if ($$1 == null) {
            $$1 = Maps.newHashMap();
        }
        return $$1;
    }

    public void removeObjective(Objective $$0) {
        this.objectivesByName.remove((Object)$$0.getName());
        for (int $$1 = 0; $$1 < 19; ++$$1) {
            if (this.getDisplayObjective($$1) != $$0) continue;
            this.setDisplayObjective($$1, null);
        }
        List $$2 = (List)this.objectivesByCriteria.get((Object)$$0.getCriteria());
        if ($$2 != null) {
            $$2.remove((Object)$$0);
        }
        for (Map $$3 : this.playerScores.values()) {
            $$3.remove((Object)$$0);
        }
        this.onObjectiveRemoved($$0);
    }

    public void setDisplayObjective(int $$0, @Nullable Objective $$1) {
        this.displayObjectives[$$0] = $$1;
    }

    @Nullable
    public Objective getDisplayObjective(int $$0) {
        return this.displayObjectives[$$0];
    }

    @Nullable
    public PlayerTeam getPlayerTeam(String $$0) {
        return (PlayerTeam)this.teamsByName.get((Object)$$0);
    }

    public PlayerTeam addPlayerTeam(String $$0) {
        PlayerTeam $$1 = this.getPlayerTeam($$0);
        if ($$1 != null) {
            LOGGER.warn("Requested creation of existing team '{}'", (Object)$$0);
            return $$1;
        }
        $$1 = new PlayerTeam(this, $$0);
        this.teamsByName.put((Object)$$0, (Object)$$1);
        this.onTeamAdded($$1);
        return $$1;
    }

    public void removePlayerTeam(PlayerTeam $$0) {
        this.teamsByName.remove((Object)$$0.getName());
        for (String $$1 : $$0.getPlayers()) {
            this.teamsByPlayer.remove((Object)$$1);
        }
        this.onTeamRemoved($$0);
    }

    public boolean addPlayerToTeam(String $$0, PlayerTeam $$1) {
        if (this.getPlayersTeam($$0) != null) {
            this.removePlayerFromTeam($$0);
        }
        this.teamsByPlayer.put((Object)$$0, (Object)$$1);
        return $$1.getPlayers().add((Object)$$0);
    }

    public boolean removePlayerFromTeam(String $$0) {
        PlayerTeam $$1 = this.getPlayersTeam($$0);
        if ($$1 != null) {
            this.removePlayerFromTeam($$0, $$1);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(String $$0, PlayerTeam $$1) {
        if (this.getPlayersTeam($$0) != $$1) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + $$1.getName() + "'.");
        }
        this.teamsByPlayer.remove((Object)$$0);
        $$1.getPlayers().remove((Object)$$0);
    }

    public Collection<String> getTeamNames() {
        return this.teamsByName.keySet();
    }

    public Collection<PlayerTeam> getPlayerTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public PlayerTeam getPlayersTeam(String $$0) {
        return (PlayerTeam)this.teamsByPlayer.get((Object)$$0);
    }

    public void onObjectiveAdded(Objective $$0) {
    }

    public void onObjectiveChanged(Objective $$0) {
    }

    public void onObjectiveRemoved(Objective $$0) {
    }

    public void onScoreChanged(Score $$0) {
    }

    public void onPlayerRemoved(String $$0) {
    }

    public void onPlayerScoreRemoved(String $$0, Objective $$1) {
    }

    public void onTeamAdded(PlayerTeam $$0) {
    }

    public void onTeamChanged(PlayerTeam $$0) {
    }

    public void onTeamRemoved(PlayerTeam $$0) {
    }

    public static String getDisplaySlotName(int $$0) {
        ChatFormatting $$1;
        switch ($$0) {
            case 0: {
                return "list";
            }
            case 1: {
                return "sidebar";
            }
            case 2: {
                return "belowName";
            }
        }
        if ($$0 >= 3 && $$0 <= 18 && ($$1 = ChatFormatting.getById($$0 - 3)) != null && $$1 != ChatFormatting.RESET) {
            return "sidebar.team." + $$1.getName();
        }
        return null;
    }

    public static int getDisplaySlotByName(String $$0) {
        String $$1;
        ChatFormatting $$2;
        if ("list".equalsIgnoreCase($$0)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase($$0)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase($$0)) {
            return 2;
        }
        if ($$0.startsWith("sidebar.team.") && ($$2 = ChatFormatting.getByName($$1 = $$0.substring("sidebar.team.".length()))) != null && $$2.getId() >= 0) {
            return $$2.getId() + 3;
        }
        return -1;
    }

    public static String[] getDisplaySlotNames() {
        if (displaySlotNames == null) {
            displaySlotNames = new String[19];
            for (int $$0 = 0; $$0 < 19; ++$$0) {
                Scoreboard.displaySlotNames[$$0] = Scoreboard.getDisplaySlotName($$0);
            }
        }
        return displaySlotNames;
    }

    public void entityRemoved(Entity $$0) {
        if ($$0 == null || $$0 instanceof Player || $$0.isAlive()) {
            return;
        }
        String $$1 = $$0.getStringUUID();
        this.resetPlayerScore($$1, null);
        this.removePlayerFromTeam($$1);
    }

    protected ListTag savePlayerScores() {
        ListTag $$0 = new ListTag();
        this.playerScores.values().stream().map(Map::values).forEach($$12 -> $$12.stream().filter($$0 -> $$0.getObjective() != null).forEach($$1 -> {
            CompoundTag $$2 = new CompoundTag();
            $$2.putString("Name", $$1.getOwner());
            $$2.putString("Objective", $$1.getObjective().getName());
            $$2.putInt("Score", $$1.getScore());
            $$2.putBoolean("Locked", $$1.isLocked());
            $$0.add($$2);
        }));
        return $$0;
    }

    protected void loadPlayerScores(ListTag $$0) {
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            CompoundTag $$2 = $$0.getCompound($$1);
            Objective $$3 = this.getOrCreateObjective($$2.getString("Objective"));
            String $$4 = $$2.getString("Name");
            Score $$5 = this.getOrCreatePlayerScore($$4, $$3);
            $$5.setScore($$2.getInt("Score"));
            if (!$$2.contains("Locked")) continue;
            $$5.setLocked($$2.getBoolean("Locked"));
        }
    }
}