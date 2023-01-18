/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Object
 */
package net.minecraft.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;

public class ItemBasedSteering {
    private static final int MIN_BOOST_TIME = 140;
    private static final int MAX_BOOST_TIME = 700;
    private final SynchedEntityData entityData;
    private final EntityDataAccessor<Integer> boostTimeAccessor;
    private final EntityDataAccessor<Boolean> hasSaddleAccessor;
    public boolean boosting;
    public int boostTime;
    public int boostTimeTotal;

    public ItemBasedSteering(SynchedEntityData $$0, EntityDataAccessor<Integer> $$1, EntityDataAccessor<Boolean> $$2) {
        this.entityData = $$0;
        this.boostTimeAccessor = $$1;
        this.hasSaddleAccessor = $$2;
    }

    public void onSynced() {
        this.boosting = true;
        this.boostTime = 0;
        this.boostTimeTotal = this.entityData.get(this.boostTimeAccessor);
    }

    public boolean boost(RandomSource $$0) {
        if (this.boosting) {
            return false;
        }
        this.boosting = true;
        this.boostTime = 0;
        this.boostTimeTotal = $$0.nextInt(841) + 140;
        this.entityData.set(this.boostTimeAccessor, this.boostTimeTotal);
        return true;
    }

    public void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putBoolean("Saddle", this.hasSaddle());
    }

    public void readAdditionalSaveData(CompoundTag $$0) {
        this.setSaddle($$0.getBoolean("Saddle"));
    }

    public void setSaddle(boolean $$0) {
        this.entityData.set(this.hasSaddleAccessor, $$0);
    }

    public boolean hasSaddle() {
        return this.entityData.get(this.hasSaddleAccessor);
    }
}