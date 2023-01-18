/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandModel
extends ArmorStandArmorModel {
    private static final String RIGHT_BODY_STICK = "right_body_stick";
    private static final String LEFT_BODY_STICK = "left_body_stick";
    private static final String SHOULDER_STICK = "shoulder_stick";
    private static final String BASE_PLATE = "base_plate";
    private final ModelPart rightBodyStick;
    private final ModelPart leftBodyStick;
    private final ModelPart shoulderStick;
    private final ModelPart basePlate;

    public ArmorStandModel(ModelPart $$0) {
        super($$0);
        this.rightBodyStick = $$0.getChild(RIGHT_BODY_STICK);
        this.leftBodyStick = $$0.getChild(LEFT_BODY_STICK);
        this.shoulderStick = $$0.getChild(SHOULDER_STICK);
        this.basePlate = $$0.getChild(BASE_PLATE);
        this.hat.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f), PartPose.offset(0.0f, 1.0f, 0.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 26).addBox(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), PartPose.offset(-5.0f, 2.0f, 0.0f));
        $$1.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 16).mirror().addBox(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), PartPose.offset(5.0f, 2.0f, 0.0f));
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), PartPose.offset(-1.9f, 12.0f, 0.0f));
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), PartPose.offset(1.9f, 12.0f, 0.0f));
        $$1.addOrReplaceChild(RIGHT_BODY_STICK, CubeListBuilder.create().texOffs(16, 0).addBox(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(LEFT_BODY_STICK, CubeListBuilder.create().texOffs(48, 16).addBox(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(SHOULDER_STICK, CubeListBuilder.create().texOffs(0, 48).addBox(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild(BASE_PLATE, CubeListBuilder.create().texOffs(0, 32).addBox(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f), PartPose.offset(0.0f, 12.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void prepareMobModel(ArmorStand $$0, float $$1, float $$2, float $$3) {
        this.basePlate.xRot = 0.0f;
        this.basePlate.yRot = (float)Math.PI / 180 * -Mth.rotLerp($$3, $$0.yRotO, $$0.getYRot());
        this.basePlate.zRot = 0.0f;
    }

    @Override
    public void setupAnim(ArmorStand $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        this.leftArm.visible = $$0.isShowArms();
        this.rightArm.visible = $$0.isShowArms();
        this.basePlate.visible = !$$0.isNoBasePlate();
        this.rightBodyStick.xRot = (float)Math.PI / 180 * $$0.getBodyPose().getX();
        this.rightBodyStick.yRot = (float)Math.PI / 180 * $$0.getBodyPose().getY();
        this.rightBodyStick.zRot = (float)Math.PI / 180 * $$0.getBodyPose().getZ();
        this.leftBodyStick.xRot = (float)Math.PI / 180 * $$0.getBodyPose().getX();
        this.leftBodyStick.yRot = (float)Math.PI / 180 * $$0.getBodyPose().getY();
        this.leftBodyStick.zRot = (float)Math.PI / 180 * $$0.getBodyPose().getZ();
        this.shoulderStick.xRot = (float)Math.PI / 180 * $$0.getBodyPose().getX();
        this.shoulderStick.yRot = (float)Math.PI / 180 * $$0.getBodyPose().getY();
        this.shoulderStick.zRot = (float)Math.PI / 180 * $$0.getBodyPose().getZ();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.rightBodyStick, (Object)this.leftBodyStick, (Object)this.shoulderStick, (Object)this.basePlate));
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        ModelPart $$2 = this.getArm($$0);
        boolean $$3 = $$2.visible;
        $$2.visible = true;
        super.translateToHand($$0, $$1);
        $$2.visible = $$3;
    }
}