/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer
extends MobRenderer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

    public CreeperRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CreeperModel($$0.bakeLayer(ModelLayers.CREEPER)), 0.5f);
        this.addLayer(new CreeperPowerLayer(this, $$0.getModelSet()));
    }

    @Override
    protected void scale(Creeper $$0, PoseStack $$1, float $$2) {
        float $$3 = $$0.getSwelling($$2);
        float $$4 = 1.0f + Mth.sin($$3 * 100.0f) * $$3 * 0.01f;
        $$3 = Mth.clamp($$3, 0.0f, 1.0f);
        $$3 *= $$3;
        $$3 *= $$3;
        float $$5 = (1.0f + $$3 * 0.4f) * $$4;
        float $$6 = (1.0f + $$3 * 0.1f) / $$4;
        $$1.scale($$5, $$6, $$5);
    }

    @Override
    protected float getWhiteOverlayProgress(Creeper $$0, float $$1) {
        float $$2 = $$0.getSwelling($$1);
        if ((int)($$2 * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return Mth.clamp($$2, 0.5f, 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(Creeper $$0) {
        return CREEPER_LOCATION;
    }
}