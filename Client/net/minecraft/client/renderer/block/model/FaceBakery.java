/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer.block.model;

import com.mojang.math.Transformation;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class FaceBakery {
    public static final int VERTEX_INT_SIZE = 8;
    private static final float RESCALE_22_5 = 1.0f / (float)Math.cos((double)0.3926991f) - 1.0f;
    private static final float RESCALE_45 = 1.0f / (float)Math.cos((double)0.7853981852531433) - 1.0f;
    public static final int VERTEX_COUNT = 4;
    private static final int COLOR_INDEX = 3;
    public static final int UV_INDEX = 4;

    public BakedQuad bakeQuad(Vector3f $$0, Vector3f $$1, BlockElementFace $$2, TextureAtlasSprite $$3, Direction $$4, ModelState $$5, @Nullable BlockElementRotation $$6, boolean $$7, ResourceLocation $$8) {
        BlockFaceUV $$9 = $$2.uv;
        if ($$5.isUvLocked()) {
            $$9 = FaceBakery.recomputeUVs($$2.uv, $$4, $$5.getRotation(), $$8);
        }
        float[] $$10 = new float[$$9.uvs.length];
        System.arraycopy((Object)$$9.uvs, (int)0, (Object)$$10, (int)0, (int)$$10.length);
        float $$11 = $$3.uvShrinkRatio();
        float $$12 = ($$9.uvs[0] + $$9.uvs[0] + $$9.uvs[2] + $$9.uvs[2]) / 4.0f;
        float $$13 = ($$9.uvs[1] + $$9.uvs[1] + $$9.uvs[3] + $$9.uvs[3]) / 4.0f;
        $$9.uvs[0] = Mth.lerp($$11, $$9.uvs[0], $$12);
        $$9.uvs[2] = Mth.lerp($$11, $$9.uvs[2], $$12);
        $$9.uvs[1] = Mth.lerp($$11, $$9.uvs[1], $$13);
        $$9.uvs[3] = Mth.lerp($$11, $$9.uvs[3], $$13);
        int[] $$14 = this.makeVertices($$9, $$3, $$4, this.setupShape($$0, $$1), $$5.getRotation(), $$6, $$7);
        Direction $$15 = FaceBakery.calculateFacing($$14);
        System.arraycopy((Object)$$10, (int)0, (Object)$$9.uvs, (int)0, (int)$$10.length);
        if ($$6 == null) {
            this.recalculateWinding($$14, $$15);
        }
        return new BakedQuad($$14, $$2.tintIndex, $$15, $$3, $$7);
    }

    public static BlockFaceUV recomputeUVs(BlockFaceUV $$0, Direction $$1, Transformation $$2, ResourceLocation $$3) {
        float $$22;
        float $$21;
        float $$18;
        float $$17;
        Matrix4f $$4 = BlockMath.getUVLockTransform($$2, $$1, (Supplier<String>)((Supplier)() -> "Unable to resolve UVLock for model: " + $$3)).getMatrix();
        float $$5 = $$0.getU($$0.getReverseIndex(0));
        float $$6 = $$0.getV($$0.getReverseIndex(0));
        Vector4f $$7 = $$4.transform(new Vector4f($$5 / 16.0f, $$6 / 16.0f, 0.0f, 1.0f));
        float $$8 = 16.0f * $$7.x();
        float $$9 = 16.0f * $$7.y();
        float $$10 = $$0.getU($$0.getReverseIndex(2));
        float $$11 = $$0.getV($$0.getReverseIndex(2));
        Vector4f $$12 = $$4.transform(new Vector4f($$10 / 16.0f, $$11 / 16.0f, 0.0f, 1.0f));
        float $$13 = 16.0f * $$12.x();
        float $$14 = 16.0f * $$12.y();
        if (Math.signum((float)($$10 - $$5)) == Math.signum((float)($$13 - $$8))) {
            float $$15 = $$8;
            float $$16 = $$13;
        } else {
            $$17 = $$13;
            $$18 = $$8;
        }
        if (Math.signum((float)($$11 - $$6)) == Math.signum((float)($$14 - $$9))) {
            float $$19 = $$9;
            float $$20 = $$14;
        } else {
            $$21 = $$14;
            $$22 = $$9;
        }
        float $$23 = (float)Math.toRadians((double)$$0.rotation);
        Matrix3f $$24 = new Matrix3f((Matrix4fc)$$4);
        Vector3f $$25 = $$24.transform(new Vector3f(Mth.cos($$23), Mth.sin($$23), 0.0f));
        int $$26 = Math.floorMod((int)(-((int)Math.round((double)(Math.toDegrees((double)Math.atan2((double)$$25.y(), (double)$$25.x())) / 90.0))) * 90), (int)360);
        return new BlockFaceUV(new float[]{$$17, $$21, $$18, $$22}, $$26);
    }

    private int[] makeVertices(BlockFaceUV $$0, TextureAtlasSprite $$1, Direction $$2, float[] $$3, Transformation $$4, @Nullable BlockElementRotation $$5, boolean $$6) {
        int[] $$7 = new int[32];
        for (int $$8 = 0; $$8 < 4; ++$$8) {
            this.bakeVertex($$7, $$8, $$2, $$0, $$3, $$1, $$4, $$5, $$6);
        }
        return $$7;
    }

    private float[] setupShape(Vector3f $$0, Vector3f $$1) {
        float[] $$2 = new float[Direction.values().length];
        $$2[FaceInfo.Constants.MIN_X] = $$0.x() / 16.0f;
        $$2[FaceInfo.Constants.MIN_Y] = $$0.y() / 16.0f;
        $$2[FaceInfo.Constants.MIN_Z] = $$0.z() / 16.0f;
        $$2[FaceInfo.Constants.MAX_X] = $$1.x() / 16.0f;
        $$2[FaceInfo.Constants.MAX_Y] = $$1.y() / 16.0f;
        $$2[FaceInfo.Constants.MAX_Z] = $$1.z() / 16.0f;
        return $$2;
    }

    private void bakeVertex(int[] $$0, int $$1, Direction $$2, BlockFaceUV $$3, float[] $$4, TextureAtlasSprite $$5, Transformation $$6, @Nullable BlockElementRotation $$7, boolean $$8) {
        FaceInfo.VertexInfo $$9 = FaceInfo.fromFacing($$2).getVertexInfo($$1);
        Vector3f $$10 = new Vector3f($$4[$$9.xFace], $$4[$$9.yFace], $$4[$$9.zFace]);
        this.applyElementRotation($$10, $$7);
        this.applyModelRotation($$10, $$6);
        this.fillVertex($$0, $$1, $$10, $$5, $$3);
    }

    private void fillVertex(int[] $$0, int $$1, Vector3f $$2, TextureAtlasSprite $$3, BlockFaceUV $$4) {
        int $$5 = $$1 * 8;
        $$0[$$5] = Float.floatToRawIntBits((float)$$2.x());
        $$0[$$5 + 1] = Float.floatToRawIntBits((float)$$2.y());
        $$0[$$5 + 2] = Float.floatToRawIntBits((float)$$2.z());
        $$0[$$5 + 3] = -1;
        $$0[$$5 + 4] = Float.floatToRawIntBits((float)$$3.getU($$4.getU($$1)));
        $$0[$$5 + 4 + 1] = Float.floatToRawIntBits((float)$$3.getV($$4.getV($$1)));
    }

    /*
     * WARNING - void declaration
     */
    private void applyElementRotation(Vector3f $$0, @Nullable BlockElementRotation $$1) {
        void $$9;
        void $$8;
        if ($$1 == null) {
            return;
        }
        switch ($$1.axis()) {
            case X: {
                Vector3f $$2 = new Vector3f(1.0f, 0.0f, 0.0f);
                Vector3f $$3 = new Vector3f(0.0f, 1.0f, 1.0f);
                break;
            }
            case Y: {
                Vector3f $$4 = new Vector3f(0.0f, 1.0f, 0.0f);
                Vector3f $$5 = new Vector3f(1.0f, 0.0f, 1.0f);
                break;
            }
            case Z: {
                Vector3f $$6 = new Vector3f(0.0f, 0.0f, 1.0f);
                Vector3f $$7 = new Vector3f(1.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                throw new IllegalArgumentException("There are only 3 axes");
            }
        }
        Quaternionf $$10 = new Quaternionf().rotationAxis($$1.angle() * ((float)Math.PI / 180), (Vector3fc)$$8);
        if ($$1.rescale()) {
            if (Math.abs((float)$$1.angle()) == 22.5f) {
                $$9.mul(RESCALE_22_5);
            } else {
                $$9.mul(RESCALE_45);
            }
            $$9.add(1.0f, 1.0f, 1.0f);
        } else {
            $$9.set(1.0f, 1.0f, 1.0f);
        }
        this.rotateVertexBy($$0, new Vector3f((Vector3fc)$$1.origin()), new Matrix4f().rotation((Quaternionfc)$$10), (Vector3f)$$9);
    }

    public void applyModelRotation(Vector3f $$0, Transformation $$1) {
        if ($$1 == Transformation.identity()) {
            return;
        }
        this.rotateVertexBy($$0, new Vector3f(0.5f, 0.5f, 0.5f), $$1.getMatrix(), new Vector3f(1.0f, 1.0f, 1.0f));
    }

    private void rotateVertexBy(Vector3f $$0, Vector3f $$1, Matrix4f $$2, Vector3f $$3) {
        Vector4f $$4 = $$2.transform(new Vector4f($$0.x() - $$1.x(), $$0.y() - $$1.y(), $$0.z() - $$1.z(), 1.0f));
        $$4.mul((Vector4fc)new Vector4f((Vector3fc)$$3, 1.0f));
        $$0.set($$4.x() + $$1.x(), $$4.y() + $$1.y(), $$4.z() + $$1.z());
    }

    public static Direction calculateFacing(int[] $$0) {
        Vector3f $$1 = new Vector3f(Float.intBitsToFloat((int)$$0[0]), Float.intBitsToFloat((int)$$0[1]), Float.intBitsToFloat((int)$$0[2]));
        Vector3f $$2 = new Vector3f(Float.intBitsToFloat((int)$$0[8]), Float.intBitsToFloat((int)$$0[9]), Float.intBitsToFloat((int)$$0[10]));
        Vector3f $$3 = new Vector3f(Float.intBitsToFloat((int)$$0[16]), Float.intBitsToFloat((int)$$0[17]), Float.intBitsToFloat((int)$$0[18]));
        Vector3f $$4 = new Vector3f((Vector3fc)$$1).sub((Vector3fc)$$2);
        Vector3f $$5 = new Vector3f((Vector3fc)$$3).sub((Vector3fc)$$2);
        Vector3f $$6 = new Vector3f((Vector3fc)$$5).cross((Vector3fc)$$4).normalize();
        if (!$$6.isFinite()) {
            return Direction.UP;
        }
        Direction $$7 = null;
        float $$8 = 0.0f;
        for (Direction $$9 : Direction.values()) {
            Vec3i $$10 = $$9.getNormal();
            Vector3f $$11 = new Vector3f((float)$$10.getX(), (float)$$10.getY(), (float)$$10.getZ());
            float $$12 = $$6.dot((Vector3fc)$$11);
            if (!($$12 >= 0.0f) || !($$12 > $$8)) continue;
            $$8 = $$12;
            $$7 = $$9;
        }
        if ($$7 == null) {
            return Direction.UP;
        }
        return $$7;
    }

    private void recalculateWinding(int[] $$0, Direction $$1) {
        int[] $$2 = new int[$$0.length];
        System.arraycopy((Object)$$0, (int)0, (Object)$$2, (int)0, (int)$$0.length);
        float[] $$3 = new float[Direction.values().length];
        $$3[FaceInfo.Constants.MIN_X] = 999.0f;
        $$3[FaceInfo.Constants.MIN_Y] = 999.0f;
        $$3[FaceInfo.Constants.MIN_Z] = 999.0f;
        $$3[FaceInfo.Constants.MAX_X] = -999.0f;
        $$3[FaceInfo.Constants.MAX_Y] = -999.0f;
        $$3[FaceInfo.Constants.MAX_Z] = -999.0f;
        for (int $$4 = 0; $$4 < 4; ++$$4) {
            int $$5 = 8 * $$4;
            float $$6 = Float.intBitsToFloat((int)$$2[$$5]);
            float $$7 = Float.intBitsToFloat((int)$$2[$$5 + 1]);
            float $$8 = Float.intBitsToFloat((int)$$2[$$5 + 2]);
            if ($$6 < $$3[FaceInfo.Constants.MIN_X]) {
                $$3[FaceInfo.Constants.MIN_X] = $$6;
            }
            if ($$7 < $$3[FaceInfo.Constants.MIN_Y]) {
                $$3[FaceInfo.Constants.MIN_Y] = $$7;
            }
            if ($$8 < $$3[FaceInfo.Constants.MIN_Z]) {
                $$3[FaceInfo.Constants.MIN_Z] = $$8;
            }
            if ($$6 > $$3[FaceInfo.Constants.MAX_X]) {
                $$3[FaceInfo.Constants.MAX_X] = $$6;
            }
            if ($$7 > $$3[FaceInfo.Constants.MAX_Y]) {
                $$3[FaceInfo.Constants.MAX_Y] = $$7;
            }
            if (!($$8 > $$3[FaceInfo.Constants.MAX_Z])) continue;
            $$3[FaceInfo.Constants.MAX_Z] = $$8;
        }
        FaceInfo $$9 = FaceInfo.fromFacing($$1);
        for (int $$10 = 0; $$10 < 4; ++$$10) {
            int $$11 = 8 * $$10;
            FaceInfo.VertexInfo $$12 = $$9.getVertexInfo($$10);
            float $$13 = $$3[$$12.xFace];
            float $$14 = $$3[$$12.yFace];
            float $$15 = $$3[$$12.zFace];
            $$0[$$11] = Float.floatToRawIntBits((float)$$13);
            $$0[$$11 + 1] = Float.floatToRawIntBits((float)$$14);
            $$0[$$11 + 2] = Float.floatToRawIntBits((float)$$15);
            for (int $$16 = 0; $$16 < 4; ++$$16) {
                int $$17 = 8 * $$16;
                float $$18 = Float.intBitsToFloat((int)$$2[$$17]);
                float $$19 = Float.intBitsToFloat((int)$$2[$$17 + 1]);
                float $$20 = Float.intBitsToFloat((int)$$2[$$17 + 2]);
                if (!Mth.equal($$13, $$18) || !Mth.equal($$14, $$19) || !Mth.equal($$15, $$20)) continue;
                $$0[$$11 + 4] = $$2[$$17 + 4];
                $$0[$$11 + 4 + 1] = $$2[$$17 + 4 + 1];
            }
        }
    }
}