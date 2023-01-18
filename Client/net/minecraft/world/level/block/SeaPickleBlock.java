/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeaPickleBlock
extends BushBlock
implements BonemealableBlock,
SimpleWaterloggedBlock {
    public static final int MAX_PICKLES = 4;
    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape ONE_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
    protected static final VoxelShape TWO_AABB = Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
    protected static final VoxelShape THREE_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    protected static final VoxelShape FOUR_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

    protected SeaPickleBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PICKLES, 1)).setValue(WATERLOGGED, true));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        if ($$1.is(this)) {
            return (BlockState)$$1.setValue(PICKLES, Math.min((int)4, (int)($$1.getValue(PICKLES) + 1)));
        }
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$3 = $$2.getType() == Fluids.WATER;
        return (BlockState)super.getStateForPlacement($$0).setValue(WATERLOGGED, $$3);
    }

    public static boolean isDead(BlockState $$0) {
        return $$0.getValue(WATERLOGGED) == false;
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return !$$0.getCollisionShape($$1, $$2).getFaceShape(Direction.UP).isEmpty() || $$0.isFaceSturdy($$1, $$2, Direction.UP);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.below();
        return this.mayPlaceOn($$1.getBlockState((BlockPos)$$3), $$1, (BlockPos)$$3);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        if (!$$1.isSecondaryUseActive() && $$1.getItemInHand().is(this.asItem()) && $$0.getValue(PICKLES) < 4) {
            return true;
        }
        return super.canBeReplaced($$0, $$1);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(PICKLES)) {
            default: {
                return ONE_AABB;
            }
            case 2: {
                return TWO_AABB;
            }
            case 3: {
                return THREE_AABB;
            }
            case 4: 
        }
        return FOUR_AABB;
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(PICKLES, WATERLOGGED);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        if (!SeaPickleBlock.isDead($$3) && $$0.getBlockState((BlockPos)$$2.below()).is(BlockTags.CORAL_BLOCKS)) {
            int $$4 = 5;
            int $$5 = 1;
            int $$6 = 2;
            int $$7 = 0;
            int $$8 = $$2.getX() - 2;
            int $$9 = 0;
            for (int $$10 = 0; $$10 < 5; ++$$10) {
                for (int $$11 = 0; $$11 < $$5; ++$$11) {
                    int $$12 = 2 + $$2.getY() - 1;
                    for (int $$13 = $$12 - 2; $$13 < $$12; ++$$13) {
                        BlockState $$15;
                        BlockPos $$14 = new BlockPos($$8 + $$10, $$13, $$2.getZ() - $$9 + $$11);
                        if ($$14 == $$2 || $$1.nextInt(6) != 0 || !$$0.getBlockState($$14).is(Blocks.WATER) || !($$15 = $$0.getBlockState((BlockPos)$$14.below())).is(BlockTags.CORAL_BLOCKS)) continue;
                        $$0.setBlock($$14, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(PICKLES, $$1.nextInt(4) + 1), 3);
                    }
                }
                if ($$7 < 2) {
                    $$5 += 2;
                    ++$$9;
                } else {
                    $$5 -= 2;
                    --$$9;
                }
                ++$$7;
            }
            $$0.setBlock($$2, (BlockState)$$3.setValue(PICKLES, 4), 2);
        }
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}