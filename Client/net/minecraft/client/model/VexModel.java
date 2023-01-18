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
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;

public class VexModel
extends HierarchicalModel<Vex>
implements ArmedModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart head;

    public VexModel(ModelPart $$0) {
        super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityTranslucent));
        this.root = $$0.getChild("root");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.head = this.root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, -2.5f, 0.0f));
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 20.0f, 0.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 4.0f, 2.0f, new CubeDeformation(0.0f)).texOffs(0, 16).addBox(-1.5f, 1.0f, -1.0f, 3.0f, 5.0f, 2.0f, new CubeDeformation(-0.2f)), PartPose.offset(0.0f, 20.0f, 0.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-1.25f, -0.5f, -1.0f, 2.0f, 4.0f, 2.0f, new CubeDeformation(-0.1f)), PartPose.offset(-1.75f, 0.25f, 0.0f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.75f, -0.5f, -1.0f, 2.0f, 4.0f, 2.0f, new CubeDeformation(-0.1f)), PartPose.offset(1.75f, 0.25f, 0.0f));
        $$3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).mirror().addBox(0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)).mirror(false), PartPose.offset(0.5f, 1.0f, 1.0f));
        $$3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(-0.5f, 1.0f, 1.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(Vex $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        float $$6 = Mth.cos($$3 * 5.5f * ((float)Math.PI / 180)) * 0.1f;
        this.rightArm.zRot = 0.62831855f + $$6;
        this.leftArm.zRot = -(0.62831855f + $$6);
        if ($$0.isCharging()) {
            this.body.xRot = 0.0f;
            this.setArmsCharging($$0.getMainHandItem(), $$0.getOffhandItem(), $$6);
        } else {
            this.body.xRot = 0.15707964f;
        }
        this.leftWing.yRot = 1.0995574f + Mth.cos($$3 * 45.836624f * ((float)Math.PI / 180)) * ((float)Math.PI / 180) * 16.2f;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.leftWing.xRot = 0.47123888f;
        this.leftWing.zRot = -0.47123888f;
        this.rightWing.xRot = 0.47123888f;
        this.rightWing.zRot = 0.47123888f;
    }

    private void setArmsCharging(ItemStack $$0, ItemStack $$1, float $$2) {
        if ($$0.isEmpty() && $$1.isEmpty()) {
            this.rightArm.xRot = -1.2217305f;
            this.rightArm.yRot = 0.2617994f;
            this.rightArm.zRot = -0.47123888f - $$2;
            this.leftArm.xRot = -1.2217305f;
            this.leftArm.yRot = -0.2617994f;
            this.leftArm.zRot = 0.47123888f + $$2;
            return;
        }
        if (!$$0.isEmpty()) {
            this.rightArm.xRot = 3.6651914f;
            this.rightArm.yRot = 0.2617994f;
            this.rightArm.zRot = -0.47123888f - $$2;
        }
        if (!$$1.isEmpty()) {
            this.leftArm.xRot = 3.6651914f;
            this.leftArm.yRot = -0.2617994f;
            this.leftArm.zRot = 0.47123888f + $$2;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        boolean $$2 = $$0 == HumanoidArm.RIGHT;
        ModelPart $$3 = $$2 ? this.rightArm : this.leftArm;
        this.root.translateAndRotate($$1);
        this.body.translateAndRotate($$1);
        $$3.translateAndRotate($$1);
        $$1.scale(0.55f, 0.55f, 0.55f);
        this.offsetStackPosition($$1, $$2);
    }

    private void offsetStackPosition(PoseStack $$0, boolean $$1) {
        if ($$1) {
            $$0.translate(0.046875, -0.15625, 0.078125);
        } else {
            $$0.translate(-0.046875, -0.15625, 0.078125);
        }
    }
}