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
import net.minecraft.client.model.ColorableAgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;

public class WolfModel<T extends Wolf>
extends ColorableAgeableListModel<T> {
    private static final String REAL_HEAD = "real_head";
    private static final String UPPER_BODY = "upper_body";
    private static final String REAL_TAIL = "real_tail";
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart upperBody;
    private static final int LEG_SIZE = 8;

    public WolfModel(ModelPart $$0) {
        this.head = $$0.getChild("head");
        this.realHead = this.head.getChild(REAL_HEAD);
        this.body = $$0.getChild("body");
        this.upperBody = $$0.getChild(UPPER_BODY);
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.tail = $$0.getChild("tail");
        this.realTail = this.tail.getChild(REAL_TAIL);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = 13.5f;
        PartDefinition $$3 = $$1.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1.0f, 13.5f, -7.0f));
        $$3.addOrReplaceChild(REAL_HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f).texOffs(16, 14).addBox(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).texOffs(16, 14).addBox(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).texOffs(0, 10).addBox(-0.5f, -0.001f, -5.0f, 3.0f, 3.0f, 4.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f), PartPose.offsetAndRotation(0.0f, 14.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        $$1.addOrReplaceChild(UPPER_BODY, CubeListBuilder.create().texOffs(21, 0).addBox(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f), PartPose.offsetAndRotation(-1.0f, 14.0f, -3.0f, 1.5707964f, 0.0f, 0.0f));
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f);
        $$1.addOrReplaceChild("right_hind_leg", $$4, PartPose.offset(-2.5f, 16.0f, 7.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$4, PartPose.offset(0.5f, 16.0f, 7.0f));
        $$1.addOrReplaceChild("right_front_leg", $$4, PartPose.offset(-2.5f, 16.0f, -4.0f));
        $$1.addOrReplaceChild("left_front_leg", $$4, PartPose.offset(0.5f, 16.0f, -4.0f));
        PartDefinition $$5 = $$1.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));
        $$5.addOrReplaceChild(REAL_TAIL, CubeListBuilder.create().texOffs(9, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.rightHindLeg, (Object)this.leftHindLeg, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg, (Object)this.tail, (Object)this.upperBody);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        this.tail.yRot = $$0.isAngry() ? 0.0f : Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        if (((TamableAnimal)$$0).isInSittingPose()) {
            this.upperBody.setPos(-1.0f, 16.0f, -3.0f);
            this.upperBody.xRot = 1.2566371f;
            this.upperBody.yRot = 0.0f;
            this.body.setPos(0.0f, 18.0f, 0.0f);
            this.body.xRot = 0.7853982f;
            this.tail.setPos(-1.0f, 21.0f, 6.0f);
            this.rightHindLeg.setPos(-2.5f, 22.7f, 2.0f);
            this.rightHindLeg.xRot = 4.712389f;
            this.leftHindLeg.setPos(0.5f, 22.7f, 2.0f);
            this.leftHindLeg.xRot = 4.712389f;
            this.rightFrontLeg.xRot = 5.811947f;
            this.rightFrontLeg.setPos(-2.49f, 17.0f, -4.0f);
            this.leftFrontLeg.xRot = 5.811947f;
            this.leftFrontLeg.setPos(0.51f, 17.0f, -4.0f);
        } else {
            this.body.setPos(0.0f, 14.0f, 2.0f);
            this.body.xRot = 1.5707964f;
            this.upperBody.setPos(-1.0f, 14.0f, -3.0f);
            this.upperBody.xRot = this.body.xRot;
            this.tail.setPos(-1.0f, 12.0f, 8.0f);
            this.rightHindLeg.setPos(-2.5f, 16.0f, 7.0f);
            this.leftHindLeg.setPos(0.5f, 16.0f, 7.0f);
            this.rightFrontLeg.setPos(-2.5f, 16.0f, -4.0f);
            this.leftFrontLeg.setPos(0.5f, 16.0f, -4.0f);
            this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
            this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
            this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
            this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        }
        this.realHead.zRot = ((Wolf)$$0).getHeadRollAngle($$3) + ((Wolf)$$0).getBodyRollAngle($$3, 0.0f);
        this.upperBody.zRot = ((Wolf)$$0).getBodyRollAngle($$3, -0.08f);
        this.body.zRot = ((Wolf)$$0).getBodyRollAngle($$3, -0.16f);
        this.realTail.zRot = ((Wolf)$$0).getBodyRollAngle($$3, -0.2f);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.tail.xRot = $$3;
    }
}