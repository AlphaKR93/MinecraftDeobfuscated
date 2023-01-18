/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Function;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.allay.Allay;

public class AllayModel
extends HierarchicalModel<Allay>
implements ArmedModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_wing;
    private final ModelPart left_wing;
    private static final float FLYING_ANIMATION_X_ROT = 0.7853982f;
    private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464f;
    private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = -1.0471976f;

    public AllayModel(ModelPart $$0) {
        super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityTranslucent));
        this.root = $$0.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_wing = this.body.getChild("right_wing");
        this.left_wing = this.body.getChild("left_wing");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 23.5f, 0.0f));
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -3.99f, 0.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 4.0f, 2.0f, new CubeDeformation(0.0f)).texOffs(0, 16).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 5.0f, 2.0f, new CubeDeformation(-0.2f)), PartPose.offset(0.0f, -4.0f, 0.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75f, -0.5f, -1.0f, 1.0f, 4.0f, 2.0f, new CubeDeformation(-0.01f)), PartPose.offset(-1.75f, 0.5f, 0.0f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25f, -0.5f, -1.0f, 1.0f, 4.0f, 2.0f, new CubeDeformation(-0.01f)), PartPose.offset(1.75f, 0.5f, 0.0f));
        $$3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0f, 1.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(-0.5f, 0.0f, 0.6f));
        $$3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0f, 1.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(0.5f, 0.0f, 0.6f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(Allay $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float $$6 = $$3 * 20.0f * ((float)Math.PI / 180) + $$1;
        float $$7 = Mth.cos($$6) * (float)Math.PI * 0.15f + $$2;
        float $$8 = $$3 - (float)$$0.tickCount;
        float $$9 = $$3 * 9.0f * ((float)Math.PI / 180);
        float $$10 = Math.min((float)($$2 / 0.3f), (float)1.0f);
        float $$11 = 1.0f - $$10;
        float $$12 = $$0.getHoldingItemAnimationProgress($$8);
        if ($$0.isDancing()) {
            float $$13 = $$3 * 8.0f * ((float)Math.PI / 180) + $$2;
            float $$14 = Mth.cos($$13) * 16.0f * ((float)Math.PI / 180);
            float $$15 = $$0.getSpinningProgress($$8);
            float $$16 = Mth.cos($$13) * 14.0f * ((float)Math.PI / 180);
            float $$17 = Mth.cos($$13) * 30.0f * ((float)Math.PI / 180);
            this.root.yRot = $$0.isSpinning() ? (float)Math.PI * 4 * $$15 : this.root.yRot;
            this.root.zRot = $$14 * (1.0f - $$15);
            this.head.yRot = $$17 * (1.0f - $$15);
            this.head.zRot = $$16 * (1.0f - $$15);
        } else {
            this.head.xRot = $$5 * ((float)Math.PI / 180);
            this.head.yRot = $$4 * ((float)Math.PI / 180);
        }
        this.right_wing.xRot = 0.43633232f * (1.0f - $$10);
        this.right_wing.yRot = -0.7853982f + $$7;
        this.left_wing.xRot = 0.43633232f * (1.0f - $$10);
        this.left_wing.yRot = 0.7853982f - $$7;
        this.body.xRot = $$10 * 0.7853982f;
        float $$18 = $$12 * Mth.lerp($$10, -1.0471976f, -1.134464f);
        this.root.y += (float)Math.cos((double)$$9) * 0.25f * $$11;
        this.right_arm.xRot = $$18;
        this.left_arm.xRot = $$18;
        float $$19 = $$11 * (1.0f - $$12);
        float $$20 = 0.43633232f - Mth.cos($$9 + 4.712389f) * (float)Math.PI * 0.075f * $$19;
        this.left_arm.zRot = -$$20;
        this.right_arm.zRot = $$20;
        this.right_arm.yRot = 0.27925268f * $$12;
        this.left_arm.yRot = -0.27925268f * $$12;
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        float $$2 = 1.0f;
        float $$3 = 3.0f;
        this.root.translateAndRotate($$1);
        this.body.translateAndRotate($$1);
        $$1.translate(0.0f, 0.0625f, 0.1875f);
        $$1.mulPose(Axis.XP.rotation(this.right_arm.xRot));
        $$1.scale(0.7f, 0.7f, 0.7f);
        $$1.translate(0.0625f, 0.0f, 0.0f);
    }
}