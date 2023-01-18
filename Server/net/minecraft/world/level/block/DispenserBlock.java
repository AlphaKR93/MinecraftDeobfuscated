/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class DispenserBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY = (Map)Util.make(new Object2ObjectOpenHashMap(), $$0 -> $$0.defaultReturnValue((Object)new DefaultDispenseItemBehavior()));
    private static final int TRIGGER_DURATION = 4;

    public static void registerBehavior(ItemLike $$0, DispenseItemBehavior $$1) {
        DISPENSER_REGISTRY.put((Object)$$0.asItem(), (Object)$$1);
    }

    protected DispenserBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TRIGGERED, false));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if ($$6 instanceof DispenserBlockEntity) {
            $$3.openMenu((DispenserBlockEntity)$$6);
            if ($$6 instanceof DropperBlockEntity) {
                $$3.awardStat(Stats.INSPECT_DROPPER);
            } else {
                $$3.awardStat(Stats.INSPECT_DISPENSER);
            }
        }
        return InteractionResult.CONSUME;
    }

    protected void dispenseFrom(ServerLevel $$0, BlockPos $$1) {
        BlockSourceImpl $$2 = new BlockSourceImpl($$0, $$1);
        DispenserBlockEntity $$3 = (DispenserBlockEntity)$$2.getEntity();
        int $$4 = $$3.getRandomSlot($$0.random);
        if ($$4 < 0) {
            $$0.levelEvent(1001, $$1, 0);
            $$0.gameEvent(null, GameEvent.DISPENSE_FAIL, $$1);
            return;
        }
        ItemStack $$5 = $$3.getItem($$4);
        DispenseItemBehavior $$6 = this.getDispenseMethod($$5);
        if ($$6 != DispenseItemBehavior.NOOP) {
            $$3.setItem($$4, $$6.dispense($$2, $$5));
        }
    }

    protected DispenseItemBehavior getDispenseMethod(ItemStack $$0) {
        return (DispenseItemBehavior)DISPENSER_REGISTRY.get((Object)$$0.getItem());
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        boolean $$6 = $$1.hasNeighborSignal($$2) || $$1.hasNeighborSignal((BlockPos)$$2.above());
        boolean $$7 = $$0.getValue(TRIGGERED);
        if ($$6 && !$$7) {
            $$1.scheduleTick($$2, this, 4);
            $$1.setBlock($$2, (BlockState)$$0.setValue(TRIGGERED, true), 4);
        } else if (!$$6 && $$7) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(TRIGGERED, false), 4);
        }
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        this.dispenseFrom($$1, $$2);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new DispenserBlockEntity($$0, $$1);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        BlockEntity $$5;
        if ($$4.hasCustomHoverName() && ($$5 = $$0.getBlockEntity($$1)) instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)$$5).setCustomName($$4.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof DispenserBlockEntity) {
            Containers.dropContents($$1, $$2, (Container)((DispenserBlockEntity)$$5));
            $$1.updateNeighbourForOutputSignal($$2, this);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    public static Position getDispensePosition(BlockSource $$0) {
        Direction $$1 = $$0.getBlockState().getValue(FACING);
        double $$2 = $$0.x() + 0.7 * (double)$$1.getStepX();
        double $$3 = $$0.y() + 0.7 * (double)$$1.getStepY();
        double $$4 = $$0.z() + 0.7 * (double)$$1.getStepZ();
        return new PositionImpl($$2, $$3, $$4);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity($$1.getBlockEntity($$2));
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
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
        $$0.add(FACING, TRIGGERED);
    }
}