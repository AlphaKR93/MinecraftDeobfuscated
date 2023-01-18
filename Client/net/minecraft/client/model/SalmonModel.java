/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
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

public class SalmonModel<T extends Entity>
extends HierarchicalModel<T> {
    private static final String BODY_FRONT = "body_front";
    private static final String BODY_BACK = "body_back";
    private final ModelPart root;
    private final ModelPart bodyBack;

    public SalmonModel(ModelPart $$0) {
        this.root = $$0;
        this.bodyBack = $$0.getChild(BODY_BACK);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 20;
        PartDefinition $$3 = $$1.addOrReplaceChild(BODY_FRONT, CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), PartPose.offset(0.0f, 20.0f, 0.0f));
        PartDefinition $$4 = $$1.addOrReplaceChild(BODY_BACK, CubeListBuilder.create().texOffs(0, 13).addBox(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), PartPose.offset(0.0f, 20.0f, 8.0f));
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), PartPose.offset(0.0f, 20.0f, 0.0f));
        $$4.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(20, 10).addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f), PartPose.offset(0.0f, 0.0f, 8.0f));
        $$3.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(2, 1).addBox(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f), PartPose.offset(0.0f, -4.5f, 5.0f));
        $$4.addOrReplaceChild("top_back_fin", CubeListBuilder.create().texOffs(0, 2).addBox(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f), PartPose.offset(0.0f, -4.5f, -1.0f));
        $$1.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(-4, 0).addBox(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), PartPose.offsetAndRotation(-1.5f, 21.5f, 0.0f, 0.0f, 0.0f, -0.7853982f));
        $$1.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), PartPose.offsetAndRotation(1.5f, 21.5f, 0.0f, 0.0f, 0.0f, 0.7853982f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = 1.0f;
        float $$7 = 1.0f;
        if (!((Entity)$$0).isInWater()) {
            $$6 = 1.3f;
            $$7 = 1.7f;
        }
        this.bodyBack.yRot = -$$6 * 0.25f * Mth.sin($$7 * 0.6f * $$3);
    }
}