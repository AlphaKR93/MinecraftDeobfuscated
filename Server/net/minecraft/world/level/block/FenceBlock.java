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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceBlock
extends CrossCollisionBlock {
    private final VoxelShape[] occlusionByIndex;

    public FenceBlock(BlockBehaviour.Properties $$0) {
        super(2.0f, 2.0f, 16.0f, 16.0f, 24.0f, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(WATERLOGGED, false));
        this.occlusionByIndex = this.makeShapes(2.0f, 1.0f, 16.0f, 6.0f, 15.0f);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.occlusionByIndex[this.getAABBIndex($$0)];
    }

    @Override
    public VoxelShape getVisualShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.getShape($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    public boolean connectsTo(BlockState $$0, boolean $$1, Direction $$2) {
        Block $$3 = $$0.getBlock();
        boolean $$4 = this.isSameFence($$0);
        boolean $$5 = $$3 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection($$0, $$2);
        return !FenceBlock.isExceptionForConnection($$0) && $$1 || $$4 || $$5;
    }

    private boolean isSameFence(BlockState $$0) {
        return $$0.is(BlockTags.FENCES) && $$0.is(BlockTags.WOODEN_FENCES) == this.defaultBlockState().is(BlockTags.WOODEN_FENCES);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            ItemStack $$6 = $$3.getItemInHand($$4);
            if ($$6.is(Items.LEAD)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return LeadItem.bindPlayerMobs($$3, $$1, $$2);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        FluidState $$3 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Vec3i $$4 = $$2.north();
        Vec3i $$5 = $$2.east();
        Vec3i $$6 = $$2.south();
        Vec3i $$7 = $$2.west();
        BlockState $$8 = $$1.getBlockState((BlockPos)$$4);
        BlockState $$9 = $$1.getBlockState((BlockPos)$$5);
        BlockState $$10 = $$1.getBlockState((BlockPos)$$6);
        BlockState $$11 = $$1.getBlockState((BlockPos)$$7);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getStateForPlacement($$0).setValue(NORTH, this.connectsTo($$8, $$8.isFaceSturdy($$1, (BlockPos)$$4, Direction.SOUTH), Direction.SOUTH))).setValue(EAST, this.connectsTo($$9, $$9.isFaceSturdy($$1, (BlockPos)$$5, Direction.WEST), Direction.WEST))).setValue(SOUTH, this.connectsTo($$10, $$10.isFaceSturdy($$1, (BlockPos)$$6, Direction.NORTH), Direction.NORTH))).setValue(WEST, this.connectsTo($$11, $$11.isFaceSturdy($$1, (BlockPos)$$7, Direction.EAST), Direction.EAST))).setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$1.getAxis().getPlane() == Direction.Plane.HORIZONTAL) {
            return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), this.connectsTo($$2, $$2.isFaceSturdy($$3, $$5, $$1.getOpposite()), $$1.getOpposite()));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}