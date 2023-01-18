/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

public class MagmaCubeRenderer
extends MobRenderer<MagmaCube, LavaSlimeModel<MagmaCube>> {
    private static final ResourceLocation MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");

    public MagmaCubeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new LavaSlimeModel($$0.bakeLayer(ModelLayers.MAGMA_CUBE)), 0.25f);
    }

    @Override
    protected int getBlockLightLevel(MagmaCube $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(MagmaCube $$0) {
        return MAGMACUBE_LOCATION;
    }

    @Override
    protected void scale(MagmaCube $$0, PoseStack $$1, float $$2) {
        int $$3 = $$0.getSize();
        float $$4 = Mth.lerp($$2, $$0.oSquish, $$0.squish) / ((float)$$3 * 0.5f + 1.0f);
        float $$5 = 1.0f / ($$4 + 1.0f);
        $$1.scale($$5 * (float)$$3, 1.0f / $$5 * (float)$$3, $$5 * (float)$$3);
    }
}