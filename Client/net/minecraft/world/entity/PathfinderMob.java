/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class PathfinderMob
extends Mob {
    protected static final float DEFAULT_WALK_TARGET_VALUE = 0.0f;

    protected PathfinderMob(EntityType<? extends PathfinderMob> $$0, Level $$1) {
        super((EntityType<? extends Mob>)$$0, $$1);
    }

    public float getWalkTargetValue(BlockPos $$0) {
        return this.getWalkTargetValue($$0, this.level);
    }

    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        return 0.0f;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor $$0, MobSpawnType $$1) {
        return this.getWalkTargetValue(this.blockPosition(), $$0) >= 0.0f;
    }

    public boolean isPathFinding() {
        return !this.getNavigation().isDone();
    }

    @Override
    protected void tickLeash() {
        super.tickLeash();
        Entity $$0 = this.getLeashHolder();
        if ($$0 != null && $$0.level == this.level) {
            this.restrictTo($$0.blockPosition(), 5);
            float $$1 = this.distanceTo($$0);
            if (this instanceof TamableAnimal && ((TamableAnimal)this).isInSittingPose()) {
                if ($$1 > 10.0f) {
                    this.dropLeash(true, true);
                }
                return;
            }
            this.onLeashDistance($$1);
            if ($$1 > 10.0f) {
                this.dropLeash(true, true);
                this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
            } else if ($$1 > 6.0f) {
                double $$2 = ($$0.getX() - this.getX()) / (double)$$1;
                double $$3 = ($$0.getY() - this.getY()) / (double)$$1;
                double $$4 = ($$0.getZ() - this.getZ()) / (double)$$1;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign((double)($$2 * $$2 * 0.4), (double)$$2), Math.copySign((double)($$3 * $$3 * 0.4), (double)$$3), Math.copySign((double)($$4 * $$4 * 0.4), (double)$$4)));
                this.checkSlowFallDistance();
            } else if (this.shouldStayCloseToLeashHolder()) {
                this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                float $$5 = 2.0f;
                Vec3 $$6 = new Vec3($$0.getX() - this.getX(), $$0.getY() - this.getY(), $$0.getZ() - this.getZ()).normalize().scale(Math.max((float)($$1 - 2.0f), (float)0.0f));
                this.getNavigation().moveTo(this.getX() + $$6.x, this.getY() + $$6.y, this.getZ() + $$6.z, this.followLeashSpeed());
            }
        }
    }

    protected boolean shouldStayCloseToLeashHolder() {
        return true;
    }

    protected double followLeashSpeed() {
        return 1.0;
    }

    protected void onLeashDistance(float $$0) {
    }
}