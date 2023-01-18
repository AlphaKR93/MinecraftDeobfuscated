/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;

public class DetectorRailBlock
extends BaseRailBlock {
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int PRESSED_CHECK_PERIOD = 20;

    public DetectorRailBlock(BlockBehaviour.Properties $$0) {
        super(true, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$1.isClientSide) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2, $$0);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2, $$0);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return 0;
        }
        return $$3 == Direction.UP ? 15 : 0;
    }

    private void checkPressed(Level $$02, BlockPos $$1, BlockState $$2) {
        if (!this.canSurvive($$2, $$02, $$1)) {
            return;
        }
        boolean $$3 = $$2.getValue(POWERED);
        boolean $$4 = false;
        List<AbstractMinecart> $$5 = this.getInteractingMinecartOfType($$02, $$1, AbstractMinecart.class, (Predicate<Entity>)((Predicate)$$0 -> true));
        if (!$$5.isEmpty()) {
            $$4 = true;
        }
        if ($$4 && !$$3) {
            BlockState $$6 = (BlockState)$$2.setValue(POWERED, true);
            $$02.setBlock($$1, $$6, 3);
            this.updatePowerToConnected($$02, $$1, $$6, true);
            $$02.updateNeighborsAt($$1, this);
            $$02.updateNeighborsAt((BlockPos)$$1.below(), this);
            $$02.setBlocksDirty($$1, $$2, $$6);
        }
        if (!$$4 && $$3) {
            BlockState $$7 = (BlockState)$$2.setValue(POWERED, false);
            $$02.setBlock($$1, $$7, 3);
            this.updatePowerToConnected($$02, $$1, $$7, false);
            $$02.updateNeighborsAt($$1, this);
            $$02.updateNeighborsAt((BlockPos)$$1.below(), this);
            $$02.setBlocksDirty($$1, $$2, $$7);
        }
        if ($$4) {
            $$02.scheduleTick($$1, this, 20);
        }
        $$02.updateNeighbourForOutputSignal($$1, this);
    }

    protected void updatePowerToConnected(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        RailState $$4 = new RailState($$0, $$1, $$2);
        List<BlockPos> $$5 = $$4.getConnections();
        for (BlockPos $$6 : $$5) {
            BlockState $$7 = $$0.getBlockState($$6);
            $$0.neighborChanged($$7, $$6, $$7.getBlock(), $$1, false);
        }
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        BlockState $$5 = this.updateState($$0, $$1, $$2, $$4);
        this.checkPressed($$1, $$2, $$5);
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$02, Level $$1, BlockPos $$2) {
        if ($$02.getValue(POWERED).booleanValue()) {
            List<MinecartCommandBlock> $$3 = this.getInteractingMinecartOfType($$1, $$2, MinecartCommandBlock.class, (Predicate<Entity>)((Predicate)$$0 -> true));
            if (!$$3.isEmpty()) {
                return ((MinecartCommandBlock)$$3.get(0)).getCommandBlock().getSuccessCount();
            }
            List<AbstractMinecart> $$4 = this.getInteractingMinecartOfType($$1, $$2, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if (!$$4.isEmpty()) {
                return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)$$4.get(0));
            }
        }
        return 0;
    }

    private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level $$0, BlockPos $$1, Class<T> $$2, Predicate<Entity> $$3) {
        return $$0.getEntitiesOfClass($$2, this.getSearchBB($$1), $$3);
    }

    private AABB getSearchBB(BlockPos $$0) {
        double $$1 = 0.2;
        return new AABB((double)$$0.getX() + 0.2, $$0.getY(), (double)$$0.getZ() + 0.2, (double)($$0.getX() + 1) - 0.2, (double)($$0.getY() + 1) - 0.2, (double)($$0.getZ() + 1) - 0.2);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                switch ($$0.getValue(SHAPE)) {
                    case ASCENDING_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch ($$0.getValue(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            }
            case CLOCKWISE_90: {
                switch ($$0.getValue(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            }
        }
        return $$0;
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        RailShape $$2 = $$0.getValue(SHAPE);
        switch ($$1) {
            case LEFT_RIGHT: {
                switch ($$2) {
                    case ASCENDING_NORTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
                break;
            }
            case FRONT_BACK: {
                switch ($$2) {
                    case ASCENDING_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)$$0.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                }
                break;
            }
        }
        return super.mirror($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(SHAPE, POWERED, WATERLOGGED);
    }
}