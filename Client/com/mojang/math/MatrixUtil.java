/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Matrix4x3f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class MatrixUtil {
    private static final float G = 3.0f + 2.0f * (float)Math.sqrt((double)2.0);
    private static final float CS = (float)Math.cos((double)0.39269908169872414);
    private static final float SS = (float)Math.sin((double)0.39269908169872414);

    private MatrixUtil() {
    }

    public static Matrix4f mulComponentWise(Matrix4f $$0, float $$1) {
        return $$0.set($$0.m00() * $$1, $$0.m01() * $$1, $$0.m02() * $$1, $$0.m03() * $$1, $$0.m10() * $$1, $$0.m11() * $$1, $$0.m12() * $$1, $$0.m13() * $$1, $$0.m20() * $$1, $$0.m21() * $$1, $$0.m22() * $$1, $$0.m23() * $$1, $$0.m30() * $$1, $$0.m31() * $$1, $$0.m32() * $$1, $$0.m33() * $$1);
    }

    private static Pair<Float, Float> approxGivensQuat(float $$0, float $$1, float $$2) {
        float $$4 = $$1;
        float $$3 = 2.0f * ($$0 - $$2);
        if (G * $$4 * $$4 < $$3 * $$3) {
            float $$5 = Mth.invSqrt($$4 * $$4 + $$3 * $$3);
            return Pair.of((Object)Float.valueOf((float)($$5 * $$4)), (Object)Float.valueOf((float)($$5 * $$3)));
        }
        return Pair.of((Object)Float.valueOf((float)SS), (Object)Float.valueOf((float)CS));
    }

    private static Pair<Float, Float> qrGivensQuat(float $$0, float $$1) {
        float $$2 = (float)Math.hypot((double)$$0, (double)$$1);
        float $$3 = $$2 > 1.0E-6f ? $$1 : 0.0f;
        float $$4 = Math.abs((float)$$0) + Math.max((float)$$2, (float)1.0E-6f);
        if ($$0 < 0.0f) {
            float $$5 = $$3;
            $$3 = $$4;
            $$4 = $$5;
        }
        float $$6 = Mth.invSqrt($$4 * $$4 + $$3 * $$3);
        return Pair.of((Object)Float.valueOf((float)($$3 *= $$6)), (Object)Float.valueOf((float)($$4 *= $$6)));
    }

    private static Quaternionf stepJacobi(Matrix3f $$0) {
        Matrix3f $$1 = new Matrix3f();
        Quaternionf $$2 = new Quaternionf();
        if ($$0.m01 * $$0.m01 + $$0.m10 * $$0.m10 > 1.0E-6f) {
            Pair<Float, Float> $$3 = MatrixUtil.approxGivensQuat($$0.m00, 0.5f * ($$0.m01 + $$0.m10), $$0.m11);
            Float $$4 = (Float)$$3.getFirst();
            Float $$5 = (Float)$$3.getSecond();
            Quaternionf $$6 = new Quaternionf(0.0f, 0.0f, $$4.floatValue(), $$5.floatValue());
            float $$7 = $$5.floatValue() * $$5.floatValue() - $$4.floatValue() * $$4.floatValue();
            float $$8 = -2.0f * $$4.floatValue() * $$5.floatValue();
            float $$9 = $$5.floatValue() * $$5.floatValue() + $$4.floatValue() * $$4.floatValue();
            $$2.mul((Quaternionfc)$$6);
            $$1.m00 = $$7;
            $$1.m11 = $$7;
            $$1.m01 = -$$8;
            $$1.m10 = $$8;
            $$1.m22 = $$9;
            $$0.mul((Matrix3fc)$$1);
            $$1.transpose();
            $$1.mul((Matrix3fc)$$0);
            $$0.set((Matrix3fc)$$1);
        }
        if ($$0.m02 * $$0.m02 + $$0.m20 * $$0.m20 > 1.0E-6f) {
            Pair<Float, Float> $$10 = MatrixUtil.approxGivensQuat($$0.m00, 0.5f * ($$0.m02 + $$0.m20), $$0.m22);
            float $$11 = -((Float)$$10.getFirst()).floatValue();
            Float $$12 = (Float)$$10.getSecond();
            Quaternionf $$13 = new Quaternionf(0.0f, $$11, 0.0f, $$12.floatValue());
            float $$14 = $$12.floatValue() * $$12.floatValue() - $$11 * $$11;
            float $$15 = -2.0f * $$11 * $$12.floatValue();
            float $$16 = $$12.floatValue() * $$12.floatValue() + $$11 * $$11;
            $$2.mul((Quaternionfc)$$13);
            $$1.m00 = $$14;
            $$1.m22 = $$14;
            $$1.m02 = $$15;
            $$1.m20 = -$$15;
            $$1.m11 = $$16;
            $$0.mul((Matrix3fc)$$1);
            $$1.transpose();
            $$1.mul((Matrix3fc)$$0);
            $$0.set((Matrix3fc)$$1);
        }
        if ($$0.m12 * $$0.m12 + $$0.m21 * $$0.m21 > 1.0E-6f) {
            Pair<Float, Float> $$17 = MatrixUtil.approxGivensQuat($$0.m11, 0.5f * ($$0.m12 + $$0.m21), $$0.m22);
            Float $$18 = (Float)$$17.getFirst();
            Float $$19 = (Float)$$17.getSecond();
            Quaternionf $$20 = new Quaternionf($$18.floatValue(), 0.0f, 0.0f, $$19.floatValue());
            float $$21 = $$19.floatValue() * $$19.floatValue() - $$18.floatValue() * $$18.floatValue();
            float $$22 = -2.0f * $$18.floatValue() * $$19.floatValue();
            float $$23 = $$19.floatValue() * $$19.floatValue() + $$18.floatValue() * $$18.floatValue();
            $$2.mul((Quaternionfc)$$20);
            $$1.m11 = $$21;
            $$1.m22 = $$21;
            $$1.m12 = -$$22;
            $$1.m21 = $$22;
            $$1.m00 = $$23;
            $$0.mul((Matrix3fc)$$1);
            $$1.transpose();
            $$1.mul((Matrix3fc)$$0);
            $$0.set((Matrix3fc)$$1);
        }
        return $$2;
    }

    public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f $$0) {
        Quaternionf $$1 = new Quaternionf();
        Quaternionf $$2 = new Quaternionf();
        Matrix3f $$3 = new Matrix3f((Matrix3fc)$$0);
        $$3.transpose();
        $$3.mul((Matrix3fc)$$0);
        for (int $$4 = 0; $$4 < 5; ++$$4) {
            $$2.mul((Quaternionfc)MatrixUtil.stepJacobi($$3));
        }
        $$2.normalize();
        Matrix3f $$5 = new Matrix3f((Matrix3fc)$$0);
        $$5.rotate((Quaternionfc)$$2);
        float $$6 = 1.0f;
        Pair<Float, Float> $$7 = MatrixUtil.qrGivensQuat($$5.m00, $$5.m01);
        Float $$8 = (Float)$$7.getFirst();
        Float $$9 = (Float)$$7.getSecond();
        float $$10 = $$9.floatValue() * $$9.floatValue() - $$8.floatValue() * $$8.floatValue();
        float $$11 = -2.0f * $$8.floatValue() * $$9.floatValue();
        float $$12 = $$9.floatValue() * $$9.floatValue() + $$8.floatValue() * $$8.floatValue();
        Quaternionf $$13 = new Quaternionf(0.0f, 0.0f, $$8.floatValue(), $$9.floatValue());
        $$1.mul((Quaternionfc)$$13);
        Matrix3f $$14 = new Matrix3f();
        $$14.m00 = $$10;
        $$14.m11 = $$10;
        $$14.m01 = $$11;
        $$14.m10 = -$$11;
        $$14.m22 = $$12;
        $$6 *= $$12;
        $$14.mul((Matrix3fc)$$5);
        $$7 = MatrixUtil.qrGivensQuat($$14.m00, $$14.m02);
        float $$15 = -((Float)$$7.getFirst()).floatValue();
        Float $$16 = (Float)$$7.getSecond();
        float $$17 = $$16.floatValue() * $$16.floatValue() - $$15 * $$15;
        float $$18 = -2.0f * $$15 * $$16.floatValue();
        float $$19 = $$16.floatValue() * $$16.floatValue() + $$15 * $$15;
        Quaternionf $$20 = new Quaternionf(0.0f, $$15, 0.0f, $$16.floatValue());
        $$1.mul((Quaternionfc)$$20);
        Matrix3f $$21 = new Matrix3f();
        $$21.m00 = $$17;
        $$21.m22 = $$17;
        $$21.m02 = -$$18;
        $$21.m20 = $$18;
        $$21.m11 = $$19;
        $$6 *= $$19;
        $$21.mul((Matrix3fc)$$14);
        $$7 = MatrixUtil.qrGivensQuat($$21.m11, $$21.m12);
        Float $$22 = (Float)$$7.getFirst();
        Float $$23 = (Float)$$7.getSecond();
        float $$24 = $$23.floatValue() * $$23.floatValue() - $$22.floatValue() * $$22.floatValue();
        float $$25 = -2.0f * $$22.floatValue() * $$23.floatValue();
        float $$26 = $$23.floatValue() * $$23.floatValue() + $$22.floatValue() * $$22.floatValue();
        Quaternionf $$27 = new Quaternionf($$22.floatValue(), 0.0f, 0.0f, $$23.floatValue());
        $$1.mul((Quaternionfc)$$27);
        Matrix3f $$28 = new Matrix3f();
        $$28.m11 = $$24;
        $$28.m22 = $$24;
        $$28.m12 = $$25;
        $$28.m21 = -$$25;
        $$28.m00 = $$26;
        $$6 *= $$26;
        $$28.mul((Matrix3fc)$$21);
        $$6 = 1.0f / $$6;
        $$1.mul((float)Math.sqrt((double)$$6));
        Vector3f $$29 = new Vector3f($$28.m00 * $$6, $$28.m11 * $$6, $$28.m22 * $$6);
        return Triple.of((Object)$$1, (Object)$$29, (Object)$$2);
    }

    public static Matrix4x3f toAffine(Matrix4f $$0) {
        float $$1 = 1.0f / $$0.m33();
        return new Matrix4x3f().set((Matrix4fc)$$0).scaleLocal($$1, $$1, $$1);
    }
}