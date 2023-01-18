/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.goat.Goat;

public class GoatModel<T extends Goat>
extends QuadrupedModel<T> {
    public GoatModel(ModelPart $$0) {
        super($$0, true, 19.0f, 1.0f, 2.5f, 2.0f, 24);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 61).addBox("right ear", -6.0f, -11.0f, -10.0f, 3.0f, 2.0f, 1.0f).texOffs(2, 61).mirror().addBox("left ear", 2.0f, -11.0f, -10.0f, 3.0f, 2.0f, 1.0f).texOffs(23, 52).addBox("goatee", -0.5f, -3.0f, -14.0f, 0.0f, 7.0f, 5.0f), PartPose.offset(1.0f, 14.0f, 0.0f));
        $$2.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(12, 55).addBox(-0.01f, -16.0f, -10.0f, 2.0f, 7.0f, 2.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(12, 55).addBox(-2.99f, -16.0f, -10.0f, 2.0f, 7.0f, 2.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(34, 46).addBox(-3.0f, -4.0f, -8.0f, 5.0f, 7.0f, 10.0f), PartPose.offsetAndRotation(0.0f, -8.0f, -8.0f, 0.9599f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 1).addBox(-4.0f, -17.0f, -7.0f, 9.0f, 11.0f, 16.0f).texOffs(0, 28).addBox(-5.0f, -18.0f, -8.0f, 11.0f, 14.0f, 11.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(36, 29).addBox(0.0f, 4.0f, 0.0f, 3.0f, 6.0f, 3.0f), PartPose.offset(1.0f, 14.0f, 4.0f));
        $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(49, 29).addBox(0.0f, 4.0f, 0.0f, 3.0f, 6.0f, 3.0f), PartPose.offset(-3.0f, 14.0f, 4.0f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(49, 2).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f), PartPose.offset(1.0f, 14.0f, -6.0f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(35, 2).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f), PartPose.offset(-3.0f, 14.0f, -6.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.head.getChild((String)"left_horn").visible = ((Goat)$$0).hasLeftHorn();
        this.head.getChild((String)"right_horn").visible = ((Goat)$$0).hasRightHorn();
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        float $$6 = ((Goat)$$0).getRammingXHeadRot();
        if ($$6 != 0.0f) {
            this.head.xRot = $$6;
        }
    }
}