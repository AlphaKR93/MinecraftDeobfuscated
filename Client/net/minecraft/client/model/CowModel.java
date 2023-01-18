/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.model;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class CowModel<T extends Entity>
extends QuadrupedModel<T> {
    public CowModel(ModelPart $$0) {
        super($$0, false, 10.0f, 4.0f, 2.0f, 2.0f, 24);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 12;
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).texOffs(22, 0).addBox("right_horn", -5.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f).texOffs(22, 0).addBox("left_horn", 4.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f), PartPose.offset(0.0f, 4.0f, -8.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 4).addBox(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).texOffs(52, 0).addBox(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), PartPose.offsetAndRotation(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        $$1.addOrReplaceChild("right_hind_leg", $$3, PartPose.offset(-4.0f, 12.0f, 7.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$3, PartPose.offset(4.0f, 12.0f, 7.0f));
        $$1.addOrReplaceChild("right_front_leg", $$3, PartPose.offset(-4.0f, 12.0f, -6.0f));
        $$1.addOrReplaceChild("left_front_leg", $$3, PartPose.offset(4.0f, 12.0f, -6.0f));
        return LayerDefinition.create($$0, 64, 32);
    }

    public ModelPart getHead() {
        return this.head;
    }
}