/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;

public class TheEndGatewayRenderer
extends TheEndPortalRenderer<TheEndGatewayBlockEntity> {
    private static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/end_gateway_beam.png");

    public TheEndGatewayRenderer(BlockEntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(TheEndGatewayBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        if ($$0.isSpawning() || $$0.isCoolingDown()) {
            float $$6 = $$0.isSpawning() ? $$0.getSpawnPercent($$1) : $$0.getCooldownPercent($$1);
            double $$7 = $$0.isSpawning() ? (double)$$0.getLevel().getMaxBuildHeight() : 50.0;
            $$6 = Mth.sin($$6 * (float)Math.PI);
            int $$8 = Mth.floor((double)$$6 * $$7);
            float[] $$9 = $$0.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColors() : DyeColor.PURPLE.getTextureDiffuseColors();
            long $$10 = $$0.getLevel().getGameTime();
            BeaconRenderer.renderBeaconBeam($$2, $$3, BEAM_LOCATION, $$1, $$6, $$10, -$$8, $$8 * 2, $$9, 0.15f, 0.175f);
        }
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected float getOffsetUp() {
        return 1.0f;
    }

    @Override
    protected float getOffsetDown() {
        return 0.0f;
    }

    @Override
    protected RenderType renderType() {
        return RenderType.endGateway();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}