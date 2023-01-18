/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.GlowSquid;

public class GlowSquidRenderer
extends SquidRenderer<GlowSquid> {
    private static final ResourceLocation GLOW_SQUID_LOCATION = new ResourceLocation("textures/entity/squid/glow_squid.png");

    public GlowSquidRenderer(EntityRendererProvider.Context $$0, SquidModel<GlowSquid> $$1) {
        super($$0, $$1);
    }

    @Override
    public ResourceLocation getTextureLocation(GlowSquid $$0) {
        return GLOW_SQUID_LOCATION;
    }

    @Override
    protected int getBlockLightLevel(GlowSquid $$0, BlockPos $$1) {
        int $$2 = (int)Mth.clampedLerp(0.0f, 15.0f, 1.0f - (float)$$0.getDarkTicksRemaining() / 10.0f);
        if ($$2 == 15) {
            return 15;
        }
        return Math.max((int)$$2, (int)super.getBlockLightLevel($$0, $$1));
    }
}