/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  org.joml.FrustumIntersection
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class Frustum {
    public static final int OFFSET_STEP = 4;
    private final FrustumIntersection intersection = new FrustumIntersection();
    private final Matrix4f matrix = new Matrix4f();
    private Vector4f viewVector;
    private double camX;
    private double camY;
    private double camZ;

    public Frustum(Matrix4f $$0, Matrix4f $$1) {
        this.calculateFrustum($$0, $$1);
    }

    public Frustum(Frustum $$0) {
        this.intersection.set((Matrix4fc)$$0.matrix);
        this.matrix.set((Matrix4fc)$$0.matrix);
        this.camX = $$0.camX;
        this.camY = $$0.camY;
        this.camZ = $$0.camZ;
        this.viewVector = $$0.viewVector;
    }

    public Frustum offsetToFullyIncludeCameraCube(int $$0) {
        double $$1 = Math.floor((double)(this.camX / (double)$$0)) * (double)$$0;
        double $$2 = Math.floor((double)(this.camY / (double)$$0)) * (double)$$0;
        double $$3 = Math.floor((double)(this.camZ / (double)$$0)) * (double)$$0;
        double $$4 = Math.ceil((double)(this.camX / (double)$$0)) * (double)$$0;
        double $$5 = Math.ceil((double)(this.camY / (double)$$0)) * (double)$$0;
        double $$6 = Math.ceil((double)(this.camZ / (double)$$0)) * (double)$$0;
        while (this.intersection.intersectAab((float)($$1 - this.camX), (float)($$2 - this.camY), (float)($$3 - this.camZ), (float)($$4 - this.camX), (float)($$5 - this.camY), (float)($$6 - this.camZ)) != -2) {
            this.camX -= (double)(this.viewVector.x() * 4.0f);
            this.camY -= (double)(this.viewVector.y() * 4.0f);
            this.camZ -= (double)(this.viewVector.z() * 4.0f);
        }
        return this;
    }

    public void prepare(double $$0, double $$1, double $$2) {
        this.camX = $$0;
        this.camY = $$1;
        this.camZ = $$2;
    }

    private void calculateFrustum(Matrix4f $$0, Matrix4f $$1) {
        $$1.mul((Matrix4fc)$$0, this.matrix);
        this.intersection.set((Matrix4fc)this.matrix);
        this.viewVector = this.matrix.transformTranspose(new Vector4f(0.0f, 0.0f, 1.0f, 0.0f));
    }

    public boolean isVisible(AABB $$0) {
        return this.cubeInFrustum($$0.minX, $$0.minY, $$0.minZ, $$0.maxX, $$0.maxY, $$0.maxZ);
    }

    private boolean cubeInFrustum(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        float $$6 = (float)($$0 - this.camX);
        float $$7 = (float)($$1 - this.camY);
        float $$8 = (float)($$2 - this.camZ);
        float $$9 = (float)($$3 - this.camX);
        float $$10 = (float)($$4 - this.camY);
        float $$11 = (float)($$5 - this.camZ);
        return this.intersection.testAab($$6, $$7, $$8, $$9, $$10, $$11);
    }
}