/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class HoglinRenderer
extends MobRenderer<Hoglin, HoglinModel<Hoglin>> {
    private static final ResourceLocation HOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/hoglin.png");

    public HoglinRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new HoglinModel($$0.bakeLayer(ModelLayers.HOGLIN)), 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(Hoglin $$0) {
        return HOGLIN_LOCATION;
    }

    @Override
    protected boolean isShaking(Hoglin $$0) {
        return super.isShaking($$0) || $$0.isConverting();
    }
}