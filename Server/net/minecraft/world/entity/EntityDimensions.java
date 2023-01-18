/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityDimensions {
    public final float width;
    public final float height;
    public final boolean fixed;

    public EntityDimensions(float $$0, float $$1, boolean $$2) {
        this.width = $$0;
        this.height = $$1;
        this.fixed = $$2;
    }

    public AABB makeBoundingBox(Vec3 $$0) {
        return this.makeBoundingBox($$0.x, $$0.y, $$0.z);
    }

    public AABB makeBoundingBox(double $$0, double $$1, double $$2) {
        float $$3 = this.width / 2.0f;
        float $$4 = this.height;
        return new AABB($$0 - (double)$$3, $$1, $$2 - (double)$$3, $$0 + (double)$$3, $$1 + (double)$$4, $$2 + (double)$$3);
    }

    public EntityDimensions scale(float $$0) {
        return this.scale($$0, $$0);
    }

    public EntityDimensions scale(float $$0, float $$1) {
        if (this.fixed || $$0 == 1.0f && $$1 == 1.0f) {
            return this;
        }
        return EntityDimensions.scalable(this.width * $$0, this.height * $$1);
    }

    public static EntityDimensions scalable(float $$0, float $$1) {
        return new EntityDimensions($$0, $$1, false);
    }

    public static EntityDimensions fixed(float $$0, float $$1) {
        return new EntityDimensions($$0, $$1, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}