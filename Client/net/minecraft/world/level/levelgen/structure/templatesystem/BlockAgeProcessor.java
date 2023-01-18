/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlockAgeProcessor
extends StructureProcessor {
    public static final Codec<BlockAgeProcessor> CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(BlockAgeProcessor::new, $$0 -> Float.valueOf((float)$$0.mossiness)).codec();
    private static final float PROBABILITY_OF_REPLACING_FULL_BLOCK = 0.5f;
    private static final float PROBABILITY_OF_REPLACING_STAIRS = 0.5f;
    private static final float PROBABILITY_OF_REPLACING_OBSIDIAN = 0.15f;
    private static final BlockState[] NON_MOSSY_REPLACEMENTS = new BlockState[]{Blocks.STONE_SLAB.defaultBlockState(), Blocks.STONE_BRICK_SLAB.defaultBlockState()};
    private final float mossiness;

    public BlockAgeProcessor(float $$0) {
        this.mossiness = $$0;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader $$0, BlockPos $$1, BlockPos $$2, StructureTemplate.StructureBlockInfo $$3, StructureTemplate.StructureBlockInfo $$4, StructurePlaceSettings $$5) {
        RandomSource $$6 = $$5.getRandom($$4.pos);
        BlockState $$7 = $$4.state;
        BlockPos $$8 = $$4.pos;
        BlockState $$9 = null;
        if ($$7.is(Blocks.STONE_BRICKS) || $$7.is(Blocks.STONE) || $$7.is(Blocks.CHISELED_STONE_BRICKS)) {
            $$9 = this.maybeReplaceFullStoneBlock($$6);
        } else if ($$7.is(BlockTags.STAIRS)) {
            $$9 = this.maybeReplaceStairs($$6, $$4.state);
        } else if ($$7.is(BlockTags.SLABS)) {
            $$9 = this.maybeReplaceSlab($$6);
        } else if ($$7.is(BlockTags.WALLS)) {
            $$9 = this.maybeReplaceWall($$6);
        } else if ($$7.is(Blocks.OBSIDIAN)) {
            $$9 = this.maybeReplaceObsidian($$6);
        }
        if ($$9 != null) {
            return new StructureTemplate.StructureBlockInfo($$8, $$9, $$4.nbt);
        }
        return $$4;
    }

    @Nullable
    private BlockState maybeReplaceFullStoneBlock(RandomSource $$0) {
        if ($$0.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] $$1 = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), BlockAgeProcessor.getRandomFacingStairs($$0, Blocks.STONE_BRICK_STAIRS)};
        BlockState[] $$2 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), BlockAgeProcessor.getRandomFacingStairs($$0, Blocks.MOSSY_STONE_BRICK_STAIRS)};
        return this.getRandomBlock($$0, $$1, $$2);
    }

    @Nullable
    private BlockState maybeReplaceStairs(RandomSource $$0, BlockState $$1) {
        Direction $$2 = $$1.getValue(StairBlock.FACING);
        Half $$3 = $$1.getValue(StairBlock.HALF);
        if ($$0.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] $$4 = new BlockState[]{(BlockState)((BlockState)Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, $$2)).setValue(StairBlock.HALF, $$3), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState()};
        return this.getRandomBlock($$0, NON_MOSSY_REPLACEMENTS, $$4);
    }

    @Nullable
    private BlockState maybeReplaceSlab(RandomSource $$0) {
        if ($$0.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState();
        }
        return null;
    }

    @Nullable
    private BlockState maybeReplaceWall(RandomSource $$0) {
        if ($$0.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState();
        }
        return null;
    }

    @Nullable
    private BlockState maybeReplaceObsidian(RandomSource $$0) {
        if ($$0.nextFloat() < 0.15f) {
            return Blocks.CRYING_OBSIDIAN.defaultBlockState();
        }
        return null;
    }

    private static BlockState getRandomFacingStairs(RandomSource $$0, Block $$1) {
        return (BlockState)((BlockState)$$1.defaultBlockState().setValue(StairBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection($$0))).setValue(StairBlock.HALF, Util.getRandom(Half.values(), $$0));
    }

    private BlockState getRandomBlock(RandomSource $$0, BlockState[] $$1, BlockState[] $$2) {
        if ($$0.nextFloat() < this.mossiness) {
            return BlockAgeProcessor.getRandomBlock($$0, $$2);
        }
        return BlockAgeProcessor.getRandomBlock($$0, $$1);
    }

    private static BlockState getRandomBlock(RandomSource $$0, BlockState[] $$1) {
        return $$1[$$0.nextInt($$1.length)];
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_AGE;
    }
}