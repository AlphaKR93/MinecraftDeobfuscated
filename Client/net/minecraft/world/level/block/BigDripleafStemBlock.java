/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigDripleafStemBlock
extends HorizontalDirectionalBlock
implements BonemealableBlock,
SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final int STEM_WIDTH = 6;
    protected static final VoxelShape NORTH_SHAPE = Block.box(5.0, 0.0, 9.0, 11.0, 16.0, 15.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(5.0, 0.0, 1.0, 11.0, 16.0, 7.0);
    protected static final VoxelShape EAST_SHAPE = Block.box(1.0, 0.0, 5.0, 7.0, 16.0, 11.0);
    protected static final VoxelShape WEST_SHAPE = Block.box(9.0, 0.0, 5.0, 15.0, 16.0, 11.0);

    protected BigDripleafStemBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(FACING)) {
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            default: {
                return NORTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
            case EAST: 
        }
        return EAST_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(WATERLOGGED, FACING);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.below();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        BlockState $$5 = $$1.getBlockState((BlockPos)$$2.above());
        return !(!$$4.is(this) && !$$4.is(BlockTags.BIG_DRIPLEAF_PLACEABLE) || !$$5.is(this) && !$$5.is(Blocks.BIG_DRIPLEAF));
    }

    protected static boolean place(LevelAccessor $$0, BlockPos $$1, FluidState $$2, Direction $$3) {
        BlockState $$4 = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF_STEM.defaultBlockState().setValue(WATERLOGGED, $$2.isSourceOfType(Fluids.WATER))).setValue(FACING, $$3);
        return $$0.setBlock($$1, $$4, 3);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!($$1 != Direction.DOWN && $$1 != Direction.UP || $$0.canSurvive($$3, $$4))) {
            $$3.scheduleTick($$4, this, 1);
        }
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        Optional<BlockPos> $$4 = BlockUtil.getTopConnectedBlock($$0, $$1, $$2.getBlock(), Direction.UP, Blocks.BIG_DRIPLEAF);
        if (!$$4.isPresent()) {
            return false;
        }
        Vec3i $$5 = ((BlockPos)$$4.get()).above();
        BlockState $$6 = $$0.getBlockState((BlockPos)$$5);
        return BigDripleafBlock.canPlaceAt($$0, (BlockPos)$$5, $$6);
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        Optional<BlockPos> $$4 = BlockUtil.getTopConnectedBlock($$0, $$2, $$3.getBlock(), Direction.UP, Blocks.BIG_DRIPLEAF);
        if (!$$4.isPresent()) {
            return;
        }
        BlockPos $$5 = (BlockPos)$$4.get();
        Vec3i $$6 = $$5.above();
        Direction $$7 = $$3.getValue(FACING);
        BigDripleafStemBlock.place($$0, $$5, $$0.getFluidState($$5), $$7);
        BigDripleafBlock.place($$0, (BlockPos)$$6, $$0.getFluidState((BlockPos)$$6), $$7);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return new ItemStack(Blocks.BIG_DRIPLEAF);
    }
}