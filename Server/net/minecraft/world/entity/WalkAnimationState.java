/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity;

import net.minecraft.util.Mth;

public class WalkAnimationState {
    private float speedOld;
    private float speed;
    private float position;

    public void setSpeed(float $$0) {
        this.speed = $$0;
    }

    public void update(float $$0, float $$1) {
        this.speedOld = this.speed;
        this.speed += ($$0 - this.speed) * $$1;
        this.position = this.isMoving() ? (this.position += this.speed) : 0.0f;
    }

    public float speed() {
        return this.speed;
    }

    public float speed(float $$0) {
        return Mth.lerp($$0, this.speedOld, this.speed);
    }

    public float position() {
        return this.position;
    }

    public float position(float $$0) {
        return this.position - this.speed * (1.0f - $$0);
    }

    public boolean isMoving() {
        return this.speed > 1.0E-5f;
    }
}