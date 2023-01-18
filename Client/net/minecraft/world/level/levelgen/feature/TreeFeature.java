/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  java.lang.Boolean
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.HashSet
 *  java.util.List
 *  java.util.OptionalInt
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class TreeFeature
extends Feature<TreeConfiguration> {
    private static final int BLOCK_UPDATE_FLAGS = 19;

    public TreeFeature(Codec<TreeConfiguration> $$0) {
        super($$0);
    }

    private static boolean isVine(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, (Predicate<BlockState>)((Predicate)$$0 -> $$0.is(Blocks.VINE)));
    }

    public static boolean isBlockWater(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, (Predicate<BlockState>)((Predicate)$$0 -> $$0.is(Blocks.WATER)));
    }

    public static boolean isAirOrLeaves(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, (Predicate<BlockState>)((Predicate)$$0 -> $$0.isAir() || $$0.is(BlockTags.LEAVES)));
    }

    private static boolean isReplaceablePlant(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, (Predicate<BlockState>)((Predicate)$$0 -> {
            Material $$1 = $$0.getMaterial();
            return $$1 == Material.REPLACEABLE_PLANT || $$1 == Material.REPLACEABLE_WATER_PLANT || $$1 == Material.REPLACEABLE_FIREPROOF_PLANT;
        }));
    }

    private static void setBlockKnownShape(LevelWriter $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, $$2, 19);
    }

    public static boolean validTreePos(LevelSimulatedReader $$0, BlockPos $$1) {
        return TreeFeature.isAirOrLeaves($$0, $$1) || TreeFeature.isReplaceablePlant($$0, $$1) || TreeFeature.isBlockWater($$0, $$1);
    }

    private boolean doPlace(WorldGenLevel $$0, RandomSource $$1, BlockPos $$22, BiConsumer<BlockPos, BlockState> $$3, BiConsumer<BlockPos, BlockState> $$4, BiConsumer<BlockPos, BlockState> $$5, TreeConfiguration $$6) {
        int $$72 = $$6.trunkPlacer.getTreeHeight($$1);
        int $$8 = $$6.foliagePlacer.foliageHeight($$1, $$72, $$6);
        int $$9 = $$72 - $$8;
        int $$10 = $$6.foliagePlacer.foliageRadius($$1, $$9);
        BlockPos $$11 = (BlockPos)$$6.rootPlacer.map($$2 -> $$2.getTrunkOrigin($$22, $$1)).orElse((Object)$$22);
        int $$12 = Math.min((int)$$22.getY(), (int)$$11.getY());
        int $$13 = Math.max((int)$$22.getY(), (int)$$11.getY()) + $$72 + 1;
        if ($$12 < $$0.getMinBuildHeight() + 1 || $$13 > $$0.getMaxBuildHeight()) {
            return false;
        }
        OptionalInt $$14 = $$6.minimumSize.minClippedHeight();
        int $$15 = this.getMaxFreeTreeHeight($$0, $$72, $$11, $$6);
        if ($$15 < $$72 && ($$14.isEmpty() || $$15 < $$14.getAsInt())) {
            return false;
        }
        if ($$6.rootPlacer.isPresent() && !((RootPlacer)$$6.rootPlacer.get()).placeRoots($$0, $$3, $$1, $$22, $$11, $$6)) {
            return false;
        }
        List<FoliagePlacer.FoliageAttachment> $$16 = $$6.trunkPlacer.placeTrunk($$0, $$4, $$1, $$15, $$11, $$6);
        $$16.forEach($$7 -> $$0.foliagePlacer.createFoliage($$0, $$5, $$1, $$6, $$15, (FoliagePlacer.FoliageAttachment)$$7, $$8, $$10));
        return true;
    }

    private int getMaxFreeTreeHeight(LevelSimulatedReader $$0, int $$1, BlockPos $$2, TreeConfiguration $$3) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (int $$5 = 0; $$5 <= $$1 + 1; ++$$5) {
            int $$6 = $$3.minimumSize.getSizeAtHeight($$1, $$5);
            for (int $$7 = -$$6; $$7 <= $$6; ++$$7) {
                for (int $$8 = -$$6; $$8 <= $$6; ++$$8) {
                    $$4.setWithOffset($$2, $$7, $$5, $$8);
                    if ($$3.trunkPlacer.isFree($$0, $$4) && ($$3.ignoreVines || !TreeFeature.isVine($$0, $$4))) continue;
                    return $$5 - 2;
                }
            }
        }
        return $$1;
    }

    @Override
    protected void setBlock(LevelWriter $$0, BlockPos $$1, BlockState $$2) {
        TreeFeature.setBlockKnownShape($$0, $$1, $$2);
    }

    @Override
    public final boolean place(FeaturePlaceContext<TreeConfiguration> $$0) {
        WorldGenLevel $$12 = $$0.level();
        RandomSource $$2 = $$0.random();
        BlockPos $$3 = $$0.origin();
        TreeConfiguration $$4 = $$0.config();
        HashSet $$5 = Sets.newHashSet();
        HashSet $$6 = Sets.newHashSet();
        HashSet $$7 = Sets.newHashSet();
        HashSet $$8 = Sets.newHashSet();
        BiConsumer $$9 = (arg_0, arg_1) -> TreeFeature.lambda$place$6((Set)$$5, $$12, arg_0, arg_1);
        BiConsumer $$10 = (arg_0, arg_1) -> TreeFeature.lambda$place$7((Set)$$6, $$12, arg_0, arg_1);
        BiConsumer $$11 = (arg_0, arg_1) -> TreeFeature.lambda$place$8((Set)$$7, $$12, arg_0, arg_1);
        BiConsumer $$122 = (arg_0, arg_1) -> TreeFeature.lambda$place$9((Set)$$8, $$12, arg_0, arg_1);
        boolean $$13 = this.doPlace($$12, $$2, $$3, (BiConsumer<BlockPos, BlockState>)$$9, (BiConsumer<BlockPos, BlockState>)$$10, (BiConsumer<BlockPos, BlockState>)$$11, $$4);
        if (!$$13 || $$6.isEmpty() && $$7.isEmpty()) {
            return false;
        }
        if (!$$4.decorators.isEmpty()) {
            TreeDecorator.Context $$14 = new TreeDecorator.Context($$12, (BiConsumer<BlockPos, BlockState>)$$122, $$2, (Set<BlockPos>)$$6, (Set<BlockPos>)$$7, (Set<BlockPos>)$$5);
            $$4.decorators.forEach($$1 -> $$1.place($$14));
        }
        return (Boolean)BoundingBox.encapsulatingPositions((Iterable<BlockPos>)Iterables.concat((Iterable)$$5, (Iterable)$$6, (Iterable)$$7, (Iterable)$$8)).map(arg_0 -> TreeFeature.lambda$place$11($$12, (Set)$$6, (Set)$$8, (Set)$$5, arg_0)).orElse((Object)false);
    }

    private static DiscreteVoxelShape updateLeaves(LevelAccessor $$0, BoundingBox $$1, Set<BlockPos> $$2, Set<BlockPos> $$3, Set<BlockPos> $$4) {
        ArrayList $$5 = Lists.newArrayList();
        BitSetDiscreteVoxelShape $$6 = new BitSetDiscreteVoxelShape($$1.getXSpan(), $$1.getYSpan(), $$1.getZSpan());
        int $$7 = 6;
        for (int $$8 = 0; $$8 < 6; ++$$8) {
            $$5.add((Object)Sets.newHashSet());
        }
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (BlockPos $$10 : Lists.newArrayList((Iterable)Sets.union($$3, $$4))) {
            if (!$$1.isInside($$10)) continue;
            ((DiscreteVoxelShape)$$6).fill($$10.getX() - $$1.minX(), $$10.getY() - $$1.minY(), $$10.getZ() - $$1.minZ());
        }
        for (BlockPos $$11 : Lists.newArrayList($$2)) {
            if ($$1.isInside($$11)) {
                ((DiscreteVoxelShape)$$6).fill($$11.getX() - $$1.minX(), $$11.getY() - $$1.minY(), $$11.getZ() - $$1.minZ());
            }
            for (Direction $$12 : Direction.values()) {
                BlockState $$13;
                $$9.setWithOffset((Vec3i)$$11, $$12);
                if ($$2.contains((Object)$$9) || !($$13 = $$0.getBlockState($$9)).hasProperty(BlockStateProperties.DISTANCE)) continue;
                ((Set)$$5.get(0)).add((Object)$$9.immutable());
                TreeFeature.setBlockKnownShape($$0, $$9, (BlockState)$$13.setValue(BlockStateProperties.DISTANCE, 1));
                if (!$$1.isInside($$9)) continue;
                ((DiscreteVoxelShape)$$6).fill($$9.getX() - $$1.minX(), $$9.getY() - $$1.minY(), $$9.getZ() - $$1.minZ());
            }
        }
        for (int $$14 = 1; $$14 < 6; ++$$14) {
            Set $$15 = (Set)$$5.get($$14 - 1);
            Set $$16 = (Set)$$5.get($$14);
            for (BlockPos $$17 : $$15) {
                if ($$1.isInside($$17)) {
                    ((DiscreteVoxelShape)$$6).fill($$17.getX() - $$1.minX(), $$17.getY() - $$1.minY(), $$17.getZ() - $$1.minZ());
                }
                for (Direction $$18 : Direction.values()) {
                    int $$20;
                    BlockState $$19;
                    $$9.setWithOffset((Vec3i)$$17, $$18);
                    if ($$15.contains((Object)$$9) || $$16.contains((Object)$$9) || !($$19 = $$0.getBlockState($$9)).hasProperty(BlockStateProperties.DISTANCE) || ($$20 = $$19.getValue(BlockStateProperties.DISTANCE).intValue()) <= $$14 + 1) continue;
                    BlockState $$21 = (BlockState)$$19.setValue(BlockStateProperties.DISTANCE, $$14 + 1);
                    TreeFeature.setBlockKnownShape($$0, $$9, $$21);
                    if ($$1.isInside($$9)) {
                        ((DiscreteVoxelShape)$$6).fill($$9.getX() - $$1.minX(), $$9.getY() - $$1.minY(), $$9.getZ() - $$1.minZ());
                    }
                    $$16.add((Object)$$9.immutable());
                }
            }
        }
        return $$6;
    }

    private static /* synthetic */ Boolean lambda$place$11(WorldGenLevel $$0, Set $$1, Set $$2, Set $$3, BoundingBox $$4) {
        DiscreteVoxelShape $$5 = TreeFeature.updateLeaves($$0, $$4, (Set<BlockPos>)$$1, (Set<BlockPos>)$$2, (Set<BlockPos>)$$3);
        StructureTemplate.updateShapeAtEdge($$0, 3, $$5, $$4.minX(), $$4.minY(), $$4.minZ());
        return true;
    }

    private static /* synthetic */ void lambda$place$9(Set $$0, WorldGenLevel $$1, BlockPos $$2, BlockState $$3) {
        $$0.add((Object)$$2.immutable());
        $$1.setBlock($$2, $$3, 19);
    }

    private static /* synthetic */ void lambda$place$8(Set $$0, WorldGenLevel $$1, BlockPos $$2, BlockState $$3) {
        $$0.add((Object)$$2.immutable());
        $$1.setBlock($$2, $$3, 19);
    }

    private static /* synthetic */ void lambda$place$7(Set $$0, WorldGenLevel $$1, BlockPos $$2, BlockState $$3) {
        $$0.add((Object)$$2.immutable());
        $$1.setBlock($$2, $$3, 19);
    }

    private static /* synthetic */ void lambda$place$6(Set $$0, WorldGenLevel $$1, BlockPos $$2, BlockState $$3) {
        $$0.add((Object)$$2.immutable());
        $$1.setBlock($$2, $$3, 19);
    }
}