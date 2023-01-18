/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Comparator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.scores;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class Score {
    public static final Comparator<Score> SCORE_COMPARATOR = ($$0, $$1) -> {
        if ($$0.getScore() > $$1.getScore()) {
            return 1;
        }
        if ($$0.getScore() < $$1.getScore()) {
            return -1;
        }
        return $$1.getOwner().compareToIgnoreCase($$0.getOwner());
    };
    private final Scoreboard scoreboard;
    @Nullable
    private final Objective objective;
    private final String owner;
    private int count;
    private boolean locked;
    private boolean forceUpdate;

    public Score(Scoreboard $$0, Objective $$1, String $$2) {
        this.scoreboard = $$0;
        this.objective = $$1;
        this.owner = $$2;
        this.locked = true;
        this.forceUpdate = true;
    }

    public void add(int $$0) {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        this.setScore(this.getScore() + $$0);
    }

    public void increment() {
        this.add(1);
    }

    public int getScore() {
        return this.count;
    }

    public void reset() {
        this.setScore(0);
    }

    public void setScore(int $$0) {
        int $$1 = this.count;
        this.count = $$0;
        if ($$1 != $$0 || this.forceUpdate) {
            this.forceUpdate = false;
            this.getScoreboard().onScoreChanged(this);
        }
    }

    @Nullable
    public Objective getObjective() {
        return this.objective;
    }

    public String getOwner() {
        return this.owner;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean $$0) {
        this.locked = $$0;
    }
}