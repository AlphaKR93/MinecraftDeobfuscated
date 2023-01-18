/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel<T extends Rabbit>
extends EntityModel<T> {
    private static final float REAR_JUMP_ANGLE = 50.0f;
    private static final float FRONT_JUMP_ANGLE = -40.0f;
    private static final String LEFT_HAUNCH = "left_haunch";
    private static final String RIGHT_HAUNCH = "right_haunch";
    private final ModelPart leftRearFoot;
    private final ModelPart rightRearFoot;
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart nose;
    private float jumpRotation;
    private static final float NEW_SCALE = 0.6f;

    public RabbitModel(ModelPart $$0) {
        this.leftRearFoot = $$0.getChild("left_hind_foot");
        this.rightRearFoot = $$0.getChild("right_hind_foot");
        this.leftHaunch = $$0.getChild(LEFT_HAUNCH);
        this.rightHaunch = $$0.getChild(RIGHT_HAUNCH);
        this.body = $$0.getChild("body");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.head = $$0.getChild("head");
        this.rightEar = $$0.getChild("right_ear");
        this.leftEar = $$0.getChild("left_ear");
        this.tail = $$0.getChild("tail");
        this.nose = $$0.getChild("nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(26, 24).addBox(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), PartPose.offset(3.0f, 17.5f, 3.7f));
        $$1.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(8, 24).addBox(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), PartPose.offset(-3.0f, 17.5f, 3.7f));
        $$1.addOrReplaceChild(LEFT_HAUNCH, CubeListBuilder.create().texOffs(30, 15).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), PartPose.offsetAndRotation(3.0f, 17.5f, 3.7f, -0.34906584f, 0.0f, 0.0f));
        $$1.addOrReplaceChild(RIGHT_HAUNCH, CubeListBuilder.create().texOffs(16, 15).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), PartPose.offsetAndRotation(-3.0f, 17.5f, 3.7f, -0.34906584f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f), PartPose.offsetAndRotation(0.0f, 19.0f, 8.0f, -0.34906584f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(8, 15).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), PartPose.offsetAndRotation(3.0f, 17.0f, -1.0f, -0.17453292f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), PartPose.offsetAndRotation(-3.0f, 17.0f, -1.0f, -0.17453292f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f), PartPose.offset(0.0f, 16.0f, -1.0f));
        $$1.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(52, 0).addBox(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), PartPose.offsetAndRotation(0.0f, 16.0f, -1.0f, 0.0f, -0.2617994f, 0.0f));
        $$1.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(58, 0).addBox(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), PartPose.offsetAndRotation(0.0f, 16.0f, -1.0f, 0.0f, 0.2617994f, 0.0f));
        $$1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(52, 6).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f), PartPose.offsetAndRotation(0.0f, 20.0f, 7.0f, -0.3490659f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(32, 9).addBox(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f), PartPose.offset(0.0f, 16.0f, -1.0f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        if (this.young) {
            float $$82 = 1.5f;
            $$0.pushPose();
            $$0.scale(0.56666666f, 0.56666666f, 0.56666666f);
            $$0.translate(0.0f, 1.375f, 0.125f);
            ImmutableList.of((Object)this.head, (Object)this.leftEar, (Object)this.rightEar, (Object)this.nose).forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            $$0.popPose();
            $$0.pushPose();
            $$0.scale(0.4f, 0.4f, 0.4f);
            $$0.translate(0.0f, 2.25f, 0.0f);
            ImmutableList.of((Object)this.leftRearFoot, (Object)this.rightRearFoot, (Object)this.leftHaunch, (Object)this.rightHaunch, (Object)this.body, (Object)this.leftFrontLeg, (Object)this.rightFrontLeg, (Object)this.tail).forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            $$0.popPose();
        } else {
            $$0.pushPose();
            $$0.scale(0.6f, 0.6f, 0.6f);
            $$0.translate(0.0f, 1.0f, 0.0f);
            ImmutableList.of((Object)this.leftRearFoot, (Object)this.rightRearFoot, (Object)this.leftHaunch, (Object)this.rightHaunch, (Object)this.body, (Object)this.leftFrontLeg, (Object)this.rightFrontLeg, (Object)this.head, (Object)this.rightEar, (Object)this.leftEar, (Object)this.tail, (Object)this.nose, (Object[])new ModelPart[0]).forEach($$8 -> $$8.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            $$0.popPose();
        }
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = $$3 - (float)((Rabbit)$$0).tickCount;
        this.nose.xRot = $$5 * ((float)Math.PI / 180);
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.rightEar.xRot = $$5 * ((float)Math.PI / 180);
        this.leftEar.xRot = $$5 * ((float)Math.PI / 180);
        this.nose.yRot = $$4 * ((float)Math.PI / 180);
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.rightEar.yRot = this.nose.yRot - 0.2617994f;
        this.leftEar.yRot = this.nose.yRot + 0.2617994f;
        this.jumpRotation = Mth.sin(((Rabbit)$$0).getJumpCompletion($$6) * (float)Math.PI);
        this.leftHaunch.xRot = (this.jumpRotation * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.rightHaunch.xRot = (this.jumpRotation * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.leftRearFoot.xRot = this.jumpRotation * 50.0f * ((float)Math.PI / 180);
        this.rightRearFoot.xRot = this.jumpRotation * 50.0f * ((float)Math.PI / 180);
        this.leftFrontLeg.xRot = (this.jumpRotation * -40.0f - 11.0f) * ((float)Math.PI / 180);
        this.rightFrontLeg.xRot = (this.jumpRotation * -40.0f - 11.0f) * ((float)Math.PI / 180);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        super.prepareMobModel($$0, $$1, $$2, $$3);
        this.jumpRotation = Mth.sin(((Rabbit)$$0).getJumpCompletion($$3) * (float)Math.PI);
    }
}