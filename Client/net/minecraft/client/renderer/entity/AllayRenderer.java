/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;

public class AllayRenderer
extends MobRenderer<Allay, AllayModel> {
    private static final ResourceLocation ALLAY_TEXTURE = new ResourceLocation("textures/entity/allay/allay.png");

    public AllayRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new AllayModel($$0.bakeLayer(ModelLayers.ALLAY)), 0.4f);
        this.addLayer(new ItemInHandLayer<Allay, AllayModel>(this, $$0.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Allay $$0) {
        return ALLAY_TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(Allay $$0, BlockPos $$1) {
        return 15;
    }
}