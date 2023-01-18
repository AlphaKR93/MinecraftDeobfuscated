/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map$Entry
 */
package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonBaseBlock
extends DirectionalBlock {
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final int TRIGGER_EXTEND = 0;
    public static final int TRIGGER_CONTRACT = 1;
    public static final int TRIGGER_DROP = 2;
    public static final float PLATFORM_THICKNESS = 4.0f;
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape UP_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    private final boolean isSticky;

    public PistonBaseBlock(boolean $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(EXTENDED, false));
        this.isSticky = $$0;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$0.getValue(EXTENDED).booleanValue()) {
            switch ($$0.getValue(FACING)) {
                case DOWN: {
                    return DOWN_AABB;
                }
                default: {
                    return UP_AABB;
                }
                case NORTH: {
                    return NORTH_AABB;
                }
                case SOUTH: {
                    return SOUTH_AABB;
                }
                case WEST: {
                    return WEST_AABB;
                }
                case EAST: 
            }
            return EAST_AABB;
        }
        return Shapes.block();
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        if (!$$0.isClientSide) {
            this.checkIfExtend($$0, $$1, $$2);
        }
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if (!$$1.isClientSide) {
            this.checkIfExtend($$1, $$2, $$0);
        }
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        if (!$$1.isClientSide && $$1.getBlockEntity($$2) == null) {
            this.checkIfExtend($$1, $$2, $$0);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getNearestLookingDirection().getOpposite())).setValue(EXTENDED, false);
    }

    private void checkIfExtend(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING);
        boolean $$4 = this.getNeighborSignal($$0, $$1, $$3);
        if ($$4 && !$$2.getValue(EXTENDED).booleanValue()) {
            if (new PistonStructureResolver($$0, $$1, $$3, true).resolve()) {
                $$0.blockEvent($$1, this, 0, $$3.get3DDataValue());
            }
        } else if (!$$4 && $$2.getValue(EXTENDED).booleanValue()) {
            PistonMovingBlockEntity $$9;
            BlockEntity $$8;
            BlockPos $$5 = $$1.relative($$3, 2);
            BlockState $$6 = $$0.getBlockState($$5);
            int $$7 = 1;
            if ($$6.is(Blocks.MOVING_PISTON) && $$6.getValue(FACING) == $$3 && ($$8 = $$0.getBlockEntity($$5)) instanceof PistonMovingBlockEntity && ($$9 = (PistonMovingBlockEntity)$$8).isExtending() && ($$9.getProgress(0.0f) < 0.5f || $$0.getGameTime() == $$9.getLastTicked() || ((ServerLevel)$$0).isHandlingTick())) {
                $$7 = 2;
            }
            $$0.blockEvent($$1, this, $$7, $$3.get3DDataValue());
        }
    }

    private boolean getNeighborSignal(Level $$0, BlockPos $$1, Direction $$2) {
        for (Direction $$3 : Direction.values()) {
            if ($$3 == $$2 || !$$0.hasSignal((BlockPos)$$1.relative($$3), $$3)) continue;
            return true;
        }
        if ($$0.hasSignal($$1, Direction.DOWN)) {
            return true;
        }
        Vec3i $$4 = $$1.above();
        for (Direction $$5 : Direction.values()) {
            if ($$5 == Direction.DOWN || !$$0.hasSignal((BlockPos)((BlockPos)$$4).relative($$5), $$5)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean triggerEvent(BlockState $$0, Level $$1, BlockPos $$2, int $$3, int $$4) {
        Direction $$5 = $$0.getValue(FACING);
        if (!$$1.isClientSide) {
            boolean $$6 = this.getNeighborSignal($$1, $$2, $$5);
            if ($$6 && ($$3 == 1 || $$3 == 2)) {
                $$1.setBlock($$2, (BlockState)$$0.setValue(EXTENDED, true), 2);
                return false;
            }
            if (!$$6 && $$3 == 0) {
                return false;
            }
        }
        if ($$3 == 0) {
            if (!this.moveBlocks($$1, $$2, $$5, true)) return false;
            $$1.setBlock($$2, (BlockState)$$0.setValue(EXTENDED, true), 67);
            $$1.playSound(null, $$2, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, $$1.random.nextFloat() * 0.25f + 0.6f);
            $$1.gameEvent(null, GameEvent.PISTON_EXTEND, $$2);
            return true;
        } else {
            if ($$3 != 1 && $$3 != 2) return true;
            BlockEntity $$7 = $$1.getBlockEntity((BlockPos)$$2.relative($$5));
            if ($$7 instanceof PistonMovingBlockEntity) {
                ((PistonMovingBlockEntity)$$7).finalTick();
            }
            BlockState $$8 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, $$5)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            $$1.setBlock($$2, $$8, 20);
            $$1.setBlockEntity(MovingPistonBlock.newMovingBlockEntity($$2, $$8, (BlockState)this.defaultBlockState().setValue(FACING, Direction.from3DDataValue($$4 & 7)), $$5, false, true));
            $$1.blockUpdated($$2, $$8.getBlock());
            $$8.updateNeighbourShapes($$1, $$2, 2);
            if (this.isSticky) {
                PistonMovingBlockEntity $$13;
                BlockEntity $$12;
                BlockPos $$9 = $$2.offset($$5.getStepX() * 2, $$5.getStepY() * 2, $$5.getStepZ() * 2);
                BlockState $$10 = $$1.getBlockState($$9);
                boolean $$11 = false;
                if ($$10.is(Blocks.MOVING_PISTON) && ($$12 = $$1.getBlockEntity($$9)) instanceof PistonMovingBlockEntity && ($$13 = (PistonMovingBlockEntity)$$12).getDirection() == $$5 && $$13.isExtending()) {
                    $$13.finalTick();
                    $$11 = true;
                }
                if (!$$11) {
                    if ($$3 == 1 && !$$10.isAir() && PistonBaseBlock.isPushable($$10, $$1, $$9, $$5.getOpposite(), false, $$5) && ($$10.getPistonPushReaction() == PushReaction.NORMAL || $$10.is(Blocks.PISTON) || $$10.is(Blocks.STICKY_PISTON))) {
                        this.moveBlocks($$1, $$2, $$5, false);
                    } else {
                        $$1.removeBlock((BlockPos)$$2.relative($$5), false);
                    }
                }
            } else {
                $$1.removeBlock((BlockPos)$$2.relative($$5), false);
            }
            $$1.playSound(null, $$2, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5f, $$1.random.nextFloat() * 0.15f + 0.6f);
            $$1.gameEvent(null, GameEvent.PISTON_CONTRACT, $$2);
        }
        return true;
    }

    public static boolean isPushable(BlockState $$0, Level $$1, BlockPos $$2, Direction $$3, boolean $$4, Direction $$5) {
        if ($$2.getY() < $$1.getMinBuildHeight() || $$2.getY() > $$1.getMaxBuildHeight() - 1 || !$$1.getWorldBorder().isWithinBounds($$2)) {
            return false;
        }
        if ($$0.isAir()) {
            return true;
        }
        if ($$0.is(Blocks.OBSIDIAN) || $$0.is(Blocks.CRYING_OBSIDIAN) || $$0.is(Blocks.RESPAWN_ANCHOR) || $$0.is(Blocks.REINFORCED_DEEPSLATE)) {
            return false;
        }
        if ($$3 == Direction.DOWN && $$2.getY() == $$1.getMinBuildHeight()) {
            return false;
        }
        if ($$3 == Direction.UP && $$2.getY() == $$1.getMaxBuildHeight() - 1) {
            return false;
        }
        if ($$0.is(Blocks.PISTON) || $$0.is(Blocks.STICKY_PISTON)) {
            if ($$0.getValue(EXTENDED).booleanValue()) {
                return false;
            }
        } else {
            if ($$0.getDestroySpeed($$1, $$2) == -1.0f) {
                return false;
            }
            switch ($$0.getPistonPushReaction()) {
                case BLOCK: {
                    return false;
                }
                case DESTROY: {
                    return $$4;
                }
                case PUSH_ONLY: {
                    return $$3 == $$5;
                }
            }
        }
        return !$$0.hasBlockEntity();
    }

    private boolean moveBlocks(Level $$0, BlockPos $$1, Direction $$2, boolean $$3) {
        PistonStructureResolver $$5;
        Vec3i $$4 = $$1.relative($$2);
        if (!$$3 && $$0.getBlockState((BlockPos)$$4).is(Blocks.PISTON_HEAD)) {
            $$0.setBlock((BlockPos)$$4, Blocks.AIR.defaultBlockState(), 20);
        }
        if (!($$5 = new PistonStructureResolver($$0, $$1, $$2, $$3)).resolve()) {
            return false;
        }
        HashMap $$6 = Maps.newHashMap();
        List<BlockPos> $$7 = $$5.getToPush();
        ArrayList $$8 = Lists.newArrayList();
        for (int $$9 = 0; $$9 < $$7.size(); ++$$9) {
            BlockPos $$10 = (BlockPos)$$7.get($$9);
            BlockState $$11 = $$0.getBlockState($$10);
            $$8.add((Object)$$11);
            $$6.put((Object)$$10, (Object)$$11);
        }
        List<BlockPos> $$12 = $$5.getToDestroy();
        BlockState[] $$13 = new BlockState[$$7.size() + $$12.size()];
        Direction $$14 = $$3 ? $$2 : $$2.getOpposite();
        int $$15 = 0;
        for (int $$16 = $$12.size() - 1; $$16 >= 0; --$$16) {
            BlockPos $$17 = (BlockPos)$$12.get($$16);
            BlockState $$18 = $$0.getBlockState($$17);
            BlockEntity $$19 = $$18.hasBlockEntity() ? $$0.getBlockEntity($$17) : null;
            PistonBaseBlock.dropResources($$18, $$0, $$17, $$19);
            $$0.setBlock($$17, Blocks.AIR.defaultBlockState(), 18);
            $$0.gameEvent(GameEvent.BLOCK_DESTROY, $$17, GameEvent.Context.of($$18));
            if (!$$18.is(BlockTags.FIRE)) {
                $$0.addDestroyBlockEffect($$17, $$18);
            }
            $$13[$$15++] = $$18;
        }
        for (int $$20 = $$7.size() - 1; $$20 >= 0; --$$20) {
            Vec3i $$21 = (BlockPos)$$7.get($$20);
            BlockState $$22 = $$0.getBlockState((BlockPos)$$21);
            $$21 = $$21.relative($$14);
            $$6.remove((Object)$$21);
            BlockState $$23 = (BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, $$2);
            $$0.setBlock((BlockPos)$$21, $$23, 68);
            $$0.setBlockEntity(MovingPistonBlock.newMovingBlockEntity($$21, $$23, (BlockState)$$8.get($$20), $$2, $$3, false));
            $$13[$$15++] = $$22;
        }
        if ($$3) {
            PistonType $$24 = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            Object $$25 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, $$2)).setValue(PistonHeadBlock.TYPE, $$24);
            BlockState $$26 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, $$2)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            $$6.remove((Object)$$4);
            $$0.setBlock((BlockPos)$$4, $$26, 68);
            $$0.setBlockEntity(MovingPistonBlock.newMovingBlockEntity((BlockPos)$$4, $$26, (BlockState)$$25, $$2, true, true));
        }
        BlockState $$27 = Blocks.AIR.defaultBlockState();
        for (BlockPos $$28 : $$6.keySet()) {
            $$0.setBlock($$28, $$27, 82);
        }
        for (Map.Entry $$29 : $$6.entrySet()) {
            BlockPos $$30 = (BlockPos)$$29.getKey();
            BlockState $$31 = (BlockState)$$29.getValue();
            $$31.updateIndirectNeighbourShapes($$0, $$30, 2);
            $$27.updateNeighbourShapes($$0, $$30, 2);
            $$27.updateIndirectNeighbourShapes($$0, $$30, 2);
        }
        $$15 = 0;
        for (int $$32 = $$12.size() - 1; $$32 >= 0; --$$32) {
            BlockState $$33 = $$13[$$15++];
            BlockPos $$34 = (BlockPos)$$12.get($$32);
            $$33.updateIndirectNeighbourShapes($$0, $$34, 2);
            $$0.updateNeighborsAt($$34, $$33.getBlock());
        }
        for (int $$35 = $$7.size() - 1; $$35 >= 0; --$$35) {
            $$0.updateNeighborsAt((BlockPos)$$7.get($$35), $$13[$$15++].getBlock());
        }
        if ($$3) {
            $$0.updateNeighborsAt((BlockPos)$$4, Blocks.PISTON_HEAD);
        }
        return true;
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
        $$0.add(FACING, EXTENDED);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return $$0.getValue(EXTENDED);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}