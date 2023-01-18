/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronBarsBlock
extends CrossCollisionBlock {
    protected IronBarsBlock(BlockBehaviour.Properties $$0) {
        super(1.0f, 1.0f, 16.0f, 16.0f, 16.0f, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        FluidState $$3 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Vec3i $$4 = $$2.north();
        Vec3i $$5 = $$2.south();
        Vec3i $$6 = $$2.west();
        Vec3i $$7 = $$2.east();
        BlockState $$8 = $$1.getBlockState((BlockPos)$$4);
        BlockState $$9 = $$1.getBlockState((BlockPos)$$5);
        BlockState $$10 = $$1.getBlockState((BlockPos)$$6);
        BlockState $$11 = $$1.getBlockState((BlockPos)$$7);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, this.attachsTo($$8, $$8.isFaceSturdy($$1, (BlockPos)$$4, Direction.SOUTH)))).setValue(SOUTH, this.attachsTo($$9, $$9.isFaceSturdy($$1, (BlockPos)$$5, Direction.NORTH)))).setValue(WEST, this.attachsTo($$10, $$10.isFaceSturdy($$1, (BlockPos)$$6, Direction.EAST)))).setValue(EAST, this.attachsTo($$11, $$11.isFaceSturdy($$1, (BlockPos)$$7, Direction.WEST)))).setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$1.getAxis().isHorizontal()) {
            return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), this.attachsTo($$2, $$2.isFaceSturdy($$3, $$5, $$1.getOpposite())));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public VoxelShape getVisualShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.empty();
    }

    @Override
    public boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        if ($$1.is(this)) {
            if (!$$2.getAxis().isHorizontal()) {
                return true;
            }
            if (((Boolean)$$0.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$2))).booleanValue() && ((Boolean)$$1.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$2.getOpposite()))).booleanValue()) {
                return true;
            }
        }
        return super.skipRendering($$0, $$1, $$2);
    }

    public final boolean attachsTo(BlockState $$0, boolean $$1) {
        return !IronBarsBlock.isExceptionForConnection($$0) && $$1 || $$0.getBlock() instanceof IronBarsBlock || $$0.is(BlockTags.WALLS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}