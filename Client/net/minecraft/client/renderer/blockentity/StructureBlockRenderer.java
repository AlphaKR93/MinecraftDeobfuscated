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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class StructureBlockRenderer
implements BlockEntityRenderer<StructureBlockEntity> {
    public StructureBlockRenderer(BlockEntityRendererProvider.Context $$0) {
    }

    @Override
    public void render(StructureBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        double $$33;
        double $$32;
        double $$31;
        double $$30;
        double $$17;
        double $$16;
        if (!Minecraft.getInstance().player.canUseGameMasterBlocks() && !Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        BlockPos $$6 = $$0.getStructurePos();
        Vec3i $$7 = $$0.getStructureSize();
        if ($$7.getX() < 1 || $$7.getY() < 1 || $$7.getZ() < 1) {
            return;
        }
        if ($$0.getMode() != StructureMode.SAVE && $$0.getMode() != StructureMode.LOAD) {
            return;
        }
        double $$8 = $$6.getX();
        double $$9 = $$6.getZ();
        double $$10 = $$6.getY();
        double $$11 = $$10 + (double)$$7.getY();
        switch ($$0.getMirror()) {
            case LEFT_RIGHT: {
                double $$12 = $$7.getX();
                double $$13 = -$$7.getZ();
                break;
            }
            case FRONT_BACK: {
                double $$14 = -$$7.getX();
                double $$15 = $$7.getZ();
                break;
            }
            default: {
                $$16 = $$7.getX();
                $$17 = $$7.getZ();
            }
        }
        switch ($$0.getRotation()) {
            case CLOCKWISE_90: {
                double $$18 = $$17 < 0.0 ? $$8 : $$8 + 1.0;
                double $$19 = $$16 < 0.0 ? $$9 + 1.0 : $$9;
                double $$20 = $$18 - $$17;
                double $$21 = $$19 + $$16;
                break;
            }
            case CLOCKWISE_180: {
                double $$22 = $$16 < 0.0 ? $$8 : $$8 + 1.0;
                double $$23 = $$17 < 0.0 ? $$9 : $$9 + 1.0;
                double $$24 = $$22 - $$16;
                double $$25 = $$23 - $$17;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                double $$26 = $$17 < 0.0 ? $$8 + 1.0 : $$8;
                double $$27 = $$16 < 0.0 ? $$9 : $$9 + 1.0;
                double $$28 = $$26 + $$17;
                double $$29 = $$27 - $$16;
                break;
            }
            default: {
                $$30 = $$16 < 0.0 ? $$8 + 1.0 : $$8;
                $$31 = $$17 < 0.0 ? $$9 + 1.0 : $$9;
                $$32 = $$30 + $$16;
                $$33 = $$31 + $$17;
            }
        }
        float $$34 = 1.0f;
        float $$35 = 0.9f;
        float $$36 = 0.5f;
        VertexConsumer $$37 = $$3.getBuffer(RenderType.lines());
        if ($$0.getMode() == StructureMode.SAVE || $$0.getShowBoundingBox()) {
            LevelRenderer.renderLineBox($$2, $$37, $$30, $$10, $$31, $$32, $$11, $$33, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        }
        if ($$0.getMode() == StructureMode.SAVE && $$0.getShowAir()) {
            this.renderInvisibleBlocks($$0, $$37, $$6, $$2);
        }
    }

    private void renderInvisibleBlocks(StructureBlockEntity $$0, VertexConsumer $$1, BlockPos $$2, PoseStack $$3) {
        Level $$4 = $$0.getLevel();
        BlockPos $$5 = $$0.getBlockPos();
        Vec3i $$6 = $$5.offset($$2);
        for (BlockPos $$7 : BlockPos.betweenClosed((BlockPos)$$6, ((BlockPos)((BlockPos)$$6).offset($$0.getStructureSize())).offset(-1, -1, -1))) {
            boolean $$13;
            BlockState $$8 = $$4.getBlockState($$7);
            boolean $$9 = $$8.isAir();
            boolean $$10 = $$8.is(Blocks.STRUCTURE_VOID);
            boolean $$11 = $$8.is(Blocks.BARRIER);
            boolean $$12 = $$8.is(Blocks.LIGHT);
            boolean bl = $$13 = $$10 || $$11 || $$12;
            if (!$$9 && !$$13) continue;
            float $$14 = $$9 ? 0.05f : 0.0f;
            double $$15 = (float)($$7.getX() - $$5.getX()) + 0.45f - $$14;
            double $$16 = (float)($$7.getY() - $$5.getY()) + 0.45f - $$14;
            double $$17 = (float)($$7.getZ() - $$5.getZ()) + 0.45f - $$14;
            double $$18 = (float)($$7.getX() - $$5.getX()) + 0.55f + $$14;
            double $$19 = (float)($$7.getY() - $$5.getY()) + 0.55f + $$14;
            double $$20 = (float)($$7.getZ() - $$5.getZ()) + 0.55f + $$14;
            if ($$9) {
                LevelRenderer.renderLineBox($$3, $$1, $$15, $$16, $$17, $$18, $$19, $$20, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            if ($$10) {
                LevelRenderer.renderLineBox($$3, $$1, $$15, $$16, $$17, $$18, $$19, $$20, 1.0f, 0.75f, 0.75f, 1.0f, 1.0f, 0.75f, 0.75f);
                continue;
            }
            if ($$11) {
                LevelRenderer.renderLineBox($$3, $$1, $$15, $$16, $$17, $$18, $$19, $$20, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
                continue;
            }
            if (!$$12) continue;
            LevelRenderer.renderLineBox($$3, $$1, $$15, $$16, $$17, $$18, $$19, $$20, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(StructureBlockEntity $$0) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}