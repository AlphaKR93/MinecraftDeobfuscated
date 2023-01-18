/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class DefendVillageTargetGoal
extends TargetGoal {
    private final IronGolem golem;
    @Nullable
    private LivingEntity potentialTarget;
    private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);

    public DefendVillageTargetGoal(IronGolem $$0) {
        super($$0, false, true);
        this.golem = $$0;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        AABB $$0 = this.golem.getBoundingBox().inflate(10.0, 8.0, 10.0);
        List $$1 = this.golem.level.getNearbyEntities(Villager.class, this.attackTargeting, this.golem, $$0);
        List $$2 = this.golem.level.getNearbyPlayers(this.attackTargeting, this.golem, $$0);
        for (LivingEntity $$3 : $$1) {
            Villager $$4 = (Villager)$$3;
            for (Player $$5 : $$2) {
                int $$6 = $$4.getPlayerReputation($$5);
                if ($$6 > -100) continue;
                this.potentialTarget = $$5;
            }
        }
        if (this.potentialTarget == null) {
            return false;
        }
        return !(this.potentialTarget instanceof Player) || !this.potentialTarget.isSpectator() && !((Player)this.potentialTarget).isCreative();
    }

    @Override
    public void start() {
        this.golem.setTarget(this.potentialTarget);
        super.start();
    }
}