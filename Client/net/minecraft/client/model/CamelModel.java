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
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class CamelModel<T extends Camel>
extends HierarchicalModel<T> {
    private static final float MAX_WALK_ANIMATION_SPEED = 2.0f;
    private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5f;
    private static final String SADDLE = "saddle";
    private static final String BRIDLE = "bridle";
    private static final String REINS = "reins";
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;

    public CamelModel(ModelPart $$0) {
        this.root = $$0;
        ModelPart $$1 = $$0.getChild("body");
        this.head = $$1.getChild("head");
        this.saddleParts = new ModelPart[]{$$1.getChild(SADDLE), this.head.getChild(BRIDLE)};
        this.ridingParts = new ModelPart[]{this.head.getChild(REINS)};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeDeformation $$2 = new CubeDeformation(0.1f);
        PartDefinition $$3 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f), PartPose.offset(0.0f, 4.0f, 9.5f));
        $$3.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(74, 0).addBox(-4.5f, -5.0f, -5.5f, 9.0f, 5.0f, 11.0f), PartPose.offset(0.0f, -12.0f, -10.0f));
        $$3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(122, 0).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 0.0f), PartPose.offset(0.0f, -9.0f, 3.5f));
        PartDefinition $$4 = $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 24).addBox(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f).texOffs(21, 0).addBox(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f).texOffs(50, 0).addBox(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f), PartPose.offset(0.0f, -3.0f, -19.5f));
        $$4.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), PartPose.offset(3.0f, -21.0f, -9.5f));
        $$4.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(67, 0).addBox(-2.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), PartPose.offset(-3.0f, -21.0f, -9.5f));
        $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(58, 16).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(4.9f, 1.0f, 9.5f));
        $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(94, 16).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(-4.9f, 1.0f, 9.5f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(4.9f, 1.0f, -10.5f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), PartPose.offset(-4.9f, 1.0f, -10.5f));
        $$3.addOrReplaceChild(SADDLE, CubeListBuilder.create().texOffs(74, 64).addBox(-4.5f, -17.0f, -15.5f, 9.0f, 5.0f, 11.0f, $$2).texOffs(92, 114).addBox(-3.5f, -20.0f, -15.5f, 7.0f, 3.0f, 11.0f, $$2).texOffs(0, 89).addBox(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f, $$2), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$4.addOrReplaceChild(REINS, CubeListBuilder.create().texOffs(98, 42).addBox(3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f).texOffs(84, 57).addBox(-3.5f, -18.0f, -2.0f, 7.0f, 7.0f, 0.0f).texOffs(98, 42).addBox(-3.51f, -18.0f, -17.0f, 0.0f, 7.0f, 15.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$4.addOrReplaceChild(BRIDLE, CubeListBuilder.create().texOffs(60, 87).addBox(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f, $$2).texOffs(21, 64).addBox(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f, $$2).texOffs(50, 64).addBox(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f, $$2).texOffs(74, 70).addBox(2.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f).texOffs(74, 70).mirror().addBox(-3.5f, -19.0f, -18.0f, 1.0f, 2.0f, 2.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 128, 128);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation($$0, $$4, $$5, $$3);
        this.toggleInvisibleParts($$0);
        this.animateWalk(CamelAnimation.CAMEL_WALK, $$1, $$2, 2.0f, 2.5f);
        this.animate(((Camel)$$0).sitAnimationState, CamelAnimation.CAMEL_SIT, $$3, 1.0f);
        this.animate(((Camel)$$0).sitPoseAnimationState, CamelAnimation.CAMEL_SIT_POSE, $$3, 1.0f);
        this.animate(((Camel)$$0).sitUpAnimationState, CamelAnimation.CAMEL_STANDUP, $$3, 1.0f);
        this.animate(((Camel)$$0).idleAnimationState, CamelAnimation.CAMEL_IDLE, $$3, 1.0f);
        this.animate(((Camel)$$0).dashAnimationState, CamelAnimation.CAMEL_DASH, $$3, 1.0f);
    }

    private void applyHeadRotation(T $$0, float $$1, float $$2, float $$3) {
        $$1 = Mth.clamp($$1, -30.0f, 30.0f);
        $$2 = Mth.clamp($$2, -25.0f, 45.0f);
        if (((Camel)$$0).getJumpCooldown() > 0) {
            float $$4 = $$3 - (float)((Camel)$$0).tickCount;
            float $$5 = 45.0f * ((float)((Camel)$$0).getJumpCooldown() - $$4) / 55.0f;
            $$2 = Mth.clamp($$2 + $$5, -25.0f, 70.0f);
        }
        this.head.yRot = $$1 * ((float)Math.PI / 180);
        this.head.xRot = $$2 * ((float)Math.PI / 180);
    }

    private void toggleInvisibleParts(T $$0) {
        boolean $$1 = ((AbstractHorse)$$0).isSaddled();
        boolean $$2 = ((Entity)$$0).isVehicle();
        for (ModelPart $$3 : this.saddleParts) {
            $$3.visible = $$1;
        }
        for (ModelPart $$4 : this.ridingParts) {
            $$4.visible = $$2 && $$1;
        }
    }

    @Override
    public void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        if (this.young) {
            float $$8 = 2.0f;
            float $$9 = 1.1f;
            $$0.pushPose();
            $$0.scale(0.45454544f, 0.41322312f, 0.45454544f);
            $$0.translate(0.0f, 2.0625f, 0.0f);
            this.root().render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$0.popPose();
        } else {
            this.root().render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}