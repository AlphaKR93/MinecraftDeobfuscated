/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
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
import net.minecraft.world.entity.monster.Slime;

public class LavaSlimeModel<T extends Slime>
extends HierarchicalModel<T> {
    private static final int SEGMENT_COUNT = 8;
    private final ModelPart root;
    private final ModelPart[] bodyCubes = new ModelPart[8];

    public LavaSlimeModel(ModelPart $$0) {
        this.root = $$0;
        Arrays.setAll((Object[])this.bodyCubes, $$1 -> $$0.getChild(LavaSlimeModel.getSegmentName($$1)));
    }

    private static String getSegmentName(int $$0) {
        return "cube" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        for (int $$2 = 0; $$2 < 8; ++$$2) {
            int $$3 = 0;
            int $$4 = $$2;
            if ($$2 == 2) {
                $$3 = 24;
                $$4 = 10;
            } else if ($$2 == 3) {
                $$3 = 24;
                $$4 = 19;
            }
            $$1.addOrReplaceChild(LavaSlimeModel.getSegmentName($$2), CubeListBuilder.create().texOffs($$3, $$4).addBox(-4.0f, 16 + $$2, -4.0f, 8.0f, 1.0f, 8.0f), PartPose.ZERO);
        }
        $$1.addOrReplaceChild("inside_cube", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 18.0f, -2.0f, 4.0f, 4.0f, 4.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
    }

    @Override
    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
        float $$4 = Mth.lerp($$3, ((Slime)$$0).oSquish, ((Slime)$$0).squish);
        if ($$4 < 0.0f) {
            $$4 = 0.0f;
        }
        for (int $$5 = 0; $$5 < this.bodyCubes.length; ++$$5) {
            this.bodyCubes[$$5].y = (float)(-(4 - $$5)) * $$4 * 1.7f;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}