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
import net.minecraft.world.entity.monster.Ravager;

public class RavagerModel
extends HierarchicalModel<Ravager> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart neck;

    public RavagerModel(ModelPart $$0) {
        this.root = $$0;
        this.neck = $$0.getChild("neck");
        this.head = this.neck.getChild("head");
        this.mouth = this.head.getChild("mouth");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 16;
        PartDefinition $$3 = $$1.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f), PartPose.offset(0.0f, -7.0f, 5.5f));
        PartDefinition $$4 = $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f).texOffs(0, 0).addBox(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f), PartPose.offset(0.0f, 16.0f, -17.0f));
        $$4.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(74, 55).addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), PartPose.offsetAndRotation(-10.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        $$4.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), PartPose.offsetAndRotation(8.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        $$4.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f), PartPose.offset(0.0f, -2.0f, 2.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 55).addBox(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f).texOffs(0, 91).addBox(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f), PartPose.offsetAndRotation(0.0f, 1.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(-8.0f, -13.0f, 18.0f));
        $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(8.0f, -13.0f, 18.0f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(-8.0f, -13.0f, -5.0f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(8.0f, -13.0f, -5.0f));
        return LayerDefinition.create($$0, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(Ravager $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        float $$6 = 0.4f * $$2;
        this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * $$6;
        this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * $$6;
        this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * $$6;
        this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * $$6;
    }

    @Override
    public void prepareMobModel(Ravager $$0, float $$1, float $$2, float $$3) {
        super.prepareMobModel($$0, $$1, $$2, $$3);
        int $$4 = $$0.getStunnedTick();
        int $$5 = $$0.getRoarTick();
        int $$6 = 20;
        int $$7 = $$0.getAttackTick();
        int $$8 = 10;
        if ($$7 > 0) {
            float $$9 = Mth.triangleWave((float)$$7 - $$3, 10.0f);
            float $$10 = (1.0f + $$9) * 0.5f;
            float $$11 = $$10 * $$10 * $$10 * 12.0f;
            float $$12 = $$11 * Mth.sin(this.neck.xRot);
            this.neck.z = -6.5f + $$11;
            this.neck.y = -7.0f - $$12;
            float $$13 = Mth.sin(((float)$$7 - $$3) / 10.0f * (float)Math.PI * 0.25f);
            this.mouth.xRot = 1.5707964f * $$13;
            this.mouth.xRot = $$7 > 5 ? Mth.sin(((float)(-4 + $$7) - $$3) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * Mth.sin((float)Math.PI * ((float)$$7 - $$3) / 10.0f);
        } else {
            float $$14 = -1.0f;
            float $$15 = -1.0f * Mth.sin(this.neck.xRot);
            this.neck.x = 0.0f;
            this.neck.y = -7.0f - $$15;
            this.neck.z = 5.5f;
            boolean $$16 = $$4 > 0;
            this.neck.xRot = $$16 ? 0.21991149f : 0.0f;
            this.mouth.xRot = (float)Math.PI * ($$16 ? 0.05f : 0.01f);
            if ($$16) {
                double $$17 = (double)$$4 / 40.0;
                this.neck.x = (float)Math.sin((double)($$17 * 10.0)) * 3.0f;
            } else if ($$5 > 0) {
                float $$18 = Mth.sin(((float)(20 - $$5) - $$3) / 20.0f * (float)Math.PI * 0.25f);
                this.mouth.xRot = 1.5707964f * $$18;
            }
        }
    }
}