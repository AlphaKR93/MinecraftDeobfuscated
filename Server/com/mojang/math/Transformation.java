/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Objects
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Matrix4x3f
 *  org.joml.Matrix4x3fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.mojang.math;

import com.mojang.math.MatrixUtil;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Transformation {
    private final Matrix4f matrix;
    private boolean decomposed;
    @Nullable
    private Vector3f translation;
    @Nullable
    private Quaternionf leftRotation;
    @Nullable
    private Vector3f scale;
    @Nullable
    private Quaternionf rightRotation;
    private static final Transformation IDENTITY = (Transformation)Util.make(() -> {
        Transformation $$0 = new Transformation(new Matrix4f());
        $$0.getLeftRotation();
        return $$0;
    });

    public Transformation(@Nullable Matrix4f $$0) {
        this.matrix = $$0 == null ? Transformation.IDENTITY.matrix : $$0;
    }

    public Transformation(@Nullable Vector3f $$0, @Nullable Quaternionf $$1, @Nullable Vector3f $$2, @Nullable Quaternionf $$3) {
        this.matrix = Transformation.compose($$0, $$1, $$2, $$3);
        this.translation = $$0 != null ? $$0 : new Vector3f();
        this.leftRotation = $$1 != null ? $$1 : new Quaternionf();
        this.scale = $$2 != null ? $$2 : new Vector3f(1.0f, 1.0f, 1.0f);
        this.rightRotation = $$3 != null ? $$3 : new Quaternionf();
        this.decomposed = true;
    }

    public static Transformation identity() {
        return IDENTITY;
    }

    public Transformation compose(Transformation $$0) {
        Matrix4f $$1 = this.getMatrix();
        $$1.mul((Matrix4fc)$$0.getMatrix());
        return new Transformation($$1);
    }

    @Nullable
    public Transformation inverse() {
        if (this == IDENTITY) {
            return this;
        }
        Matrix4f $$0 = this.getMatrix().invert();
        if ($$0.isFinite()) {
            return new Transformation($$0);
        }
        return null;
    }

    private void ensureDecomposed() {
        if (!this.decomposed) {
            Matrix4x3f $$0 = MatrixUtil.toAffine(this.matrix);
            Triple<Quaternionf, Vector3f, Quaternionf> $$1 = MatrixUtil.svdDecompose(new Matrix3f().set((Matrix4x3fc)$$0));
            this.translation = $$0.getTranslation(new Vector3f());
            this.leftRotation = new Quaternionf((Quaternionfc)$$1.getLeft());
            this.scale = new Vector3f((Vector3fc)$$1.getMiddle());
            this.rightRotation = new Quaternionf((Quaternionfc)$$1.getRight());
            this.decomposed = true;
        }
    }

    private static Matrix4f compose(@Nullable Vector3f $$0, @Nullable Quaternionf $$1, @Nullable Vector3f $$2, @Nullable Quaternionf $$3) {
        Matrix4f $$4 = new Matrix4f();
        if ($$0 != null) {
            $$4.translation((Vector3fc)$$0);
        }
        if ($$1 != null) {
            $$4.rotate((Quaternionfc)$$1);
        }
        if ($$2 != null) {
            $$4.scale((Vector3fc)$$2);
        }
        if ($$3 != null) {
            $$4.rotate((Quaternionfc)$$3);
        }
        return $$4;
    }

    public Matrix4f getMatrix() {
        return new Matrix4f((Matrix4fc)this.matrix);
    }

    public Vector3f getTranslation() {
        this.ensureDecomposed();
        return new Vector3f((Vector3fc)this.translation);
    }

    public Quaternionf getLeftRotation() {
        this.ensureDecomposed();
        return new Quaternionf((Quaternionfc)this.leftRotation);
    }

    public Vector3f getScale() {
        this.ensureDecomposed();
        return new Vector3f((Vector3fc)this.scale);
    }

    public Quaternionf getRightRotation() {
        this.ensureDecomposed();
        return new Quaternionf((Quaternionfc)this.rightRotation);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        Transformation $$1 = (Transformation)$$0;
        return Objects.equals((Object)this.matrix, (Object)$$1.matrix);
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.matrix});
    }

    public Transformation slerp(Transformation $$0, float $$1) {
        Vector3f $$2 = this.getTranslation();
        Quaternionf $$3 = this.getLeftRotation();
        Vector3f $$4 = this.getScale();
        Quaternionf $$5 = this.getRightRotation();
        $$2.lerp((Vector3fc)$$0.getTranslation(), $$1);
        $$3.slerp((Quaternionfc)$$0.getLeftRotation(), $$1);
        $$4.lerp((Vector3fc)$$0.getScale(), $$1);
        $$5.slerp((Quaternionfc)$$0.getRightRotation(), $$1);
        return new Transformation($$2, $$3, $$4, $$5);
    }
}