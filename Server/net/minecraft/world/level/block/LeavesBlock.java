/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final int DECAY_DISTANCE = 7;
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final int TICK_DELAY = 1;

    public LeavesBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(PERSISTENT, false)).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(DISTANCE) == 7 && $$0.getValue(PERSISTENT) == false;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (this.decaying($$0)) {
            LeavesBlock.dropResources($$0, $$1, $$2);
            $$1.removeBlock($$2, false);
        }
    }

    protected boolean decaying(BlockState $$0) {
        return $$0.getValue(PERSISTENT) == false && $$0.getValue(DISTANCE) == 7;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        $$1.setBlock($$2, LeavesBlock.updateDistance($$0, $$1, $$2), 3);
    }

    @Override
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return 1;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        int $$6;
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if (($$6 = LeavesBlock.getDistanceAt($$2) + 1) != 1 || $$0.getValue(DISTANCE) != $$6) {
            $$3.scheduleTick($$4, this, 1);
        }
        return $$0;
    }

    private static BlockState updateDistance(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        int $$3 = 7;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Direction $$5 : Direction.values()) {
            $$4.setWithOffset((Vec3i)$$2, $$5);
            $$3 = Math.min((int)$$3, (int)(LeavesBlock.getDistanceAt($$1.getBlockState($$4)) + 1));
            if ($$3 == 1) break;
        }
        return (BlockState)$$0.setValue(DISTANCE, $$3);
    }

    private static int getDistanceAt(BlockState $$0) {
        if ($$0.is(BlockTags.LOGS)) {
            return 0;
        }
        if ($$0.getBlock() instanceof LeavesBlock) {
            return $$0.getValue(DISTANCE);
        }
        return 7;
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.isRainingAt((BlockPos)$$2.above())) {
            return;
        }
        if ($$3.nextInt(15) != 1) {
            return;
        }
        Vec3i $$4 = $$2.below();
        BlockState $$5 = $$1.getBlockState((BlockPos)$$4);
        if ($$5.canOcclude() && $$5.isFaceSturdy($$1, (BlockPos)$$4, Direction.UP)) {
            return;
        }
        double $$6 = (double)$$2.getX() + $$3.nextDouble();
        double $$7 = (double)$$2.getY() - 0.05;
        double $$8 = (double)$$2.getZ() + $$3.nextDouble();
        $$1.addParticle(ParticleTypes.DRIPPING_WATER, $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(DISTANCE, PERSISTENT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        BlockState $$2 = (BlockState)((BlockState)this.defaultBlockState().setValue(PERSISTENT, true)).setValue(WATERLOGGED, $$1.getType() == Fluids.WATER);
        return LeavesBlock.updateDistance($$2, $$0.getLevel(), $$0.getClickedPos());
    }
}