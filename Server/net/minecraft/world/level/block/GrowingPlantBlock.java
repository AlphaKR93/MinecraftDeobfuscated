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
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantBlock
extends Block {
    protected final Direction growthDirection;
    protected final boolean scheduleFluidTicks;
    protected final VoxelShape shape;

    protected GrowingPlantBlock(BlockBehaviour.Properties $$0, Direction $$1, VoxelShape $$2, boolean $$3) {
        super($$0);
        this.growthDirection = $$1;
        this.shape = $$2;
        this.scheduleFluidTicks = $$3;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState((BlockPos)$$0.getClickedPos().relative(this.growthDirection));
        if ($$1.is(this.getHeadBlock()) || $$1.is(this.getBodyBlock())) {
            return this.getBodyBlock().defaultBlockState();
        }
        return this.getStateForPlacement($$0.getLevel());
    }

    public BlockState getStateForPlacement(LevelAccessor $$0) {
        return this.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.relative(this.growthDirection.getOpposite());
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        if (!this.canAttachTo($$4)) {
            return false;
        }
        return $$4.is(this.getHeadBlock()) || $$4.is(this.getBodyBlock()) || $$4.isFaceSturdy($$1, (BlockPos)$$3, this.growthDirection);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    protected boolean canAttachTo(BlockState $$0) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shape;
    }

    protected abstract GrowingPlantHeadBlock getHeadBlock();

    protected abstract Block getBodyBlock();
}