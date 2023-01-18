/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

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

public class CreeperModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private static final int Y_OFFSET = 6;

    public CreeperModel(ModelPart $$0) {
        this.root = $$0;
        this.head = $$0.getChild("head");
        this.leftHindLeg = $$0.getChild("right_hind_leg");
        this.rightHindLeg = $$0.getChild("left_hind_leg");
        this.leftFrontLeg = $$0.getChild("right_front_leg");
        this.rightFrontLeg = $$0.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.offset(0.0f, 6.0f, 0.0f));
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0), PartPose.offset(0.0f, 6.0f, 0.0f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, $$0);
        $$2.addOrReplaceChild("right_hind_leg", $$3, PartPose.offset(-2.0f, 18.0f, 4.0f));
        $$2.addOrReplaceChild("left_hind_leg", $$3, PartPose.offset(2.0f, 18.0f, 4.0f));
        $$2.addOrReplaceChild("right_front_leg", $$3, PartPose.offset(-2.0f, 18.0f, -4.0f));
        $$2.addOrReplaceChild("left_front_leg", $$3, PartPose.offset(2.0f, 18.0f, -4.0f));
        return LayerDefinition.create($$1, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
    }
}