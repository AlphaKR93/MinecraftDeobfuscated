/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.nio.ByteBuffer
 *  java.nio.IntBuffer
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
    public VertexConsumer vertex(double var1, double var3, double var5);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer uv(float var1, float var2);

    public VertexConsumer overlayCoords(int var1, int var2);

    public VertexConsumer uv2(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public void endVertex();

    default public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
        this.vertex($$0, $$1, $$2);
        this.color($$3, $$4, $$5, $$6);
        this.uv($$7, $$8);
        this.overlayCoords($$9);
        this.uv2($$10);
        this.normal($$11, $$12, $$13);
        this.endVertex();
    }

    public void defaultColor(int var1, int var2, int var3, int var4);

    public void unsetDefaultColor();

    default public VertexConsumer color(float $$0, float $$1, float $$2, float $$3) {
        return this.color((int)($$0 * 255.0f), (int)($$1 * 255.0f), (int)($$2 * 255.0f), (int)($$3 * 255.0f));
    }

    default public VertexConsumer color(int $$0) {
        return this.color(FastColor.ARGB32.red($$0), FastColor.ARGB32.green($$0), FastColor.ARGB32.blue($$0), FastColor.ARGB32.alpha($$0));
    }

    default public VertexConsumer uv2(int $$0) {
        return this.uv2($$0 & 0xFFFF, $$0 >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlayCoords(int $$0) {
        return this.overlayCoords($$0 & 0xFFFF, $$0 >> 16 & 0xFFFF);
    }

    default public void putBulkData(PoseStack.Pose $$0, BakedQuad $$1, float $$2, float $$3, float $$4, int $$5, int $$6) {
        this.putBulkData($$0, $$1, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, $$2, $$3, $$4, new int[]{$$5, $$5, $$5, $$5}, $$6, false);
    }

    default public void putBulkData(PoseStack.Pose $$0, BakedQuad $$1, float[] $$2, float $$3, float $$4, float $$5, int[] $$6, int $$7, boolean $$8) {
        float[] $$9 = new float[]{$$2[0], $$2[1], $$2[2], $$2[3]};
        int[] $$10 = new int[]{$$6[0], $$6[1], $$6[2], $$6[3]};
        int[] $$11 = $$1.getVertices();
        Vec3i $$12 = $$1.getDirection().getNormal();
        Matrix4f $$13 = $$0.pose();
        Vector3f $$14 = $$0.normal().transform(new Vector3f((float)$$12.getX(), (float)$$12.getY(), (float)$$12.getZ()));
        int $$15 = 8;
        int $$16 = $$11.length / 8;
        try (MemoryStack $$17 = MemoryStack.stackPush();){
            ByteBuffer $$18 = $$17.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer $$19 = $$18.asIntBuffer();
            for (int $$20 = 0; $$20 < $$16; ++$$20) {
                float $$32;
                float $$31;
                float $$30;
                $$19.clear();
                $$19.put($$11, $$20 * 8, 8);
                float $$21 = $$18.getFloat(0);
                float $$22 = $$18.getFloat(4);
                float $$23 = $$18.getFloat(8);
                if ($$8) {
                    float $$24 = (float)($$18.get(12) & 0xFF) / 255.0f;
                    float $$25 = (float)($$18.get(13) & 0xFF) / 255.0f;
                    float $$26 = (float)($$18.get(14) & 0xFF) / 255.0f;
                    float $$27 = $$24 * $$9[$$20] * $$3;
                    float $$28 = $$25 * $$9[$$20] * $$4;
                    float $$29 = $$26 * $$9[$$20] * $$5;
                } else {
                    $$30 = $$9[$$20] * $$3;
                    $$31 = $$9[$$20] * $$4;
                    $$32 = $$9[$$20] * $$5;
                }
                int $$33 = $$10[$$20];
                float $$34 = $$18.getFloat(16);
                float $$35 = $$18.getFloat(20);
                Vector4f $$36 = $$13.transform(new Vector4f($$21, $$22, $$23, 1.0f));
                this.vertex($$36.x(), $$36.y(), $$36.z(), $$30, $$31, $$32, 1.0f, $$34, $$35, $$7, $$33, $$14.x(), $$14.y(), $$14.z());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f $$0, float $$1, float $$2, float $$3) {
        Vector4f $$4 = $$0.transform(new Vector4f($$1, $$2, $$3, 1.0f));
        return this.vertex($$4.x(), $$4.y(), $$4.z());
    }

    default public VertexConsumer normal(Matrix3f $$0, float $$1, float $$2, float $$3) {
        Vector3f $$4 = $$0.transform(new Vector3f($$1, $$2, $$3));
        return this.normal($$4.x(), $$4.y(), $$4.z());
    }
}