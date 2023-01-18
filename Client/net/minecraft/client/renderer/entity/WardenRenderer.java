/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenRenderer
extends MobRenderer<Warden, WardenModel<Warden>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/warden/warden.png");
    private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = new ResourceLocation("textures/entity/warden/warden_bioluminescent_layer.png");
    private static final ResourceLocation HEART_TEXTURE = new ResourceLocation("textures/entity/warden/warden_heart.png");
    private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_1.png");
    private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_2.png");

    public WardenRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new WardenModel($$02.bakeLayer(ModelLayers.WARDEN)), 0.9f);
        this.addLayer(new WardenEmissiveLayer<Warden, WardenModel>(this, BIOLUMINESCENT_LAYER_TEXTURE, ($$0, $$1, $$2) -> 1.0f, WardenModel::getBioluminescentLayerModelParts));
        this.addLayer(new WardenEmissiveLayer<Warden, WardenModel>(this, PULSATING_SPOTS_TEXTURE_1, ($$0, $$1, $$2) -> Math.max((float)0.0f, (float)(Mth.cos($$2 * 0.045f) * 0.25f)), WardenModel::getPulsatingSpotsLayerModelParts));
        this.addLayer(new WardenEmissiveLayer<Warden, WardenModel>(this, PULSATING_SPOTS_TEXTURE_2, ($$0, $$1, $$2) -> Math.max((float)0.0f, (float)(Mth.cos($$2 * 0.045f + (float)Math.PI) * 0.25f)), WardenModel::getPulsatingSpotsLayerModelParts));
        this.addLayer(new WardenEmissiveLayer<Warden, WardenModel>(this, TEXTURE, ($$0, $$1, $$2) -> $$0.getTendrilAnimation($$1), WardenModel::getTendrilsLayerModelParts));
        this.addLayer(new WardenEmissiveLayer<Warden, WardenModel>(this, HEART_TEXTURE, ($$0, $$1, $$2) -> $$0.getHeartAnimation($$1), WardenModel::getHeartLayerModelParts));
    }

    @Override
    public ResourceLocation getTextureLocation(Warden $$0) {
        return TEXTURE;
    }
}