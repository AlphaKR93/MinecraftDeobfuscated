/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Blocks;

public class TntRenderer
extends EntityRenderer<PrimedTnt> {
    private final BlockRenderDispatcher blockRenderer;

    public TntRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(PrimedTnt $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        $$3.pushPose();
        $$3.translate(0.0f, 0.5f, 0.0f);
        int $$6 = $$0.getFuse();
        if ((float)$$6 - $$2 + 1.0f < 10.0f) {
            float $$7 = 1.0f - ((float)$$6 - $$2 + 1.0f) / 10.0f;
            $$7 = Mth.clamp($$7, 0.0f, 1.0f);
            $$7 *= $$7;
            $$7 *= $$7;
            float $$8 = 1.0f + $$7 * 0.3f;
            $$3.scale($$8, $$8, $$8);
        }
        $$3.mulPose(Axis.YP.rotationDegrees(-90.0f));
        $$3.translate(-0.5f, -0.5f, 0.5f);
        $$3.mulPose(Axis.YP.rotationDegrees(90.0f));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, Blocks.TNT.defaultBlockState(), $$3, $$4, $$5, $$6 / 5 % 2 == 0);
        $$3.popPose();
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public ResourceLocation getTextureLocation(PrimedTnt $$0) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}