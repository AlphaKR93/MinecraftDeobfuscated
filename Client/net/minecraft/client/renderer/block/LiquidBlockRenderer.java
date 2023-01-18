/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlockRenderer {
    private static final float MAX_FLUID_HEIGHT = 0.8888889f;
    private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
    private TextureAtlasSprite waterOverlay;

    protected void setupSprites() {
        this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
        this.lavaIcons[1] = ModelBakery.LAVA_FLOW.sprite();
        this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        this.waterIcons[1] = ModelBakery.WATER_FLOW.sprite();
        this.waterOverlay = ModelBakery.WATER_OVERLAY.sprite();
    }

    private static boolean isNeighborSameFluid(FluidState $$0, FluidState $$1) {
        return $$1.getType().isSame($$0.getType());
    }

    private static boolean isFaceOccludedByState(BlockGetter $$0, Direction $$1, float $$2, BlockPos $$3, BlockState $$4) {
        if ($$4.canOcclude()) {
            VoxelShape $$5 = Shapes.box(0.0, 0.0, 0.0, 1.0, $$2, 1.0);
            VoxelShape $$6 = $$4.getOcclusionShape($$0, $$3);
            return Shapes.blockOccudes($$5, $$6, $$1);
        }
        return false;
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter $$0, BlockPos $$1, Direction $$2, float $$3, BlockState $$4) {
        return LiquidBlockRenderer.isFaceOccludedByState($$0, $$2, $$3, (BlockPos)$$1.relative($$2), $$4);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter $$0, BlockPos $$1, BlockState $$2, Direction $$3) {
        return LiquidBlockRenderer.isFaceOccludedByState($$0, $$3.getOpposite(), 1.0f, $$1, $$2);
    }

    public static boolean shouldRenderFace(BlockAndTintGetter $$0, BlockPos $$1, FluidState $$2, BlockState $$3, Direction $$4, FluidState $$5) {
        return !LiquidBlockRenderer.isFaceOccludedBySelf($$0, $$1, $$3, $$4) && !LiquidBlockRenderer.isNeighborSameFluid($$2, $$5);
    }

    /*
     * WARNING - void declaration
     */
    public void tesselate(BlockAndTintGetter $$0, BlockPos $$1, VertexConsumer $$2, BlockState $$3, FluidState $$4) {
        float $$51;
        float $$46;
        float $$45;
        float $$44;
        float $$43;
        boolean $$5 = $$4.is(FluidTags.LAVA);
        TextureAtlasSprite[] $$6 = $$5 ? this.lavaIcons : this.waterIcons;
        int $$7 = $$5 ? 0xFFFFFF : BiomeColors.getAverageWaterColor($$0, $$1);
        float $$8 = (float)($$7 >> 16 & 0xFF) / 255.0f;
        float $$9 = (float)($$7 >> 8 & 0xFF) / 255.0f;
        float $$10 = (float)($$7 & 0xFF) / 255.0f;
        BlockState $$11 = $$0.getBlockState((BlockPos)$$1.relative(Direction.DOWN));
        FluidState $$12 = $$11.getFluidState();
        BlockState $$13 = $$0.getBlockState((BlockPos)$$1.relative(Direction.UP));
        FluidState $$14 = $$13.getFluidState();
        BlockState $$15 = $$0.getBlockState((BlockPos)$$1.relative(Direction.NORTH));
        FluidState $$16 = $$15.getFluidState();
        BlockState $$17 = $$0.getBlockState((BlockPos)$$1.relative(Direction.SOUTH));
        FluidState $$18 = $$17.getFluidState();
        BlockState $$19 = $$0.getBlockState((BlockPos)$$1.relative(Direction.WEST));
        FluidState $$20 = $$19.getFluidState();
        BlockState $$21 = $$0.getBlockState((BlockPos)$$1.relative(Direction.EAST));
        FluidState $$22 = $$21.getFluidState();
        boolean $$23 = !LiquidBlockRenderer.isNeighborSameFluid($$4, $$14);
        boolean $$24 = LiquidBlockRenderer.shouldRenderFace($$0, $$1, $$4, $$3, Direction.DOWN, $$12) && !LiquidBlockRenderer.isFaceOccludedByNeighbor($$0, $$1, Direction.DOWN, 0.8888889f, $$11);
        boolean $$25 = LiquidBlockRenderer.shouldRenderFace($$0, $$1, $$4, $$3, Direction.NORTH, $$16);
        boolean $$26 = LiquidBlockRenderer.shouldRenderFace($$0, $$1, $$4, $$3, Direction.SOUTH, $$18);
        boolean $$27 = LiquidBlockRenderer.shouldRenderFace($$0, $$1, $$4, $$3, Direction.WEST, $$20);
        boolean $$28 = LiquidBlockRenderer.shouldRenderFace($$0, $$1, $$4, $$3, Direction.EAST, $$22);
        if (!($$23 || $$24 || $$28 || $$27 || $$25 || $$26)) {
            return;
        }
        float $$29 = $$0.getShade(Direction.DOWN, true);
        float $$30 = $$0.getShade(Direction.UP, true);
        float $$31 = $$0.getShade(Direction.NORTH, true);
        float $$32 = $$0.getShade(Direction.WEST, true);
        Fluid $$33 = $$4.getType();
        float $$34 = this.getHeight($$0, $$33, $$1, $$3, $$4);
        if ($$34 >= 1.0f) {
            float $$35 = 1.0f;
            float $$36 = 1.0f;
            float $$37 = 1.0f;
            float $$38 = 1.0f;
        } else {
            float $$39 = this.getHeight($$0, $$33, (BlockPos)$$1.north(), $$15, $$16);
            float $$40 = this.getHeight($$0, $$33, (BlockPos)$$1.south(), $$17, $$18);
            float $$41 = this.getHeight($$0, $$33, (BlockPos)$$1.east(), $$21, $$22);
            float $$42 = this.getHeight($$0, $$33, (BlockPos)$$1.west(), $$19, $$20);
            $$43 = this.calculateAverageHeight($$0, $$33, $$34, $$39, $$41, (BlockPos)((BlockPos)$$1.relative(Direction.NORTH)).relative(Direction.EAST));
            $$44 = this.calculateAverageHeight($$0, $$33, $$34, $$39, $$42, (BlockPos)((BlockPos)$$1.relative(Direction.NORTH)).relative(Direction.WEST));
            $$45 = this.calculateAverageHeight($$0, $$33, $$34, $$40, $$41, (BlockPos)((BlockPos)$$1.relative(Direction.SOUTH)).relative(Direction.EAST));
            $$46 = this.calculateAverageHeight($$0, $$33, $$34, $$40, $$42, (BlockPos)((BlockPos)$$1.relative(Direction.SOUTH)).relative(Direction.WEST));
        }
        double $$47 = $$1.getX() & 0xF;
        double $$48 = $$1.getY() & 0xF;
        double $$49 = $$1.getZ() & 0xF;
        float $$50 = 0.001f;
        float f = $$51 = $$24 ? 0.001f : 0.0f;
        if ($$23 && !LiquidBlockRenderer.isFaceOccludedByNeighbor($$0, $$1, Direction.UP, Math.min((float)Math.min((float)$$44, (float)$$46), (float)Math.min((float)$$45, (float)$$43)), $$13)) {
            float $$74;
            float $$73;
            float $$72;
            float $$71;
            float $$70;
            float $$69;
            float $$68;
            float $$67;
            $$44 -= 0.001f;
            $$46 -= 0.001f;
            $$45 -= 0.001f;
            $$43 -= 0.001f;
            Vec3 $$52 = $$4.getFlow($$0, $$1);
            if ($$52.x == 0.0 && $$52.z == 0.0) {
                TextureAtlasSprite $$53 = $$6[0];
                float $$54 = $$53.getU(0.0);
                float $$55 = $$53.getV(0.0);
                float $$56 = $$54;
                float $$57 = $$53.getV(16.0);
                float $$58 = $$53.getU(16.0);
                float $$59 = $$57;
                float $$60 = $$58;
                float $$61 = $$55;
            } else {
                TextureAtlasSprite $$62 = $$6[1];
                float $$63 = (float)Mth.atan2($$52.z, $$52.x) - 1.5707964f;
                float $$64 = Mth.sin($$63) * 0.25f;
                float $$65 = Mth.cos($$63) * 0.25f;
                float $$66 = 8.0f;
                $$67 = $$62.getU(8.0f + (-$$65 - $$64) * 16.0f);
                $$68 = $$62.getV(8.0f + (-$$65 + $$64) * 16.0f);
                $$69 = $$62.getU(8.0f + (-$$65 + $$64) * 16.0f);
                $$70 = $$62.getV(8.0f + ($$65 + $$64) * 16.0f);
                $$71 = $$62.getU(8.0f + ($$65 + $$64) * 16.0f);
                $$72 = $$62.getV(8.0f + ($$65 - $$64) * 16.0f);
                $$73 = $$62.getU(8.0f + ($$65 - $$64) * 16.0f);
                $$74 = $$62.getV(8.0f + (-$$65 - $$64) * 16.0f);
            }
            void $$75 = ($$67 + $$69 + $$71 + $$73) / 4.0f;
            void $$76 = ($$68 + $$70 + $$72 + $$74) / 4.0f;
            float $$77 = $$6[0].uvShrinkRatio();
            $$67 = Mth.lerp($$77, $$67, (float)$$75);
            $$69 = Mth.lerp($$77, $$69, (float)$$75);
            $$71 = Mth.lerp($$77, $$71, (float)$$75);
            $$73 = Mth.lerp($$77, $$73, (float)$$75);
            $$68 = Mth.lerp($$77, $$68, (float)$$76);
            $$70 = Mth.lerp($$77, $$70, (float)$$76);
            $$72 = Mth.lerp($$77, $$72, (float)$$76);
            $$74 = Mth.lerp($$77, $$74, (float)$$76);
            int $$78 = this.getLightColor($$0, $$1);
            float $$79 = $$30 * $$8;
            float $$80 = $$30 * $$9;
            float $$81 = $$30 * $$10;
            this.vertex($$2, $$47 + 0.0, $$48 + (double)$$44, $$49 + 0.0, $$79, $$80, $$81, $$67, $$68, $$78);
            this.vertex($$2, $$47 + 0.0, $$48 + (double)$$46, $$49 + 1.0, $$79, $$80, $$81, $$69, $$70, $$78);
            this.vertex($$2, $$47 + 1.0, $$48 + (double)$$45, $$49 + 1.0, $$79, $$80, $$81, $$71, $$72, $$78);
            this.vertex($$2, $$47 + 1.0, $$48 + (double)$$43, $$49 + 0.0, $$79, $$80, $$81, $$73, $$74, $$78);
            if ($$4.shouldRenderBackwardUpFace($$0, (BlockPos)$$1.above())) {
                this.vertex($$2, $$47 + 0.0, $$48 + (double)$$44, $$49 + 0.0, $$79, $$80, $$81, $$67, $$68, $$78);
                this.vertex($$2, $$47 + 1.0, $$48 + (double)$$43, $$49 + 0.0, $$79, $$80, $$81, $$73, $$74, $$78);
                this.vertex($$2, $$47 + 1.0, $$48 + (double)$$45, $$49 + 1.0, $$79, $$80, $$81, $$71, $$72, $$78);
                this.vertex($$2, $$47 + 0.0, $$48 + (double)$$46, $$49 + 1.0, $$79, $$80, $$81, $$69, $$70, $$78);
            }
        }
        if ($$24) {
            float $$82 = $$6[0].getU0();
            float $$83 = $$6[0].getU1();
            float $$84 = $$6[0].getV0();
            float $$85 = $$6[0].getV1();
            int $$86 = this.getLightColor($$0, (BlockPos)$$1.below());
            float $$87 = $$29 * $$8;
            float $$88 = $$29 * $$9;
            float $$89 = $$29 * $$10;
            this.vertex($$2, $$47, $$48 + (double)$$51, $$49 + 1.0, $$87, $$88, $$89, $$82, $$85, $$86);
            this.vertex($$2, $$47, $$48 + (double)$$51, $$49, $$87, $$88, $$89, $$82, $$84, $$86);
            this.vertex($$2, $$47 + 1.0, $$48 + (double)$$51, $$49, $$87, $$88, $$89, $$83, $$84, $$86);
            this.vertex($$2, $$47 + 1.0, $$48 + (double)$$51, $$49 + 1.0, $$87, $$88, $$89, $$83, $$85, $$86);
        }
        int $$90 = this.getLightColor($$0, $$1);
        for (Direction $$91 : Direction.Plane.HORIZONTAL) {
            Block $$122;
            boolean $$119;
            double $$118;
            double $$117;
            double $$116;
            double $$115;
            void $$114;
            float $$113;
            switch ($$91) {
                case NORTH: {
                    void $$92 = $$44;
                    float $$93 = $$43;
                    double $$94 = $$47;
                    double $$95 = $$47 + 1.0;
                    double $$96 = $$49 + (double)0.001f;
                    double $$97 = $$49 + (double)0.001f;
                    boolean $$98 = $$25;
                    break;
                }
                case SOUTH: {
                    void $$99 = $$45;
                    void $$100 = $$46;
                    double $$101 = $$47 + 1.0;
                    double $$102 = $$47;
                    double $$103 = $$49 + 1.0 - (double)0.001f;
                    double $$104 = $$49 + 1.0 - (double)0.001f;
                    boolean $$105 = $$26;
                    break;
                }
                case WEST: {
                    void $$106 = $$46;
                    void $$107 = $$44;
                    double $$108 = $$47 + (double)0.001f;
                    double $$109 = $$47 + (double)0.001f;
                    double $$110 = $$49 + 1.0;
                    double $$111 = $$49;
                    boolean $$112 = $$27;
                    break;
                }
                default: {
                    $$113 = $$43;
                    $$114 = $$45;
                    $$115 = $$47 + 1.0 - (double)0.001f;
                    $$116 = $$47 + 1.0 - (double)0.001f;
                    $$117 = $$49;
                    $$118 = $$49 + 1.0;
                    $$119 = $$28;
                }
            }
            if (!$$119 || LiquidBlockRenderer.isFaceOccludedByNeighbor($$0, $$1, $$91, Math.max((float)$$113, (float)$$114), $$0.getBlockState((BlockPos)$$1.relative($$91)))) continue;
            Vec3i $$120 = $$1.relative($$91);
            TextureAtlasSprite $$121 = $$6[1];
            if (!$$5 && (($$122 = $$0.getBlockState((BlockPos)$$120).getBlock()) instanceof HalfTransparentBlock || $$122 instanceof LeavesBlock)) {
                $$121 = this.waterOverlay;
            }
            float $$123 = $$121.getU(0.0);
            float $$124 = $$121.getU(8.0);
            float $$125 = $$121.getV((1.0f - $$113) * 16.0f * 0.5f);
            float $$126 = $$121.getV((1.0f - $$114) * 16.0f * 0.5f);
            float $$127 = $$121.getV(8.0);
            float $$128 = $$91.getAxis() == Direction.Axis.Z ? $$31 : $$32;
            float $$129 = $$30 * $$128 * $$8;
            float $$130 = $$30 * $$128 * $$9;
            float $$131 = $$30 * $$128 * $$10;
            this.vertex($$2, $$115, $$48 + (double)$$113, $$117, $$129, $$130, $$131, $$123, $$125, $$90);
            this.vertex($$2, $$116, $$48 + (double)$$114, $$118, $$129, $$130, $$131, $$124, $$126, $$90);
            this.vertex($$2, $$116, $$48 + (double)$$51, $$118, $$129, $$130, $$131, $$124, $$127, $$90);
            this.vertex($$2, $$115, $$48 + (double)$$51, $$117, $$129, $$130, $$131, $$123, $$127, $$90);
            if ($$121 == this.waterOverlay) continue;
            this.vertex($$2, $$115, $$48 + (double)$$51, $$117, $$129, $$130, $$131, $$123, $$127, $$90);
            this.vertex($$2, $$116, $$48 + (double)$$51, $$118, $$129, $$130, $$131, $$124, $$127, $$90);
            this.vertex($$2, $$116, $$48 + (double)$$114, $$118, $$129, $$130, $$131, $$124, $$126, $$90);
            this.vertex($$2, $$115, $$48 + (double)$$113, $$117, $$129, $$130, $$131, $$123, $$125, $$90);
        }
    }

    private float calculateAverageHeight(BlockAndTintGetter $$0, Fluid $$1, float $$2, float $$3, float $$4, BlockPos $$5) {
        if ($$4 >= 1.0f || $$3 >= 1.0f) {
            return 1.0f;
        }
        float[] $$6 = new float[2];
        if ($$4 > 0.0f || $$3 > 0.0f) {
            float $$7 = this.getHeight($$0, $$1, $$5);
            if ($$7 >= 1.0f) {
                return 1.0f;
            }
            this.addWeightedHeight($$6, $$7);
        }
        this.addWeightedHeight($$6, $$2);
        this.addWeightedHeight($$6, $$4);
        this.addWeightedHeight($$6, $$3);
        return $$6[0] / $$6[1];
    }

    private void addWeightedHeight(float[] $$0, float $$1) {
        if ($$1 >= 0.8f) {
            $$0[0] = $$0[0] + $$1 * 10.0f;
            $$0[1] = $$0[1] + 10.0f;
        } else if ($$1 >= 0.0f) {
            $$0[0] = $$0[0] + $$1;
            $$0[1] = $$0[1] + 1.0f;
        }
    }

    private float getHeight(BlockAndTintGetter $$0, Fluid $$1, BlockPos $$2) {
        BlockState $$3 = $$0.getBlockState($$2);
        return this.getHeight($$0, $$1, $$2, $$3, $$3.getFluidState());
    }

    private float getHeight(BlockAndTintGetter $$0, Fluid $$1, BlockPos $$2, BlockState $$3, FluidState $$4) {
        if ($$1.isSame($$4.getType())) {
            BlockState $$5 = $$0.getBlockState((BlockPos)$$2.above());
            if ($$1.isSame($$5.getFluidState().getType())) {
                return 1.0f;
            }
            return $$4.getOwnHeight();
        }
        if (!$$3.getMaterial().isSolid()) {
            return 0.0f;
        }
        return -1.0f;
    }

    private void vertex(VertexConsumer $$0, double $$1, double $$2, double $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9) {
        $$0.vertex($$1, $$2, $$3).color($$4, $$5, $$6, 1.0f).uv($$7, $$8).uv2($$9).normal(0.0f, 1.0f, 0.0f).endVertex();
    }

    private int getLightColor(BlockAndTintGetter $$0, BlockPos $$1) {
        int $$2 = LevelRenderer.getLightColor($$0, $$1);
        int $$3 = LevelRenderer.getLightColor($$0, (BlockPos)$$1.above());
        int $$4 = $$2 & 0xFF;
        int $$5 = $$3 & 0xFF;
        int $$6 = $$2 >> 16 & 0xFF;
        int $$7 = $$3 >> 16 & 0xFF;
        return ($$4 > $$5 ? $$4 : $$5) | ($$6 > $$7 ? $$6 : $$7) << 16;
    }
}