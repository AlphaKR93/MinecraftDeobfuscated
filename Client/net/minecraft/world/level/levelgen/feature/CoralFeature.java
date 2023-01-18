/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public abstract class CoralFeature
extends Feature<NoneFeatureConfiguration> {
    public CoralFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        RandomSource $$12 = $$0.random();
        WorldGenLevel $$2 = $$0.level();
        BlockPos $$3 = $$0.origin();
        Optional $$4 = BuiltInRegistries.BLOCK.getTag(BlockTags.CORAL_BLOCKS).flatMap($$1 -> $$1.getRandomElement($$12)).map(Holder::value);
        if ($$4.isEmpty()) {
            return false;
        }
        return this.placeFeature($$2, $$12, $$3, ((Block)$$4.get()).defaultBlockState());
    }

    protected abstract boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4);

    protected boolean placeCoralBlock(LevelAccessor $$0, RandomSource $$12, BlockPos $$2, BlockState $$3) {
        Vec3i $$4 = $$2.above();
        BlockState $$5 = $$0.getBlockState($$2);
        if (!$$5.is(Blocks.WATER) && !$$5.is(BlockTags.CORALS) || !$$0.getBlockState((BlockPos)$$4).is(Blocks.WATER)) {
            return false;
        }
        $$0.setBlock($$2, $$3, 3);
        if ($$12.nextFloat() < 0.25f) {
            BuiltInRegistries.BLOCK.getTag(BlockTags.CORALS).flatMap($$1 -> $$1.getRandomElement($$12)).map(Holder::value).ifPresent(arg_0 -> CoralFeature.lambda$placeCoralBlock$2($$0, (BlockPos)$$4, arg_0));
        } else if ($$12.nextFloat() < 0.05f) {
            $$0.setBlock((BlockPos)$$4, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, $$12.nextInt(4) + 1), 2);
        }
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            Vec3i $$7;
            if (!($$12.nextFloat() < 0.2f) || !$$0.getBlockState((BlockPos)($$7 = $$2.relative($$6))).is(Blocks.WATER)) continue;
            BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap($$1 -> $$1.getRandomElement($$12)).map(Holder::value).ifPresent(arg_0 -> CoralFeature.lambda$placeCoralBlock$4($$6, $$0, (BlockPos)$$7, arg_0));
        }
        return true;
    }

    private static /* synthetic */ void lambda$placeCoralBlock$4(Direction $$0, LevelAccessor $$1, BlockPos $$2, Block $$3) {
        BlockState $$4 = $$3.defaultBlockState();
        if ($$4.hasProperty(BaseCoralWallFanBlock.FACING)) {
            $$4 = (BlockState)$$4.setValue(BaseCoralWallFanBlock.FACING, $$0);
        }
        $$1.setBlock($$2, $$4, 2);
    }

    private static /* synthetic */ void lambda$placeCoralBlock$2(LevelAccessor $$0, BlockPos $$1, Block $$2) {
        $$0.setBlock($$1, $$2.defaultBlockState(), 2);
    }
}