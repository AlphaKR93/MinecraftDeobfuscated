/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRenderer
extends MobRenderer<Chicken, ChickenModel<Chicken>> {
    private static final ResourceLocation CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");

    public ChickenRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ChickenModel($$0.bakeLayer(ModelLayers.CHICKEN)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Chicken $$0) {
        return CHICKEN_LOCATION;
    }

    @Override
    protected float getBob(Chicken $$0, float $$1) {
        float $$2 = Mth.lerp($$1, $$0.oFlap, $$0.flap);
        float $$3 = Mth.lerp($$1, $$0.oFlapSpeed, $$0.flapSpeed);
        return (Mth.sin($$2) + 1.0f) * $$3;
    }
}