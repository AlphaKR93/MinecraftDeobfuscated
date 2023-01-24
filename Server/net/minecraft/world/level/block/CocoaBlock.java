/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CocoaBlock
extends HorizontalDirectionalBlock
implements BonemealableBlock {
    public static final int MAX_AGE = 2;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    protected static final int AGE_0_WIDTH = 4;
    protected static final int AGE_0_HEIGHT = 5;
    protected static final int AGE_0_HALFWIDTH = 2;
    protected static final int AGE_1_WIDTH = 6;
    protected static final int AGE_1_HEIGHT = 7;
    protected static final int AGE_1_HALFWIDTH = 3;
    protected static final int AGE_2_WIDTH = 8;
    protected static final int AGE_2_HEIGHT = 9;
    protected static final int AGE_2_HALFWIDTH = 4;
    protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{Block.box(11.0, 7.0, 6.0, 15.0, 12.0, 10.0), Block.box(9.0, 5.0, 5.0, 15.0, 12.0, 11.0), Block.box(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)};
    protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{Block.box(1.0, 7.0, 6.0, 5.0, 12.0, 10.0), Block.box(1.0, 5.0, 5.0, 7.0, 12.0, 11.0), Block.box(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)};
    protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{Block.box(6.0, 7.0, 1.0, 10.0, 12.0, 5.0), Block.box(5.0, 5.0, 1.0, 11.0, 12.0, 7.0), Block.box(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)};
    protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{Block.box(6.0, 7.0, 11.0, 10.0, 12.0, 15.0), Block.box(5.0, 5.0, 9.0, 11.0, 12.0, 15.0), Block.box(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)};

    public CocoaBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AGE, 0));
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(AGE) < 2;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4;
        if ($$1.random.nextInt(5) == 0 && ($$4 = $$0.getValue(AGE).intValue()) < 2) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, $$4 + 1), 2);
        }
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.relative($$0.getValue(FACING)));
        return $$3.is(BlockTags.JUNGLE_LOGS);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        int $$4 = $$0.getValue(AGE);
        switch ($$0.getValue(FACING)) {
            case SOUTH: {
                return SOUTH_AABB[$$4];
            }
            default: {
                return NORTH_AABB[$$4];
            }
            case WEST: {
                return WEST_AABB[$$4];
            }
            case EAST: 
        }
        return EAST_AABB[$$4];
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = this.defaultBlockState();
        Level $$2 = $$0.getLevel();
        BlockPos $$3 = $$0.getClickedPos();
        for (Direction $$4 : $$0.getNearestLookingDirections()) {
            if (!$$4.getAxis().isHorizontal() || !($$1 = (BlockState)$$1.setValue(FACING, $$4)).canSurvive($$2, $$3)) continue;
            return $$1;
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == $$0.getValue(FACING) && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return $$2.getValue(AGE) < 2;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        $$0.setBlock($$2, (BlockState)$$3.setValue(AGE, $$3.getValue(AGE) + 1), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, AGE);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}