/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandArmorModel
extends HumanoidModel<ArmorStand> {
    public ArmorStandArmorModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = HumanoidModel.createMesh($$0, 0.0f);
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.offset(0.0f, 1.0f, 0.0f));
        $$2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0.extend(0.5f)), PartPose.offset(0.0f, 1.0f, 0.0f));
        $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(-1.9f, 11.0f, 0.0f));
        $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(1.9f, 11.0f, 0.0f));
        return LayerDefinition.create($$1, 64, 32);
    }

    @Override
    public void setupAnim(ArmorStand $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.head.xRot = (float)Math.PI / 180 * $$0.getHeadPose().getX();
        this.head.yRot = (float)Math.PI / 180 * $$0.getHeadPose().getY();
        this.head.zRot = (float)Math.PI / 180 * $$0.getHeadPose().getZ();
        this.body.xRot = (float)Math.PI / 180 * $$0.getBodyPose().getX();
        this.body.yRot = (float)Math.PI / 180 * $$0.getBodyPose().getY();
        this.body.zRot = (float)Math.PI / 180 * $$0.getBodyPose().getZ();
        this.leftArm.xRot = (float)Math.PI / 180 * $$0.getLeftArmPose().getX();
        this.leftArm.yRot = (float)Math.PI / 180 * $$0.getLeftArmPose().getY();
        this.leftArm.zRot = (float)Math.PI / 180 * $$0.getLeftArmPose().getZ();
        this.rightArm.xRot = (float)Math.PI / 180 * $$0.getRightArmPose().getX();
        this.rightArm.yRot = (float)Math.PI / 180 * $$0.getRightArmPose().getY();
        this.rightArm.zRot = (float)Math.PI / 180 * $$0.getRightArmPose().getZ();
        this.leftLeg.xRot = (float)Math.PI / 180 * $$0.getLeftLegPose().getX();
        this.leftLeg.yRot = (float)Math.PI / 180 * $$0.getLeftLegPose().getY();
        this.leftLeg.zRot = (float)Math.PI / 180 * $$0.getLeftLegPose().getZ();
        this.rightLeg.xRot = (float)Math.PI / 180 * $$0.getRightLegPose().getX();
        this.rightLeg.yRot = (float)Math.PI / 180 * $$0.getRightLegPose().getY();
        this.rightLeg.zRot = (float)Math.PI / 180 * $$0.getRightLegPose().getZ();
        this.hat.copyFrom(this.head);
    }
}