/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetingConditions {
    public static final TargetingConditions DEFAULT = TargetingConditions.forCombat();
    private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0;
    private final boolean isCombat;
    private double range = -1.0;
    private boolean checkLineOfSight = true;
    private boolean testInvisible = true;
    @Nullable
    private Predicate<LivingEntity> selector;

    private TargetingConditions(boolean $$0) {
        this.isCombat = $$0;
    }

    public static TargetingConditions forCombat() {
        return new TargetingConditions(true);
    }

    public static TargetingConditions forNonCombat() {
        return new TargetingConditions(false);
    }

    public TargetingConditions copy() {
        TargetingConditions $$0 = this.isCombat ? TargetingConditions.forCombat() : TargetingConditions.forNonCombat();
        $$0.range = this.range;
        $$0.checkLineOfSight = this.checkLineOfSight;
        $$0.testInvisible = this.testInvisible;
        $$0.selector = this.selector;
        return $$0;
    }

    public TargetingConditions range(double $$0) {
        this.range = $$0;
        return this;
    }

    public TargetingConditions ignoreLineOfSight() {
        this.checkLineOfSight = false;
        return this;
    }

    public TargetingConditions ignoreInvisibilityTesting() {
        this.testInvisible = false;
        return this;
    }

    public TargetingConditions selector(@Nullable Predicate<LivingEntity> $$0) {
        this.selector = $$0;
        return this;
    }

    public boolean test(@Nullable LivingEntity $$0, LivingEntity $$1) {
        if ($$0 == $$1) {
            return false;
        }
        if (!$$1.canBeSeenByAnyone()) {
            return false;
        }
        if (this.selector != null && !this.selector.test((Object)$$1)) {
            return false;
        }
        if ($$0 == null) {
            if (this.isCombat && (!$$1.canBeSeenAsEnemy() || $$1.level.getDifficulty() == Difficulty.PEACEFUL)) {
                return false;
            }
        } else {
            Mob $$5;
            if (this.isCombat && (!$$0.canAttack($$1) || !$$0.canAttackType($$1.getType()) || $$0.isAlliedTo($$1))) {
                return false;
            }
            if (this.range > 0.0) {
                double $$2 = this.testInvisible ? $$1.getVisibilityPercent($$0) : 1.0;
                double $$3 = Math.max((double)(this.range * $$2), (double)2.0);
                double $$4 = $$0.distanceToSqr($$1.getX(), $$1.getY(), $$1.getZ());
                if ($$4 > $$3 * $$3) {
                    return false;
                }
            }
            if (this.checkLineOfSight && $$0 instanceof Mob && !($$5 = (Mob)$$0).getSensing().hasLineOfSight($$1)) {
                return false;
            }
        }
        return true;
    }
}