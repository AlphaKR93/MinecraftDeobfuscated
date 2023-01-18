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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowGolemHeadLayer
extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
    private final BlockRenderDispatcher blockRenderer;
    private final ItemRenderer itemRenderer;

    public SnowGolemHeadLayer(RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> $$0, BlockRenderDispatcher $$1, ItemRenderer $$2) {
        super($$0);
        this.blockRenderer = $$1;
        this.itemRenderer = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, SnowGolem $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        boolean $$10;
        if (!$$3.hasPumpkin()) {
            return;
        }
        boolean bl = $$10 = Minecraft.getInstance().shouldEntityAppearGlowing($$3) && $$3.isInvisible();
        if ($$3.isInvisible() && !$$10) {
            return;
        }
        $$0.pushPose();
        ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate($$0);
        float $$11 = 0.625f;
        $$0.translate(0.0f, -0.34375f, 0.0f);
        $$0.mulPose(Axis.YP.rotationDegrees(180.0f));
        $$0.scale(0.625f, -0.625f, -0.625f);
        ItemStack $$12 = new ItemStack(Blocks.CARVED_PUMPKIN);
        if ($$10) {
            BlockState $$13 = Blocks.CARVED_PUMPKIN.defaultBlockState();
            BakedModel $$14 = this.blockRenderer.getBlockModel($$13);
            int $$15 = LivingEntityRenderer.getOverlayCoords($$3, 0.0f);
            $$0.translate(-0.5f, -0.5f, -0.5f);
            this.blockRenderer.getModelRenderer().renderModel($$0.last(), $$1.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), $$13, $$14, 0.0f, 0.0f, 0.0f, $$2, $$15);
        } else {
            this.itemRenderer.renderStatic($$3, $$12, ItemTransforms.TransformType.HEAD, false, $$0, $$1, $$3.level, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), $$3.getId());
        }
        $$0.popPose();
    }
}