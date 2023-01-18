/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockRenderDispatcher
implements ResourceManagerReloadListener {
    private final BlockModelShaper blockModelShaper;
    private final ModelBlockRenderer modelRenderer;
    private final BlockEntityWithoutLevelRenderer blockEntityRenderer;
    private final LiquidBlockRenderer liquidBlockRenderer;
    private final RandomSource random = RandomSource.create();
    private final BlockColors blockColors;

    public BlockRenderDispatcher(BlockModelShaper $$0, BlockEntityWithoutLevelRenderer $$1, BlockColors $$2) {
        this.blockModelShaper = $$0;
        this.blockEntityRenderer = $$1;
        this.blockColors = $$2;
        this.modelRenderer = new ModelBlockRenderer(this.blockColors);
        this.liquidBlockRenderer = new LiquidBlockRenderer();
    }

    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }

    public void renderBreakingTexture(BlockState $$0, BlockPos $$1, BlockAndTintGetter $$2, PoseStack $$3, VertexConsumer $$4) {
        if ($$0.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        BakedModel $$5 = this.blockModelShaper.getBlockModel($$0);
        long $$6 = $$0.getSeed($$1);
        this.modelRenderer.tesselateBlock($$2, $$5, $$0, $$1, $$3, $$4, true, this.random, $$6, OverlayTexture.NO_OVERLAY);
    }

    public void renderBatched(BlockState $$0, BlockPos $$1, BlockAndTintGetter $$2, PoseStack $$3, VertexConsumer $$4, boolean $$5, RandomSource $$6) {
        try {
            RenderShape $$7 = $$0.getRenderShape();
            if ($$7 == RenderShape.MODEL) {
                this.modelRenderer.tesselateBlock($$2, this.getBlockModel($$0), $$0, $$1, $$3, $$4, $$5, $$6, $$0.getSeed($$1), OverlayTexture.NO_OVERLAY);
            }
        }
        catch (Throwable $$8) {
            CrashReport $$9 = CrashReport.forThrowable($$8, "Tesselating block in world");
            CrashReportCategory $$10 = $$9.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails($$10, $$2, $$1, $$0);
            throw new ReportedException($$9);
        }
    }

    public void renderLiquid(BlockPos $$0, BlockAndTintGetter $$1, VertexConsumer $$2, BlockState $$3, FluidState $$4) {
        try {
            this.liquidBlockRenderer.tesselate($$1, $$0, $$2, $$3, $$4);
        }
        catch (Throwable $$5) {
            CrashReport $$6 = CrashReport.forThrowable($$5, "Tesselating liquid in world");
            CrashReportCategory $$7 = $$6.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails($$7, $$1, $$0, null);
            throw new ReportedException($$6);
        }
    }

    public ModelBlockRenderer getModelRenderer() {
        return this.modelRenderer;
    }

    public BakedModel getBlockModel(BlockState $$0) {
        return this.blockModelShaper.getBlockModel($$0);
    }

    public void renderSingleBlock(BlockState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4) {
        RenderShape $$5 = $$0.getRenderShape();
        if ($$5 == RenderShape.INVISIBLE) {
            return;
        }
        switch ($$5) {
            case MODEL: {
                BakedModel $$6 = this.getBlockModel($$0);
                int $$7 = this.blockColors.getColor($$0, null, null, 0);
                float $$8 = (float)($$7 >> 16 & 0xFF) / 255.0f;
                float $$9 = (float)($$7 >> 8 & 0xFF) / 255.0f;
                float $$10 = (float)($$7 & 0xFF) / 255.0f;
                this.modelRenderer.renderModel($$1.last(), $$2.getBuffer(ItemBlockRenderTypes.getRenderType($$0, false)), $$0, $$6, $$8, $$9, $$10, $$3, $$4);
                break;
            }
            case ENTITYBLOCK_ANIMATED: {
                this.blockEntityRenderer.renderByItem(new ItemStack($$0.getBlock()), ItemTransforms.TransformType.NONE, $$1, $$2, $$3, $$4);
            }
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        this.liquidBlockRenderer.setupSprites();
    }
}