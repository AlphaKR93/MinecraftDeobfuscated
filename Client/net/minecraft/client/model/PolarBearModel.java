/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearModel<T extends PolarBear>
extends QuadrupedModel<T> {
    public PolarBearModel(ModelPart $$0) {
        super($$0, true, 16.0f, 4.0f, 2.25f, 2.0f, 24);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -3.0f, -3.0f, 7.0f, 7.0f, 7.0f).texOffs(0, 44).addBox("mouth", -2.5f, 1.0f, -6.0f, 5.0f, 3.0f, 3.0f).texOffs(26, 0).addBox("right_ear", -4.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f).texOffs(26, 0).mirror().addBox("left_ear", 2.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 10.0f, -16.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0f, -13.0f, -7.0f, 14.0f, 14.0f, 11.0f).texOffs(39, 0).addBox(-4.0f, -25.0f, -7.0f, 12.0f, 12.0f, 10.0f), PartPose.offsetAndRotation(-2.0f, 9.0f, 12.0f, 1.5707964f, 0.0f, 0.0f));
        int $$2 = 10;
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f);
        $$1.addOrReplaceChild("right_hind_leg", $$3, PartPose.offset(-4.5f, 14.0f, 6.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$3, PartPose.offset(4.5f, 14.0f, 6.0f));
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f);
        $$1.addOrReplaceChild("right_front_leg", $$4, PartPose.offset(-3.5f, 14.0f, -8.0f));
        $$1.addOrReplaceChild("left_front_leg", $$4, PartPose.offset(3.5f, 14.0f, -8.0f));
        return LayerDefinition.create($$0, 128, 64);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        super.setupAnim($$0, $$1, $$2, $$3, $$4, $$5);
        float $$6 = $$3 - (float)((PolarBear)$$0).tickCount;
        float $$7 = ((PolarBear)$$0).getStandingAnimationScale($$6);
        $$7 *= $$7;
        float $$8 = 1.0f - $$7;
        this.body.xRot = 1.5707964f - $$7 * (float)Math.PI * 0.35f;
        this.body.y = 9.0f * $$8 + 11.0f * $$7;
        this.rightFrontLeg.y = 14.0f * $$8 - 6.0f * $$7;
        this.rightFrontLeg.z = -8.0f * $$8 - 4.0f * $$7;
        this.rightFrontLeg.xRot -= $$7 * (float)Math.PI * 0.45f;
        this.leftFrontLeg.y = this.rightFrontLeg.y;
        this.leftFrontLeg.z = this.rightFrontLeg.z;
        this.leftFrontLeg.xRot -= $$7 * (float)Math.PI * 0.45f;
        if (this.young) {
            this.head.y = 10.0f * $$8 - 9.0f * $$7;
            this.head.z = -16.0f * $$8 - 7.0f * $$7;
        } else {
            this.head.y = 10.0f * $$8 - 14.0f * $$7;
            this.head.z = -16.0f * $$8 - 3.0f * $$7;
        }
        this.head.xRot += $$7 * (float)Math.PI * 0.15f;
    }
}