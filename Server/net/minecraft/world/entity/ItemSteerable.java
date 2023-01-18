/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface ItemSteerable {
    public boolean boost();

    public void travelWithInput(Vec3 var1);

    public float getSteeringSpeed();

    default public boolean travel(Mob $$0, ItemBasedSteering $$1, Vec3 $$2) {
        if (!$$0.isAlive()) {
            return false;
        }
        Entity $$3 = $$0.getControllingPassenger();
        if (!$$0.isVehicle() || !($$3 instanceof Player)) {
            $$0.maxUpStep = 0.5f;
            $$0.flyingSpeed = 0.02f;
            this.travelWithInput($$2);
            return false;
        }
        $$0.setYRot($$3.getYRot());
        $$0.yRotO = $$0.getYRot();
        $$0.setXRot($$3.getXRot() * 0.5f);
        $$0.setRot($$0.getYRot(), $$0.getXRot());
        $$0.yBodyRot = $$0.getYRot();
        $$0.yHeadRot = $$0.getYRot();
        $$0.maxUpStep = 1.0f;
        $$0.flyingSpeed = $$0.getSpeed() * 0.1f;
        if ($$1.boosting && $$1.boostTime++ > $$1.boostTimeTotal) {
            $$1.boosting = false;
        }
        if ($$0.isControlledByLocalInstance()) {
            float $$4 = this.getSteeringSpeed();
            if ($$1.boosting) {
                $$4 += $$4 * 1.15f * Mth.sin((float)$$1.boostTime / (float)$$1.boostTimeTotal * (float)Math.PI);
            }
            $$0.setSpeed($$4);
            this.travelWithInput(new Vec3(0.0, 0.0, 1.0));
            $$0.lerpSteps = 0;
        } else {
            $$0.calculateEntityAnimation($$0, false);
            $$0.setDeltaMovement(Vec3.ZERO);
        }
        $$0.tryCheckInsideBlocks();
        return true;
    }
}