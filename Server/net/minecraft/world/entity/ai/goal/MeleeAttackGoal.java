/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class MeleeAttackGoal
extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval = 20;
    private long lastCanUseCheck;
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

    public MeleeAttackGoal(PathfinderMob $$0, double $$1, boolean $$2) {
        this.mob = $$0;
        this.speedModifier = $$1;
        this.followingTargetEvenIfNotSeen = $$2;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long $$0 = this.mob.level.getGameTime();
        if ($$0 - this.lastCanUseCheck < 20L) {
            return false;
        }
        this.lastCanUseCheck = $$0;
        LivingEntity $$1 = this.mob.getTarget();
        if ($$1 == null) {
            return false;
        }
        if (!$$1.isAlive()) {
            return false;
        }
        this.path = this.mob.getNavigation().createPath($$1, 0);
        if (this.path != null) {
            return true;
        }
        return this.getAttackReachSqr($$1) >= this.mob.distanceToSqr($$1.getX(), $$1.getY(), $$1.getZ());
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity $$0 = this.mob.getTarget();
        if ($$0 == null) {
            return false;
        }
        if (!$$0.isAlive()) {
            return false;
        }
        if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        }
        if (!this.mob.isWithinRestriction($$0.blockPosition())) {
            return false;
        }
        return !($$0 instanceof Player) || !$$0.isSpectator() && !((Player)$$0).isCreative();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        LivingEntity $$0 = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test((Object)$$0)) {
            this.mob.setTarget(null);
        }
        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity $$0 = this.mob.getTarget();
        if ($$0 == null) {
            return;
        }
        this.mob.getLookControl().setLookAt($$0, 30.0f, 30.0f);
        double $$1 = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack($$0);
        this.ticksUntilNextPathRecalculation = Math.max((int)(this.ticksUntilNextPathRecalculation - 1), (int)0);
        if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight($$0)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || $$0.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f)) {
            this.pathedTargetX = $$0.getX();
            this.pathedTargetY = $$0.getY();
            this.pathedTargetZ = $$0.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
            if ($$1 > 1024.0) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if ($$1 > 256.0) {
                this.ticksUntilNextPathRecalculation += 5;
            }
            if (!this.mob.getNavigation().moveTo($$0, this.speedModifier)) {
                this.ticksUntilNextPathRecalculation += 15;
            }
            this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
        }
        this.ticksUntilNextAttack = Math.max((int)(this.ticksUntilNextAttack - 1), (int)0);
        this.checkAndPerformAttack($$0, $$1);
    }

    protected void checkAndPerformAttack(LivingEntity $$0, double $$1) {
        double $$2 = this.getAttackReachSqr($$0);
        if ($$1 <= $$2 && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget($$0);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(20);
    }

    protected double getAttackReachSqr(LivingEntity $$0) {
        return this.mob.getBbWidth() * 2.0f * (this.mob.getBbWidth() * 2.0f) + $$0.getBbWidth();
    }
}