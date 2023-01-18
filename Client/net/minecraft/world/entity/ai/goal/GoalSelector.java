/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumMap
 *  java.util.EnumSet
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.slf4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final WrappedGoal NO_GOAL = new WrappedGoal(Integer.MAX_VALUE, new Goal(){

        @Override
        public boolean canUse() {
            return false;
        }
    }){

        @Override
        public boolean isRunning() {
            return false;
        }
    };
    private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap(Goal.Flag.class);
    private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
    private final Supplier<ProfilerFiller> profiler;
    private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
    private int tickCount;
    private int newGoalRate = 3;

    public GoalSelector(Supplier<ProfilerFiller> $$0) {
        this.profiler = $$0;
    }

    public void addGoal(int $$0, Goal $$1) {
        this.availableGoals.add((Object)new WrappedGoal($$0, $$1));
    }

    @VisibleForTesting
    public void removeAllGoals(Predicate<Goal> $$0) {
        this.availableGoals.removeIf($$1 -> $$0.test((Object)$$1.getGoal()));
    }

    public void removeGoal(Goal $$0) {
        this.availableGoals.stream().filter($$1 -> $$1.getGoal() == $$0).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
        this.availableGoals.removeIf($$1 -> $$1.getGoal() == $$0);
    }

    private static boolean goalContainsAnyFlags(WrappedGoal $$0, EnumSet<Goal.Flag> $$1) {
        for (Goal.Flag $$2 : $$0.getFlags()) {
            if (!$$1.contains((Object)$$2)) continue;
            return true;
        }
        return false;
    }

    private static boolean goalCanBeReplacedForAllFlags(WrappedGoal $$0, Map<Goal.Flag, WrappedGoal> $$1) {
        for (Goal.Flag $$2 : $$0.getFlags()) {
            if (((WrappedGoal)$$1.getOrDefault((Object)$$2, (Object)NO_GOAL)).canBeReplacedBy($$0)) continue;
            return false;
        }
        return true;
    }

    public void tick() {
        ProfilerFiller $$0 = (ProfilerFiller)this.profiler.get();
        $$0.push("goalCleanup");
        for (WrappedGoal $$1 : this.availableGoals) {
            if (!$$1.isRunning() || !GoalSelector.goalContainsAnyFlags($$1, this.disabledFlags) && $$1.canContinueToUse()) continue;
            $$1.stop();
        }
        Iterator $$2 = this.lockedFlags.entrySet().iterator();
        while ($$2.hasNext()) {
            Map.Entry $$3 = (Map.Entry)$$2.next();
            if (((WrappedGoal)$$3.getValue()).isRunning()) continue;
            $$2.remove();
        }
        $$0.pop();
        $$0.push("goalUpdate");
        for (WrappedGoal $$4 : this.availableGoals) {
            if ($$4.isRunning() || GoalSelector.goalContainsAnyFlags($$4, this.disabledFlags) || !GoalSelector.goalCanBeReplacedForAllFlags($$4, this.lockedFlags) || !$$4.canUse()) continue;
            for (Goal.Flag $$5 : $$4.getFlags()) {
                WrappedGoal $$6 = (WrappedGoal)this.lockedFlags.getOrDefault((Object)$$5, (Object)NO_GOAL);
                $$6.stop();
                this.lockedFlags.put((Object)$$5, (Object)$$4);
            }
            $$4.start();
        }
        $$0.pop();
        this.tickRunningGoals(true);
    }

    public void tickRunningGoals(boolean $$0) {
        ProfilerFiller $$1 = (ProfilerFiller)this.profiler.get();
        $$1.push("goalTick");
        for (WrappedGoal $$2 : this.availableGoals) {
            if (!$$2.isRunning() || !$$0 && !$$2.requiresUpdateEveryTick()) continue;
            $$2.tick();
        }
        $$1.pop();
    }

    public Set<WrappedGoal> getAvailableGoals() {
        return this.availableGoals;
    }

    public Stream<WrappedGoal> getRunningGoals() {
        return this.availableGoals.stream().filter(WrappedGoal::isRunning);
    }

    public void setNewGoalRate(int $$0) {
        this.newGoalRate = $$0;
    }

    public void disableControlFlag(Goal.Flag $$0) {
        this.disabledFlags.add((Object)$$0);
    }

    public void enableControlFlag(Goal.Flag $$0) {
        this.disabledFlags.remove((Object)$$0);
    }

    public void setControlFlag(Goal.Flag $$0, boolean $$1) {
        if ($$1) {
            this.enableControlFlag($$0);
        } else {
            this.disableControlFlag($$0);
        }
    }
}