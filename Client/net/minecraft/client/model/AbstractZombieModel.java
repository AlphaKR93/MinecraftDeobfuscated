/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Monster;

public abstract class AbstractZombieModel<T extends Monster>
extends HumanoidModel<T> {
    protected AbstractZombieModel(ModelPart $$0) {
        super($$0);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive($$0), this.attackTime, $$3);
    }

    public abstract boolean isAggressive(T var1);
}