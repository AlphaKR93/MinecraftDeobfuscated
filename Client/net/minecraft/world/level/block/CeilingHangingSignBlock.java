/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingHangingSignBlock
extends SignBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    protected static final float AABB_OFFSET = 5.0f;
    protected static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final Map<Integer, VoxelShape> AABBS = Maps.newHashMap((Map)ImmutableMap.of((Object)0, (Object)Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), (Object)4, (Object)Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0), (Object)8, (Object)Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), (Object)12, (Object)Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0)));

    public CeilingHangingSignBlock(BlockBehaviour.Properties $$0, WoodType $$1) {
        super($$0, $$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(ATTACHED, false)).setValue(WATERLOGGED, false));
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
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return $$1.getBlockState((BlockPos)$$2.above()).isFaceSturdy($$1, (BlockPos)$$2.above(), Direction.DOWN, SupportType.CENTER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        boolean $$7;
        Level $$1 = $$0.getLevel();
        FluidState $$2 = $$1.getFluidState($$0.getClickedPos());
        Vec3i $$3 = $$0.getClickedPos().above();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        boolean $$5 = $$4.is(BlockTags.ALL_HANGING_SIGNS);
        Direction $$6 = Direction.fromYRot($$0.getRotation());
        boolean bl = $$7 = !Block.isFaceFull($$4.getCollisionShape($$1, (BlockPos)$$3), Direction.DOWN) || $$0.isSecondaryUseActive();
        if ($$5 && !$$0.isSecondaryUseActive()) {
            Optional<Direction> $$9;
            if ($$4.hasProperty(WallHangingSignBlock.FACING)) {
                Direction $$8 = $$4.getValue(WallHangingSignBlock.FACING);
                if ($$8.getAxis().test($$6)) {
                    $$7 = false;
                }
            } else if ($$4.hasProperty(ROTATION) && ($$9 = RotationSegment.convertToDirection($$4.getValue(ROTATION))).isPresent() && ((Direction)$$9.get()).getAxis().test($$6)) {
                $$7 = false;
            }
        }
        int $$10 = !$$7 ? RotationSegment.convertToSegment($$6.getOpposite()) : RotationSegment.convertToSegment($$0.getRotation() + 180.0f);
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, $$7)).setValue(ROTATION, $$10)).setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        VoxelShape $$4 = (VoxelShape)AABBS.get((Object)$$0.getValue(ROTATION));
        return $$4 == null ? SHAPE : $$4;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.getShape($$0, $$1, $$2, CollisionContext.empty());
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.UP && !this.canSurvive($$0, $$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.rotate($$0.getValue(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.mirror($$0.getValue(ROTATION), 16));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(ROTATION, ATTACHED, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new HangingSignBlockEntity($$0, $$1);
    }
}