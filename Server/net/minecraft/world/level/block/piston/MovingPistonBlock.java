/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.Level
 */
package net.minecraft.world.level.block.piston;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovingPistonBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = PistonHeadBlock.FACING;
    public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

    public MovingPistonBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return null;
    }

    public static BlockEntity newMovingBlockEntity(BlockPos $$0, BlockState $$1, BlockState $$2, Direction $$3, boolean $$4, boolean $$5) {
        return new PistonMovingBlockEntity($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return MovingPistonBlock.createTickerHelper($$2, BlockEntityType.PISTON, PistonMovingBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)$$5).finalTick();
        }
    }

    @Override
    public void destroy(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        Vec3i $$3 = $$1.relative($$2.getValue(FACING).getOpposite());
        BlockState $$4 = $$0.getBlockState((BlockPos)$$3);
        if ($$4.getBlock() instanceof PistonBaseBlock && $$4.getValue(PistonBaseBlock.EXTENDED).booleanValue()) {
            $$0.removeBlock((BlockPos)$$3, false);
        }
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if (!$$1.isClientSide && $$1.getBlockEntity($$2) == null) {
            $$1.removeBlock($$2, false);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState $$0, LootContext.Builder $$1) {
        PistonMovingBlockEntity $$2 = this.getBlockEntity($$1.getLevel(), new BlockPos($$1.getParameter(LootContextParams.ORIGIN)));
        if ($$2 == null) {
            return Collections.emptyList();
        }
        return $$2.getMovedState().getDrops($$1);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        PistonMovingBlockEntity $$4 = this.getBlockEntity($$1, $$2);
        if ($$4 != null) {
            return $$4.getCollisionShape($$1, $$2);
        }
        return Shapes.empty();
    }

    @Nullable
    private PistonMovingBlockEntity getBlockEntity(BlockGetter $$0, BlockPos $$1) {
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if ($$2 instanceof PistonMovingBlockEntity) {
            return (PistonMovingBlockEntity)$$2;
        }
        return null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return ItemStack.EMPTY;
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
        $$0.add(FACING, TYPE);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}