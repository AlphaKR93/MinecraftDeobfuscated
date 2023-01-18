/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
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

public class ButtonBlock
extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int PRESSED_DEPTH = 1;
    private static final int UNPRESSED_DEPTH = 2;
    protected static final int HALF_AABB_HEIGHT = 2;
    protected static final int HALF_AABB_WIDTH = 3;
    protected static final VoxelShape CEILING_AABB_X = Block.box(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
    protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
    protected static final VoxelShape NORTH_AABB = Block.box(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
    protected static final VoxelShape WEST_AABB = Block.box(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
    protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.box(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.box(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
    protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
    protected static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
    protected static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    private final SoundEvent soundOff;
    private final SoundEvent soundOn;
    private final int ticksToStayPressed;
    private final boolean arrowsCanPress;

    protected ButtonBlock(BlockBehaviour.Properties $$0, int $$1, boolean $$2, SoundEvent $$3, SoundEvent $$4) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
        this.ticksToStayPressed = $$1;
        this.arrowsCanPress = $$2;
        this.soundOff = $$3;
        this.soundOn = $$4;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Direction $$4 = $$0.getValue(FACING);
        boolean $$5 = $$0.getValue(POWERED);
        switch ((AttachFace)$$0.getValue(FACE)) {
            case FLOOR: {
                if ($$4.getAxis() == Direction.Axis.X) {
                    return $$5 ? PRESSED_FLOOR_AABB_X : FLOOR_AABB_X;
                }
                return $$5 ? PRESSED_FLOOR_AABB_Z : FLOOR_AABB_Z;
            }
            case WALL: {
                return switch ($$4) {
                    default -> throw new IncompatibleClassChangeError();
                    case Direction.EAST -> {
                        if ($$5) {
                            yield PRESSED_EAST_AABB;
                        }
                        yield EAST_AABB;
                    }
                    case Direction.WEST -> {
                        if ($$5) {
                            yield PRESSED_WEST_AABB;
                        }
                        yield WEST_AABB;
                    }
                    case Direction.SOUTH -> {
                        if ($$5) {
                            yield PRESSED_SOUTH_AABB;
                        }
                        yield SOUTH_AABB;
                    }
                    case Direction.NORTH, Direction.UP, Direction.DOWN -> $$5 ? PRESSED_NORTH_AABB : NORTH_AABB;
                };
            }
        }
        if ($$4.getAxis() == Direction.Axis.X) {
            return $$5 ? PRESSED_CEILING_AABB_X : CEILING_AABB_X;
        }
        return $$5 ? PRESSED_CEILING_AABB_Z : CEILING_AABB_Z;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$0.getValue(POWERED).booleanValue()) {
            return InteractionResult.CONSUME;
        }
        this.press($$0, $$1, $$2);
        this.playSound($$3, $$1, $$2, true);
        $$1.gameEvent($$3, GameEvent.BLOCK_ACTIVATE, $$2);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    public void press(BlockState $$0, Level $$1, BlockPos $$2) {
        $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, true), 3);
        this.updateNeighbours($$0, $$1, $$2);
        $$1.scheduleTick($$2, this, this.ticksToStayPressed);
    }

    protected void playSound(@Nullable Player $$0, LevelAccessor $$1, BlockPos $$2, boolean $$3) {
        $$1.playSound($$3 ? $$0 : null, $$2, this.getSound($$3), SoundSource.BLOCKS);
    }

    protected SoundEvent getSound(boolean $$0) {
        return $$0 ? this.soundOn : this.soundOff;
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
        if ($$0.getValue(POWERED).booleanValue() && ButtonBlock.getConnectedDirection($$0) == $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$0, $$1, $$2);
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$1.isClientSide || !this.arrowsCanPress || $$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$0, $$1, $$2);
    }

    protected void checkPressed(BlockState $$0, Level $$1, BlockPos $$2) {
        boolean $$5;
        AbstractArrow $$3 = this.arrowsCanPress ? (AbstractArrow)$$1.getEntitiesOfClass(AbstractArrow.class, $$0.getShape($$1, $$2).bounds().move($$2)).stream().findFirst().orElse(null) : null;
        boolean $$4 = $$3 != null;
        if ($$4 != ($$5 = $$0.getValue(POWERED).booleanValue())) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$4), 3);
            this.updateNeighbours($$0, $$1, $$2);
            this.playSound(null, $$1, $$2, $$4);
            $$1.gameEvent($$3, $$4 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, $$2);
        }
        if ($$4) {
            $$1.scheduleTick(new BlockPos($$2), this, this.ticksToStayPressed);
        }
    }

    private void updateNeighbours(BlockState $$0, Level $$1, BlockPos $$2) {
        $$1.updateNeighborsAt($$2, this);
        $$1.updateNeighborsAt((BlockPos)$$2.relative(ButtonBlock.getConnectedDirection($$0).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, POWERED, FACE);
    }
}