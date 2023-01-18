/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallHangingSignBlock
extends SignBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape PLANK_NORTHSOUTH = Block.box(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
    public static final VoxelShape PLANK_EASTWEST = Block.box(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_NORTHSOUTH = Shapes.or(PLANK_NORTHSOUTH, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0));
    public static final VoxelShape SHAPE_EASTWEST = Shapes.or(PLANK_EASTWEST, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0));
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)SHAPE_NORTHSOUTH, (Object)Direction.SOUTH, (Object)SHAPE_NORTHSOUTH, (Object)Direction.EAST, (Object)SHAPE_EASTWEST, (Object)Direction.WEST, (Object)SHAPE_EASTWEST));

    public WallHangingSignBlock(BlockBehaviour.Properties $$0, WoodType $$1) {
        super($$0, $$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity $$6 = (SignBlockEntity)blockEntity;
            ItemStack $$7 = $$3.getItemInHand($$4);
            if (!$$6.hasAnyClickCommands($$3) && $$7.getItem() instanceof BlockItem) {
                return InteractionResult.PASS;
            }
        }
        return super.use($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)AABBS.get((Object)$$0.getValue(FACING));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.getShape($$0, $$1, $$2, CollisionContext.empty());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(FACING)) {
            case EAST: 
            case WEST: {
                return PLANK_EASTWEST;
            }
        }
        return PLANK_NORTHSOUTH;
    }

    public boolean canPlace(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction $$3 = $$0.getValue(FACING).getClockWise();
        Direction $$4 = $$0.getValue(FACING).getCounterClockWise();
        return this.canAttachTo($$1, $$0, (BlockPos)$$2.relative($$3), $$4) || this.canAttachTo($$1, $$0, (BlockPos)$$2.relative($$4), $$3);
    }

    public boolean canAttachTo(LevelReader $$0, BlockState $$1, BlockPos $$2, Direction $$3) {
        BlockState $$4 = $$0.getBlockState($$2);
        if ($$4.is(BlockTags.WALL_HANGING_SIGNS)) {
            return $$4.getValue(FACING).getAxis().test($$1.getValue(FACING));
        }
        return $$4.isFaceSturdy($$0, $$2, $$3, SupportType.FULL);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = this.defaultBlockState();
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Level $$3 = $$0.getLevel();
        BlockPos $$4 = $$0.getClickedPos();
        for (Direction $$5 : $$0.getNearestLookingDirections()) {
            Direction $$6;
            if (!$$5.getAxis().isHorizontal() || $$5.getAxis().test($$0.getClickedFace()) || !($$1 = (BlockState)$$1.setValue(FACING, $$6 = $$5.getOpposite())).canSurvive($$3, $$4) || !this.canPlace($$1, $$3, $$4)) continue;
            return (BlockState)$$1.setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1.getAxis() == $$0.getValue(FACING).getClockWise().getAxis() && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new HangingSignBlockEntity($$0, $$1);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}