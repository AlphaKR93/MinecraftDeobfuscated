/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  java.lang.Object
 *  java.util.Deque
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class PoseStack {
    private final Deque<Pose> poseStack = (Deque)Util.make(Queues.newArrayDeque(), $$0 -> {
        Matrix4f $$1 = new Matrix4f();
        Matrix3f $$2 = new Matrix3f();
        $$0.add((Object)new Pose($$1, $$2));
    });

    public void translate(double $$0, double $$1, double $$2) {
        this.translate((float)$$0, (float)$$1, (float)$$2);
    }

    public void translate(float $$0, float $$1, float $$2) {
        Pose $$3 = (Pose)this.poseStack.getLast();
        $$3.pose.translate($$0, $$1, $$2);
    }

    public void scale(float $$0, float $$1, float $$2) {
        Pose $$3 = (Pose)this.poseStack.getLast();
        $$3.pose.scale($$0, $$1, $$2);
        if ($$0 == $$1 && $$1 == $$2) {
            if ($$0 > 0.0f) {
                return;
            }
            $$3.normal.scale(-1.0f);
        }
        float $$4 = 1.0f / $$0;
        float $$5 = 1.0f / $$1;
        float $$6 = 1.0f / $$2;
        float $$7 = Mth.fastInvCubeRoot($$4 * $$5 * $$6);
        $$3.normal.scale($$7 * $$4, $$7 * $$5, $$7 * $$6);
    }

    public void mulPose(Quaternionf $$0) {
        Pose $$1 = (Pose)this.poseStack.getLast();
        $$1.pose.rotate((Quaternionfc)$$0);
        $$1.normal.rotate((Quaternionfc)$$0);
    }

    public void pushPose() {
        Pose $$0 = (Pose)this.poseStack.getLast();
        this.poseStack.addLast((Object)new Pose(new Matrix4f((Matrix4fc)$$0.pose), new Matrix3f((Matrix3fc)$$0.normal)));
    }

    public void popPose() {
        this.poseStack.removeLast();
    }

    public Pose last() {
        return (Pose)this.poseStack.getLast();
    }

    public boolean clear() {
        return this.poseStack.size() == 1;
    }

    public void setIdentity() {
        Pose $$0 = (Pose)this.poseStack.getLast();
        $$0.pose.identity();
        $$0.normal.identity();
    }

    public void mulPoseMatrix(Matrix4f $$0) {
        ((Pose)this.poseStack.getLast()).pose.mul((Matrix4fc)$$0);
    }

    public static final class Pose {
        final Matrix4f pose;
        final Matrix3f normal;

        Pose(Matrix4f $$0, Matrix3f $$1) {
            this.pose = $$0;
            this.normal = $$1;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }
    }
}