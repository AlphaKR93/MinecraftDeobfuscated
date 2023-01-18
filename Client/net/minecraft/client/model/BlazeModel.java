/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 */
package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BlazeModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart[] upperBodyParts;
    private final ModelPart head;

    public BlazeModel(ModelPart $$0) {
        this.root = $$0;
        this.head = $$0.getChild("head");
        this.upperBodyParts = new ModelPart[12];
        Arrays.setAll((Object[])this.upperBodyParts, $$1 -> $$0.getChild(BlazeModel.getPartName($$1)));
    }

    private static String getPartName(int $$0) {
        return "part" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        float $$2 = 0.0f;
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(0, 16).addBox(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        for (int $$4 = 0; $$4 < 4; ++$$4) {
            float $$5 = Mth.cos($$2) * 9.0f;
            float $$6 = -2.0f + Mth.cos((float)($$4 * 2) * 0.25f);
            float $$7 = Mth.sin($$2) * 9.0f;
            $$1.addOrReplaceChild(BlazeModel.getPartName($$4), $$3, PartPose.offset($$5, $$6, $$7));
            $$2 += 1.5707964f;
        }
        $$2 = 0.7853982f;
        for (int $$8 = 4; $$8 < 8; ++$$8) {
            float $$9 = Mth.cos($$2) * 7.0f;
            float $$10 = 2.0f + Mth.cos((float)($$8 * 2) * 0.25f);
            float $$11 = Mth.sin($$2) * 7.0f;
            $$1.addOrReplaceChild(BlazeModel.getPartName($$8), $$3, PartPose.offset($$9, $$10, $$11));
            $$2 += 1.5707964f;
        }
        $$2 = 0.47123894f;
        for (int $$12 = 8; $$12 < 12; ++$$12) {
            float $$13 = Mth.cos($$2) * 5.0f;
            float $$14 = 11.0f + Mth.cos((float)$$12 * 1.5f * 0.5f);
            float $$15 = Mth.sin($$2) * 5.0f;
            $$1.addOrReplaceChild(BlazeModel.getPartName($$12), $$3, PartPose.offset($$13, $$14, $$15));
            $$2 += 1.5707964f;
        }
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = $$3 * (float)Math.PI * -0.1f;
        for (int $$7 = 0; $$7 < 4; ++$$7) {
            this.upperBodyParts[$$7].y = -2.0f + Mth.cos(((float)($$7 * 2) + $$3) * 0.25f);
            this.upperBodyParts[$$7].x = Mth.cos($$6) * 9.0f;
            this.upperBodyParts[$$7].z = Mth.sin($$6) * 9.0f;
            $$6 += 1.5707964f;
        }
        $$6 = 0.7853982f + $$3 * (float)Math.PI * 0.03f;
        for (int $$8 = 4; $$8 < 8; ++$$8) {
            this.upperBodyParts[$$8].y = 2.0f + Mth.cos(((float)($$8 * 2) + $$3) * 0.25f);
            this.upperBodyParts[$$8].x = Mth.cos($$6) * 7.0f;
            this.upperBodyParts[$$8].z = Mth.sin($$6) * 7.0f;
            $$6 += 1.5707964f;
        }
        $$6 = 0.47123894f + $$3 * (float)Math.PI * -0.05f;
        for (int $$9 = 8; $$9 < 12; ++$$9) {
            this.upperBodyParts[$$9].y = 11.0f + Mth.cos(((float)$$9 * 1.5f + $$3) * 0.5f);
            this.upperBodyParts[$$9].x = Mth.cos($$6) * 5.0f;
            this.upperBodyParts[$$9].z = Mth.sin($$6) * 5.0f;
            $$6 += 1.5707964f;
        }
        this.head.yRot = $$4 * ((float)Math.PI / 180);
        this.head.xRot = $$5 * ((float)Math.PI / 180);
    }
}