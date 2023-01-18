/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Panda;

public class PandaModel<T extends Panda>
extends QuadrupedModel<T> {
    private float sitAmount;
    private float lieOnBackAmount;
    private float rollAmount;

    public PandaModel(ModelPart $$0) {
        super($$0, true, 23.0f, 4.8f, 2.7f, 3.0f, 49);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 6).addBox(-6.5f, -5.0f, -4.0f, 13.0f, 10.0f, 9.0f).texOffs(45, 16).addBox("nose", -3.5f, 0.0f, -6.0f, 7.0f, 5.0f, 2.0f).texOffs(52, 25).addBox("left_ear", 3.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f).texOffs(52, 25).addBox("right_ear", -8.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 11.5f, -17.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-9.5f, -13.0f, -6.5f, 19.0f, 26.0f, 13.0f), PartPose.offsetAndRotation(0.0f, 10.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        int $$2 = 9;
        int $$3 = 6;
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(40, 0).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 9.0f, 6.0f);
        $$1.addOrReplaceChild("right_hind_leg", $$4, PartPose.offset(-5.5f, 15.0f, 9.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$4, PartPose.offset(5.5f, 15.0f, 9.0f));
        $$1.addOrReplaceChild("right_front_leg", $$4, PartPose.offset(-5.5f, 15.0f, -9.0f));
        $$1.addOrReplaceChild("left_front_leg", $$4, PartPose.offset(5.5f, 15.0f, -9.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        super.prepareMobModel($$0, $$1, $$2, $$3);
        this.sitAmount = ((Panda)$$0).getSitAmount($$3);
        this.lieOnBackAmount = ((Panda)$$0).getLieOnBackAmount($$3);
        this.rollAmount = ((AgeableMob)$$0).isBaby() ? 0.0f : ((Panda)$$0).getRollAmount($$3);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        boolean $$6 = ((Panda)$$0).getUnhappyCounter() > 0;
        boolean $$7 = ((Panda)$$0).isSneezing();
        int $$8 = ((Panda)$$0).getSneezeCounter();
        boolean $$9 = ((Panda)$$0).isEating();
        boolean $$10 = ((Panda)$$0).isScared();
        if ($$6) {
            this.head.yRot = 0.35f * Mth.sin(0.6f * $$3);
            this.head.zRot = 0.35f * Mth.sin(0.6f * $$3);
            this.rightFrontLeg.xRot = -0.75f * Mth.sin(0.3f * $$3);
            this.leftFrontLeg.xRot = 0.75f * Mth.sin(0.3f * $$3);
        } else {
            this.head.zRot = 0.0f;
        }
        if ($$7) {
            if ($$8 < 15) {
                this.head.xRot = -0.7853982f * (float)$$8 / 14.0f;
            } else if ($$8 < 20) {
                float $$11 = ($$8 - 15) / 5;
                this.head.xRot = -0.7853982f + 0.7853982f * $$11;
            }
        }
        if (this.sitAmount > 0.0f) {
            this.body.xRot = ModelUtils.rotlerpRad(this.body.xRot, 1.7407963f, this.sitAmount);
            this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, 1.5707964f, this.sitAmount);
            this.rightFrontLeg.zRot = -0.27079642f;
            this.leftFrontLeg.zRot = 0.27079642f;
            this.rightHindLeg.zRot = 0.5707964f;
            this.leftHindLeg.zRot = -0.5707964f;
            if ($$9) {
                this.head.xRot = 1.5707964f + 0.2f * Mth.sin($$3 * 0.6f);
                this.rightFrontLeg.xRot = -0.4f - 0.2f * Mth.sin($$3 * 0.6f);
                this.leftFrontLeg.xRot = -0.4f - 0.2f * Mth.sin($$3 * 0.6f);
            }
            if ($$10) {
                this.head.xRot = 2.1707964f;
                this.rightFrontLeg.xRot = -0.9f;
                this.leftFrontLeg.xRot = -0.9f;
            }
        } else {
            this.rightHindLeg.zRot = 0.0f;
            this.leftHindLeg.zRot = 0.0f;
            this.rightFrontLeg.zRot = 0.0f;
            this.leftFrontLeg.zRot = 0.0f;
        }
        if (this.lieOnBackAmount > 0.0f) {
            this.rightHindLeg.xRot = -0.6f * Mth.sin($$3 * 0.15f);
            this.leftHindLeg.xRot = 0.6f * Mth.sin($$3 * 0.15f);
            this.rightFrontLeg.xRot = 0.3f * Mth.sin($$3 * 0.25f);
            this.leftFrontLeg.xRot = -0.3f * Mth.sin($$3 * 0.25f);
            this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, 1.5707964f, this.lieOnBackAmount);
        }
        if (this.rollAmount > 0.0f) {
            this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, 2.0561945f, this.rollAmount);
            this.rightHindLeg.xRot = -0.5f * Mth.sin($$3 * 0.5f);
            this.leftHindLeg.xRot = 0.5f * Mth.sin($$3 * 0.5f);
            this.rightFrontLeg.xRot = 0.5f * Mth.sin($$3 * 0.5f);
            this.leftFrontLeg.xRot = -0.5f * Mth.sin($$3 * 0.5f);
        }
    }
}