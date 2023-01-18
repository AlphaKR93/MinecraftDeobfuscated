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
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Blocks;

public class IronGolemFlowerLayer
extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    private final BlockRenderDispatcher blockRenderer;

    public IronGolemFlowerLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, IronGolem $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if ($$3.getOfferFlowerTick() == 0) {
            return;
        }
        $$0.pushPose();
        ModelPart $$10 = ((IronGolemModel)this.getParentModel()).getFlowerHoldingArm();
        $$10.translateAndRotate($$0);
        $$0.translate(-1.1875f, 1.0625f, -0.9375f);
        $$0.translate(0.5f, 0.5f, 0.5f);
        float $$11 = 0.5f;
        $$0.scale(0.5f, 0.5f, 0.5f);
        $$0.mulPose(Axis.XP.rotationDegrees(-90.0f));
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.blockRenderer.renderSingleBlock(Blocks.POPPY.defaultBlockState(), $$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}