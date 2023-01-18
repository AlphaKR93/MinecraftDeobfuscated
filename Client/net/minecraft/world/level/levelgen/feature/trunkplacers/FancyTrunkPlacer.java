/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class FancyTrunkPlacer
extends TrunkPlacer {
    public static final Codec<FancyTrunkPlacer> CODEC = RecordCodecBuilder.create($$0 -> FancyTrunkPlacer.trunkPlacerParts($$0).apply((Applicative)$$0, FancyTrunkPlacer::new));
    private static final double TRUNK_HEIGHT_SCALE = 0.618;
    private static final double CLUSTER_DENSITY_MAGIC = 1.382;
    private static final double BRANCH_SLOPE = 0.381;
    private static final double BRANCH_LENGTH_MAGIC = 0.328;

    public FancyTrunkPlacer(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.FANCY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        int $$12;
        int $$6 = 5;
        int $$7 = $$3 + 2;
        int $$8 = Mth.floor((double)$$7 * 0.618);
        FancyTrunkPlacer.setDirtAt($$0, $$1, $$2, (BlockPos)$$4.below(), $$5);
        double $$9 = 1.0;
        int $$10 = Math.min((int)1, (int)Mth.floor(1.382 + Math.pow((double)(1.0 * (double)$$7 / 13.0), (double)2.0)));
        int $$11 = $$4.getY() + $$8;
        ArrayList $$13 = Lists.newArrayList();
        $$13.add((Object)new FoliageCoords((BlockPos)$$4.above($$12), $$11));
        for ($$12 = $$7 - 5; $$12 >= 0; --$$12) {
            float $$14 = FancyTrunkPlacer.treeShape($$7, $$12);
            if ($$14 < 0.0f) continue;
            for (int $$15 = 0; $$15 < $$10; ++$$15) {
                Vec3i $$22;
                double $$20;
                double $$18;
                double $$16 = 1.0;
                double $$17 = 1.0 * (double)$$14 * ((double)$$2.nextFloat() + 0.328);
                double $$19 = $$17 * Math.sin((double)($$18 = (double)($$2.nextFloat() * 2.0f) * Math.PI)) + 0.5;
                BlockPos $$21 = $$4.offset($$19, (double)($$12 - 1), $$20 = $$17 * Math.cos((double)$$18) + 0.5);
                if (!this.makeLimb($$0, $$1, $$2, $$21, (BlockPos)($$22 = $$21.above(5)), false, $$5)) continue;
                int $$23 = $$4.getX() - $$21.getX();
                int $$24 = $$4.getZ() - $$21.getZ();
                double $$25 = (double)$$21.getY() - Math.sqrt((double)($$23 * $$23 + $$24 * $$24)) * 0.381;
                int $$26 = $$25 > (double)$$11 ? $$11 : (int)$$25;
                BlockPos $$27 = new BlockPos($$4.getX(), $$26, $$4.getZ());
                if (!this.makeLimb($$0, $$1, $$2, $$27, $$21, false, $$5)) continue;
                $$13.add((Object)new FoliageCoords($$21, $$27.getY()));
            }
        }
        this.makeLimb($$0, $$1, $$2, $$4, (BlockPos)$$4.above($$8), true, $$5);
        this.makeBranches($$0, $$1, $$2, $$7, $$4, (List<FoliageCoords>)$$13, $$5);
        ArrayList $$28 = Lists.newArrayList();
        for (FoliageCoords $$29 : $$13) {
            if (!this.trimBranches($$7, $$29.getBranchBase() - $$4.getY())) continue;
            $$28.add((Object)$$29.attachment);
        }
        return $$28;
    }

    private boolean makeLimb(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$22, BlockPos $$3, BlockPos $$4, boolean $$5, TreeConfiguration $$6) {
        if (!$$5 && Objects.equals((Object)$$3, (Object)$$4)) {
            return true;
        }
        BlockPos $$7 = $$4.offset(-$$3.getX(), -$$3.getY(), -$$3.getZ());
        int $$8 = this.getSteps($$7);
        float $$9 = (float)$$7.getX() / (float)$$8;
        float $$10 = (float)$$7.getY() / (float)$$8;
        float $$11 = (float)$$7.getZ() / (float)$$8;
        for (int $$12 = 0; $$12 <= $$8; ++$$12) {
            BlockPos $$13 = $$3.offset(0.5f + (float)$$12 * $$9, 0.5f + (float)$$12 * $$10, 0.5f + (float)$$12 * $$11);
            if ($$5) {
                this.placeLog($$0, $$1, $$22, $$13, $$6, (Function<BlockState, BlockState>)((Function)$$2 -> (BlockState)$$2.trySetValue(RotatedPillarBlock.AXIS, this.getLogAxis($$3, $$13))));
                continue;
            }
            if (this.isFree($$0, $$13)) continue;
            return false;
        }
        return true;
    }

    private int getSteps(BlockPos $$0) {
        int $$1 = Mth.abs($$0.getX());
        int $$2 = Mth.abs($$0.getY());
        int $$3 = Mth.abs($$0.getZ());
        return Math.max((int)$$1, (int)Math.max((int)$$2, (int)$$3));
    }

    private Direction.Axis getLogAxis(BlockPos $$0, BlockPos $$1) {
        int $$4;
        Direction.Axis $$2 = Direction.Axis.Y;
        int $$3 = Math.abs((int)($$1.getX() - $$0.getX()));
        int $$5 = Math.max((int)$$3, (int)($$4 = Math.abs((int)($$1.getZ() - $$0.getZ()))));
        if ($$5 > 0) {
            $$2 = $$3 == $$5 ? Direction.Axis.X : Direction.Axis.Z;
        }
        return $$2;
    }

    private boolean trimBranches(int $$0, int $$1) {
        return (double)$$1 >= (double)$$0 * 0.2;
    }

    private void makeBranches(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, List<FoliageCoords> $$5, TreeConfiguration $$6) {
        for (FoliageCoords $$7 : $$5) {
            int $$8 = $$7.getBranchBase();
            BlockPos $$9 = new BlockPos($$4.getX(), $$8, $$4.getZ());
            if ($$9.equals($$7.attachment.pos()) || !this.trimBranches($$3, $$8 - $$4.getY())) continue;
            this.makeLimb($$0, $$1, $$2, $$9, $$7.attachment.pos(), true, $$6);
        }
    }

    private static float treeShape(int $$0, int $$1) {
        if ((float)$$1 < (float)$$0 * 0.3f) {
            return -1.0f;
        }
        float $$2 = (float)$$0 / 2.0f;
        float $$3 = $$2 - (float)$$1;
        float $$4 = Mth.sqrt($$2 * $$2 - $$3 * $$3);
        if ($$3 == 0.0f) {
            $$4 = $$2;
        } else if (Math.abs((float)$$3) >= $$2) {
            return 0.0f;
        }
        return $$4 * 0.5f;
    }

    static class FoliageCoords {
        final FoliagePlacer.FoliageAttachment attachment;
        private final int branchBase;

        public FoliageCoords(BlockPos $$0, int $$1) {
            this.attachment = new FoliagePlacer.FoliageAttachment($$0, 0, false);
            this.branchBase = $$1;
        }

        public int getBranchBase() {
            return this.branchBase;
        }
    }
}