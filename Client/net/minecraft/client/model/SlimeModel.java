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
import net.minecraft.world.entity.Entity;

public class SlimeModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart root;

    public SlimeModel(ModelPart $$0) {
        this.root = $$0;
    }

    public static LayerDefinition createOuterBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    public static LayerDefinition createInnerBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(32, 0).addBox(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(32, 4).addBox(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(32, 8).addBox(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}