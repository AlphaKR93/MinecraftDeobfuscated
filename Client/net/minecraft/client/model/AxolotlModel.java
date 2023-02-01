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
 *  java.util.Map
 *  org.joml.Vector3f
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.joml.Vector3f;

public class AxolotlModel<T extends Axolotl>
extends AgeableListModel<T> {
    public static final float SWIMMING_LEG_XROT = 1.8849558f;
    private final ModelPart tail;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart topGills;
    private final ModelPart leftGills;
    private final ModelPart rightGills;

    public AxolotlModel(ModelPart $$0) {
        super(true, 8.0f, 3.35f);
        this.body = $$0.getChild("body");
        this.head = this.body.getChild("head");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
        this.topGills = this.head.getChild("top_gills");
        this.leftGills = this.head.getChild("left_gills");
        this.rightGills = this.head.getChild("right_gills");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-4.0f, -2.0f, -9.0f, 8.0f, 4.0f, 10.0f).texOffs(2, 17).addBox(0.0f, -3.0f, -8.0f, 0.0f, 5.0f, 9.0f), PartPose.offset(0.0f, 20.0f, 5.0f));
        CubeDeformation $$3 = new CubeDeformation(0.001f);
        PartDefinition $$4 = $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0f, -3.0f, -5.0f, 8.0f, 5.0f, 5.0f, $$3), PartPose.offset(0.0f, 0.0f, -9.0f));
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(3, 37).addBox(-4.0f, -3.0f, 0.0f, 8.0f, 3.0f, 0.0f, $$3);
        CubeListBuilder $$6 = CubeListBuilder.create().texOffs(0, 40).addBox(-3.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, $$3);
        CubeListBuilder $$7 = CubeListBuilder.create().texOffs(11, 40).addBox(0.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, $$3);
        $$4.addOrReplaceChild("top_gills", $$5, PartPose.offset(0.0f, -3.0f, -1.0f));
        $$4.addOrReplaceChild("left_gills", $$6, PartPose.offset(-4.0f, 0.0f, -1.0f));
        $$4.addOrReplaceChild("right_gills", $$7, PartPose.offset(4.0f, 0.0f, -1.0f));
        CubeListBuilder $$8 = CubeListBuilder.create().texOffs(2, 13).addBox(-1.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, $$3);
        CubeListBuilder $$9 = CubeListBuilder.create().texOffs(2, 13).addBox(-2.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, $$3);
        $$2.addOrReplaceChild("right_hind_leg", $$9, PartPose.offset(-3.5f, 1.0f, -1.0f));
        $$2.addOrReplaceChild("left_hind_leg", $$8, PartPose.offset(3.5f, 1.0f, -1.0f));
        $$2.addOrReplaceChild("right_front_leg", $$9, PartPose.offset(-3.5f, 1.0f, -8.0f));
        $$2.addOrReplaceChild("left_front_leg", $$8, PartPose.offset(3.5f, 1.0f, -8.0f));
        $$2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(2, 19).addBox(0.0f, -3.0f, 0.0f, 0.0f, 5.0f, 12.0f), PartPose.offset(0.0f, 0.0f, 1.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        boolean $$6;
        this.setupInitialAnimationValues($$0, $$4, $$5);
        if (((Axolotl)$$0).isPlayingDead()) {
            this.setupPlayDeadAnimation($$4);
            this.saveAnimationValues($$0);
            return;
        }
        boolean bl = $$6 = $$2 > 1.0E-5f || ((Entity)$$0).getXRot() != ((Axolotl)$$0).xRotO || ((Entity)$$0).getYRot() != ((Axolotl)$$0).yRotO;
        if (((Entity)$$0).isInWaterOrBubble()) {
            if ($$6) {
                this.setupSwimmingAnimation($$3, $$5);
            } else {
                this.setupWaterHoveringAnimation($$3);
            }
            this.saveAnimationValues($$0);
            return;
        }
        if (((Entity)$$0).isOnGround()) {
            if ($$6) {
                this.setupGroundCrawlingAnimation($$3, $$4);
            } else {
                this.setupLayStillOnGroundAnimation($$3, $$4);
            }
        }
        this.saveAnimationValues($$0);
    }

    private void saveAnimationValues(T $$0) {
        Map<String, Vector3f> $$1 = ((Axolotl)$$0).getModelRotationValues();
        $$1.put((Object)"body", (Object)this.getRotationVector(this.body));
        $$1.put((Object)"head", (Object)this.getRotationVector(this.head));
        $$1.put((Object)"right_hind_leg", (Object)this.getRotationVector(this.rightHindLeg));
        $$1.put((Object)"left_hind_leg", (Object)this.getRotationVector(this.leftHindLeg));
        $$1.put((Object)"right_front_leg", (Object)this.getRotationVector(this.rightFrontLeg));
        $$1.put((Object)"left_front_leg", (Object)this.getRotationVector(this.leftFrontLeg));
        $$1.put((Object)"tail", (Object)this.getRotationVector(this.tail));
        $$1.put((Object)"top_gills", (Object)this.getRotationVector(this.topGills));
        $$1.put((Object)"left_gills", (Object)this.getRotationVector(this.leftGills));
        $$1.put((Object)"right_gills", (Object)this.getRotationVector(this.rightGills));
    }

    private Vector3f getRotationVector(ModelPart $$0) {
        return new Vector3f($$0.xRot, $$0.yRot, $$0.zRot);
    }

    private void setRotationFromVector(ModelPart $$0, Vector3f $$1) {
        $$0.setRotation($$1.x(), $$1.y(), $$1.z());
    }

    private void setupInitialAnimationValues(T $$0, float $$1, float $$2) {
        this.body.x = 0.0f;
        this.head.y = 0.0f;
        this.body.y = 20.0f;
        Map<String, Vector3f> $$3 = ((Axolotl)$$0).getModelRotationValues();
        if ($$3.isEmpty()) {
            this.body.setRotation($$2 * ((float)Math.PI / 180), $$1 * ((float)Math.PI / 180), 0.0f);
            this.head.setRotation(0.0f, 0.0f, 0.0f);
            this.leftHindLeg.setRotation(0.0f, 0.0f, 0.0f);
            this.rightHindLeg.setRotation(0.0f, 0.0f, 0.0f);
            this.leftFrontLeg.setRotation(0.0f, 0.0f, 0.0f);
            this.rightFrontLeg.setRotation(0.0f, 0.0f, 0.0f);
            this.leftGills.setRotation(0.0f, 0.0f, 0.0f);
            this.rightGills.setRotation(0.0f, 0.0f, 0.0f);
            this.topGills.setRotation(0.0f, 0.0f, 0.0f);
            this.tail.setRotation(0.0f, 0.0f, 0.0f);
        } else {
            this.setRotationFromVector(this.body, (Vector3f)$$3.get((Object)"body"));
            this.setRotationFromVector(this.head, (Vector3f)$$3.get((Object)"head"));
            this.setRotationFromVector(this.leftHindLeg, (Vector3f)$$3.get((Object)"left_hind_leg"));
            this.setRotationFromVector(this.rightHindLeg, (Vector3f)$$3.get((Object)"right_hind_leg"));
            this.setRotationFromVector(this.leftFrontLeg, (Vector3f)$$3.get((Object)"left_front_leg"));
            this.setRotationFromVector(this.rightFrontLeg, (Vector3f)$$3.get((Object)"right_front_leg"));
            this.setRotationFromVector(this.leftGills, (Vector3f)$$3.get((Object)"left_gills"));
            this.setRotationFromVector(this.rightGills, (Vector3f)$$3.get((Object)"right_gills"));
            this.setRotationFromVector(this.topGills, (Vector3f)$$3.get((Object)"top_gills"));
            this.setRotationFromVector(this.tail, (Vector3f)$$3.get((Object)"tail"));
        }
    }

    private float lerpTo(float $$0, float $$1) {
        return this.lerpTo(0.05f, $$0, $$1);
    }

    private float lerpTo(float $$0, float $$1, float $$2) {
        return Mth.rotLerp($$0, $$1, $$2);
    }

    private void lerpPart(ModelPart $$0, float $$1, float $$2, float $$3) {
        $$0.setRotation(this.lerpTo($$0.xRot, $$1), this.lerpTo($$0.yRot, $$2), this.lerpTo($$0.zRot, $$3));
    }

    private void setupLayStillOnGroundAnimation(float $$0, float $$1) {
        float $$2 = $$0 * 0.09f;
        float $$3 = Mth.sin($$2);
        float $$4 = Mth.cos($$2);
        float $$5 = $$3 * $$3 - 2.0f * $$3;
        float $$6 = $$4 * $$4 - 3.0f * $$3;
        this.head.xRot = this.lerpTo(this.head.xRot, -0.09f * $$5);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, -0.2f);
        this.tail.yRot = this.lerpTo(this.tail.yRot, -0.1f + 0.1f * $$5);
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6f + 0.05f * $$6);
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.lerpPart(this.leftHindLeg, 1.1f, 1.0f, 0.0f);
        this.lerpPart(this.leftFrontLeg, 0.8f, 2.3f, -0.5f);
        this.applyMirrorLegRotations();
        this.body.xRot = this.lerpTo(0.2f, this.body.xRot, 0.0f);
        this.body.yRot = this.lerpTo(this.body.yRot, $$1 * ((float)Math.PI / 180));
        this.body.zRot = this.lerpTo(this.body.zRot, 0.0f);
    }

    private void setupGroundCrawlingAnimation(float $$0, float $$1) {
        float $$2 = $$0 * 0.11f;
        float $$3 = Mth.cos($$2);
        float $$4 = ($$3 * $$3 - 2.0f * $$3) / 5.0f;
        float $$5 = 0.7f * $$3;
        this.head.xRot = this.lerpTo(this.head.xRot, 0.0f);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.09f * $$3);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
        this.tail.yRot = this.lerpTo(this.tail.yRot, this.head.yRot);
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6f - 0.08f * ($$3 * $$3 + 2.0f * Mth.sin($$2)));
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.lerpPart(this.leftHindLeg, 0.9424779f, 1.5f - $$4, -0.1f);
        this.lerpPart(this.leftFrontLeg, 1.0995574f, 1.5707964f - $$5, 0.0f);
        this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -1.0f - $$4, 0.0f);
        this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -1.5707964f - $$5, 0.0f);
        this.body.xRot = this.lerpTo(0.2f, this.body.xRot, 0.0f);
        this.body.yRot = this.lerpTo(this.body.yRot, $$1 * ((float)Math.PI / 180));
        this.body.zRot = this.lerpTo(this.body.zRot, 0.0f);
    }

    private void setupWaterHoveringAnimation(float $$0) {
        float $$1 = $$0 * 0.075f;
        float $$2 = Mth.cos($$1);
        float $$3 = Mth.sin($$1) * 0.15f;
        this.body.xRot = this.lerpTo(this.body.xRot, -0.15f + 0.075f * $$2);
        this.body.y -= $$3;
        this.head.xRot = this.lerpTo(this.head.xRot, -this.body.xRot);
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.2f * $$2);
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -0.3f * $$2 - 0.19f);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.lerpPart(this.leftHindLeg, 2.3561945f - $$2 * 0.11f, 0.47123894f, 1.7278761f);
        this.lerpPart(this.leftFrontLeg, 0.7853982f - $$2 * 0.2f, 2.042035f, 0.0f);
        this.applyMirrorLegRotations();
        this.tail.yRot = this.lerpTo(this.tail.yRot, 0.5f * $$2);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
    }

    private void setupSwimmingAnimation(float $$0, float $$1) {
        float $$2 = $$0 * 0.33f;
        float $$3 = Mth.sin($$2);
        float $$4 = Mth.cos($$2);
        float $$5 = 0.13f * $$3;
        this.body.xRot = this.lerpTo(0.1f, this.body.xRot, $$1 * ((float)Math.PI / 180) + $$5);
        this.head.xRot = -$$5 * 1.8f;
        this.body.y -= 0.45f * $$4;
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, -0.5f * $$3 - 0.8f);
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, 0.3f * $$3 + 0.9f);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.tail.yRot = this.lerpTo(this.tail.yRot, 0.3f * Mth.cos($$2 * 0.9f));
        this.lerpPart(this.leftHindLeg, 1.8849558f, -0.4f * $$3, 1.5707964f);
        this.lerpPart(this.leftFrontLeg, 1.8849558f, -0.2f * $$4 - 0.1f, 1.5707964f);
        this.applyMirrorLegRotations();
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
    }

    private void setupPlayDeadAnimation(float $$0) {
        this.lerpPart(this.leftHindLeg, 1.4137167f, 1.0995574f, 0.7853982f);
        this.lerpPart(this.leftFrontLeg, 0.7853982f, 2.042035f, 0.0f);
        this.body.xRot = this.lerpTo(this.body.xRot, -0.15f);
        this.body.zRot = this.lerpTo(this.body.zRot, 0.35f);
        this.applyMirrorLegRotations();
        this.body.yRot = this.lerpTo(this.body.yRot, $$0 * ((float)Math.PI / 180));
        this.head.xRot = this.lerpTo(this.head.xRot, 0.0f);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
        this.tail.yRot = this.lerpTo(this.tail.yRot, 0.0f);
        this.lerpPart(this.topGills, 0.0f, 0.0f, 0.0f);
        this.lerpPart(this.leftGills, 0.0f, 0.0f, 0.0f);
        this.lerpPart(this.rightGills, 0.0f, 0.0f, 0.0f);
    }

    private void applyMirrorLegRotations() {
        this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -this.leftHindLeg.yRot, -this.leftHindLeg.zRot);
        this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -this.leftFrontLeg.yRot, -this.leftFrontLeg.zRot);
    }
}