/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
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

public class EvokerFangsModel<T extends Entity>
extends HierarchicalModel<T> {
    private static final String BASE = "base";
    private static final String UPPER_JAW = "upper_jaw";
    private static final String LOWER_JAW = "lower_jaw";
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;

    public EvokerFangsModel(ModelPart $$0) {
        this.root = $$0;
        this.base = $$0.getChild(BASE);
        this.upperJaw = $$0.getChild(UPPER_JAW);
        this.lowerJaw = $$0.getChild(LOWER_JAW);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f), PartPose.offset(-5.0f, 24.0f, -5.0f));
        CubeListBuilder $$2 = CubeListBuilder.create().texOffs(40, 0).addBox(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        $$1.addOrReplaceChild(UPPER_JAW, $$2, PartPose.offset(1.5f, 24.0f, -4.0f));
        $$1.addOrReplaceChild(LOWER_JAW, $$2, PartPose.offsetAndRotation(-1.5f, 24.0f, 4.0f, 0.0f, (float)Math.PI, 0.0f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = $$1 * 2.0f;
        if ($$6 > 1.0f) {
            $$6 = 1.0f;
        }
        $$6 = 1.0f - $$6 * $$6 * $$6;
        this.upperJaw.zRot = (float)Math.PI - $$6 * 0.35f * (float)Math.PI;
        this.lowerJaw.zRot = (float)Math.PI + $$6 * 0.35f * (float)Math.PI;
        float $$7 = ($$1 + Mth.sin($$1 * 2.7f)) * 0.6f * 12.0f;
        this.lowerJaw.y = this.upperJaw.y = 24.0f - $$7;
        this.base.y = this.upperJaw.y;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}