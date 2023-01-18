/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class PanicGoal
extends Goal {
    public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
    protected final PathfinderMob mob;
    protected final double speedModifier;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean isRunning;

    public PanicGoal(PathfinderMob $$0, double $$1) {
        this.mob = $$0;
        this.speedModifier = $$1;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        BlockPos $$0;
        if (!this.shouldPanic()) {
            return false;
        }
        if (this.mob.isOnFire() && ($$0 = this.lookForWater(this.mob.level, this.mob, 5)) != null) {
            this.posX = $$0.getX();
            this.posY = $$0.getY();
            this.posZ = $$0.getZ();
            return true;
        }
        return this.findRandomPosition();
    }

    protected boolean shouldPanic() {
        return this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
    }

    protected boolean findRandomPosition() {
        Vec3 $$0 = DefaultRandomPos.getPos(this.mob, 5, 4);
        if ($$0 == null) {
            return false;
        }
        this.posX = $$0.x;
        this.posY = $$0.y;
        this.posZ = $$0.z;
        return true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Nullable
    protected BlockPos lookForWater(BlockGetter $$0, Entity $$12, int $$2) {
        BlockPos $$3 = $$12.blockPosition();
        if (!$$0.getBlockState($$3).getCollisionShape($$0, $$3).isEmpty()) {
            return null;
        }
        return (BlockPos)BlockPos.findClosestMatch($$12.blockPosition(), $$2, 1, (Predicate<BlockPos>)((Predicate)$$1 -> $$0.getFluidState((BlockPos)$$1).is(FluidTags.WATER))).orElse(null);
    }
}