/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearRenderer
extends MobRenderer<PolarBear, PolarBearModel<PolarBear>> {
    private static final ResourceLocation BEAR_LOCATION = new ResourceLocation("textures/entity/bear/polarbear.png");

    public PolarBearRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PolarBearModel($$0.bakeLayer(ModelLayers.POLAR_BEAR)), 0.9f);
    }

    @Override
    public ResourceLocation getTextureLocation(PolarBear $$0) {
        return BEAR_LOCATION;
    }

    @Override
    protected void scale(PolarBear $$0, PoseStack $$1, float $$2) {
        $$1.scale(1.2f, 1.2f, 1.2f);
        super.scale($$0, $$1, $$2);
    }
}