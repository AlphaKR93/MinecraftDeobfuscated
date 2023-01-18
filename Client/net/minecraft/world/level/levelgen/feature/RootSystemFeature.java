/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;

public class RootSystemFeature
extends Feature<RootSystemConfiguration> {
    public RootSystemFeature(Codec<RootSystemConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<RootSystemConfiguration> $$0) {
        BlockPos $$2;
        WorldGenLevel $$1 = $$0.level();
        if (!$$1.getBlockState($$2 = $$0.origin()).isAir()) {
            return false;
        }
        RandomSource $$3 = $$0.random();
        BlockPos $$4 = $$0.origin();
        RootSystemConfiguration $$5 = $$0.config();
        BlockPos.MutableBlockPos $$6 = $$4.mutable();
        if (RootSystemFeature.placeDirtAndTree($$1, $$0.chunkGenerator(), $$5, $$3, $$6, $$4)) {
            RootSystemFeature.placeRoots($$1, $$5, $$3, $$4, $$6);
        }
        return true;
    }

    private static boolean spaceForTree(WorldGenLevel $$0, RootSystemConfiguration $$1, BlockPos $$2) {
        BlockPos.MutableBlockPos $$3 = $$2.mutable();
        for (int $$4 = 1; $$4 <= $$1.requiredVerticalSpaceForTree; ++$$4) {
            $$3.move(Direction.UP);
            BlockState $$5 = $$0.getBlockState($$3);
            if (RootSystemFeature.isAllowedTreeSpace($$5, $$4, $$1.allowedVerticalWaterForTree)) continue;
            return false;
        }
        return true;
    }

    private static boolean isAllowedTreeSpace(BlockState $$0, int $$1, int $$2) {
        if ($$0.isAir()) {
            return true;
        }
        int $$3 = $$1 + 1;
        return $$3 <= $$2 && $$0.getFluidState().is(FluidTags.WATER);
    }

    private static boolean placeDirtAndTree(WorldGenLevel $$0, ChunkGenerator $$1, RootSystemConfiguration $$2, RandomSource $$3, BlockPos.MutableBlockPos $$4, BlockPos $$5) {
        for (int $$6 = 0; $$6 < $$2.rootColumnMaxHeight; ++$$6) {
            $$4.move(Direction.UP);
            if (!$$2.allowedTreePosition.test($$0, $$4) || !RootSystemFeature.spaceForTree($$0, $$2, $$4)) continue;
            Vec3i $$7 = $$4.below();
            if ($$0.getFluidState((BlockPos)$$7).is(FluidTags.LAVA) || !$$0.getBlockState((BlockPos)$$7).getMaterial().isSolid()) {
                return false;
            }
            if (!$$2.treeFeature.value().place($$0, $$1, $$3, $$4)) continue;
            RootSystemFeature.placeDirt($$5, $$5.getY() + $$6, $$0, $$2, $$3);
            return true;
        }
        return false;
    }

    private static void placeDirt(BlockPos $$0, int $$1, WorldGenLevel $$2, RootSystemConfiguration $$3, RandomSource $$4) {
        int $$5 = $$0.getX();
        int $$6 = $$0.getZ();
        BlockPos.MutableBlockPos $$7 = $$0.mutable();
        for (int $$8 = $$0.getY(); $$8 < $$1; ++$$8) {
            RootSystemFeature.placeRootedDirt($$2, $$3, $$4, $$5, $$6, $$7.set($$5, $$8, $$6));
        }
    }

    private static void placeRootedDirt(WorldGenLevel $$0, RootSystemConfiguration $$12, RandomSource $$2, int $$3, int $$4, BlockPos.MutableBlockPos $$5) {
        int $$6 = $$12.rootRadius;
        Predicate $$7 = $$1 -> $$1.is($$0.rootReplaceable);
        for (int $$8 = 0; $$8 < $$12.rootPlacementAttempts; ++$$8) {
            $$5.setWithOffset($$5, $$2.nextInt($$6) - $$2.nextInt($$6), 0, $$2.nextInt($$6) - $$2.nextInt($$6));
            if ($$7.test((Object)$$0.getBlockState($$5))) {
                $$0.setBlock($$5, $$12.rootStateProvider.getState($$2, $$5), 2);
            }
            $$5.setX($$3);
            $$5.setZ($$4);
        }
    }

    private static void placeRoots(WorldGenLevel $$0, RootSystemConfiguration $$1, RandomSource $$2, BlockPos $$3, BlockPos.MutableBlockPos $$4) {
        int $$5 = $$1.hangingRootRadius;
        int $$6 = $$1.hangingRootsVerticalSpan;
        for (int $$7 = 0; $$7 < $$1.hangingRootPlacementAttempts; ++$$7) {
            BlockState $$8;
            $$4.setWithOffset($$3, $$2.nextInt($$5) - $$2.nextInt($$5), $$2.nextInt($$6) - $$2.nextInt($$6), $$2.nextInt($$5) - $$2.nextInt($$5));
            if (!$$0.isEmptyBlock($$4) || !($$8 = $$1.hangingRootStateProvider.getState($$2, $$4)).canSurvive($$0, $$4) || !$$0.getBlockState((BlockPos)$$4.above()).isFaceSturdy($$0, $$4, Direction.DOWN)) continue;
            $$0.setBlock($$4, $$8, 2);
        }
    }
}