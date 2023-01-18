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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralBlock
extends Block {
    private final Block deadBlock;

    public CoralBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.deadBlock = $$0;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!this.scanForWater($$1, $$2)) {
            $$1.setBlock($$2, this.deadBlock.defaultBlockState(), 2);
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!this.scanForWater($$3, $$4)) {
            $$3.scheduleTick($$4, this, 60 + $$3.getRandom().nextInt(40));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    protected boolean scanForWater(BlockGetter $$0, BlockPos $$1) {
        for (Direction $$2 : Direction.values()) {
            FluidState $$3 = $$0.getFluidState((BlockPos)$$1.relative($$2));
            if (!$$3.is(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        if (!this.scanForWater($$0.getLevel(), $$0.getClickedPos())) {
            $$0.getLevel().scheduleTick($$0.getClickedPos(), this, 60 + $$0.getLevel().getRandom().nextInt(40));
        }
        return this.defaultBlockState();
    }
}