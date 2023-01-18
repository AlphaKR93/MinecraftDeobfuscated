/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.ThreadLocal
 *  java.lang.Throwable
 *  java.util.BitSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ModelBlockRenderer {
    private static final int FACE_CUBIC = 0;
    private static final int FACE_PARTIAL = 1;
    static final Direction[] DIRECTIONS = Direction.values();
    private final BlockColors blockColors;
    private static final int CACHE_SIZE = 100;
    static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(Cache::new);

    public ModelBlockRenderer(BlockColors $$0) {
        this.blockColors = $$0;
    }

    public void tesselateBlock(BlockAndTintGetter $$0, BakedModel $$1, BlockState $$2, BlockPos $$3, PoseStack $$4, VertexConsumer $$5, boolean $$6, RandomSource $$7, long $$8, int $$9) {
        boolean $$10 = Minecraft.useAmbientOcclusion() && $$2.getLightEmission() == 0 && $$1.useAmbientOcclusion();
        Vec3 $$11 = $$2.getOffset($$0, $$3);
        $$4.translate($$11.x, $$11.y, $$11.z);
        try {
            if ($$10) {
                this.tesselateWithAO($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
            } else {
                this.tesselateWithoutAO($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
            }
        }
        catch (Throwable $$12) {
            CrashReport $$13 = CrashReport.forThrowable($$12, "Tesselating block model");
            CrashReportCategory $$14 = $$13.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails($$14, $$0, $$3, $$2);
            $$14.setDetail("Using AO", $$10);
            throw new ReportedException($$13);
        }
    }

    public void tesselateWithAO(BlockAndTintGetter $$0, BakedModel $$1, BlockState $$2, BlockPos $$3, PoseStack $$4, VertexConsumer $$5, boolean $$6, RandomSource $$7, long $$8, int $$9) {
        float[] $$10 = new float[DIRECTIONS.length * 2];
        BitSet $$11 = new BitSet(3);
        AmbientOcclusionFace $$12 = new AmbientOcclusionFace();
        BlockPos.MutableBlockPos $$13 = $$3.mutable();
        for (Direction $$14 : DIRECTIONS) {
            $$7.setSeed($$8);
            List<BakedQuad> $$15 = $$1.getQuads($$2, $$14, $$7);
            if ($$15.isEmpty()) continue;
            $$13.setWithOffset((Vec3i)$$3, $$14);
            if ($$6 && !Block.shouldRenderFace($$2, $$0, $$3, $$14, $$13)) continue;
            this.renderModelFaceAO($$0, $$2, $$3, $$4, $$5, $$15, $$10, $$11, $$12, $$9);
        }
        $$7.setSeed($$8);
        List<BakedQuad> $$16 = $$1.getQuads($$2, null, $$7);
        if (!$$16.isEmpty()) {
            this.renderModelFaceAO($$0, $$2, $$3, $$4, $$5, $$16, $$10, $$11, $$12, $$9);
        }
    }

    public void tesselateWithoutAO(BlockAndTintGetter $$0, BakedModel $$1, BlockState $$2, BlockPos $$3, PoseStack $$4, VertexConsumer $$5, boolean $$6, RandomSource $$7, long $$8, int $$9) {
        BitSet $$10 = new BitSet(3);
        BlockPos.MutableBlockPos $$11 = $$3.mutable();
        for (Direction $$12 : DIRECTIONS) {
            $$7.setSeed($$8);
            List<BakedQuad> $$13 = $$1.getQuads($$2, $$12, $$7);
            if ($$13.isEmpty()) continue;
            $$11.setWithOffset((Vec3i)$$3, $$12);
            if ($$6 && !Block.shouldRenderFace($$2, $$0, $$3, $$12, $$11)) continue;
            int $$14 = LevelRenderer.getLightColor($$0, $$2, $$11);
            this.renderModelFaceFlat($$0, $$2, $$3, $$14, $$9, false, $$4, $$5, $$13, $$10);
        }
        $$7.setSeed($$8);
        List<BakedQuad> $$15 = $$1.getQuads($$2, null, $$7);
        if (!$$15.isEmpty()) {
            this.renderModelFaceFlat($$0, $$2, $$3, -1, $$9, true, $$4, $$5, $$15, $$10);
        }
    }

    private void renderModelFaceAO(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, PoseStack $$3, VertexConsumer $$4, List<BakedQuad> $$5, float[] $$6, BitSet $$7, AmbientOcclusionFace $$8, int $$9) {
        for (BakedQuad $$10 : $$5) {
            this.calculateShape($$0, $$1, $$2, $$10.getVertices(), $$10.getDirection(), $$6, $$7);
            $$8.calculate($$0, $$1, $$2, $$10.getDirection(), $$6, $$7, $$10.isShade());
            this.putQuadData($$0, $$1, $$2, $$4, $$3.last(), $$10, $$8.brightness[0], $$8.brightness[1], $$8.brightness[2], $$8.brightness[3], $$8.lightmap[0], $$8.lightmap[1], $$8.lightmap[2], $$8.lightmap[3], $$9);
        }
    }

    private void putQuadData(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, VertexConsumer $$3, PoseStack.Pose $$4, BakedQuad $$5, float $$6, float $$7, float $$8, float $$9, int $$10, int $$11, int $$12, int $$13, int $$14) {
        float $$21;
        float $$20;
        float $$19;
        if ($$5.isTinted()) {
            int $$15 = this.blockColors.getColor($$1, $$0, $$2, $$5.getTintIndex());
            float $$16 = (float)($$15 >> 16 & 0xFF) / 255.0f;
            float $$17 = (float)($$15 >> 8 & 0xFF) / 255.0f;
            float $$18 = (float)($$15 & 0xFF) / 255.0f;
        } else {
            $$19 = 1.0f;
            $$20 = 1.0f;
            $$21 = 1.0f;
        }
        $$3.putBulkData($$4, $$5, new float[]{$$6, $$7, $$8, $$9}, $$19, $$20, $$21, new int[]{$$10, $$11, $$12, $$13}, $$14, true);
    }

    private void calculateShape(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, int[] $$3, Direction $$4, @Nullable float[] $$5, BitSet $$6) {
        float $$7 = 32.0f;
        float $$8 = 32.0f;
        float $$9 = 32.0f;
        float $$10 = -32.0f;
        float $$11 = -32.0f;
        float $$12 = -32.0f;
        for (int $$13 = 0; $$13 < 4; ++$$13) {
            float $$14 = Float.intBitsToFloat((int)$$3[$$13 * 8]);
            float $$15 = Float.intBitsToFloat((int)$$3[$$13 * 8 + 1]);
            float $$16 = Float.intBitsToFloat((int)$$3[$$13 * 8 + 2]);
            $$7 = Math.min((float)$$7, (float)$$14);
            $$8 = Math.min((float)$$8, (float)$$15);
            $$9 = Math.min((float)$$9, (float)$$16);
            $$10 = Math.max((float)$$10, (float)$$14);
            $$11 = Math.max((float)$$11, (float)$$15);
            $$12 = Math.max((float)$$12, (float)$$16);
        }
        if ($$5 != null) {
            $$5[Direction.WEST.get3DDataValue()] = $$7;
            $$5[Direction.EAST.get3DDataValue()] = $$10;
            $$5[Direction.DOWN.get3DDataValue()] = $$8;
            $$5[Direction.UP.get3DDataValue()] = $$11;
            $$5[Direction.NORTH.get3DDataValue()] = $$9;
            $$5[Direction.SOUTH.get3DDataValue()] = $$12;
            int $$17 = DIRECTIONS.length;
            $$5[Direction.WEST.get3DDataValue() + $$17] = 1.0f - $$7;
            $$5[Direction.EAST.get3DDataValue() + $$17] = 1.0f - $$10;
            $$5[Direction.DOWN.get3DDataValue() + $$17] = 1.0f - $$8;
            $$5[Direction.UP.get3DDataValue() + $$17] = 1.0f - $$11;
            $$5[Direction.NORTH.get3DDataValue() + $$17] = 1.0f - $$9;
            $$5[Direction.SOUTH.get3DDataValue() + $$17] = 1.0f - $$12;
        }
        float $$18 = 1.0E-4f;
        float $$19 = 0.9999f;
        switch ($$4) {
            case DOWN: {
                $$6.set(1, $$7 >= 1.0E-4f || $$9 >= 1.0E-4f || $$10 <= 0.9999f || $$12 <= 0.9999f);
                $$6.set(0, $$8 == $$11 && ($$8 < 1.0E-4f || $$1.isCollisionShapeFullBlock($$0, $$2)));
                break;
            }
            case UP: {
                $$6.set(1, $$7 >= 1.0E-4f || $$9 >= 1.0E-4f || $$10 <= 0.9999f || $$12 <= 0.9999f);
                $$6.set(0, $$8 == $$11 && ($$11 > 0.9999f || $$1.isCollisionShapeFullBlock($$0, $$2)));
                break;
            }
            case NORTH: {
                $$6.set(1, $$7 >= 1.0E-4f || $$8 >= 1.0E-4f || $$10 <= 0.9999f || $$11 <= 0.9999f);
                $$6.set(0, $$9 == $$12 && ($$9 < 1.0E-4f || $$1.isCollisionShapeFullBlock($$0, $$2)));
                break;
            }
            case SOUTH: {
                $$6.set(1, $$7 >= 1.0E-4f || $$8 >= 1.0E-4f || $$10 <= 0.9999f || $$11 <= 0.9999f);
                $$6.set(0, $$9 == $$12 && ($$12 > 0.9999f || $$1.isCollisionShapeFullBlock($$0, $$2)));
                break;
            }
            case WEST: {
                $$6.set(1, $$8 >= 1.0E-4f || $$9 >= 1.0E-4f || $$11 <= 0.9999f || $$12 <= 0.9999f);
                $$6.set(0, $$7 == $$10 && ($$7 < 1.0E-4f || $$1.isCollisionShapeFullBlock($$0, $$2)));
                break;
            }
            case EAST: {
                $$6.set(1, $$8 >= 1.0E-4f || $$9 >= 1.0E-4f || $$11 <= 0.9999f || $$12 <= 0.9999f);
                $$6.set(0, $$7 == $$10 && ($$10 > 0.9999f || $$1.isCollisionShapeFullBlock($$0, $$2)));
            }
        }
    }

    private void renderModelFaceFlat(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, int $$3, int $$4, boolean $$5, PoseStack $$6, VertexConsumer $$7, List<BakedQuad> $$8, BitSet $$9) {
        for (BakedQuad $$10 : $$8) {
            if ($$5) {
                this.calculateShape($$0, $$1, $$2, $$10.getVertices(), $$10.getDirection(), null, $$9);
                BlockPos $$11 = $$9.get(0) ? $$2.relative($$10.getDirection()) : $$2;
                $$3 = LevelRenderer.getLightColor($$0, $$1, $$11);
            }
            float $$12 = $$0.getShade($$10.getDirection(), $$10.isShade());
            this.putQuadData($$0, $$1, $$2, $$7, $$6.last(), $$10, $$12, $$12, $$12, $$12, $$3, $$3, $$3, $$3, $$4);
        }
    }

    public void renderModel(PoseStack.Pose $$0, VertexConsumer $$1, @Nullable BlockState $$2, BakedModel $$3, float $$4, float $$5, float $$6, int $$7, int $$8) {
        RandomSource $$9 = RandomSource.create();
        long $$10 = 42L;
        for (Direction $$11 : DIRECTIONS) {
            $$9.setSeed(42L);
            ModelBlockRenderer.renderQuadList($$0, $$1, $$4, $$5, $$6, $$3.getQuads($$2, $$11, $$9), $$7, $$8);
        }
        $$9.setSeed(42L);
        ModelBlockRenderer.renderQuadList($$0, $$1, $$4, $$5, $$6, $$3.getQuads($$2, null, $$9), $$7, $$8);
    }

    private static void renderQuadList(PoseStack.Pose $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, List<BakedQuad> $$5, int $$6, int $$7) {
        for (BakedQuad $$8 : $$5) {
            float $$14;
            float $$13;
            float $$12;
            if ($$8.isTinted()) {
                float $$9 = Mth.clamp($$2, 0.0f, 1.0f);
                float $$10 = Mth.clamp($$3, 0.0f, 1.0f);
                float $$11 = Mth.clamp($$4, 0.0f, 1.0f);
            } else {
                $$12 = 1.0f;
                $$13 = 1.0f;
                $$14 = 1.0f;
            }
            $$1.putBulkData($$0, $$8, $$12, $$13, $$14, $$6, $$7);
        }
    }

    public static void enableCaching() {
        ((Cache)CACHE.get()).enable();
    }

    public static void clearCache() {
        ((Cache)CACHE.get()).disable();
    }

    static class AmbientOcclusionFace {
        final float[] brightness = new float[4];
        final int[] lightmap = new int[4];

        public void calculate(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, Direction $$3, float[] $$4, BitSet $$5, boolean $$6) {
            int $$50;
            float $$49;
            int $$45;
            float $$44;
            int $$40;
            float $$39;
            int $$35;
            float $$34;
            boolean $$30;
            BlockPos $$7 = $$5.get(0) ? $$2.relative($$3) : $$2;
            AdjacencyInfo $$8 = AdjacencyInfo.fromFacing($$3);
            BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
            Cache $$10 = (Cache)CACHE.get();
            $$9.setWithOffset((Vec3i)$$7, $$8.corners[0]);
            BlockState $$11 = $$0.getBlockState($$9);
            int $$12 = $$10.getLightColor($$11, $$0, $$9);
            float $$13 = $$10.getShadeBrightness($$11, $$0, $$9);
            $$9.setWithOffset((Vec3i)$$7, $$8.corners[1]);
            BlockState $$14 = $$0.getBlockState($$9);
            int $$15 = $$10.getLightColor($$14, $$0, $$9);
            float $$16 = $$10.getShadeBrightness($$14, $$0, $$9);
            $$9.setWithOffset((Vec3i)$$7, $$8.corners[2]);
            BlockState $$17 = $$0.getBlockState($$9);
            int $$18 = $$10.getLightColor($$17, $$0, $$9);
            float $$19 = $$10.getShadeBrightness($$17, $$0, $$9);
            $$9.setWithOffset((Vec3i)$$7, $$8.corners[3]);
            BlockState $$20 = $$0.getBlockState($$9);
            int $$21 = $$10.getLightColor($$20, $$0, $$9);
            float $$22 = $$10.getShadeBrightness($$20, $$0, $$9);
            BlockState $$23 = $$0.getBlockState($$9.setWithOffset((Vec3i)$$7, $$8.corners[0]).move($$3));
            boolean $$24 = !$$23.isViewBlocking($$0, $$9) || $$23.getLightBlock($$0, $$9) == 0;
            BlockState $$25 = $$0.getBlockState($$9.setWithOffset((Vec3i)$$7, $$8.corners[1]).move($$3));
            boolean $$26 = !$$25.isViewBlocking($$0, $$9) || $$25.getLightBlock($$0, $$9) == 0;
            BlockState $$27 = $$0.getBlockState($$9.setWithOffset((Vec3i)$$7, $$8.corners[2]).move($$3));
            boolean $$28 = !$$27.isViewBlocking($$0, $$9) || $$27.getLightBlock($$0, $$9) == 0;
            BlockState $$29 = $$0.getBlockState($$9.setWithOffset((Vec3i)$$7, $$8.corners[3]).move($$3));
            boolean bl = $$30 = !$$29.isViewBlocking($$0, $$9) || $$29.getLightBlock($$0, $$9) == 0;
            if ($$28 || $$24) {
                $$9.setWithOffset((Vec3i)$$7, $$8.corners[0]).move($$8.corners[2]);
                BlockState $$31 = $$0.getBlockState($$9);
                float $$32 = $$10.getShadeBrightness($$31, $$0, $$9);
                int $$33 = $$10.getLightColor($$31, $$0, $$9);
            } else {
                $$34 = $$13;
                $$35 = $$12;
            }
            if ($$30 || $$24) {
                $$9.setWithOffset((Vec3i)$$7, $$8.corners[0]).move($$8.corners[3]);
                BlockState $$36 = $$0.getBlockState($$9);
                float $$37 = $$10.getShadeBrightness($$36, $$0, $$9);
                int $$38 = $$10.getLightColor($$36, $$0, $$9);
            } else {
                $$39 = $$13;
                $$40 = $$12;
            }
            if ($$28 || $$26) {
                $$9.setWithOffset((Vec3i)$$7, $$8.corners[1]).move($$8.corners[2]);
                BlockState $$41 = $$0.getBlockState($$9);
                float $$42 = $$10.getShadeBrightness($$41, $$0, $$9);
                int $$43 = $$10.getLightColor($$41, $$0, $$9);
            } else {
                $$44 = $$13;
                $$45 = $$12;
            }
            if ($$30 || $$26) {
                $$9.setWithOffset((Vec3i)$$7, $$8.corners[1]).move($$8.corners[3]);
                BlockState $$46 = $$0.getBlockState($$9);
                float $$47 = $$10.getShadeBrightness($$46, $$0, $$9);
                int $$48 = $$10.getLightColor($$46, $$0, $$9);
            } else {
                $$49 = $$13;
                $$50 = $$12;
            }
            int $$51 = $$10.getLightColor($$1, $$0, $$2);
            $$9.setWithOffset((Vec3i)$$2, $$3);
            BlockState $$52 = $$0.getBlockState($$9);
            if ($$5.get(0) || !$$52.isSolidRender($$0, $$9)) {
                $$51 = $$10.getLightColor($$52, $$0, $$9);
            }
            float $$53 = $$5.get(0) ? $$10.getShadeBrightness($$0.getBlockState($$7), $$0, $$7) : $$10.getShadeBrightness($$0.getBlockState($$2), $$0, $$2);
            AmbientVertexRemap $$54 = AmbientVertexRemap.fromFacing($$3);
            if (!$$5.get(1) || !$$8.doNonCubicWeight) {
                float $$55 = ($$22 + $$13 + $$39 + $$53) * 0.25f;
                float $$56 = ($$19 + $$13 + $$34 + $$53) * 0.25f;
                float $$57 = ($$19 + $$16 + $$44 + $$53) * 0.25f;
                float $$58 = ($$22 + $$16 + $$49 + $$53) * 0.25f;
                this.lightmap[$$54.vert0] = this.blend($$21, $$12, $$40, $$51);
                this.lightmap[$$54.vert1] = this.blend($$18, $$12, $$35, $$51);
                this.lightmap[$$54.vert2] = this.blend($$18, $$15, $$45, $$51);
                this.lightmap[$$54.vert3] = this.blend($$21, $$15, $$50, $$51);
                this.brightness[$$54.vert0] = $$55;
                this.brightness[$$54.vert1] = $$56;
                this.brightness[$$54.vert2] = $$57;
                this.brightness[$$54.vert3] = $$58;
            } else {
                float $$59 = ($$22 + $$13 + $$39 + $$53) * 0.25f;
                float $$60 = ($$19 + $$13 + $$34 + $$53) * 0.25f;
                float $$61 = ($$19 + $$16 + $$44 + $$53) * 0.25f;
                float $$62 = ($$22 + $$16 + $$49 + $$53) * 0.25f;
                float $$63 = $$4[$$8.vert0Weights[0].shape] * $$4[$$8.vert0Weights[1].shape];
                float $$64 = $$4[$$8.vert0Weights[2].shape] * $$4[$$8.vert0Weights[3].shape];
                float $$65 = $$4[$$8.vert0Weights[4].shape] * $$4[$$8.vert0Weights[5].shape];
                float $$66 = $$4[$$8.vert0Weights[6].shape] * $$4[$$8.vert0Weights[7].shape];
                float $$67 = $$4[$$8.vert1Weights[0].shape] * $$4[$$8.vert1Weights[1].shape];
                float $$68 = $$4[$$8.vert1Weights[2].shape] * $$4[$$8.vert1Weights[3].shape];
                float $$69 = $$4[$$8.vert1Weights[4].shape] * $$4[$$8.vert1Weights[5].shape];
                float $$70 = $$4[$$8.vert1Weights[6].shape] * $$4[$$8.vert1Weights[7].shape];
                float $$71 = $$4[$$8.vert2Weights[0].shape] * $$4[$$8.vert2Weights[1].shape];
                float $$72 = $$4[$$8.vert2Weights[2].shape] * $$4[$$8.vert2Weights[3].shape];
                float $$73 = $$4[$$8.vert2Weights[4].shape] * $$4[$$8.vert2Weights[5].shape];
                float $$74 = $$4[$$8.vert2Weights[6].shape] * $$4[$$8.vert2Weights[7].shape];
                float $$75 = $$4[$$8.vert3Weights[0].shape] * $$4[$$8.vert3Weights[1].shape];
                float $$76 = $$4[$$8.vert3Weights[2].shape] * $$4[$$8.vert3Weights[3].shape];
                float $$77 = $$4[$$8.vert3Weights[4].shape] * $$4[$$8.vert3Weights[5].shape];
                float $$78 = $$4[$$8.vert3Weights[6].shape] * $$4[$$8.vert3Weights[7].shape];
                this.brightness[$$54.vert0] = $$59 * $$63 + $$60 * $$64 + $$61 * $$65 + $$62 * $$66;
                this.brightness[$$54.vert1] = $$59 * $$67 + $$60 * $$68 + $$61 * $$69 + $$62 * $$70;
                this.brightness[$$54.vert2] = $$59 * $$71 + $$60 * $$72 + $$61 * $$73 + $$62 * $$74;
                this.brightness[$$54.vert3] = $$59 * $$75 + $$60 * $$76 + $$61 * $$77 + $$62 * $$78;
                int $$79 = this.blend($$21, $$12, $$40, $$51);
                int $$80 = this.blend($$18, $$12, $$35, $$51);
                int $$81 = this.blend($$18, $$15, $$45, $$51);
                int $$82 = this.blend($$21, $$15, $$50, $$51);
                this.lightmap[$$54.vert0] = this.blend($$79, $$80, $$81, $$82, $$63, $$64, $$65, $$66);
                this.lightmap[$$54.vert1] = this.blend($$79, $$80, $$81, $$82, $$67, $$68, $$69, $$70);
                this.lightmap[$$54.vert2] = this.blend($$79, $$80, $$81, $$82, $$71, $$72, $$73, $$74);
                this.lightmap[$$54.vert3] = this.blend($$79, $$80, $$81, $$82, $$75, $$76, $$77, $$78);
            }
            float $$83 = $$0.getShade($$3, $$6);
            int $$84 = 0;
            while ($$84 < this.brightness.length) {
                int n = $$84++;
                this.brightness[n] = this.brightness[n] * $$83;
            }
        }

        private int blend(int $$0, int $$1, int $$2, int $$3) {
            if ($$0 == 0) {
                $$0 = $$3;
            }
            if ($$1 == 0) {
                $$1 = $$3;
            }
            if ($$2 == 0) {
                $$2 = $$3;
            }
            return $$0 + $$1 + $$2 + $$3 >> 2 & 0xFF00FF;
        }

        private int blend(int $$0, int $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
            int $$8 = (int)((float)($$0 >> 16 & 0xFF) * $$4 + (float)($$1 >> 16 & 0xFF) * $$5 + (float)($$2 >> 16 & 0xFF) * $$6 + (float)($$3 >> 16 & 0xFF) * $$7) & 0xFF;
            int $$9 = (int)((float)($$0 & 0xFF) * $$4 + (float)($$1 & 0xFF) * $$5 + (float)($$2 & 0xFF) * $$6 + (float)($$3 & 0xFF) * $$7) & 0xFF;
            return $$8 << 16 | $$9;
        }
    }

    static class Cache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap colorCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
            Long2IntLinkedOpenHashMap $$0 = new Long2IntLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int $$0) {
                }
            };
            $$0.defaultReturnValue(Integer.MAX_VALUE);
            return $$0;
        });
        private final Long2FloatLinkedOpenHashMap brightnessCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
            Long2FloatLinkedOpenHashMap $$0 = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int $$0) {
                }
            };
            $$0.defaultReturnValue(Float.NaN);
            return $$0;
        });

        private Cache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.colorCache.clear();
            this.brightnessCache.clear();
        }

        public int getLightColor(BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2) {
            int $$4;
            long $$3 = $$2.asLong();
            if (this.enabled && ($$4 = this.colorCache.get($$3)) != Integer.MAX_VALUE) {
                return $$4;
            }
            int $$5 = LevelRenderer.getLightColor($$1, $$0, $$2);
            if (this.enabled) {
                if (this.colorCache.size() == 100) {
                    this.colorCache.removeFirstInt();
                }
                this.colorCache.put($$3, $$5);
            }
            return $$5;
        }

        public float getShadeBrightness(BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2) {
            float $$4;
            long $$3 = $$2.asLong();
            if (this.enabled && !Float.isNaN((float)($$4 = this.brightnessCache.get($$3)))) {
                return $$4;
            }
            float $$5 = $$0.getShadeBrightness($$1, $$2);
            if (this.enabled) {
                if (this.brightnessCache.size() == 100) {
                    this.brightnessCache.removeFirstFloat();
                }
                this.brightnessCache.put($$3, $$5);
            }
            return $$5;
        }
    }

    protected static enum AdjacencyInfo {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new SizeInfo[]{SizeInfo.EAST, SizeInfo.SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.EAST, SizeInfo.NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.UP, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.DOWN, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.DOWN, SizeInfo.EAST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.UP, SizeInfo.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.UP, SizeInfo.NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.SOUTH});

        final Direction[] corners;
        final boolean doNonCubicWeight;
        final SizeInfo[] vert0Weights;
        final SizeInfo[] vert1Weights;
        final SizeInfo[] vert2Weights;
        final SizeInfo[] vert3Weights;
        private static final AdjacencyInfo[] BY_FACING;

        private AdjacencyInfo(Direction[] $$0, float $$1, boolean $$2, SizeInfo[] $$3, SizeInfo[] $$4, SizeInfo[] $$5, SizeInfo[] $$6) {
            this.corners = $$0;
            this.doNonCubicWeight = $$2;
            this.vert0Weights = $$3;
            this.vert1Weights = $$4;
            this.vert2Weights = $$5;
            this.vert3Weights = $$6;
        }

        public static AdjacencyInfo fromFacing(Direction $$0) {
            return BY_FACING[$$0.get3DDataValue()];
        }

        static {
            BY_FACING = Util.make(new AdjacencyInfo[6], $$0 -> {
                $$0[Direction.DOWN.get3DDataValue()] = DOWN;
                $$0[Direction.UP.get3DDataValue()] = UP;
                $$0[Direction.NORTH.get3DDataValue()] = NORTH;
                $$0[Direction.SOUTH.get3DDataValue()] = SOUTH;
                $$0[Direction.WEST.get3DDataValue()] = WEST;
                $$0[Direction.EAST.get3DDataValue()] = EAST;
            });
        }
    }

    protected static enum SizeInfo {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        final int shape;

        private SizeInfo(Direction $$0, boolean $$1) {
            this.shape = $$0.get3DDataValue() + ($$1 ? DIRECTIONS.length : 0);
        }
    }

    static enum AmbientVertexRemap {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        final int vert0;
        final int vert1;
        final int vert2;
        final int vert3;
        private static final AmbientVertexRemap[] BY_FACING;

        private AmbientVertexRemap(int $$0, int $$1, int $$2, int $$3) {
            this.vert0 = $$0;
            this.vert1 = $$1;
            this.vert2 = $$2;
            this.vert3 = $$3;
        }

        public static AmbientVertexRemap fromFacing(Direction $$0) {
            return BY_FACING[$$0.get3DDataValue()];
        }

        static {
            BY_FACING = Util.make(new AmbientVertexRemap[6], $$0 -> {
                $$0[Direction.DOWN.get3DDataValue()] = DOWN;
                $$0[Direction.UP.get3DDataValue()] = UP;
                $$0[Direction.NORTH.get3DDataValue()] = NORTH;
                $$0[Direction.SOUTH.get3DDataValue()] = SOUTH;
                $$0[Direction.WEST.get3DDataValue()] = WEST;
                $$0[Direction.EAST.get3DDataValue()] = EAST;
            });
        }
    }
}