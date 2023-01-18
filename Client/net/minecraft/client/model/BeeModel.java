/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;

public class BeeModel<T extends Bee>
extends AgeableListModel<T> {
    private static final float BEE_Y_BASE = 19.0f;
    private static final String BONE = "bone";
    private static final String STINGER = "stinger";
    private static final String LEFT_ANTENNA = "left_antenna";
    private static final String RIGHT_ANTENNA = "right_antenna";
    private static final String FRONT_LEGS = "front_legs";
    private static final String MIDDLE_LEGS = "middle_legs";
    private static final String BACK_LEGS = "back_legs";
    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeg;
    private final ModelPart midLeg;
    private final ModelPart backLeg;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float rollAmount;

    public BeeModel(ModelPart $$0) {
        super(false, 24.0f, 0.0f);
        this.bone = $$0.getChild(BONE);
        ModelPart $$1 = this.bone.getChild("body");
        this.stinger = $$1.getChild(STINGER);
        this.leftAntenna = $$1.getChild(LEFT_ANTENNA);
        this.rightAntenna = $$1.getChild(RIGHT_ANTENNA);
        this.rightWing = this.bone.getChild("right_wing");
        this.leftWing = this.bone.getChild("left_wing");
        this.frontLeg = this.bone.getChild(FRONT_LEGS);
        this.midLeg = this.bone.getChild(MIDDLE_LEGS);
        this.backLeg = this.bone.getChild(BACK_LEGS);
    }

    public static LayerDefinition createBodyLayer() {
        float $$0 = 19.0f;
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.addOrReplaceChild(BONE, CubeListBuilder.create(), PartPose.offset(0.0f, 19.0f, 0.0f));
        PartDefinition $$4 = $$3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), PartPose.ZERO);
        $$4.addOrReplaceChild(STINGER, CubeListBuilder.create().texOffs(26, 7).addBox(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), PartPose.ZERO);
        $$4.addOrReplaceChild(LEFT_ANTENNA, CubeListBuilder.create().texOffs(2, 0).addBox(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), PartPose.offset(0.0f, -2.0f, -5.0f));
        $$4.addOrReplaceChild(RIGHT_ANTENNA, CubeListBuilder.create().texOffs(2, 3).addBox(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), PartPose.offset(0.0f, -2.0f, -5.0f));
        CubeDeformation $$5 = new CubeDeformation(0.001f);
        $$3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 18).addBox(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, $$5), PartPose.offsetAndRotation(-1.5f, -4.0f, -3.0f, 0.0f, -0.2618f, 0.0f));
        $$3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, $$5), PartPose.offsetAndRotation(1.5f, -4.0f, -3.0f, 0.0f, 0.2618f, 0.0f));
        $$3.addOrReplaceChild(FRONT_LEGS, CubeListBuilder.create().addBox(FRONT_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), PartPose.offset(1.5f, 3.0f, -2.0f));
        $$3.addOrReplaceChild(MIDDLE_LEGS, CubeListBuilder.create().addBox(MIDDLE_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), PartPose.offset(1.5f, 3.0f, 0.0f));
        $$3.addOrReplaceChild(BACK_LEGS, CubeListBuilder.create().addBox(BACK_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), PartPose.offset(1.5f, 3.0f, 2.0f));
        return LayerDefinition.create($$1, 64, 64);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        super.prepareMobModel($$0, $$1, $$2, $$3);
        this.rollAmount = ((Bee)$$0).getRollAmount($$3);
        this.stinger.visible = !((Bee)$$0).hasStung();
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        boolean $$6;
        this.rightWing.xRot = 0.0f;
        this.leftAntenna.xRot = 0.0f;
        this.rightAntenna.xRot = 0.0f;
        this.bone.xRot = 0.0f;
        boolean bl = $$6 = ((Entity)$$0).isOnGround() && ((Entity)$$0).getDeltaMovement().lengthSqr() < 1.0E-7;
        if ($$6) {
            this.rightWing.yRot = -0.2618f;
            this.rightWing.zRot = 0.0f;
            this.leftWing.xRot = 0.0f;
            this.leftWing.yRot = 0.2618f;
            this.leftWing.zRot = 0.0f;
            this.frontLeg.xRot = 0.0f;
            this.midLeg.xRot = 0.0f;
            this.backLeg.xRot = 0.0f;
        } else {
            float $$7 = $$3 * 120.32113f * ((float)Math.PI / 180);
            this.rightWing.yRot = 0.0f;
            this.rightWing.zRot = Mth.cos($$7) * (float)Math.PI * 0.15f;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = this.rightWing.yRot;
            this.leftWing.zRot = -this.rightWing.zRot;
            this.frontLeg.xRot = 0.7853982f;
            this.midLeg.xRot = 0.7853982f;
            this.backLeg.xRot = 0.7853982f;
            this.bone.xRot = 0.0f;
            this.bone.yRot = 0.0f;
            this.bone.zRot = 0.0f;
        }
        if (!$$0.isAngry()) {
            this.bone.xRot = 0.0f;
            this.bone.yRot = 0.0f;
            this.bone.zRot = 0.0f;
            if (!$$6) {
                float $$8 = Mth.cos($$3 * 0.18f);
                this.bone.xRot = 0.1f + $$8 * (float)Math.PI * 0.025f;
                this.leftAntenna.xRot = $$8 * (float)Math.PI * 0.03f;
                this.rightAntenna.xRot = $$8 * (float)Math.PI * 0.03f;
                this.frontLeg.xRot = -$$8 * (float)Math.PI * 0.1f + 0.3926991f;
                this.backLeg.xRot = -$$8 * (float)Math.PI * 0.05f + 0.7853982f;
                this.bone.y = 19.0f - Mth.cos($$3 * 0.18f) * 0.9f;
            }
        }
        if (this.rollAmount > 0.0f) {
            this.bone.xRot = ModelUtils.rotlerpRad(this.bone.xRot, 3.0915928f, this.rollAmount);
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.bone);
    }
}