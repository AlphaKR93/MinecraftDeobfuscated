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

public class EndermiteModel<T extends Entity>
extends HierarchicalModel<T> {
    private static final int BODY_COUNT = 4;
    private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private final ModelPart root;
    private final ModelPart[] bodyParts;

    public EndermiteModel(ModelPart $$0) {
        this.root = $$0;
        this.bodyParts = new ModelPart[4];
        for (int $$1 = 0; $$1 < 4; ++$$1) {
            this.bodyParts[$$1] = $$0.getChild(EndermiteModel.createSegmentName($$1));
        }
    }

    private static String createSegmentName(int $$0) {
        return "segment" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = -3.5f;
        for (int $$3 = 0; $$3 < 4; ++$$3) {
            $$1.addOrReplaceChild(EndermiteModel.createSegmentName($$3), CubeListBuilder.create().texOffs(BODY_TEXS[$$3][0], BODY_TEXS[$$3][1]).addBox((float)BODY_SIZES[$$3][0] * -0.5f, 0.0f, (float)BODY_SIZES[$$3][2] * -0.5f, BODY_SIZES[$$3][0], BODY_SIZES[$$3][1], BODY_SIZES[$$3][2]), PartPose.offset(0.0f, 24 - BODY_SIZES[$$3][1], $$2));
            if ($$3 >= 3) continue;
            $$2 += (float)(BODY_SIZES[$$3][2] + BODY_SIZES[$$3 + 1][2]) * 0.5f;
        }
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        for (int $$6 = 0; $$6 < this.bodyParts.length; ++$$6) {
            this.bodyParts[$$6].yRot = Mth.cos($$3 * 0.9f + (float)$$6 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.01f * (float)(1 + Math.abs((int)($$6 - 2)));
            this.bodyParts[$$6].x = Mth.sin($$3 * 0.9f + (float)$$6 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.1f * (float)Math.abs((int)($$6 - 2));
        }
    }
}