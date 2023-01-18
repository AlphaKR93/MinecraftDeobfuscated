/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.model.geom;

public class PartPose {
    public static final PartPose ZERO = PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    public final float x;
    public final float y;
    public final float z;
    public final float xRot;
    public final float yRot;
    public final float zRot;

    private PartPose(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.xRot = $$3;
        this.yRot = $$4;
        this.zRot = $$5;
    }

    public static PartPose offset(float $$0, float $$1, float $$2) {
        return PartPose.offsetAndRotation($$0, $$1, $$2, 0.0f, 0.0f, 0.0f);
    }

    public static PartPose rotation(float $$0, float $$1, float $$2) {
        return PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, $$0, $$1, $$2);
    }

    public static PartPose offsetAndRotation(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        return new PartPose($$0, $$1, $$2, $$3, $$4, $$5);
    }
}