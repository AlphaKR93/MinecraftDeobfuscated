/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.FrogAnimation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogModel<T extends Frog>
extends HierarchicalModel<T> {
    private static final float MAX_WALK_ANIMATION_SPEED = 1.5f;
    private static final float MAX_SWIM_ANIMATION_SPEED = 1.0f;
    private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5f;
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart tongue;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart croakingBody;

    public FrogModel(ModelPart $$0) {
        this.root = $$0.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.tongue = this.body.getChild("tongue");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.croakingBody = this.body.getChild("croaking_body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 1).addBox(-3.5f, -2.0f, -8.0f, 7.0f, 3.0f, 9.0f).texOffs(23, 22).addBox(-3.5f, -1.0f, -8.0f, 7.0f, 0.0f, 9.0f), PartPose.offset(0.0f, -2.0f, 4.0f));
        PartDefinition $$4 = $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(23, 13).addBox(-3.5f, -1.0f, -7.0f, 7.0f, 0.0f, 9.0f).texOffs(0, 13).addBox(-3.5f, -2.0f, -7.0f, 7.0f, 3.0f, 9.0f), PartPose.offset(0.0f, -2.0f, -1.0f));
        PartDefinition $$5 = $$4.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(-0.5f, 0.0f, 2.0f));
        $$5.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), PartPose.offset(-1.5f, -3.0f, -6.5f));
        $$5.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 5).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), PartPose.offset(2.5f, -3.0f, -6.5f));
        $$3.addOrReplaceChild("croaking_body", CubeListBuilder.create().texOffs(26, 5).addBox(-3.5f, -0.1f, -2.9f, 7.0f, 2.0f, 3.0f, new CubeDeformation(-0.1f)), PartPose.offset(0.0f, -1.0f, -5.0f));
        PartDefinition $$6 = $$3.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(17, 13).addBox(-2.0f, 0.0f, -7.1f, 4.0f, 0.0f, 7.0f), PartPose.offset(0.0f, -1.01f, 1.0f));
        PartDefinition $$7 = $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), PartPose.offset(4.0f, -1.0f, -6.5f));
        $$7.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(18, 40).addBox(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(0.0f, 3.0f, -1.0f));
        PartDefinition $$8 = $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), PartPose.offset(-4.0f, -1.0f, -6.5f));
        $$8.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(2, 40).addBox(-4.0f, 0.01f, -5.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(0.0f, 3.0f, 0.0f));
        PartDefinition $$9 = $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(14, 25).addBox(-1.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), PartPose.offset(3.5f, -3.0f, 4.0f));
        $$9.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(2, 32).addBox(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(2.0f, 3.0f, 0.0f));
        PartDefinition $$10 = $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), PartPose.offset(-3.5f, -3.0f, 4.0f));
        $$10.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(18, 32).addBox(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(-2.0f, 3.0f, 0.0f));
        return LayerDefinition.create($$0, 48, 48);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(((Frog)$$0).jumpAnimationState, FrogAnimation.FROG_JUMP, $$3);
        this.animate(((Frog)$$0).croakAnimationState, FrogAnimation.FROG_CROAK, $$3);
        this.animate(((Frog)$$0).tongueAnimationState, FrogAnimation.FROG_TONGUE, $$3);
        if (((Entity)$$0).isInWaterOrBubble()) {
            this.animateWalk(FrogAnimation.FROG_SWIM, $$1, $$2, 1.0f, 2.5f);
        } else {
            this.animateWalk(FrogAnimation.FROG_WALK, $$1, $$2, 1.5f, 2.5f);
        }
        this.animate(((Frog)$$0).swimIdleAnimationState, FrogAnimation.FROG_IDLE_WATER, $$3);
        this.croakingBody.visible = ((Frog)$$0).croakAnimationState.isStarted();
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}