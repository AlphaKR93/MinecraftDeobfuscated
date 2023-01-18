/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SheetedDecalTextureGenerator
extends DefaultedVertexConsumer {
    private final VertexConsumer delegate;
    private final Matrix4f cameraInversePose;
    private final Matrix3f normalInversePose;
    private final float textureScale;
    private float x;
    private float y;
    private float z;
    private int overlayU;
    private int overlayV;
    private int lightCoords;
    private float nx;
    private float ny;
    private float nz;

    public SheetedDecalTextureGenerator(VertexConsumer $$0, Matrix4f $$1, Matrix3f $$2, float $$3) {
        this.delegate = $$0;
        this.cameraInversePose = new Matrix4f((Matrix4fc)$$1).invert();
        this.normalInversePose = new Matrix3f((Matrix3fc)$$2).invert();
        this.textureScale = $$3;
        this.resetState();
    }

    private void resetState() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.overlayU = 0;
        this.overlayV = 10;
        this.lightCoords = 0xF000F0;
        this.nx = 0.0f;
        this.ny = 1.0f;
        this.nz = 0.0f;
    }

    @Override
    public void endVertex() {
        Vector3f $$0 = this.normalInversePose.transform(new Vector3f(this.nx, this.ny, this.nz));
        Direction $$1 = Direction.getNearest($$0.x(), $$0.y(), $$0.z());
        Vector4f $$2 = this.cameraInversePose.transform(new Vector4f(this.x, this.y, this.z, 1.0f));
        $$2.rotateY((float)Math.PI);
        $$2.rotateX(-1.5707964f);
        $$2.rotate((Quaternionfc)$$1.getRotation());
        float $$3 = -$$2.x() * this.textureScale;
        float $$4 = -$$2.y() * this.textureScale;
        this.delegate.vertex(this.x, this.y, this.z).color(1.0f, 1.0f, 1.0f, 1.0f).uv($$3, $$4).overlayCoords(this.overlayU, this.overlayV).uv2(this.lightCoords).normal(this.nx, this.ny, this.nz).endVertex();
        this.resetState();
    }

    @Override
    public VertexConsumer vertex(double $$0, double $$1, double $$2) {
        this.x = (float)$$0;
        this.y = (float)$$1;
        this.z = (float)$$2;
        return this;
    }

    @Override
    public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
        return this;
    }

    @Override
    public VertexConsumer uv(float $$0, float $$1) {
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int $$0, int $$1) {
        this.overlayU = $$0;
        this.overlayV = $$1;
        return this;
    }

    @Override
    public VertexConsumer uv2(int $$0, int $$1) {
        this.lightCoords = $$0 | $$1 << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float $$0, float $$1, float $$2) {
        this.nx = $$0;
        this.ny = $$1;
        this.nz = $$2;
        return this;
    }
}