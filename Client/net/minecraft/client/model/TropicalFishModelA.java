/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TropicalFishModelA<T extends Entity>
extends ColorableHierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart tail;

    public TropicalFishModelA(ModelPart $$0) {
        this.root = $$0;
        this.tail = $$0.getChild("tail");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        int $$3 = 22;
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -1.5f, -3.0f, 2.0f, 3.0f, 6.0f, $$0), PartPose.offset(0.0f, 22.0f, 0.0f));
        $$2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, -6).addBox(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 6.0f, $$0), PartPose.offset(0.0f, 22.0f, 3.0f));
        $$2.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(2, 16).addBox(-2.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, $$0), PartPose.offsetAndRotation(-1.0f, 22.5f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        $$2.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(2, 12).addBox(0.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, $$0), PartPose.offsetAndRotation(1.0f, 22.5f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        $$2.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(10, -5).addBox(0.0f, -3.0f, 0.0f, 0.0f, 3.0f, 6.0f, $$0), PartPose.offset(0.0f, 20.5f, -3.0f));
        return LayerDefinition.create($$1, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = 1.0f;
        if (!((Entity)$$0).isInWater()) {
            $$6 = 1.5f;
        }
        this.tail.yRot = -$$6 * 0.45f * Mth.sin(0.6f * $$3);
    }
}