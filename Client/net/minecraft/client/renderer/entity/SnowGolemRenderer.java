/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;

public class SnowGolemRenderer
extends MobRenderer<SnowGolem, SnowGolemModel<SnowGolem>> {
    private static final ResourceLocation SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");

    public SnowGolemRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SnowGolemModel($$0.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5f);
        this.addLayer(new SnowGolemHeadLayer(this, $$0.getBlockRenderDispatcher(), $$0.getItemRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(SnowGolem $$0) {
        return SNOW_GOLEM_LOCATION;
    }
}