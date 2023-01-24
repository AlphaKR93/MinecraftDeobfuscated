/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Giant;

public class GiantMobRenderer
extends MobRenderer<Giant, HumanoidModel<Giant>> {
    private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");
    private final float scale;

    public GiantMobRenderer(EntityRendererProvider.Context $$0, float $$1) {
        super($$0, new GiantZombieModel($$0.bakeLayer(ModelLayers.GIANT)), 0.5f * $$1);
        this.scale = $$1;
        this.addLayer(new ItemInHandLayer<Giant, HumanoidModel<Giant>>(this, $$0.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<Giant, HumanoidModel<Giant>, GiantZombieModel>(this, new GiantZombieModel($$0.bakeLayer(ModelLayers.GIANT_INNER_ARMOR)), new GiantZombieModel($$0.bakeLayer(ModelLayers.GIANT_OUTER_ARMOR)), $$0.getModelManager()));
    }

    @Override
    protected void scale(Giant $$0, PoseStack $$1, float $$2) {
        $$1.scale(this.scale, this.scale, this.scale);
    }

    @Override
    public ResourceLocation getTextureLocation(Giant $$0) {
        return ZOMBIE_LOCATION;
    }
}