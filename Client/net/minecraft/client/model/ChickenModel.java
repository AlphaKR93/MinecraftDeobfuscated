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
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ChickenModel<T extends Entity>
extends AgeableListModel<T> {
    public static final String RED_THING = "red_thing";
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart beak;
    private final ModelPart redThing;

    public ChickenModel(ModelPart $$0) {
        this.head = $$0.getChild("head");
        this.beak = $$0.getChild("beak");
        this.redThing = $$0.getChild(RED_THING);
        this.body = $$0.getChild("body");
        this.rightLeg = $$0.getChild("right_leg");
        this.leftLeg = $$0.getChild("left_leg");
        this.rightWing = $$0.getChild("right_wing");
        this.leftWing = $$0.getChild("left_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 16;
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), PartPose.offset(0.0f, 15.0f, -4.0f));
        $$1.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(14, 0).addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), PartPose.offset(0.0f, 15.0f, -4.0f));
        $$1.addOrReplaceChild(RED_THING, CubeListBuilder.create().texOffs(14, 4).addBox(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), PartPose.offset(0.0f, 15.0f, -4.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), PartPose.offsetAndRotation(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(26, 0).addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        $$1.addOrReplaceChild("right_leg", $$3, PartPose.offset(-2.0f, 19.0f, 1.0f));
        $$1.addOrReplaceChild("left_leg", $$3, PartPose.offset(1.0f, 19.0f, 1.0f));
        $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, 13).addBox(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), PartPose.offset(-4.0f, 13.0f, 0.0f));
        $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), PartPose.offset(4.0f, 13.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of((Object)this.head, (Object)this.beak, (Object)this.redThing);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of((Object)this.body, (Object)this.rightLeg, (Object)this.leftLeg, (Object)this.rightWing, (Object)this.leftWing);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.beak.xRot = this.head.xRot;
        this.beak.yRot = this.head.yRot;
        this.redThing.xRot = this.head.xRot;
        this.redThing.yRot = this.head.yRot;
        this.rightLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.leftLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.rightWing.zRot = $$3;
        this.leftWing.zRot = -$$3;
    }
}