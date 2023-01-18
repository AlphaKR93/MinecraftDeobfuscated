/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerModel<T extends Shulker>
extends ListModel<T> {
    private static final String LID = "lid";
    private static final String BASE = "base";
    private final ModelPart base;
    private final ModelPart lid;
    private final ModelPart head;

    public ShulkerModel(ModelPart $$0) {
        super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCullZOffset));
        this.lid = $$0.getChild(LID);
        this.base = $$0.getChild(BASE);
        this.head = $$0.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        $$1.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 28).addBox(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 52).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), PartPose.offset(0.0f, 12.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(T $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = $$3 - (float)((Shulker)$$0).tickCount;
        float $$7 = (0.5f + ((Shulker)$$0).getClientPeekAmount($$6)) * (float)Math.PI;
        float $$8 = -1.0f + Mth.sin($$7);
        float $$9 = 0.0f;
        if ($$7 > (float)Math.PI) {
            $$9 = Mth.sin($$3 * 0.1f) * 0.7f;
        }
        this.lid.setPos(0.0f, 16.0f + Mth.sin($$7) * 8.0f + $$9, 0.0f);
        this.lid.yRot = ((Shulker)$$0).getClientPeekAmount($$6) > 0.3f ? $$8 * $$8 * $$8 * $$8 * (float)Math.PI * 0.125f : 0.0f;
        this.head.xRot = $$5 * ((float)Math.PI / 180);
        this.head.yRot = (((Shulker)$$0).yHeadRot - 180.0f - ((Shulker)$$0).yBodyRot) * ((float)Math.PI / 180);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.base, (Object)this.lid);
    }

    public ModelPart getLid() {
        return this.lid;
    }

    public ModelPart getHead() {
        return this.head;
    }
}