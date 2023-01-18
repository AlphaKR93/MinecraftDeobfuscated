/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.util.List
 *  java.util.function.BiPredicate
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.AllOfPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.AnyOfPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.HasSturdyFacePredicate;
import net.minecraft.world.level.levelgen.blockpredicates.InsideWorldBoundsPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlockTagPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingFluidsPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.NotPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.ReplaceablePredicate;
import net.minecraft.world.level.levelgen.blockpredicates.SolidPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.TrueBlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.WouldSurvivePredicate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public interface BlockPredicate
extends BiPredicate<WorldGenLevel, BlockPos> {
    public static final Codec<BlockPredicate> CODEC = BuiltInRegistries.BLOCK_PREDICATE_TYPE.byNameCodec().dispatch(BlockPredicate::type, BlockPredicateType::codec);
    public static final BlockPredicate ONLY_IN_AIR_PREDICATE = BlockPredicate.matchesBlocks(Blocks.AIR);
    public static final BlockPredicate ONLY_IN_AIR_OR_WATER_PREDICATE = BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER);

    public BlockPredicateType<?> type();

    public static BlockPredicate allOf(List<BlockPredicate> $$0) {
        return new AllOfPredicate($$0);
    }

    public static BlockPredicate allOf(BlockPredicate ... $$0) {
        return BlockPredicate.allOf((List<BlockPredicate>)List.of((Object[])$$0));
    }

    public static BlockPredicate allOf(BlockPredicate $$0, BlockPredicate $$1) {
        return BlockPredicate.allOf((List<BlockPredicate>)List.of((Object)$$0, (Object)$$1));
    }

    public static BlockPredicate anyOf(List<BlockPredicate> $$0) {
        return new AnyOfPredicate($$0);
    }

    public static BlockPredicate anyOf(BlockPredicate ... $$0) {
        return BlockPredicate.anyOf((List<BlockPredicate>)List.of((Object[])$$0));
    }

    public static BlockPredicate anyOf(BlockPredicate $$0, BlockPredicate $$1) {
        return BlockPredicate.anyOf((List<BlockPredicate>)List.of((Object)$$0, (Object)$$1));
    }

    public static BlockPredicate matchesBlocks(Vec3i $$0, List<Block> $$1) {
        return new MatchingBlocksPredicate($$0, HolderSet.direct(Block::builtInRegistryHolder, $$1));
    }

    public static BlockPredicate matchesBlocks(List<Block> $$0) {
        return BlockPredicate.matchesBlocks(Vec3i.ZERO, $$0);
    }

    public static BlockPredicate matchesBlocks(Vec3i $$0, Block ... $$1) {
        return BlockPredicate.matchesBlocks($$0, (List<Block>)List.of((Object[])$$1));
    }

    public static BlockPredicate matchesBlocks(Block ... $$0) {
        return BlockPredicate.matchesBlocks(Vec3i.ZERO, $$0);
    }

    public static BlockPredicate matchesTag(Vec3i $$0, TagKey<Block> $$1) {
        return new MatchingBlockTagPredicate($$0, $$1);
    }

    public static BlockPredicate matchesTag(TagKey<Block> $$0) {
        return BlockPredicate.matchesTag(Vec3i.ZERO, $$0);
    }

    public static BlockPredicate matchesFluids(Vec3i $$0, List<Fluid> $$1) {
        return new MatchingFluidsPredicate($$0, HolderSet.direct(Fluid::builtInRegistryHolder, $$1));
    }

    public static BlockPredicate matchesFluids(Vec3i $$0, Fluid ... $$1) {
        return BlockPredicate.matchesFluids($$0, (List<Fluid>)List.of((Object[])$$1));
    }

    public static BlockPredicate matchesFluids(Fluid ... $$0) {
        return BlockPredicate.matchesFluids(Vec3i.ZERO, $$0);
    }

    public static BlockPredicate not(BlockPredicate $$0) {
        return new NotPredicate($$0);
    }

    public static BlockPredicate replaceable(Vec3i $$0) {
        return new ReplaceablePredicate($$0);
    }

    public static BlockPredicate replaceable() {
        return BlockPredicate.replaceable(Vec3i.ZERO);
    }

    public static BlockPredicate wouldSurvive(BlockState $$0, Vec3i $$1) {
        return new WouldSurvivePredicate($$1, $$0);
    }

    public static BlockPredicate hasSturdyFace(Vec3i $$0, Direction $$1) {
        return new HasSturdyFacePredicate($$0, $$1);
    }

    public static BlockPredicate hasSturdyFace(Direction $$0) {
        return BlockPredicate.hasSturdyFace(Vec3i.ZERO, $$0);
    }

    public static BlockPredicate solid(Vec3i $$0) {
        return new SolidPredicate($$0);
    }

    public static BlockPredicate solid() {
        return BlockPredicate.solid(Vec3i.ZERO);
    }

    public static BlockPredicate noFluid() {
        return BlockPredicate.noFluid(Vec3i.ZERO);
    }

    public static BlockPredicate noFluid(Vec3i $$0) {
        return BlockPredicate.matchesFluids($$0, Fluids.EMPTY);
    }

    public static BlockPredicate insideWorld(Vec3i $$0) {
        return new InsideWorldBoundsPredicate($$0);
    }

    public static BlockPredicate alwaysTrue() {
        return TrueBlockPredicate.INSTANCE;
    }
}