/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ChestRaftModel
extends RaftModel {
    private static final String CHEST_BOTTOM = "chest_bottom";
    private static final String CHEST_LID = "chest_lid";
    private static final String CHEST_LOCK = "chest_lock";

    public ChestRaftModel(ModelPart $$0) {
        super($$0);
    }

    @Override
    protected ImmutableList.Builder<ModelPart> createPartsBuilder(ModelPart $$0) {
        ImmutableList.Builder<ModelPart> $$1 = super.createPartsBuilder($$0);
        $$1.add((Object)$$0.getChild(CHEST_BOTTOM));
        $$1.add((Object)$$0.getChild(CHEST_LID));
        $$1.add((Object)$$0.getChild(CHEST_LOCK));
        return $$1;
    }

    public static LayerDefinition createBodyModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        RaftModel.createChildren($$1);
        $$1.addOrReplaceChild(CHEST_BOTTOM, CubeListBuilder.create().texOffs(0, 76).addBox(0.0f, 0.0f, 0.0f, 12.0f, 8.0f, 12.0f), PartPose.offsetAndRotation(-2.0f, -11.0f, -6.0f, 0.0f, -1.5707964f, 0.0f));
        $$1.addOrReplaceChild(CHEST_LID, CubeListBuilder.create().texOffs(0, 59).addBox(0.0f, 0.0f, 0.0f, 12.0f, 4.0f, 12.0f), PartPose.offsetAndRotation(-2.0f, -15.0f, -6.0f, 0.0f, -1.5707964f, 0.0f));
        $$1.addOrReplaceChild(CHEST_LOCK, CubeListBuilder.create().texOffs(0, 59).addBox(0.0f, 0.0f, 0.0f, 2.0f, 4.0f, 1.0f), PartPose.offsetAndRotation(-1.0f, -12.0f, -1.0f, 0.0f, -1.5707964f, 0.0f));
        return LayerDefinition.create($$0, 128, 128);
    }
}