/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;

public class FoxModel<T extends Fox>
extends AgeableListModel<T> {
    public final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private static final int LEG_SIZE = 6;
    private static final float HEAD_HEIGHT = 16.5f;
    private static final float LEG_POS = 17.5f;
    private float legMotionPos;

    public FoxModel(ModelPart $$0) {
        super(true, 8.0f, 3.35f);
        this.head = $$0.getChild("head");
        this.body = $$0.getChild("body");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f), PartPose.offset(-1.0f, 16.5f, -3.0f));
        $$2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), PartPose.ZERO);
        $$2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), PartPose.ZERO);
        $$2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f), PartPose.ZERO);
        PartDefinition $$3 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 15).addBox(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f), PartPose.offsetAndRotation(0.0f, 16.0f, -6.0f, 1.5707964f, 0.0f, 0.0f));
        CubeDeformation $$4 = new CubeDeformation(0.001f);
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(4, 24).addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, $$4);
        CubeListBuilder $$6 = CubeListBuilder.create().texOffs(13, 24).addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, $$4);
        $$1.addOrReplaceChild("right_hind_leg", $$6, PartPose.offset(-5.0f, 17.5f, 7.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$5, PartPose.offset(-1.0f, 17.5f, 7.0f));
        $$1.addOrReplaceChild("right_front_leg", $$6, PartPose.offset(-5.0f, 17.5f, 0.0f));
        $$1.addOrReplaceChild("left_front_leg", $$5, PartPose.offset(-1.0f, 17.5f, 0.0f));
        $$3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 0).addBox(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f), PartPose.offsetAndRotation(-4.0f, 15.0f, -1.0f, -0.05235988f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 48, 32);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        this.body.xRot = 1.5707964f;
        this.tail.xRot = -0.05235988f;
        this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.head.setPos(-1.0f, 16.5f, -3.0f);
        this.head.yRot = 0.0f;
        this.head.zRot = ((Fox)$$0).getHeadRollAngle($$3);
        this.rightHindLeg.visible = true;
        this.leftHindLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        this.body.setPos(0.0f, 16.0f, -6.0f);
        this.body.zRot = 0.0f;
        this.rightHindLeg.setPos(-5.0f, 17.5f, 7.0f);
        this.leftHindLeg.setPos(-1.0f, 17.5f, 7.0f);
        if (((Fox)$$0).isCrouching()) {
            this.body.xRot = 1.6755161f;
            float $$4 = ((Fox)$$0).getCrouchAmount($$3);
            this.body.setPos(0.0f, 16.0f + ((Fox)$$0).getCrouchAmount($$3), -6.0f);
            this.head.setPos(-1.0f, 16.5f + $$4, -3.0f);
            this.head.yRot = 0.0f;
        } else if (((Fox)$$0).isSleeping()) {
            this.body.zRot = -1.5707964f;
            this.body.setPos(0.0f, 21.0f, -6.0f);
            this.tail.xRot = -2.6179938f;
            if (this.young) {
                this.tail.xRot = -2.1816616f;
                this.body.setPos(0.0f, 21.0f, -2.0f);
            }
            this.head.setPos(1.0f, 19.49f, -3.0f);
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = 0.0f;
            this.rightHindLeg.visible = false;
            this.leftHindLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if (((Fox)$$0).isSitting()) {
            this.body.xRot = 0.5235988f;
            this.body.setPos(0.0f, 9.0f, -3.0f);
            this.tail.xRot = 0.7853982f;
            this.tail.setPos(-4.0f, 15.0f, -2.0f);
            this.head.setPos(-1.0f, 10.0f, -0.25f);
            this.head.xRot = 0.0f;
            this.head.yRot = 0.0f;
            if (this.young) {
                this.head.setPos(-1.0f, 13.0f, -3.75f);
            }
            this.rightHindLeg.xRot = -1.3089969f;
            this.rightHindLeg.setPos(-5.0f, 21.5f, 6.75f);
            this.leftHindLeg.xRot = -1.3089969f;
            this.leftHindLeg.setPos(-1.0f, 21.5f, 6.75f);
            this.rightFrontLeg.xRot = -0.2617994f;
            this.leftFrontLeg.xRot = -0.2617994f;
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.rightHindLeg, (Object)this.leftHindLeg, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        if (!(((Fox)$$0).isSleeping() || ((Fox)$$0).isFaceplanted() || ((Fox)$$0).isCrouching())) {
            this.head.xRot = $$5 * ((float)Math.PI / 180);
            this.head.yRot = $$4 * ((float)Math.PI / 180);
        }
        if (((Fox)$$0).isSleeping()) {
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = Mth.cos($$3 * 0.027f) / 22.0f;
        }
        if (((Fox)$$0).isCrouching()) {
            float $$6;
            this.body.yRot = $$6 = Mth.cos($$3) * 0.01f;
            this.rightHindLeg.zRot = $$6;
            this.leftHindLeg.zRot = $$6;
            this.rightFrontLeg.zRot = $$6 / 2.0f;
            this.leftFrontLeg.zRot = $$6 / 2.0f;
        }
        if (((Fox)$$0).isFaceplanted()) {
            float $$7 = 0.1f;
            this.legMotionPos += 0.67f;
            this.rightHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
            this.leftHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f + (float)Math.PI) * 0.1f;
            this.rightFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f + (float)Math.PI) * 0.1f;
            this.leftFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
        }
    }
}