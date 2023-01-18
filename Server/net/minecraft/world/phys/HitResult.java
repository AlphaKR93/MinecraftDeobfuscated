/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class HitResult {
    protected final Vec3 location;

    protected HitResult(Vec3 $$0) {
        this.location = $$0;
    }

    public double distanceTo(Entity $$0) {
        double $$1 = this.location.x - $$0.getX();
        double $$2 = this.location.y - $$0.getY();
        double $$3 = this.location.z - $$0.getZ();
        return $$1 * $$1 + $$2 * $$2 + $$3 * $$3;
    }

    public abstract Type getType();

    public Vec3 getLocation() {
        return this.location;
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY;

    }
}