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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeverBlock
extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final int DEPTH = 6;
    protected static final int WIDTH = 6;
    protected static final int HEIGHT = 8;
    protected static final VoxelShape NORTH_AABB = Block.box(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_AABB = Block.box(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape UP_AABB_Z = Block.box(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape UP_AABB_X = Block.box(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape DOWN_AABB_Z = Block.box(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape DOWN_AABB_X = Block.box(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    protected LeverBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ((AttachFace)$$0.getValue(FACE)) {
            case FLOOR: {
                switch ($$0.getValue(FACING).getAxis()) {
                    case X: {
                        return UP_AABB_X;
                    }
                }
                return UP_AABB_Z;
            }
            case WALL: {
                switch ($$0.getValue(FACING)) {
                    case EAST: {
                        return EAST_AABB;
                    }
                    case WEST: {
                        return WEST_AABB;
                    }
                    case SOUTH: {
                        return SOUTH_AABB;
                    }
                }
                return NORTH_AABB;
            }
        }
        switch ($$0.getValue(FACING).getAxis()) {
            case X: {
                return DOWN_AABB_X;
            }
        }
        return DOWN_AABB_Z;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            BlockState $$6 = (BlockState)$$0.cycle(POWERED);
            if ($$6.getValue(POWERED).booleanValue()) {
                LeverBlock.makeParticle($$6, $$1, $$2, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        BlockState $$7 = this.pull($$0, $$1, $$2);
        float $$8 = $$7.getValue(POWERED) != false ? 0.6f : 0.5f;
        $$1.playSound(null, $$2, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, $$8);
        $$1.gameEvent($$3, $$7.getValue(POWERED) != false ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, $$2);
        return InteractionResult.CONSUME;
    }

    public BlockState pull(BlockState $$0, Level $$1, BlockPos $$2) {
        $$0 = (BlockState)$$0.cycle(POWERED);
        $$1.setBlock($$2, $$0, 3);
        this.updateNeighbours($$0, $$1, $$2);
        return $$0;
    }

    private static void makeParticle(BlockState $$0, LevelAccessor $$1, BlockPos $$2, float $$3) {
        Direction $$4 = $$0.getValue(FACING).getOpposite();
        Direction $$5 = LeverBlock.getConnectedDirection($$0).getOpposite();
        double $$6 = (double)$$2.getX() + 0.5 + 0.1 * (double)$$4.getStepX() + 0.2 * (double)$$5.getStepX();
        double $$7 = (double)$$2.getY() + 0.5 + 0.1 * (double)$$4.getStepY() + 0.2 * (double)$$5.getStepY();
        double $$8 = (double)$$2.getZ() + 0.5 + 0.1 * (double)$$4.getStepZ() + 0.2 * (double)$$5.getStepZ();
        $$1.addParticle(new DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, $$3), $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(POWERED).booleanValue() && $$3.nextFloat() < 0.25f) {
            LeverBlock.makeParticle($$0, $$1, $$2, 0.5f);
        }
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4 || $$0.is($$3.getBlock())) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            this.updateNeighbours($$0, $$1, $$2);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(POWERED).booleanValue() && LeverBlock.getConnectedDirection($$0) == $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    private void updateNeighbours(BlockState $$0, Level $$1, BlockPos $$2) {
        $$1.updateNeighborsAt($$2, this);
        $$1.updateNeighborsAt((BlockPos)$$2.relative(LeverBlock.getConnectedDirection($$0).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACE, FACING, POWERED);
    }
}