/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.client.model.geom.builders;

public class UVPair {
    private final float u;
    private final float v;

    public UVPair(float $$0, float $$1) {
        this.u = $$0;
        this.v = $$1;
    }

    public float u() {
        return this.u;
    }

    public float v() {
        return this.v;
    }

    public String toString() {
        return "(" + this.u + "," + this.v + ")";
    }
}