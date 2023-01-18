/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;

public class TntMinecartRenderer
extends MinecartRenderer<MinecartTNT> {
    private final BlockRenderDispatcher blockRenderer;

    public TntMinecartRenderer(EntityRendererProvider.Context $$0) {
        super($$0, ModelLayers.TNT_MINECART);
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    protected void renderMinecartContents(MinecartTNT $$0, float $$1, BlockState $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        int $$6 = $$0.getFuse();
        if ($$6 > -1 && (float)$$6 - $$1 + 1.0f < 10.0f) {
            float $$7 = 1.0f - ((float)$$6 - $$1 + 1.0f) / 10.0f;
            $$7 = Mth.clamp($$7, 0.0f, 1.0f);
            $$7 *= $$7;
            $$7 *= $$7;
            float $$8 = 1.0f + $$7 * 0.3f;
            $$3.scale($$8, $$8, $$8);
        }
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, $$2, $$3, $$4, $$5, $$6 > -1 && $$6 / 5 % 2 == 0);
    }

    public static void renderWhiteSolidBlock(BlockRenderDispatcher $$0, BlockState $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, boolean $$5) {
        int $$7;
        if ($$5) {
            int $$6 = OverlayTexture.pack(OverlayTexture.u(1.0f), 10);
        } else {
            $$7 = OverlayTexture.NO_OVERLAY;
        }
        $$0.renderSingleBlock($$1, $$2, $$3, $$4, $$7);
    }
}