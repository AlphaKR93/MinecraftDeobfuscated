/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PufferfishBigModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart leftBlueFin;
    private final ModelPart rightBlueFin;

    public PufferfishBigModel(ModelPart $$0) {
        this.root = $$0;
        this.leftBlueFin = $$0.getChild("left_blue_fin");
        this.rightBlueFin = $$0.getChild("right_blue_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 22;
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.offset(0.0f, 22.0f, 0.0f));
        $$1.addOrReplaceChild("right_blue_fin", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f), PartPose.offset(-4.0f, 15.0f, -2.0f));
        $$1.addOrReplaceChild("left_blue_fin", CubeListBuilder.create().texOffs(24, 3).addBox(0.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f), PartPose.offset(4.0f, 15.0f, -2.0f));
        $$1.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(15, 17).addBox(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 14.0f, -4.0f, 0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("top_middle_fin", CubeListBuilder.create().texOffs(14, 16).addBox(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 1.0f), PartPose.offset(0.0f, 14.0f, 0.0f));
        $$1.addOrReplaceChild("top_back_fin", CubeListBuilder.create().texOffs(23, 18).addBox(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 14.0f, 4.0f, -0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_front_fin", CubeListBuilder.create().texOffs(5, 17).addBox(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), PartPose.offsetAndRotation(-4.0f, 22.0f, -4.0f, 0.0f, -0.7853982f, 0.0f));
        $$1.addOrReplaceChild("left_front_fin", CubeListBuilder.create().texOffs(1, 17).addBox(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), PartPose.offsetAndRotation(4.0f, 22.0f, -4.0f, 0.0f, 0.7853982f, 0.0f));
        $$1.addOrReplaceChild("bottom_front_fin", CubeListBuilder.create().texOffs(15, 20).addBox(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 22.0f, -4.0f, -0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("bottom_middle_fin", CubeListBuilder.create().texOffs(15, 20).addBox(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), PartPose.offset(0.0f, 22.0f, 0.0f));
        $$1.addOrReplaceChild("bottom_back_fin", CubeListBuilder.create().texOffs(15, 20).addBox(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 22.0f, 4.0f, 0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_back_fin", CubeListBuilder.create().texOffs(9, 17).addBox(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), PartPose.offsetAndRotation(-4.0f, 22.0f, 4.0f, 0.0f, 0.7853982f, 0.0f));
        $$1.addOrReplaceChild("left_back_fin", CubeListBuilder.create().texOffs(9, 17).addBox(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), PartPose.offsetAndRotation(4.0f, 22.0f, 4.0f, 0.0f, -0.7853982f, 0.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.rightBlueFin.zRot = -0.2f + 0.4f * Mth.sin($$3 * 0.2f);
        this.leftBlueFin.zRot = 0.2f - 0.4f * Mth.sin($$3 * 0.2f);
    }
}