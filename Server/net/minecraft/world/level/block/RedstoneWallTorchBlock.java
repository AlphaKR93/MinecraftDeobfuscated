/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock
extends RedstoneTorchBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    protected RedstoneWallTorchBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true));
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return WallTorchBlock.getShape($$0);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return Blocks.WALL_TORCH.canSurvive($$0, $$1, $$2);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        return Blocks.WALL_TORCH.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = Blocks.WALL_TORCH.getStateForPlacement($$0);
        return $$1 == null ? null : (BlockState)this.defaultBlockState().setValue(FACING, $$1.getValue(FACING));
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(LIT).booleanValue()) {
            return;
        }
        Direction $$4 = $$0.getValue(FACING).getOpposite();
        double $$5 = 0.27;
        double $$6 = (double)$$2.getX() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2 + 0.27 * (double)$$4.getStepX();
        double $$7 = (double)$$2.getY() + 0.7 + ($$3.nextDouble() - 0.5) * 0.2 + 0.22;
        double $$8 = (double)$$2.getZ() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2 + 0.27 * (double)$$4.getStepZ();
        $$1.addParticle(this.flameParticle, $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Override
    protected boolean hasNeighborSignal(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING).getOpposite();
        return $$0.hasSignal((BlockPos)$$1.relative($$3), $$3);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(LIT).booleanValue() && $$0.getValue(FACING) != $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return Blocks.WALL_TORCH.rotate($$0, $$1);
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return Blocks.WALL_TORCH.mirror($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, LIT);
    }
}