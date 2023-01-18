/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BasePressurePlateBlock
extends Block {
    protected static final VoxelShape PRESSED_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
    protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    protected static final AABB TOUCH_AABB = new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.25, 0.9375);

    protected BasePressurePlateBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.getSignalForState($$0) > 0 ? PRESSED_AABB : AABB;
    }

    protected int getPressedTime() {
        return 20;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.DOWN && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.below();
        return BasePressurePlateBlock.canSupportRigidBlock($$1, (BlockPos)$$3) || BasePressurePlateBlock.canSupportCenter($$1, (BlockPos)$$3, Direction.UP);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = this.getSignalForState($$0);
        if ($$4 > 0) {
            this.checkPressed(null, $$1, $$2, $$0, $$4);
        }
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$1.isClientSide) {
            return;
        }
        int $$4 = this.getSignalForState($$0);
        if ($$4 == 0) {
            this.checkPressed($$3, $$1, $$2, $$0, $$4);
        }
    }

    protected void checkPressed(@Nullable Entity $$0, Level $$1, BlockPos $$2, BlockState $$3, int $$4) {
        boolean $$7;
        int $$5 = this.getSignalStrength($$1, $$2);
        boolean $$6 = $$4 > 0;
        boolean bl = $$7 = $$5 > 0;
        if ($$4 != $$5) {
            BlockState $$8 = this.setSignalForState($$3, $$5);
            $$1.setBlock($$2, $$8, 2);
            this.updateNeighbours($$1, $$2);
            $$1.setBlocksDirty($$2, $$3, $$8);
        }
        if (!$$7 && $$6) {
            this.playOffSound($$1, $$2);
            $$1.gameEvent($$0, GameEvent.BLOCK_DEACTIVATE, $$2);
        } else if ($$7 && !$$6) {
            this.playOnSound($$1, $$2);
            $$1.gameEvent($$0, GameEvent.BLOCK_ACTIVATE, $$2);
        }
        if ($$7) {
            $$1.scheduleTick(new BlockPos($$2), this, this.getPressedTime());
        }
    }

    protected abstract void playOnSound(LevelAccessor var1, BlockPos var2);

    protected abstract void playOffSound(LevelAccessor var1, BlockPos var2);

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4 || $$0.is($$3.getBlock())) {
            return;
        }
        if (this.getSignalForState($$0) > 0) {
            this.updateNeighbours($$1, $$2);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    protected void updateNeighbours(Level $$0, BlockPos $$1) {
        $$0.updateNeighborsAt($$1, this);
        $$0.updateNeighborsAt((BlockPos)$$1.below(), this);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return this.getSignalForState($$0);
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$3 == Direction.UP) {
            return this.getSignalForState($$0);
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    protected abstract int getSignalStrength(Level var1, BlockPos var2);

    protected abstract int getSignalForState(BlockState var1);

    protected abstract BlockState setSignalForState(BlockState var1, int var2);
}