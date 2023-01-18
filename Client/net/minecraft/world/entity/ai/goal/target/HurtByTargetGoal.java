/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class HurtByTargetGoal
extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final int ALERT_RANGE_Y = 10;
    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    @Nullable
    private Class<?>[] toIgnoreAlert;

    public HurtByTargetGoal(PathfinderMob $$0, Class<?> ... $$1) {
        super($$0, true);
        this.toIgnoreDamage = $$1;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int $$0 = this.mob.getLastHurtByMobTimestamp();
        LivingEntity $$1 = this.mob.getLastHurtByMob();
        if ($$0 == this.timestamp || $$1 == null) {
            return false;
        }
        if ($$1.getType() == EntityType.PLAYER && this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            return false;
        }
        for (Class<?> $$2 : this.toIgnoreDamage) {
            if (!$$2.isAssignableFrom($$1.getClass())) continue;
            return false;
        }
        return this.canAttack($$1, HURT_BY_TARGETING);
    }

    public HurtByTargetGoal setAlertOthers(Class<?> ... $$0) {
        this.alertSameType = true;
        this.toIgnoreAlert = $$0;
        return this;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        if (this.alertSameType) {
            this.alertOthers();
        }
        super.start();
    }

    protected void alertOthers() {
        double $$0 = this.getFollowDistance();
        AABB $$1 = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate($$0, 10.0, $$0);
        List $$2 = this.mob.level.getEntitiesOfClass(this.mob.getClass(), $$1, EntitySelector.NO_SPECTATORS);
        for (Mob $$3 : $$2) {
            if (this.mob == $$3 || $$3.getTarget() != null || this.mob instanceof TamableAnimal && ((TamableAnimal)this.mob).getOwner() != ((TamableAnimal)$$3).getOwner() || $$3.isAlliedTo(this.mob.getLastHurtByMob())) continue;
            if (this.toIgnoreAlert != null) {
                boolean $$4 = false;
                for (Class<?> $$5 : this.toIgnoreAlert) {
                    if ($$3.getClass() != $$5) continue;
                    $$4 = true;
                    break;
                }
                if ($$4) continue;
            }
            this.alertOther($$3, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob $$0, LivingEntity $$1) {
        $$0.setTarget($$1);
    }
}