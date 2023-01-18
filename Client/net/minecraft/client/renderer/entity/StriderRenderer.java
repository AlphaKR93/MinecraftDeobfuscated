/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Strider;

public class StriderRenderer
extends MobRenderer<Strider, StriderModel<Strider>> {
    private static final ResourceLocation STRIDER_LOCATION = new ResourceLocation("textures/entity/strider/strider.png");
    private static final ResourceLocation COLD_LOCATION = new ResourceLocation("textures/entity/strider/strider_cold.png");

    public StriderRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new StriderModel($$0.bakeLayer(ModelLayers.STRIDER)), 0.5f);
        this.addLayer(new SaddleLayer(this, new StriderModel($$0.bakeLayer(ModelLayers.STRIDER_SADDLE)), new ResourceLocation("textures/entity/strider/strider_saddle.png")));
    }

    @Override
    public ResourceLocation getTextureLocation(Strider $$0) {
        return $$0.isSuffocating() ? COLD_LOCATION : STRIDER_LOCATION;
    }

    @Override
    protected void scale(Strider $$0, PoseStack $$1, float $$2) {
        if ($$0.isBaby()) {
            $$1.scale(0.5f, 0.5f, 0.5f);
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
    }

    @Override
    protected boolean isShaking(Strider $$0) {
        return super.isShaking($$0) || $$0.isSuffocating();
    }
}