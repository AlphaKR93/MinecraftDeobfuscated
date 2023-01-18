/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotModel
extends HierarchicalModel<Parrot> {
    private static final String FEATHER = "feather";
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart head;
    private final ModelPart feather;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ParrotModel(ModelPart $$0) {
        this.root = $$0;
        this.body = $$0.getChild("body");
        this.tail = $$0.getChild("tail");
        this.leftWing = $$0.getChild("left_wing");
        this.rightWing = $$0.getChild("right_wing");
        this.head = $$0.getChild("head");
        this.feather = this.head.getChild(FEATHER);
        this.leftLeg = $$0.getChild("left_leg");
        this.rightLeg = $$0.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), PartPose.offset(0.0f, 16.5f, -3.0f));
        $$1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 21.07f, 1.16f));
        $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), PartPose.offset(1.5f, 16.94f, -2.76f));
        $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), PartPose.offset(-1.5f, 16.94f, -2.76f));
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), PartPose.offset(0.0f, 15.69f, -2.76f));
        $$2.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), PartPose.offset(0.0f, -2.0f, -1.0f));
        $$2.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), PartPose.offset(0.0f, -0.5f, -1.5f));
        $$2.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), PartPose.offset(0.0f, -1.75f, -2.45f));
        $$2.addOrReplaceChild(FEATHER, CubeListBuilder.create().texOffs(2, 18).addBox(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), PartPose.offset(0.0f, -2.15f, 0.15f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        $$1.addOrReplaceChild("left_leg", $$3, PartPose.offset(1.0f, 22.0f, -1.05f));
        $$1.addOrReplaceChild("right_leg", $$3, PartPose.offset(-1.0f, 22.0f, -1.05f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(Parrot $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.setupAnim(ParrotModel.getState($$0), $$0.tickCount, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void prepareMobModel(Parrot $$0, float $$1, float $$2, float $$3) {
        this.prepare(ParrotModel.getState($$0));
    }

    public void renderOnShoulder(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7, int $$8) {
        this.prepare(State.ON_SHOULDER);
        this.setupAnim(State.ON_SHOULDER, $$8, $$4, $$5, 0.0f, $$6, $$7);
        this.root.render($$0, $$1, $$2, $$3);
    }

    private void setupAnim(State $$0, int $$1, float $$2, float $$3, float $$4, float $$5, float $$6) {
        this.head.xRot = $$6 * ((float)Math.PI / 180);
        this.head.yRot = $$5 * ((float)Math.PI / 180);
        this.head.zRot = 0.0f;
        this.head.x = 0.0f;
        this.body.x = 0.0f;
        this.tail.x = 0.0f;
        this.rightWing.x = -1.5f;
        this.leftWing.x = 1.5f;
        switch ($$0) {
            case SITTING: {
                break;
            }
            case PARTY: {
                float $$7 = Mth.cos($$1);
                float $$8 = Mth.sin($$1);
                this.head.x = $$7;
                this.head.y = 15.69f + $$8;
                this.head.xRot = 0.0f;
                this.head.yRot = 0.0f;
                this.head.zRot = Mth.sin($$1) * 0.4f;
                this.body.x = $$7;
                this.body.y = 16.5f + $$8;
                this.leftWing.zRot = -0.0873f - $$4;
                this.leftWing.x = 1.5f + $$7;
                this.leftWing.y = 16.94f + $$8;
                this.rightWing.zRot = 0.0873f + $$4;
                this.rightWing.x = -1.5f + $$7;
                this.rightWing.y = 16.94f + $$8;
                this.tail.x = $$7;
                this.tail.y = 21.07f + $$8;
                break;
            }
            case STANDING: {
                this.leftLeg.xRot += Mth.cos($$2 * 0.6662f) * 1.4f * $$3;
                this.rightLeg.xRot += Mth.cos($$2 * 0.6662f + (float)Math.PI) * 1.4f * $$3;
            }
            default: {
                float $$9 = $$4 * 0.3f;
                this.head.y = 15.69f + $$9;
                this.tail.xRot = 1.015f + Mth.cos($$2 * 0.6662f) * 0.3f * $$3;
                this.tail.y = 21.07f + $$9;
                this.body.y = 16.5f + $$9;
                this.leftWing.zRot = -0.0873f - $$4;
                this.leftWing.y = 16.94f + $$9;
                this.rightWing.zRot = 0.0873f + $$4;
                this.rightWing.y = 16.94f + $$9;
                this.leftLeg.y = 22.0f + $$9;
                this.rightLeg.y = 22.0f + $$9;
            }
        }
    }

    private void prepare(State $$0) {
        this.feather.xRot = -0.2214f;
        this.body.xRot = 0.4937f;
        this.leftWing.xRot = -0.6981f;
        this.leftWing.yRot = (float)(-Math.PI);
        this.rightWing.xRot = -0.6981f;
        this.rightWing.yRot = (float)(-Math.PI);
        this.leftLeg.xRot = -0.0299f;
        this.rightLeg.xRot = -0.0299f;
        this.leftLeg.y = 22.0f;
        this.rightLeg.y = 22.0f;
        this.leftLeg.zRot = 0.0f;
        this.rightLeg.zRot = 0.0f;
        switch ($$0) {
            case FLYING: {
                this.leftLeg.xRot += 0.6981317f;
                this.rightLeg.xRot += 0.6981317f;
                break;
            }
            case SITTING: {
                float $$1 = 1.9f;
                this.head.y = 17.59f;
                this.tail.xRot = 1.5388988f;
                this.tail.y = 22.97f;
                this.body.y = 18.4f;
                this.leftWing.zRot = -0.0873f;
                this.leftWing.y = 18.84f;
                this.rightWing.zRot = 0.0873f;
                this.rightWing.y = 18.84f;
                this.leftLeg.y += 1.9f;
                this.rightLeg.y += 1.9f;
                this.leftLeg.xRot += 1.5707964f;
                this.rightLeg.xRot += 1.5707964f;
                break;
            }
            case PARTY: {
                this.leftLeg.zRot = -0.34906584f;
                this.rightLeg.zRot = 0.34906584f;
                break;
            }
        }
    }

    private static State getState(Parrot $$0) {
        if ($$0.isPartyParrot()) {
            return State.PARTY;
        }
        if ($$0.isInSittingPose()) {
            return State.SITTING;
        }
        if ($$0.isFlying()) {
            return State.FLYING;
        }
        return State.STANDING;
    }

    public static enum State {
        FLYING,
        STANDING,
        SITTING,
        PARTY,
        ON_SHOULDER;

    }
}