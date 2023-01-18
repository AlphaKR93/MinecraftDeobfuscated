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
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class DolphinModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tailFin;

    public DolphinModel(ModelPart $$0) {
        this.root = $$0;
        this.body = $$0.getChild("body");
        this.tail = this.body.getChild("tail");
        this.tailFin = this.tail.getChild("tail_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = 18.0f;
        float $$3 = -8.0f;
        PartDefinition $$4 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(22, 0).addBox(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f), PartPose.offset(0.0f, 22.0f, -5.0f));
        $$4.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(51, 0).addBox(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f), PartPose.rotation(1.0471976f, 0.0f, 0.0f));
        $$4.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(48, 20).mirror().addBox(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), PartPose.offsetAndRotation(2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, 2.0943952f));
        $$4.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(48, 20).addBox(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), PartPose.offsetAndRotation(-2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, -2.0943952f));
        PartDefinition $$5 = $$4.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f), PartPose.offsetAndRotation(0.0f, -2.5f, 11.0f, -0.10471976f, 0.0f, 0.0f));
        $$5.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(19, 20).addBox(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f), PartPose.offset(0.0f, 0.0f, 9.0f));
        PartDefinition $$6 = $$4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f), PartPose.offset(0.0f, -4.0f, -3.0f));
        $$6.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.body.xRot = $$5 * ((float)Math.PI / 180);
        this.body.yRot = $$4 * ((float)Math.PI / 180);
        if (((Entity)$$0).getDeltaMovement().horizontalDistanceSqr() > 1.0E-7) {
            this.body.xRot += -0.05f - 0.05f * Mth.cos($$3 * 0.3f);
            this.tail.xRot = -0.1f * Mth.cos($$3 * 0.3f);
            this.tailFin.xRot = -0.2f * Mth.cos($$3 * 0.3f);
        }
    }
}