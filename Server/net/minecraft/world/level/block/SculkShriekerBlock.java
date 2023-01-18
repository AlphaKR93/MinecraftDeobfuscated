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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkShriekerBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final BooleanProperty SHRIEKING = BlockStateProperties.SHRIEKING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty CAN_SUMMON = BlockStateProperties.CAN_SUMMON;
    protected static final VoxelShape COLLIDER = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final double TOP_Y = COLLIDER.max(Direction.Axis.Y);

    public SculkShriekerBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHRIEKING, false)).setValue(WATERLOGGED, false)).setValue(CAN_SUMMON, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(SHRIEKING);
        $$0.add(WATERLOGGED);
        $$0.add(CAN_SUMMON);
    }

    @Override
    public void stepOn(Level $$0, BlockPos $$1, BlockState $$22, Entity $$3) {
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$4 = (ServerLevel)$$0;
            ServerPlayer $$5 = SculkShriekerBlockEntity.tryGetPlayer($$3);
            if ($$5 != null) {
                $$4.getBlockEntity($$1, BlockEntityType.SCULK_SHRIEKER).ifPresent($$2 -> $$2.tryShriek($$4, $$5));
            }
        }
        super.stepOn($$0, $$1, $$22, $$3);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$12, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$12 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$12;
            if ($$0.getValue(SHRIEKING).booleanValue() && !$$0.is($$3.getBlock())) {
                $$5.getBlockEntity($$2, BlockEntityType.SCULK_SHRIEKER).ifPresent($$1 -> $$1.tryRespond($$5));
            }
        }
        super.onRemove($$0, $$12, $$2, $$3, $$4);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$12, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(SHRIEKING).booleanValue()) {
            $$12.setBlock($$2, (BlockState)$$0.setValue(SHRIEKING, false), 3);
            $$12.getBlockEntity($$2, BlockEntityType.SCULK_SHRIEKER).ifPresent($$1 -> $$1.tryRespond($$12));
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return COLLIDER;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return COLLIDER;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new SculkShriekerBlockEntity($$0, $$1);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$0.getLevel().getFluidState($$0.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$4) {
            this.tryDropExperience($$1, $$2, $$3, ConstantInt.of(5));
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel $$0, T $$1) {
        if ($$1 instanceof SculkShriekerBlockEntity) {
            SculkShriekerBlockEntity $$2 = (SculkShriekerBlockEntity)$$1;
            return $$2.getListener();
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$02, BlockState $$12, BlockEntityType<T> $$22) {
        if (!$$02.isClientSide) {
            return BaseEntityBlock.createTickerHelper($$22, BlockEntityType.SCULK_SHRIEKER, ($$0, $$1, $$2, $$3) -> $$3.getListener().tick($$0));
        }
        return null;
    }
}