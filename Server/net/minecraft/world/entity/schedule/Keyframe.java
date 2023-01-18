/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity.schedule;

public class Keyframe {
    private final int timeStamp;
    private final float value;

    public Keyframe(int $$0, float $$1) {
        this.timeStamp = $$0;
        this.value = $$1;
    }

    public int getTimeStamp() {
        return this.timeStamp;
    }

    public float getValue() {
        return this.value;
    }
}