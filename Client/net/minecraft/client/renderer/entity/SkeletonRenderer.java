/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class SkeletonRenderer
extends HumanoidMobRenderer<AbstractSkeleton, SkeletonModel<AbstractSkeleton>> {
    private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public SkeletonRenderer(EntityRendererProvider.Context $$0) {
        this($$0, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public SkeletonRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, ModelLayerLocation $$3) {
        super($$0, new SkeletonModel($$0.bakeLayer($$1)), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel($$0.bakeLayer($$2)), new SkeletonModel($$0.bakeLayer($$3)), $$0.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSkeleton $$0) {
        return SKELETON_LOCATION;
    }

    @Override
    protected boolean isShaking(AbstractSkeleton $$0) {
        return $$0.isShaking();
    }
}