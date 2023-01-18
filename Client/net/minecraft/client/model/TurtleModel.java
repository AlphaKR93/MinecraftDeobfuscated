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
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleModel<T extends Turtle>
extends QuadrupedModel<T> {
    private static final String EGG_BELLY = "egg_belly";
    private final ModelPart eggBelly;

    public TurtleModel(ModelPart $$0) {
        super($$0, true, 120.0f, 0.0f, 9.0f, 6.0f, 120);
        this.eggBelly = $$0.getChild(EGG_BELLY);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(3, 0).addBox(-3.0f, -1.0f, -3.0f, 6.0f, 5.0f, 6.0f), PartPose.offset(0.0f, 19.0f, -10.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(7, 37).addBox("shell", -9.5f, 3.0f, -10.0f, 19.0f, 20.0f, 6.0f).texOffs(31, 1).addBox("belly", -5.5f, 3.0f, -13.0f, 11.0f, 18.0f, 3.0f), PartPose.offsetAndRotation(0.0f, 11.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        $$1.addOrReplaceChild(EGG_BELLY, CubeListBuilder.create().texOffs(70, 33).addBox(-4.5f, 3.0f, -14.0f, 9.0f, 18.0f, 1.0f), PartPose.offsetAndRotation(0.0f, 11.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        boolean $$2 = true;
        $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(1, 23).addBox(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f), PartPose.offset(-3.5f, 22.0f, 11.0f));
        $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(1, 12).addBox(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f), PartPose.offset(3.5f, 22.0f, 11.0f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(27, 30).addBox(-13.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f), PartPose.offset(-5.0f, 21.0f, -4.0f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(27, 24).addBox(0.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f), PartPose.offset(5.0f, 21.0f, -4.0f));
        return LayerDefinition.create($$0, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.eggBelly));
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f * 0.6f) * 0.5f * $$2;
        this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * $$2;
        this.rightFrontLeg.zRot = Mth.cos($$1 * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * $$2;
        this.leftFrontLeg.zRot = Mth.cos($$1 * 0.6662f * 0.6f) * 0.5f * $$2;
        this.rightFrontLeg.xRot = 0.0f;
        this.leftFrontLeg.xRot = 0.0f;
        this.rightFrontLeg.yRot = 0.0f;
        this.leftFrontLeg.yRot = 0.0f;
        this.rightHindLeg.yRot = 0.0f;
        this.leftHindLeg.yRot = 0.0f;
        if (!((Entity)$$0).isInWater() && ((Entity)$$0).isOnGround()) {
            float $$6 = ((Turtle)$$0).isLayingEgg() ? 4.0f : 1.0f;
            float $$7 = ((Turtle)$$0).isLayingEgg() ? 2.0f : 1.0f;
            float $$8 = 5.0f;
            this.rightFrontLeg.yRot = Mth.cos($$6 * $$1 * 5.0f + (float)Math.PI) * 8.0f * $$2 * $$7;
            this.rightFrontLeg.zRot = 0.0f;
            this.leftFrontLeg.yRot = Mth.cos($$6 * $$1 * 5.0f) * 8.0f * $$2 * $$7;
            this.leftFrontLeg.zRot = 0.0f;
            this.rightHindLeg.yRot = Mth.cos($$1 * 5.0f + (float)Math.PI) * 3.0f * $$2;
            this.rightHindLeg.xRot = 0.0f;
            this.leftHindLeg.yRot = Mth.cos($$1 * 5.0f) * 3.0f * $$2;
            this.leftHindLeg.xRot = 0.0f;
        }
        this.eggBelly.visible = !this.young && ((Turtle)$$0).hasEgg();
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        boolean $$8 = this.eggBelly.visible;
        if ($$8) {
            $$0.pushPose();
            $$0.translate(0.0f, -0.08f, 0.0f);
        }
        super.renderToBuffer($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        if ($$8) {
            $$0.popPose();
        }
    }
}