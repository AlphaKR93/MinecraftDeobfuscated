/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WanderingTraderRenderer
extends MobRenderer<WanderingTrader, VillagerModel<WanderingTrader>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/wandering_trader.png");

    public WanderingTraderRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new VillagerModel($$0.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5f);
        this.addLayer(new CustomHeadLayer<WanderingTrader, VillagerModel<WanderingTrader>>(this, $$0.getModelSet(), $$0.getItemInHandRenderer()));
        this.addLayer(new CrossedArmsItemLayer<WanderingTrader, VillagerModel<WanderingTrader>>(this, $$0.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(WanderingTrader $$0) {
        return VILLAGER_BASE_SKIN;
    }

    @Override
    protected void scale(WanderingTrader $$0, PoseStack $$1, float $$2) {
        float $$3 = 0.9375f;
        $$1.scale(0.9375f, 0.9375f, 0.9375f);
    }
}