/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class AgeableMob
extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(AgeableMob.class, EntityDataSerializers.BOOLEAN);
    public static final int BABY_START_AGE = -24000;
    private static final int FORCED_AGE_PARTICLE_TICKS = 40;
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;

    protected AgeableMob(EntityType<? extends AgeableMob> $$0, Level $$1) {
        super((EntityType<? extends PathfinderMob>)$$0, $$1);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        AgeableMobGroupData $$5;
        if ($$3 == null) {
            $$3 = new AgeableMobGroupData(true);
        }
        if (($$5 = (AgeableMobGroupData)$$3).isShouldSpawnBaby() && $$5.getGroupSize() > 0 && $$0.getRandom().nextFloat() <= $$5.getBabySpawnChance()) {
            this.setAge(-24000);
        }
        $$5.increaseGroupSizeByOne();
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Nullable
    public abstract AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BABY_ID, false);
    }

    public boolean canBreed() {
        return false;
    }

    public int getAge() {
        if (this.level.isClientSide) {
            return this.entityData.get(DATA_BABY_ID) != false ? -1 : 1;
        }
        return this.age;
    }

    public void ageUp(int $$0, boolean $$1) {
        int $$2;
        int $$3 = $$2 = this.getAge();
        if (($$2 += $$0 * 20) > 0) {
            $$2 = 0;
        }
        int $$4 = $$2 - $$3;
        this.setAge($$2);
        if ($$1) {
            this.forcedAge += $$4;
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }
        if (this.getAge() == 0) {
            this.setAge(this.forcedAge);
        }
    }

    public void ageUp(int $$0) {
        this.ageUp($$0, false);
    }

    public void setAge(int $$0) {
        int $$1 = this.getAge();
        this.age = $$0;
        if ($$1 < 0 && $$0 >= 0 || $$1 >= 0 && $$0 < 0) {
            this.entityData.set(DATA_BABY_ID, $$0 < 0);
            this.ageBoundaryReached();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Age", this.getAge());
        $$0.putInt("ForcedAge", this.forcedAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setAge($$0.getInt("Age"));
        this.forcedAge = $$0.getInt("ForcedAge");
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_BABY_ID.equals($$0)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
                }
                --this.forcedAgeTimer;
            }
        } else if (this.isAlive()) {
            int $$0 = this.getAge();
            if ($$0 < 0) {
                this.setAge(++$$0);
            } else if ($$0 > 0) {
                this.setAge(--$$0);
            }
        }
    }

    protected void ageBoundaryReached() {
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public void setBaby(boolean $$0) {
        this.setAge($$0 ? -24000 : 0);
    }

    public static int getSpeedUpSecondsWhenFeeding(int $$0) {
        return (int)((float)($$0 / 20) * 0.1f);
    }

    public static class AgeableMobGroupData
    implements SpawnGroupData {
        private int groupSize;
        private final boolean shouldSpawnBaby;
        private final float babySpawnChance;

        private AgeableMobGroupData(boolean $$0, float $$1) {
            this.shouldSpawnBaby = $$0;
            this.babySpawnChance = $$1;
        }

        public AgeableMobGroupData(boolean $$0) {
            this($$0, 0.05f);
        }

        public AgeableMobGroupData(float $$0) {
            this(true, $$0);
        }

        public int getGroupSize() {
            return this.groupSize;
        }

        public void increaseGroupSizeByOne() {
            ++this.groupSize;
        }

        public boolean isShouldSpawnBaby() {
            return this.shouldSpawnBaby;
        }

        public float getBabySpawnChance() {
            return this.babySpawnChance;
        }
    }
}