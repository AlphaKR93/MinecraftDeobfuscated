/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 */
package net.minecraft.world.entity.ai.goal;

import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;

public class FollowFlockLeaderGoal
extends Goal {
    private static final int INTERVAL_TICKS = 200;
    private final AbstractSchoolingFish mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public FollowFlockLeaderGoal(AbstractSchoolingFish $$0) {
        this.mob = $$0;
        this.nextStartTick = this.nextStartTick($$0);
    }

    protected int nextStartTick(AbstractSchoolingFish $$0) {
        return FollowFlockLeaderGoal.reducedTickDelay(200 + $$0.getRandom().nextInt(200) % 20);
    }

    @Override
    public boolean canUse() {
        if (this.mob.hasFollowers()) {
            return false;
        }
        if (this.mob.isFollower()) {
            return true;
        }
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        }
        this.nextStartTick = this.nextStartTick(this.mob);
        Predicate $$02 = $$0 -> $$0.canBeFollowed() || !$$0.isFollower();
        List $$1 = this.mob.level.getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0), $$02);
        AbstractSchoolingFish $$2 = (AbstractSchoolingFish)DataFixUtils.orElse((Optional)$$1.stream().filter(AbstractSchoolingFish::canBeFollowed).findAny(), (Object)this.mob);
        $$2.addFollowers((Stream<? extends AbstractSchoolingFish>)$$1.stream().filter($$0 -> !$$0.isFollower()));
        return this.mob.isFollower();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isFollower() && this.mob.inRangeOfLeader();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.mob.stopFollowing();
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = this.adjustedTickDelay(10);
        this.mob.pathToLeader();
    }
}