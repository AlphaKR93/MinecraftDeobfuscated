/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModel<T extends LivingEntity>
extends HumanoidModel<T> {
    private static final String EAR = "ear";
    private static final String CLOAK = "cloak";
    private static final String LEFT_SLEEVE = "left_sleeve";
    private static final String RIGHT_SLEEVE = "right_sleeve";
    private static final String LEFT_PANTS = "left_pants";
    private static final String RIGHT_PANTS = "right_pants";
    private final List<ModelPart> parts;
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final ModelPart cloak;
    private final ModelPart ear;
    private final boolean slim;

    public PlayerModel(ModelPart $$02, boolean $$1) {
        super($$02, (Function<ResourceLocation, RenderType>)((Function)RenderType::entityTranslucent));
        this.slim = $$1;
        this.ear = $$02.getChild(EAR);
        this.cloak = $$02.getChild(CLOAK);
        this.leftSleeve = $$02.getChild(LEFT_SLEEVE);
        this.rightSleeve = $$02.getChild(RIGHT_SLEEVE);
        this.leftPants = $$02.getChild(LEFT_PANTS);
        this.rightPants = $$02.getChild(RIGHT_PANTS);
        this.jacket = $$02.getChild("jacket");
        this.parts = (List)$$02.getAllParts().filter($$0 -> !$$0.isEmpty()).collect(ImmutableList.toImmutableList());
    }

    public static MeshDefinition createMesh(CubeDeformation $$0, boolean $$1) {
        MeshDefinition $$2 = HumanoidModel.createMesh($$0, 0.0f);
        PartDefinition $$3 = $$2.getRoot();
        $$3.addOrReplaceChild(EAR, CubeListBuilder.create().texOffs(24, 0).addBox(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, $$0), PartPose.ZERO);
        $$3.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, $$0, 1.0f, 0.5f), PartPose.offset(0.0f, 0.0f, 0.0f));
        float $$4 = 0.25f;
        if ($$1) {
            $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0), PartPose.offset(5.0f, 2.5f, 0.0f));
            $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0), PartPose.offset(-5.0f, 2.5f, 0.0f));
            $$3.addOrReplaceChild(LEFT_SLEEVE, CubeListBuilder.create().texOffs(48, 48).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.offset(5.0f, 2.5f, 0.0f));
            $$3.addOrReplaceChild(RIGHT_SLEEVE, CubeListBuilder.create().texOffs(40, 32).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.offset(-5.0f, 2.5f, 0.0f));
        } else {
            $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(5.0f, 2.0f, 0.0f));
            $$3.addOrReplaceChild(LEFT_SLEEVE, CubeListBuilder.create().texOffs(48, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.offset(5.0f, 2.0f, 0.0f));
            $$3.addOrReplaceChild(RIGHT_SLEEVE, CubeListBuilder.create().texOffs(40, 32).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.offset(-5.0f, 2.0f, 0.0f));
        }
        $$3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(1.9f, 12.0f, 0.0f));
        $$3.addOrReplaceChild(LEFT_PANTS, CubeListBuilder.create().texOffs(0, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.offset(1.9f, 12.0f, 0.0f));
        $$3.addOrReplaceChild(RIGHT_PANTS, CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.offset(-1.9f, 12.0f, 0.0f));
        $$3.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
        return $$2;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.leftPants, (Object)this.rightPants, (Object)this.leftSleeve, (Object)this.rightSleeve, (Object)this.jacket));
    }

    public void renderEars(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3) {
        this.ear.copyFrom(this.head);
        this.ear.x = 0.0f;
        this.ear.y = 0.0f;
        this.ear.render($$0, $$1, $$2, $$3);
    }

    public void renderCloak(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3) {
        this.cloak.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        if (((LivingEntity)$$0).getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            if (((Entity)$$0).isCrouching()) {
                this.cloak.z = 1.4f;
                this.cloak.y = 1.85f;
            } else {
                this.cloak.z = 0.0f;
                this.cloak.y = 0.0f;
            }
        } else if (((Entity)$$0).isCrouching()) {
            this.cloak.z = 0.3f;
            this.cloak.y = 0.8f;
        } else {
            this.cloak.z = -1.1f;
            this.cloak.y = -0.85f;
        }
    }

    @Override
    public void setAllVisible(boolean $$0) {
        super.setAllVisible($$0);
        this.leftSleeve.visible = $$0;
        this.rightSleeve.visible = $$0;
        this.leftPants.visible = $$0;
        this.rightPants.visible = $$0;
        this.jacket.visible = $$0;
        this.cloak.visible = $$0;
        this.ear.visible = $$0;
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        ModelPart $$2 = this.getArm($$0);
        if (this.slim) {
            float $$3 = 0.5f * (float)($$0 == HumanoidArm.RIGHT ? 1 : -1);
            $$2.x += $$3;
            $$2.translateAndRotate($$1);
            $$2.x -= $$3;
        } else {
            $$2.translateAndRotate($$1);
        }
    }

    public ModelPart getRandomModelPart(RandomSource $$0) {
        return (ModelPart)this.parts.get($$0.nextInt(this.parts.size()));
    }
}