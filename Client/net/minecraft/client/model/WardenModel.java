/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.animation.definitions.WardenAnimation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenModel<T extends Warden>
extends HierarchicalModel<T> {
    private static final float DEFAULT_ARM_X_Y = 13.0f;
    private static final float DEFAULT_ARM_Z = 1.0f;
    private final ModelPart root;
    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;
    protected final ModelPart leftLeg;
    protected final ModelPart leftArm;
    protected final ModelPart leftRibcage;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;
    protected final ModelPart rightRibcage;
    private final List<ModelPart> tendrilsLayerModelParts;
    private final List<ModelPart> heartLayerModelParts;
    private final List<ModelPart> bioluminescentLayerModelParts;
    private final List<ModelPart> pulsatingSpotsLayerModelParts;

    public WardenModel(ModelPart $$0) {
        super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
        this.root = $$0;
        this.bone = $$0.getChild("bone");
        this.body = this.bone.getChild("body");
        this.head = this.body.getChild("head");
        this.rightLeg = this.bone.getChild("right_leg");
        this.leftLeg = this.bone.getChild("left_leg");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightTendril = this.head.getChild("right_tendril");
        this.leftTendril = this.head.getChild("left_tendril");
        this.rightRibcage = this.body.getChild("right_ribcage");
        this.leftRibcage = this.body.getChild("left_ribcage");
        this.tendrilsLayerModelParts = ImmutableList.of((Object)this.leftTendril, (Object)this.rightTendril);
        this.heartLayerModelParts = ImmutableList.of((Object)this.body);
        this.bioluminescentLayerModelParts = ImmutableList.of((Object)this.head, (Object)this.leftArm, (Object)this.rightArm, (Object)this.leftLeg, (Object)this.rightLeg);
        this.pulsatingSpotsLayerModelParts = ImmutableList.of((Object)this.body, (Object)this.head, (Object)this.leftArm, (Object)this.rightArm, (Object)this.leftLeg, (Object)this.rightLeg);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0f, -13.0f, -4.0f, 18.0f, 21.0f, 11.0f), PartPose.offset(0.0f, -21.0f, 0.0f));
        $$3.addOrReplaceChild("right_ribcage", CubeListBuilder.create().texOffs(90, 11).addBox(-2.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f), PartPose.offset(-7.0f, -2.0f, -4.0f));
        $$3.addOrReplaceChild("left_ribcage", CubeListBuilder.create().texOffs(90, 11).mirror().addBox(-7.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f).mirror(false), PartPose.offset(7.0f, -2.0f, -4.0f));
        PartDefinition $$4 = $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0f, -16.0f, -5.0f, 16.0f, 16.0f, 10.0f), PartPose.offset(0.0f, -13.0f, 0.0f));
        $$4.addOrReplaceChild("right_tendril", CubeListBuilder.create().texOffs(52, 32).addBox(-16.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), PartPose.offset(-8.0f, -12.0f, 0.0f));
        $$4.addOrReplaceChild("left_tendril", CubeListBuilder.create().texOffs(58, 0).addBox(0.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), PartPose.offset(8.0f, -12.0f, 0.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 50).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), PartPose.offset(-13.0f, -13.0f, 1.0f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), PartPose.offset(13.0f, -13.0f, 1.0f));
        $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(76, 48).addBox(-3.1f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), PartPose.offset(-5.9f, -13.0f, 0.0f));
        $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(76, 76).addBox(-2.9f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), PartPose.offset(5.9f, -13.0f, 0.0f));
        return LayerDefinition.create($$0, 128, 128);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float $$6 = $$3 - (float)((Warden)$$0).tickCount;
        this.animateHeadLookTarget($$4, $$5);
        this.animateWalk($$1, $$2);
        this.animateIdlePose($$3);
        this.animateTendrils($$0, $$3, $$6);
        this.animate(((Warden)$$0).attackAnimationState, WardenAnimation.WARDEN_ATTACK, $$3);
        this.animate(((Warden)$$0).sonicBoomAnimationState, WardenAnimation.WARDEN_SONIC_BOOM, $$3);
        this.animate(((Warden)$$0).diggingAnimationState, WardenAnimation.WARDEN_DIG, $$3);
        this.animate(((Warden)$$0).emergeAnimationState, WardenAnimation.WARDEN_EMERGE, $$3);
        this.animate(((Warden)$$0).roarAnimationState, WardenAnimation.WARDEN_ROAR, $$3);
        this.animate(((Warden)$$0).sniffAnimationState, WardenAnimation.WARDEN_SNIFF, $$3);
    }

    private void animateHeadLookTarget(float $$0, float $$1) {
        this.head.xRot = $$1 * ((float)Math.PI / 180);
        this.head.yRot = $$0 * ((float)Math.PI / 180);
    }

    private void animateIdlePose(float $$0) {
        float $$1 = $$0 * 0.1f;
        float $$2 = Mth.cos($$1);
        float $$3 = Mth.sin($$1);
        this.head.zRot += 0.06f * $$2;
        this.head.xRot += 0.06f * $$3;
        this.body.zRot += 0.025f * $$3;
        this.body.xRot += 0.025f * $$2;
    }

    private void animateWalk(float $$0, float $$1) {
        float $$2 = Math.min((float)0.5f, (float)(3.0f * $$1));
        float $$3 = $$0 * 0.8662f;
        float $$4 = Mth.cos($$3);
        float $$5 = Mth.sin($$3);
        float $$6 = Math.min((float)0.35f, (float)$$2);
        this.head.zRot += 0.3f * $$5 * $$2;
        this.head.xRot += 1.2f * Mth.cos($$3 + 1.5707964f) * $$6;
        this.body.zRot = 0.1f * $$5 * $$2;
        this.body.xRot = 1.0f * $$4 * $$6;
        this.leftLeg.xRot = 1.0f * $$4 * $$2;
        this.rightLeg.xRot = 1.0f * Mth.cos($$3 + (float)Math.PI) * $$2;
        this.leftArm.xRot = -(0.8f * $$4 * $$2);
        this.leftArm.zRot = 0.0f;
        this.rightArm.xRot = -(0.8f * $$5 * $$2);
        this.rightArm.zRot = 0.0f;
        this.resetArmPoses();
    }

    private void resetArmPoses() {
        this.leftArm.yRot = 0.0f;
        this.leftArm.z = 1.0f;
        this.leftArm.x = 13.0f;
        this.leftArm.y = -13.0f;
        this.rightArm.yRot = 0.0f;
        this.rightArm.z = 1.0f;
        this.rightArm.x = -13.0f;
        this.rightArm.y = -13.0f;
    }

    private void animateTendrils(T $$0, float $$1, float $$2) {
        float $$3;
        this.leftTendril.xRot = $$3 = ((Warden)$$0).getTendrilAnimation($$2) * (float)(Math.cos((double)((double)$$1 * 2.25)) * Math.PI * (double)0.1f);
        this.rightTendril.xRot = -$$3;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public List<ModelPart> getTendrilsLayerModelParts() {
        return this.tendrilsLayerModelParts;
    }

    public List<ModelPart> getHeartLayerModelParts() {
        return this.heartLayerModelParts;
    }

    public List<ModelPart> getBioluminescentLayerModelParts() {
        return this.bioluminescentLayerModelParts;
    }

    public List<ModelPart> getPulsatingSpotsLayerModelParts() {
        return this.pulsatingSpotsLayerModelParts;
    }
}