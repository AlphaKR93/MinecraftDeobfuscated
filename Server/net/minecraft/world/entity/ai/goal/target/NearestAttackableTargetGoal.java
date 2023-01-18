/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class NearestAttackableTargetGoal<T extends LivingEntity>
extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final Class<T> targetType;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;

    public NearestAttackableTargetGoal(Mob $$0, Class<T> $$1, boolean $$2) {
        this($$0, $$1, 10, $$2, false, null);
    }

    public NearestAttackableTargetGoal(Mob $$0, Class<T> $$1, boolean $$2, Predicate<LivingEntity> $$3) {
        this($$0, $$1, 10, $$2, false, $$3);
    }

    public NearestAttackableTargetGoal(Mob $$0, Class<T> $$1, boolean $$2, boolean $$3) {
        this($$0, $$1, 10, $$2, $$3, null);
    }

    public NearestAttackableTargetGoal(Mob $$0, Class<T> $$1, int $$2, boolean $$3, boolean $$4, @Nullable Predicate<LivingEntity> $$5) {
        super($$0, $$3, $$4);
        this.targetType = $$1;
        this.randomInterval = NearestAttackableTargetGoal.reducedTickDelay($$2);
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector($$5);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }
        this.findTarget();
        return this.target != null;
    }

    protected AABB getTargetSearchArea(double $$0) {
        return this.mob.getBoundingBox().inflate($$0, 4.0, $$0);
    }

    protected void findTarget() {
        this.target = this.targetType == Player.class || this.targetType == ServerPlayer.class ? this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), $$0 -> true), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity $$0) {
        this.target = $$0;
    }
}