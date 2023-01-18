/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 */
package net.minecraft.world.scores;

import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardSaveData
extends SavedData {
    public static final String FILE_ID = "scoreboard";
    private final Scoreboard scoreboard;

    public ScoreboardSaveData(Scoreboard $$0) {
        this.scoreboard = $$0;
    }

    public ScoreboardSaveData load(CompoundTag $$0) {
        this.loadObjectives($$0.getList("Objectives", 10));
        this.scoreboard.loadPlayerScores($$0.getList("PlayerScores", 10));
        if ($$0.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots($$0.getCompound("DisplaySlots"));
        }
        if ($$0.contains("Teams", 9)) {
            this.loadTeams($$0.getList("Teams", 10));
        }
        return this;
    }

    private void loadTeams(ListTag $$0) {
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            Team.CollisionRule $$10;
            Team.Visibility $$9;
            Team.Visibility $$8;
            MutableComponent $$7;
            MutableComponent $$6;
            CompoundTag $$2 = $$0.getCompound($$1);
            String $$3 = $$2.getString("Name");
            PlayerTeam $$4 = this.scoreboard.addPlayerTeam($$3);
            MutableComponent $$5 = Component.Serializer.fromJson($$2.getString("DisplayName"));
            if ($$5 != null) {
                $$4.setDisplayName($$5);
            }
            if ($$2.contains("TeamColor", 8)) {
                $$4.setColor(ChatFormatting.getByName($$2.getString("TeamColor")));
            }
            if ($$2.contains("AllowFriendlyFire", 99)) {
                $$4.setAllowFriendlyFire($$2.getBoolean("AllowFriendlyFire"));
            }
            if ($$2.contains("SeeFriendlyInvisibles", 99)) {
                $$4.setSeeFriendlyInvisibles($$2.getBoolean("SeeFriendlyInvisibles"));
            }
            if ($$2.contains("MemberNamePrefix", 8) && ($$6 = Component.Serializer.fromJson($$2.getString("MemberNamePrefix"))) != null) {
                $$4.setPlayerPrefix($$6);
            }
            if ($$2.contains("MemberNameSuffix", 8) && ($$7 = Component.Serializer.fromJson($$2.getString("MemberNameSuffix"))) != null) {
                $$4.setPlayerSuffix($$7);
            }
            if ($$2.contains("NameTagVisibility", 8) && ($$8 = Team.Visibility.byName($$2.getString("NameTagVisibility"))) != null) {
                $$4.setNameTagVisibility($$8);
            }
            if ($$2.contains("DeathMessageVisibility", 8) && ($$9 = Team.Visibility.byName($$2.getString("DeathMessageVisibility"))) != null) {
                $$4.setDeathMessageVisibility($$9);
            }
            if ($$2.contains("CollisionRule", 8) && ($$10 = Team.CollisionRule.byName($$2.getString("CollisionRule"))) != null) {
                $$4.setCollisionRule($$10);
            }
            this.loadTeamPlayers($$4, $$2.getList("Players", 8));
        }
    }

    private void loadTeamPlayers(PlayerTeam $$0, ListTag $$1) {
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            this.scoreboard.addPlayerToTeam($$1.getString($$2), $$0);
        }
    }

    private void loadDisplaySlots(CompoundTag $$0) {
        for (int $$1 = 0; $$1 < 19; ++$$1) {
            if (!$$0.contains("slot_" + $$1, 8)) continue;
            String $$2 = $$0.getString("slot_" + $$1);
            Objective $$3 = this.scoreboard.getObjective($$2);
            this.scoreboard.setDisplayObjective($$1, $$3);
        }
    }

    private void loadObjectives(ListTag $$0) {
        for (int $$12 = 0; $$12 < $$0.size(); ++$$12) {
            CompoundTag $$2 = $$0.getCompound($$12);
            ObjectiveCriteria.byName($$2.getString("CriteriaName")).ifPresent($$1 -> {
                String $$2 = $$2.getString("Name");
                MutableComponent $$3 = Component.Serializer.fromJson($$2.getString("DisplayName"));
                ObjectiveCriteria.RenderType $$4 = ObjectiveCriteria.RenderType.byId($$2.getString("RenderType"));
                this.scoreboard.addObjective($$2, (ObjectiveCriteria)$$1, $$3, $$4);
            });
        }
    }

    @Override
    public CompoundTag save(CompoundTag $$0) {
        $$0.put("Objectives", this.saveObjectives());
        $$0.put("PlayerScores", this.scoreboard.savePlayerScores());
        $$0.put("Teams", this.saveTeams());
        this.saveDisplaySlots($$0);
        return $$0;
    }

    private ListTag saveTeams() {
        ListTag $$0 = new ListTag();
        Collection<PlayerTeam> $$1 = this.scoreboard.getPlayerTeams();
        for (PlayerTeam $$2 : $$1) {
            CompoundTag $$3 = new CompoundTag();
            $$3.putString("Name", $$2.getName());
            $$3.putString("DisplayName", Component.Serializer.toJson($$2.getDisplayName()));
            if ($$2.getColor().getId() >= 0) {
                $$3.putString("TeamColor", $$2.getColor().getName());
            }
            $$3.putBoolean("AllowFriendlyFire", $$2.isAllowFriendlyFire());
            $$3.putBoolean("SeeFriendlyInvisibles", $$2.canSeeFriendlyInvisibles());
            $$3.putString("MemberNamePrefix", Component.Serializer.toJson($$2.getPlayerPrefix()));
            $$3.putString("MemberNameSuffix", Component.Serializer.toJson($$2.getPlayerSuffix()));
            $$3.putString("NameTagVisibility", $$2.getNameTagVisibility().name);
            $$3.putString("DeathMessageVisibility", $$2.getDeathMessageVisibility().name);
            $$3.putString("CollisionRule", $$2.getCollisionRule().name);
            ListTag $$4 = new ListTag();
            for (String $$5 : $$2.getPlayers()) {
                $$4.add(StringTag.valueOf($$5));
            }
            $$3.put("Players", $$4);
            $$0.add($$3);
        }
        return $$0;
    }

    private void saveDisplaySlots(CompoundTag $$0) {
        CompoundTag $$1 = new CompoundTag();
        boolean $$2 = false;
        for (int $$3 = 0; $$3 < 19; ++$$3) {
            Objective $$4 = this.scoreboard.getDisplayObjective($$3);
            if ($$4 == null) continue;
            $$1.putString("slot_" + $$3, $$4.getName());
            $$2 = true;
        }
        if ($$2) {
            $$0.put("DisplaySlots", $$1);
        }
    }

    private ListTag saveObjectives() {
        ListTag $$0 = new ListTag();
        Collection<Objective> $$1 = this.scoreboard.getObjectives();
        for (Objective $$2 : $$1) {
            if ($$2.getCriteria() == null) continue;
            CompoundTag $$3 = new CompoundTag();
            $$3.putString("Name", $$2.getName());
            $$3.putString("CriteriaName", $$2.getCriteria().getName());
            $$3.putString("DisplayName", Component.Serializer.toJson($$2.getDisplayName()));
            $$3.putString("RenderType", $$2.getRenderType().getId());
            $$0.add($$3);
        }
        return $$0;
    }
}