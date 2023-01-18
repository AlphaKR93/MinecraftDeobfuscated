/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatModel
extends ListModel<Boat>
implements WaterPatchModel {
    private static final String LEFT_PADDLE = "left_paddle";
    private static final String RIGHT_PADDLE = "right_paddle";
    private static final String WATER_PATCH = "water_patch";
    private static final String BOTTOM = "bottom";
    private static final String BACK = "back";
    private static final String FRONT = "front";
    private static final String RIGHT = "right";
    private static final String LEFT = "left";
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ModelPart waterPatch;
    private final ImmutableList<ModelPart> parts;

    public BoatModel(ModelPart $$0) {
        this.leftPaddle = $$0.getChild(LEFT_PADDLE);
        this.rightPaddle = $$0.getChild(RIGHT_PADDLE);
        this.waterPatch = $$0.getChild(WATER_PATCH);
        this.parts = this.createPartsBuilder($$0).build();
    }

    protected ImmutableList.Builder<ModelPart> createPartsBuilder(ModelPart $$0) {
        ImmutableList.Builder $$1 = new ImmutableList.Builder();
        $$1.add((Object[])new ModelPart[]{$$0.getChild(BOTTOM), $$0.getChild(BACK), $$0.getChild(FRONT), $$0.getChild(RIGHT), $$0.getChild(LEFT), this.leftPaddle, this.rightPaddle});
        return $$1;
    }

    public static void createChildren(PartDefinition $$0) {
        int $$1 = 32;
        int $$2 = 6;
        int $$3 = 20;
        int $$4 = 4;
        int $$5 = 28;
        $$0.addOrReplaceChild(BOTTOM, CubeListBuilder.create().texOffs(0, 0).addBox(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), PartPose.offsetAndRotation(0.0f, 3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        $$0.addOrReplaceChild(BACK, CubeListBuilder.create().texOffs(0, 19).addBox(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f), PartPose.offsetAndRotation(-15.0f, 4.0f, 4.0f, 0.0f, 4.712389f, 0.0f));
        $$0.addOrReplaceChild(FRONT, CubeListBuilder.create().texOffs(0, 27).addBox(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f), PartPose.offsetAndRotation(15.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        $$0.addOrReplaceChild(RIGHT, CubeListBuilder.create().texOffs(0, 35).addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), PartPose.offsetAndRotation(0.0f, 4.0f, -9.0f, 0.0f, (float)Math.PI, 0.0f));
        $$0.addOrReplaceChild(LEFT, CubeListBuilder.create().texOffs(0, 43).addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), PartPose.offset(0.0f, 4.0f, 9.0f));
        int $$6 = 20;
        int $$7 = 7;
        int $$8 = 6;
        float $$9 = -5.0f;
        $$0.addOrReplaceChild(LEFT_PADDLE, CubeListBuilder.create().texOffs(62, 0).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).addBox(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), PartPose.offsetAndRotation(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        $$0.addOrReplaceChild(RIGHT_PADDLE, CubeListBuilder.create().texOffs(62, 20).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).addBox(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), PartPose.offsetAndRotation(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
        $$0.addOrReplaceChild(WATER_PATCH, CubeListBuilder.create().texOffs(0, 0).addBox(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), PartPose.offsetAndRotation(0.0f, -3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
    }

    public static LayerDefinition createBodyModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        BoatModel.createChildren($$1);
        return LayerDefinition.create($$0, 128, 64);
    }

    @Override
    public void setupAnim(Boat $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        BoatModel.animatePaddle($$0, 0, this.leftPaddle, $$1);
        BoatModel.animatePaddle($$0, 1, this.rightPaddle, $$1);
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    private static void animatePaddle(Boat $$0, int $$1, ModelPart $$2, float $$3) {
        float $$4 = $$0.getRowingTime($$1, $$3);
        $$2.xRot = Mth.clampedLerp(-1.0471976f, -0.2617994f, (Mth.sin(-$$4) + 1.0f) / 2.0f);
        $$2.yRot = Mth.clampedLerp(-0.7853982f, 0.7853982f, (Mth.sin(-$$4 + 1.0f) + 1.0f) / 2.0f);
        if ($$1 == 1) {
            $$2.yRot = (float)Math.PI - $$2.yRot;
        }
    }
}