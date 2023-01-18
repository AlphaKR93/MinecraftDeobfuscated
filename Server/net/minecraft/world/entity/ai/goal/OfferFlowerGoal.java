/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;

public class OfferFlowerGoal
extends Goal {
    private static final TargetingConditions OFFER_TARGER_CONTEXT = TargetingConditions.forNonCombat().range(6.0);
    public static final int OFFER_TICKS = 400;
    private final IronGolem golem;
    private Villager villager;
    private int tick;

    public OfferFlowerGoal(IronGolem $$0) {
        this.golem = $$0;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.golem.level.isDay()) {
            return false;
        }
        if (this.golem.getRandom().nextInt(8000) != 0) {
            return false;
        }
        this.villager = (Villager)this.golem.level.getNearestEntity(Villager.class, OFFER_TARGER_CONTEXT, this.golem, this.golem.getX(), this.golem.getY(), this.golem.getZ(), this.golem.getBoundingBox().inflate(6.0, 2.0, 6.0));
        return this.villager != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.tick > 0;
    }

    @Override
    public void start() {
        this.tick = this.adjustedTickDelay(400);
        this.golem.offerFlower(true);
    }

    @Override
    public void stop() {
        this.golem.offerFlower(false);
        this.villager = null;
    }

    @Override
    public void tick() {
        this.golem.getLookControl().setLookAt(this.villager, 30.0f, 30.0f);
        --this.tick;
    }
}