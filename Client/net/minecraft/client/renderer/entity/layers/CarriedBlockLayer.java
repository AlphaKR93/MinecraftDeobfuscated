/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

public class CarriedBlockLayer
extends RenderLayer<EnderMan, EndermanModel<EnderMan>> {
    private final BlockRenderDispatcher blockRenderer;

    public CarriedBlockLayer(RenderLayerParent<EnderMan, EndermanModel<EnderMan>> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, EnderMan $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        BlockState $$10 = $$3.getCarriedBlock();
        if ($$10 == null) {
            return;
        }
        $$0.pushPose();
        $$0.translate(0.0f, 0.6875f, -0.75f);
        $$0.mulPose(Axis.XP.rotationDegrees(20.0f));
        $$0.mulPose(Axis.YP.rotationDegrees(45.0f));
        $$0.translate(0.25f, 0.1875f, 0.25f);
        float $$11 = 0.5f;
        $$0.scale(-0.5f, -0.5f, 0.5f);
        $$0.mulPose(Axis.YP.rotationDegrees(90.0f));
        this.blockRenderer.renderSingleBlock($$10, $$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}