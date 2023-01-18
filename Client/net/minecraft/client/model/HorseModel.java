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
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class HorseModel<T extends AbstractHorse>
extends AgeableListModel<T> {
    private static final float DEG_125 = 2.1816616f;
    private static final float DEG_60 = 1.0471976f;
    private static final float DEG_45 = 0.7853982f;
    private static final float DEG_30 = 0.5235988f;
    private static final float DEG_15 = 0.2617994f;
    protected static final String HEAD_PARTS = "head_parts";
    private static final String LEFT_HIND_BABY_LEG = "left_hind_baby_leg";
    private static final String RIGHT_HIND_BABY_LEG = "right_hind_baby_leg";
    private static final String LEFT_FRONT_BABY_LEG = "left_front_baby_leg";
    private static final String RIGHT_FRONT_BABY_LEG = "right_front_baby_leg";
    private static final String SADDLE = "saddle";
    private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
    private static final String LEFT_SADDLE_LINE = "left_saddle_line";
    private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
    private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
    private static final String HEAD_SADDLE = "head_saddle";
    private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
    protected final ModelPart body;
    protected final ModelPart headParts;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindBabyLeg;
    private final ModelPart leftHindBabyLeg;
    private final ModelPart rightFrontBabyLeg;
    private final ModelPart leftFrontBabyLeg;
    private final ModelPart tail;
    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;

    public HorseModel(ModelPart $$0) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.body = $$0.getChild("body");
        this.headParts = $$0.getChild(HEAD_PARTS);
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.rightHindBabyLeg = $$0.getChild(RIGHT_HIND_BABY_LEG);
        this.leftHindBabyLeg = $$0.getChild(LEFT_HIND_BABY_LEG);
        this.rightFrontBabyLeg = $$0.getChild(RIGHT_FRONT_BABY_LEG);
        this.leftFrontBabyLeg = $$0.getChild(LEFT_FRONT_BABY_LEG);
        this.tail = this.body.getChild("tail");
        ModelPart $$1 = this.body.getChild(SADDLE);
        ModelPart $$2 = this.headParts.getChild(LEFT_SADDLE_MOUTH);
        ModelPart $$3 = this.headParts.getChild(RIGHT_SADDLE_MOUTH);
        ModelPart $$4 = this.headParts.getChild(LEFT_SADDLE_LINE);
        ModelPart $$5 = this.headParts.getChild(RIGHT_SADDLE_LINE);
        ModelPart $$6 = this.headParts.getChild(HEAD_SADDLE);
        ModelPart $$7 = this.headParts.getChild(MOUTH_SADDLE_WRAP);
        this.saddleParts = new ModelPart[]{$$1, $$2, $$3, $$6, $$7};
        this.ridingParts = new ModelPart[]{$$4, $$5};
    }

    public static MeshDefinition createBodyMesh(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, new CubeDeformation(0.05f)), PartPose.offset(0.0f, 11.0f, 5.0f));
        PartDefinition $$4 = $$2.addOrReplaceChild(HEAD_PARTS, CubeListBuilder.create().texOffs(0, 35).addBox(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f), PartPose.offsetAndRotation(0.0f, 4.0f, -12.0f, 0.5235988f, 0.0f, 0.0f));
        PartDefinition $$5 = $$4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, $$0), PartPose.ZERO);
        $$4.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, $$0), PartPose.ZERO);
        $$4.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, $$0), PartPose.ZERO);
        $$2.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(-4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(4.0f, 14.0f, -12.0f));
        $$2.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(-4.0f, 14.0f, -12.0f));
        CubeDeformation $$6 = $$0.extend(0.0f, 5.5f, 0.0f);
        $$2.addOrReplaceChild(LEFT_HIND_BABY_LEG, CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$6), PartPose.offset(4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild(RIGHT_HIND_BABY_LEG, CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$6), PartPose.offset(-4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild(LEFT_FRONT_BABY_LEG, CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$6), PartPose.offset(4.0f, 14.0f, -12.0f));
        $$2.addOrReplaceChild(RIGHT_FRONT_BABY_LEG, CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$6), PartPose.offset(-4.0f, 14.0f, -12.0f));
        $$3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, $$0), PartPose.offsetAndRotation(0.0f, -5.0f, 2.0f, 0.5235988f, 0.0f, 0.0f));
        $$3.addOrReplaceChild(SADDLE, CubeListBuilder.create().texOffs(26, 0).addBox(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, new CubeDeformation(0.5f)), PartPose.ZERO);
        $$4.addOrReplaceChild(LEFT_SADDLE_MOUTH, CubeListBuilder.create().texOffs(29, 5).addBox(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, $$0), PartPose.ZERO);
        $$4.addOrReplaceChild(RIGHT_SADDLE_MOUTH, CubeListBuilder.create().texOffs(29, 5).addBox(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, $$0), PartPose.ZERO);
        $$4.addOrReplaceChild(LEFT_SADDLE_LINE, CubeListBuilder.create().texOffs(32, 2).addBox(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, $$0), PartPose.rotation(-0.5235988f, 0.0f, 0.0f));
        $$4.addOrReplaceChild(RIGHT_SADDLE_LINE, CubeListBuilder.create().texOffs(32, 2).addBox(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, $$0), PartPose.rotation(-0.5235988f, 0.0f, 0.0f));
        $$4.addOrReplaceChild(HEAD_SADDLE, CubeListBuilder.create().texOffs(1, 1).addBox(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, new CubeDeformation(0.2f)), PartPose.ZERO);
        $$4.addOrReplaceChild(MOUTH_SADDLE_WRAP, CubeListBuilder.create().texOffs(19, 0).addBox(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, new CubeDeformation(0.2f)), PartPose.ZERO);
        $$5.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new CubeDeformation(-0.001f)), PartPose.ZERO);
        $$5.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new CubeDeformation(-0.001f)), PartPose.ZERO);
        return $$1;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        boolean $$6 = ((AbstractHorse)$$0).isSaddled();
        boolean $$7 = ((Entity)$$0).isVehicle();
        for (ModelPart $$8 : this.saddleParts) {
            $$8.visible = $$6;
        }
        for (ModelPart $$9 : this.ridingParts) {
            $$9.visible = $$7 && $$6;
        }
        this.body.y = 11.0f;
    }

    @Override
    public Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.headParts);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.rightHindLeg, (Object)this.leftHindLeg, (Object)this.rightFrontLeg, (Object)this.leftFrontLeg, (Object)this.rightHindBabyLeg, (Object)this.leftHindBabyLeg, (Object)this.rightFrontBabyLeg, (Object)this.leftFrontBabyLeg);
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        super.prepareMobModel($$0, $$1, $$2, $$3);
        float $$4 = Mth.rotLerp($$3, ((AbstractHorse)$$0).yBodyRotO, ((AbstractHorse)$$0).yBodyRot);
        float $$5 = Mth.rotLerp($$3, ((AbstractHorse)$$0).yHeadRotO, ((AbstractHorse)$$0).yHeadRot);
        float $$6 = Mth.lerp($$3, ((AbstractHorse)$$0).xRotO, ((Entity)$$0).getXRot());
        float $$7 = $$5 - $$4;
        float $$8 = $$6 * ((float)Math.PI / 180);
        if ($$7 > 20.0f) {
            $$7 = 20.0f;
        }
        if ($$7 < -20.0f) {
            $$7 = -20.0f;
        }
        if ($$2 > 0.2f) {
            $$8 += Mth.cos($$1 * 0.4f) * 0.15f * $$2;
        }
        float $$9 = ((AbstractHorse)$$0).getEatAnim($$3);
        float $$10 = ((AbstractHorse)$$0).getStandAnim($$3);
        float $$11 = 1.0f - $$10;
        float $$12 = ((AbstractHorse)$$0).getMouthAnim($$3);
        boolean $$13 = ((AbstractHorse)$$0).tailCounter != 0;
        float $$14 = (float)((AbstractHorse)$$0).tickCount + $$3;
        this.headParts.y = 4.0f;
        this.headParts.z = -12.0f;
        this.body.xRot = 0.0f;
        this.headParts.xRot = 0.5235988f + $$8;
        this.headParts.yRot = $$7 * ((float)Math.PI / 180);
        float $$15 = ((Entity)$$0).isInWater() ? 0.2f : 1.0f;
        float $$16 = Mth.cos($$15 * $$1 * 0.6662f + (float)Math.PI);
        float $$17 = $$16 * 0.8f * $$2;
        float $$18 = (1.0f - Math.max((float)$$10, (float)$$9)) * (0.5235988f + $$8 + $$12 * Mth.sin($$14) * 0.05f);
        this.headParts.xRot = $$10 * (0.2617994f + $$8) + $$9 * (2.1816616f + Mth.sin($$14) * 0.05f) + $$18;
        this.headParts.yRot = $$10 * $$7 * ((float)Math.PI / 180) + (1.0f - Math.max((float)$$10, (float)$$9)) * this.headParts.yRot;
        this.headParts.y = $$10 * -4.0f + $$9 * 11.0f + (1.0f - Math.max((float)$$10, (float)$$9)) * this.headParts.y;
        this.headParts.z = $$10 * -4.0f + $$9 * -12.0f + (1.0f - Math.max((float)$$10, (float)$$9)) * this.headParts.z;
        this.body.xRot = $$10 * -0.7853982f + $$11 * this.body.xRot;
        float $$19 = 0.2617994f * $$10;
        float $$20 = Mth.cos($$14 * 0.6f + (float)Math.PI);
        this.leftFrontLeg.y = 2.0f * $$10 + 14.0f * $$11;
        this.leftFrontLeg.z = -6.0f * $$10 - 10.0f * $$11;
        this.rightFrontLeg.y = this.leftFrontLeg.y;
        this.rightFrontLeg.z = this.leftFrontLeg.z;
        float $$21 = (-1.0471976f + $$20) * $$10 + $$17 * $$11;
        float $$22 = (-1.0471976f - $$20) * $$10 - $$17 * $$11;
        this.leftHindLeg.xRot = $$19 - $$16 * 0.5f * $$2 * $$11;
        this.rightHindLeg.xRot = $$19 + $$16 * 0.5f * $$2 * $$11;
        this.leftFrontLeg.xRot = $$21;
        this.rightFrontLeg.xRot = $$22;
        this.tail.xRot = 0.5235988f + $$2 * 0.75f;
        this.tail.y = -5.0f + $$2;
        this.tail.z = 2.0f + $$2 * 2.0f;
        this.tail.yRot = $$13 ? Mth.cos($$14 * 0.7f) : 0.0f;
        this.rightHindBabyLeg.y = this.rightHindLeg.y;
        this.rightHindBabyLeg.z = this.rightHindLeg.z;
        this.rightHindBabyLeg.xRot = this.rightHindLeg.xRot;
        this.leftHindBabyLeg.y = this.leftHindLeg.y;
        this.leftHindBabyLeg.z = this.leftHindLeg.z;
        this.leftHindBabyLeg.xRot = this.leftHindLeg.xRot;
        this.rightFrontBabyLeg.y = this.rightFrontLeg.y;
        this.rightFrontBabyLeg.z = this.rightFrontLeg.z;
        this.rightFrontBabyLeg.xRot = this.rightFrontLeg.xRot;
        this.leftFrontBabyLeg.y = this.leftFrontLeg.y;
        this.leftFrontBabyLeg.z = this.leftFrontLeg.z;
        this.leftFrontBabyLeg.xRot = this.leftFrontLeg.xRot;
        boolean $$23 = ((AgeableMob)$$0).isBaby();
        this.rightHindLeg.visible = !$$23;
        this.leftHindLeg.visible = !$$23;
        this.rightFrontLeg.visible = !$$23;
        this.leftFrontLeg.visible = !$$23;
        this.rightHindBabyLeg.visible = $$23;
        this.leftHindBabyLeg.visible = $$23;
        this.rightFrontBabyLeg.visible = $$23;
        this.leftFrontBabyLeg.visible = $$23;
        this.body.y = $$23 ? 10.8f : 0.0f;
    }
}