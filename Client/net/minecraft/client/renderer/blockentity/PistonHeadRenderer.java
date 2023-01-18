/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class PistonHeadRenderer
implements BlockEntityRenderer<PistonMovingBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public PistonHeadRenderer(BlockEntityRendererProvider.Context $$0) {
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(PistonMovingBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        Level $$6 = $$0.getLevel();
        if ($$6 == null) {
            return;
        }
        Vec3i $$7 = $$0.getBlockPos().relative($$0.getMovementDirection().getOpposite());
        BlockState $$8 = $$0.getMovedState();
        if ($$8.isAir()) {
            return;
        }
        ModelBlockRenderer.enableCaching();
        $$2.pushPose();
        $$2.translate($$0.getXOff($$1), $$0.getYOff($$1), $$0.getZOff($$1));
        if ($$8.is(Blocks.PISTON_HEAD) && $$0.getProgress($$1) <= 4.0f) {
            $$8 = (BlockState)$$8.setValue(PistonHeadBlock.SHORT, $$0.getProgress($$1) <= 0.5f);
            this.renderBlock((BlockPos)$$7, $$8, $$2, $$3, $$6, false, $$5);
        } else if ($$0.isSourcePiston() && !$$0.isExtending()) {
            PistonType $$9 = $$8.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState $$10 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, $$9)).setValue(PistonHeadBlock.FACING, $$8.getValue(PistonBaseBlock.FACING));
            $$10 = (BlockState)$$10.setValue(PistonHeadBlock.SHORT, $$0.getProgress($$1) >= 0.5f);
            this.renderBlock((BlockPos)$$7, $$10, $$2, $$3, $$6, false, $$5);
            Vec3i $$11 = ((BlockPos)$$7).relative($$0.getMovementDirection());
            $$2.popPose();
            $$2.pushPose();
            $$8 = (BlockState)$$8.setValue(PistonBaseBlock.EXTENDED, true);
            this.renderBlock((BlockPos)$$11, $$8, $$2, $$3, $$6, true, $$5);
        } else {
            this.renderBlock((BlockPos)$$7, $$8, $$2, $$3, $$6, false, $$5);
        }
        $$2.popPose();
        ModelBlockRenderer.clearCache();
    }

    private void renderBlock(BlockPos $$0, BlockState $$1, PoseStack $$2, MultiBufferSource $$3, Level $$4, boolean $$5, int $$6) {
        RenderType $$7 = ItemBlockRenderTypes.getMovingBlockRenderType($$1);
        VertexConsumer $$8 = $$3.getBuffer($$7);
        this.blockRenderer.getModelRenderer().tesselateBlock($$4, this.blockRenderer.getBlockModel($$1), $$1, $$0, $$2, $$8, $$5, RandomSource.create(), $$1.getSeed($$0), $$6);
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}