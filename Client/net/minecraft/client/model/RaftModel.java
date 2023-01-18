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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class RaftModel
extends ListModel<Boat> {
    private static final String LEFT_PADDLE = "left_paddle";
    private static final String RIGHT_PADDLE = "right_paddle";
    private static final String BOTTOM = "bottom";
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ImmutableList<ModelPart> parts;

    public RaftModel(ModelPart $$0) {
        this.leftPaddle = $$0.getChild(LEFT_PADDLE);
        this.rightPaddle = $$0.getChild(RIGHT_PADDLE);
        this.parts = this.createPartsBuilder($$0).build();
    }

    protected ImmutableList.Builder<ModelPart> createPartsBuilder(ModelPart $$0) {
        ImmutableList.Builder $$1 = new ImmutableList.Builder();
        $$1.add((Object[])new ModelPart[]{$$0.getChild(BOTTOM), this.leftPaddle, this.rightPaddle});
        return $$1;
    }

    public static void createChildren(PartDefinition $$0) {
        $$0.addOrReplaceChild(BOTTOM, CubeListBuilder.create().texOffs(0, 0).addBox(-14.0f, -11.0f, -4.0f, 28.0f, 20.0f, 4.0f).texOffs(0, 0).addBox(-14.0f, -9.0f, -8.0f, 28.0f, 16.0f, 4.0f), PartPose.offsetAndRotation(0.0f, -3.0f, 1.0f, 1.5708f, 0.0f, 0.0f));
        int $$1 = 20;
        int $$2 = 7;
        int $$3 = 6;
        float $$4 = -5.0f;
        $$0.addOrReplaceChild(LEFT_PADDLE, CubeListBuilder.create().texOffs(0, 24).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).addBox(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), PartPose.offsetAndRotation(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        $$0.addOrReplaceChild(RIGHT_PADDLE, CubeListBuilder.create().texOffs(40, 24).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).addBox(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), PartPose.offsetAndRotation(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
    }

    public static LayerDefinition createBodyModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        RaftModel.createChildren($$1);
        return LayerDefinition.create($$0, 128, 64);
    }

    @Override
    public void setupAnim(Boat $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        RaftModel.animatePaddle($$0, 0, this.leftPaddle, $$1);
        RaftModel.animatePaddle($$0, 1, this.rightPaddle, $$1);
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
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