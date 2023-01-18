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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusBlock
extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    public static final int MAX_AGE = 15;
    protected static final int AABB_OFFSET = 1;
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    protected CactusBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        Vec3i $$4 = $$2.above();
        if (!$$1.isEmptyBlock((BlockPos)$$4)) {
            return;
        }
        int $$5 = 1;
        while ($$1.getBlockState((BlockPos)$$2.below($$5)).is(this)) {
            ++$$5;
        }
        if ($$5 >= 3) {
            return;
        }
        int $$6 = $$0.getValue(AGE);
        if ($$6 == 15) {
            $$1.setBlockAndUpdate((BlockPos)$$4, this.defaultBlockState());
            BlockState $$7 = (BlockState)$$0.setValue(AGE, 0);
            $$1.setBlock($$2, $$7, 4);
            $$1.neighborChanged($$7, (BlockPos)$$4, this, $$2, false);
        } else {
            $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, $$6 + 1), 4);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return OUTLINE_SHAPE;
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
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            BlockState $$4 = $$1.getBlockState((BlockPos)$$2.relative($$3));
            Material $$5 = $$4.getMaterial();
            if (!$$5.isSolid() && !$$1.getFluidState((BlockPos)$$2.relative($$3)).is(FluidTags.LAVA)) continue;
            return false;
        }
        BlockState $$6 = $$1.getBlockState((BlockPos)$$2.below());
        return ($$6.is(Blocks.CACTUS) || $$6.is(Blocks.SAND) || $$6.is(Blocks.RED_SAND)) && !$$1.getBlockState((BlockPos)$$2.above()).getMaterial().isLiquid();
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        $$3.hurt(DamageSource.CACTUS, 1.0f);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}