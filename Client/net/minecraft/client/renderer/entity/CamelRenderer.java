/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelRenderer
extends MobRenderer<Camel, CamelModel<Camel>> {
    private static final ResourceLocation CAMEL_LOCATION = new ResourceLocation("textures/entity/camel/camel.png");

    public CamelRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1) {
        super($$0, new CamelModel($$0.bakeLayer($$1)), 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(Camel $$0) {
        return CAMEL_LOCATION;
    }
}