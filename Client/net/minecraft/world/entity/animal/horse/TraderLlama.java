/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TraderLlama
extends Llama {
    private int despawnDelay = 47999;

    public TraderLlama(EntityType<? extends TraderLlama> $$0, Level $$1) {
        super((EntityType<? extends Llama>)$$0, $$1);
    }

    @Override
    public boolean isTraderLlama() {
        return true;
    }

    @Override
    @Nullable
    protected Llama makeNewLlama() {
        return EntityType.TRADER_LLAMA.create(this.level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("DespawnDelay", 99)) {
            this.despawnDelay = $$0.getInt("DespawnDelay");
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.targetSelector.addGoal(1, new TraderLlamaDefendWanderingTraderGoal(this));
    }

    public void setDespawnDelay(int $$0) {
        this.despawnDelay = $$0;
    }

    @Override
    protected void doPlayerRide(Player $$0) {
        Entity $$1 = this.getLeashHolder();
        if ($$1 instanceof WanderingTrader) {
            return;
        }
        super.doPlayerRide($$0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.maybeDespawn();
        }
    }

    private void maybeDespawn() {
        if (!this.canDespawn()) {
            return;
        }
        int n = this.despawnDelay = this.isLeashedToWanderingTrader() ? ((WanderingTrader)this.getLeashHolder()).getDespawnDelay() - 1 : this.despawnDelay - 1;
        if (this.despawnDelay <= 0) {
            this.dropLeash(true, false);
            this.discard();
        }
    }

    private boolean canDespawn() {
        return !this.isTamed() && !this.isLeashedToSomethingOtherThanTheWanderingTrader() && !this.hasExactlyOnePlayerPassenger();
    }

    private boolean isLeashedToWanderingTrader() {
        return this.getLeashHolder() instanceof WanderingTrader;
    }

    private boolean isLeashedToSomethingOtherThanTheWanderingTrader() {
        return this.isLeashed() && !this.isLeashedToWanderingTrader();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        if ($$2 == MobSpawnType.EVENT) {
            this.setAge(0);
        }
        if ($$3 == null) {
            $$3 = new AgeableMob.AgeableMobGroupData(false);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    protected static class TraderLlamaDefendWanderingTraderGoal
    extends TargetGoal {
        private final Llama llama;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;

        public TraderLlamaDefendWanderingTraderGoal(Llama $$0) {
            super($$0, false);
            this.llama = $$0;
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!this.llama.isLeashed()) {
                return false;
            }
            Entity $$0 = this.llama.getLeashHolder();
            if (!($$0 instanceof WanderingTrader)) {
                return false;
            }
            WanderingTrader $$1 = (WanderingTrader)$$0;
            this.ownerLastHurtBy = $$1.getLastHurtByMob();
            int $$2 = $$1.getLastHurtByMobTimestamp();
            return $$2 != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.mob.setTarget(this.ownerLastHurtBy);
            Entity $$0 = this.llama.getLeashHolder();
            if ($$0 instanceof WanderingTrader) {
                this.timestamp = ((WanderingTrader)$$0).getLastHurtByMobTimestamp();
            }
            super.start();
        }
    }
}