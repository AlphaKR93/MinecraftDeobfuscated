/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

public class BatModel
extends HierarchicalModel<Bat> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;

    public BatModel(ModelPart $$0) {
        this.root = $$0;
        this.head = $$0.getChild("head");
        this.body = $$0.getChild("body");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), PartPose.ZERO);
        $$2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f), PartPose.ZERO);
        $$2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(24, 0).mirror().addBox(1.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f), PartPose.ZERO);
        PartDefinition $$3 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0f, 4.0f, -3.0f, 6.0f, 12.0f, 6.0f).texOffs(0, 34).addBox(-5.0f, 16.0f, 0.0f, 10.0f, 6.0f, 1.0f), PartPose.ZERO);
        PartDefinition $$4 = $$3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(42, 0).addBox(-12.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f), PartPose.ZERO);
        $$4.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(24, 16).addBox(-8.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f), PartPose.offset(-12.0f, 1.0f, 1.5f));
        PartDefinition $$5 = $$3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(42, 0).mirror().addBox(2.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f), PartPose.ZERO);
        $$5.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(24, 16).mirror().addBox(0.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f), PartPose.offset(12.0f, 1.0f, 1.5f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(Bat $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        if ($$0.isResting()) {
            this.head.xRot = $$5 * ((float)Math.PI / 180);
            this.head.yRot = (float)Math.PI - $$4 * ((float)Math.PI / 180);
            this.head.zRot = (float)Math.PI;
            this.head.setPos(0.0f, -2.0f, 0.0f);
            this.rightWing.setPos(-3.0f, 0.0f, 3.0f);
            this.leftWing.setPos(3.0f, 0.0f, 3.0f);
            this.body.xRot = (float)Math.PI;
            this.rightWing.xRot = -0.15707964f;
            this.rightWing.yRot = -1.2566371f;
            this.rightWingTip.yRot = -1.7278761f;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.leftWingTip.yRot = -this.rightWingTip.yRot;
        } else {
            this.head.xRot = $$5 * ((float)Math.PI / 180);
            this.head.yRot = $$4 * ((float)Math.PI / 180);
            this.head.zRot = 0.0f;
            this.head.setPos(0.0f, 0.0f, 0.0f);
            this.rightWing.setPos(0.0f, 0.0f, 0.0f);
            this.leftWing.setPos(0.0f, 0.0f, 0.0f);
            this.body.xRot = 0.7853982f + Mth.cos($$3 * 0.1f) * 0.15f;
            this.body.yRot = 0.0f;
            this.rightWing.yRot = Mth.cos($$3 * 74.48451f * ((float)Math.PI / 180)) * (float)Math.PI * 0.25f;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.rightWingTip.yRot = this.rightWing.yRot * 0.5f;
            this.leftWingTip.yRot = -this.rightWing.yRot * 0.5f;
        }
    }
}