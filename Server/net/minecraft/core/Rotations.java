/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;

public class Rotations {
    protected final float x;
    protected final float y;
    protected final float z;

    public Rotations(float $$0, float $$1, float $$2) {
        this.x = Float.isInfinite((float)$$0) || Float.isNaN((float)$$0) ? 0.0f : $$0 % 360.0f;
        this.y = Float.isInfinite((float)$$1) || Float.isNaN((float)$$1) ? 0.0f : $$1 % 360.0f;
        this.z = Float.isInfinite((float)$$2) || Float.isNaN((float)$$2) ? 0.0f : $$2 % 360.0f;
    }

    public Rotations(ListTag $$0) {
        this($$0.getFloat(0), $$0.getFloat(1), $$0.getFloat(2));
    }

    public ListTag save() {
        ListTag $$0 = new ListTag();
        $$0.add(FloatTag.valueOf(this.x));
        $$0.add(FloatTag.valueOf(this.y));
        $$0.add(FloatTag.valueOf(this.z));
        return $$0;
    }

    public boolean equals(Object $$0) {
        if (!($$0 instanceof Rotations)) {
            return false;
        }
        Rotations $$1 = (Rotations)$$0;
        return this.x == $$1.x && this.y == $$1.y && this.z == $$1.z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getWrappedX() {
        return Mth.wrapDegrees(this.x);
    }

    public float getWrappedY() {
        return Mth.wrapDegrees(this.y);
    }

    public float getWrappedZ() {
        return Mth.wrapDegrees(this.z);
    }
}