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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock
extends FallingBlock {
    private final BlockState concrete;

    public ConcretePowderBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.concrete = $$0.defaultBlockState();
    }

    @Override
    public void onLand(Level $$0, BlockPos $$1, BlockState $$2, BlockState $$3, FallingBlockEntity $$4) {
        if (ConcretePowderBlock.shouldSolidify($$0, $$1, $$3)) {
            $$0.setBlock($$1, this.concrete, 3);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$3;
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        if (ConcretePowderBlock.shouldSolidify($$1, $$2 = $$0.getClickedPos(), $$3 = $$1.getBlockState($$2))) {
            return this.concrete;
        }
        return super.getStateForPlacement($$0);
    }

    private static boolean shouldSolidify(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return ConcretePowderBlock.canSolidify($$2) || ConcretePowderBlock.touchesLiquid($$0, $$1);
    }

    private static boolean touchesLiquid(BlockGetter $$0, BlockPos $$1) {
        boolean $$2 = false;
        BlockPos.MutableBlockPos $$3 = $$1.mutable();
        for (Direction $$4 : Direction.values()) {
            BlockState $$5 = $$0.getBlockState($$3);
            if ($$4 == Direction.DOWN && !ConcretePowderBlock.canSolidify($$5)) continue;
            $$3.setWithOffset((Vec3i)$$1, $$4);
            $$5 = $$0.getBlockState($$3);
            if (!ConcretePowderBlock.canSolidify($$5) || $$5.isFaceSturdy($$0, $$1, $$4.getOpposite())) continue;
            $$2 = true;
            break;
        }
        return $$2;
    }

    private static boolean canSolidify(BlockState $$0) {
        return $$0.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (ConcretePowderBlock.touchesLiquid($$3, $$4)) {
            return this.concrete;
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public int getDustColor(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getMapColor((BlockGetter)$$1, (BlockPos)$$2).col;
    }
}