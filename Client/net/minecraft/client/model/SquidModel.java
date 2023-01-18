/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  net.minecraft.world.entity.Entity
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
import net.minecraft.world.entity.Entity;

public class SquidModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart[] tentacles = new ModelPart[8];
    private final ModelPart root;

    public SquidModel(ModelPart $$0) {
        this.root = $$0;
        Arrays.setAll((Object[])this.tentacles, $$1 -> $$0.getChild(SquidModel.createTentacleName($$1)));
    }

    private static String createTentacleName(int $$0) {
        return "tentacle" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = -16;
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f), PartPose.offset(0.0f, 8.0f, 0.0f));
        int $$3 = 8;
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
        for (int $$5 = 0; $$5 < 8; ++$$5) {
            double $$6 = (double)$$5 * Math.PI * 2.0 / 8.0;
            float $$7 = (float)Math.cos((double)$$6) * 5.0f;
            float $$8 = 15.0f;
            float $$9 = (float)Math.sin((double)$$6) * 5.0f;
            $$6 = (double)$$5 * Math.PI * -2.0 / 8.0 + 1.5707963267948966;
            float $$10 = (float)$$6;
            $$1.addOrReplaceChild(SquidModel.createTentacleName($$5), $$4, PartPose.offsetAndRotation($$7, 15.0f, $$9, 0.0f, $$10, 0.0f));
        }
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        for (ModelPart $$6 : this.tentacles) {
            $$6.xRot = $$3;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}