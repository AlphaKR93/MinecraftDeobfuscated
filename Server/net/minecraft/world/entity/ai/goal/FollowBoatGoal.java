/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.BoatGoals;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class FollowBoatGoal
extends Goal {
    private int timeToRecalcPath;
    private final PathfinderMob mob;
    @Nullable
    private Player following;
    private BoatGoals currentGoal;

    public FollowBoatGoal(PathfinderMob $$0) {
        this.mob = $$0;
    }

    @Override
    public boolean canUse() {
        List $$0 = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0));
        boolean $$1 = false;
        for (Boat $$2 : $$0) {
            Entity $$3 = $$2.getControllingPassenger();
            if (!($$3 instanceof Player) || !(Mth.abs(((Player)$$3).xxa) > 0.0f) && !(Mth.abs(((Player)$$3).zza) > 0.0f)) continue;
            $$1 = true;
            break;
        }
        return this.following != null && (Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f) || $$1;
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.following != null && this.following.isPassenger() && (Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f);
    }

    @Override
    public void start() {
        List $$0 = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0));
        for (Boat $$1 : $$0) {
            if ($$1.getControllingPassenger() == null || !($$1.getControllingPassenger() instanceof Player)) continue;
            this.following = (Player)$$1.getControllingPassenger();
            break;
        }
        this.timeToRecalcPath = 0;
        this.currentGoal = BoatGoals.GO_TO_BOAT;
    }

    @Override
    public void stop() {
        this.following = null;
    }

    @Override
    public void tick() {
        boolean $$0;
        boolean bl = $$0 = Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f;
        float $$1 = this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION ? ($$0 ? 0.01f : 0.0f) : 0.015f;
        this.mob.moveRelative($$1, new Vec3(this.mob.xxa, this.mob.yya, this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = this.adjustedTickDelay(10);
        if (this.currentGoal == BoatGoals.GO_TO_BOAT) {
            Vec3i $$2 = this.following.blockPosition().relative(this.following.getDirection().getOpposite());
            $$2 = ((BlockPos)$$2).offset(0, -1, 0);
            this.mob.getNavigation().moveTo($$2.getX(), $$2.getY(), $$2.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) < 4.0f) {
                this.timeToRecalcPath = 0;
                this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
        } else if (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction $$3 = this.following.getMotionDirection();
            BlockPos $$4 = this.following.blockPosition().relative($$3, 10);
            this.mob.getNavigation().moveTo($$4.getX(), $$4.getY() - 1, $$4.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) > 12.0f) {
                this.timeToRecalcPath = 0;
                this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
        }
    }
}