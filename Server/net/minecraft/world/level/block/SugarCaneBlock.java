/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SugarCaneBlock
extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    protected static final float AABB_OFFSET = 6.0f;
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    protected SugarCaneBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.isEmptyBlock((BlockPos)$$2.above())) {
            int $$4 = 1;
            while ($$1.getBlockState((BlockPos)$$2.below($$4)).is(this)) {
                ++$$4;
            }
            if ($$4 < 3) {
                int $$5 = $$0.getValue(AGE);
                if ($$5 == 15) {
                    $$1.setBlockAndUpdate((BlockPos)$$2.above(), this.defaultBlockState());
                    $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, 0), 4);
                } else {
                    $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, $$5 + 1), 4);
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!$$0.canSurvive($$3, $$4)) {
            $$3.scheduleTick($$4, this, 1);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.below());
        if ($$3.is(this)) {
            return true;
        }
        if ($$3.is(BlockTags.DIRT) || $$3.is(Blocks.SAND) || $$3.is(Blocks.RED_SAND)) {
            Vec3i $$4 = $$2.below();
            for (Direction $$5 : Direction.Plane.HORIZONTAL) {
                BlockState $$6 = $$1.getBlockState((BlockPos)((BlockPos)$$4).relative($$5));
                FluidState $$7 = $$1.getFluidState((BlockPos)((BlockPos)$$4).relative($$5));
                if (!$$7.is(FluidTags.WATER) && !$$6.is(Blocks.FROSTED_ICE)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }
}