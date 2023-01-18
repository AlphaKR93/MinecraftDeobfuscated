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
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer
extends MobRenderer<Villager, VillagerModel<Villager>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

    public VillagerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new VillagerModel($$0.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
        this.addLayer(new CustomHeadLayer<Villager, VillagerModel<Villager>>(this, $$0.getModelSet(), $$0.getItemInHandRenderer()));
        this.addLayer(new VillagerProfessionLayer<Villager, VillagerModel<Villager>>(this, $$0.getResourceManager(), "villager"));
        this.addLayer(new CrossedArmsItemLayer<Villager, VillagerModel<Villager>>(this, $$0.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Villager $$0) {
        return VILLAGER_BASE_SKIN;
    }

    @Override
    protected void scale(Villager $$0, PoseStack $$1, float $$2) {
        float $$3 = 0.9375f;
        if ($$0.isBaby()) {
            $$3 *= 0.5f;
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
        $$1.scale($$3, $$3, $$3);
    }
}