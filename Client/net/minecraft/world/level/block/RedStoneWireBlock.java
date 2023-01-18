/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.HashSet
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock
extends Block {
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, NORTH, (Object)Direction.EAST, EAST, (Object)Direction.SOUTH, SOUTH, (Object)Direction.WEST, WEST));
    protected static final int H = 1;
    protected static final int W = 3;
    protected static final int E = 13;
    protected static final int N = 3;
    protected static final int S = 13;
    private static final VoxelShape SHAPE_DOT = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), (Object)Direction.SOUTH, (Object)Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), (Object)Direction.EAST, (Object)Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), (Object)Direction.WEST, (Object)Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
    private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Shapes.or((VoxelShape)SHAPES_FLOOR.get((Object)Direction.NORTH), Block.box(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), (Object)Direction.SOUTH, (Object)Shapes.or((VoxelShape)SHAPES_FLOOR.get((Object)Direction.SOUTH), Block.box(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), (Object)Direction.EAST, (Object)Shapes.or((VoxelShape)SHAPES_FLOOR.get((Object)Direction.EAST), Block.box(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), (Object)Direction.WEST, (Object)Shapes.or((VoxelShape)SHAPES_FLOOR.get((Object)Direction.WEST), Block.box(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
    private static final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();
    private static final Vec3[] COLORS = Util.make(new Vec3[16], $$0 -> {
        for (int $$1 = 0; $$1 <= 15; ++$$1) {
            float $$2;
            float $$3 = $$2 * 0.6f + (($$2 = (float)$$1 / 15.0f) > 0.0f ? 0.4f : 0.3f);
            float $$4 = Mth.clamp($$2 * $$2 * 0.7f - 0.5f, 0.0f, 1.0f);
            float $$5 = Mth.clamp($$2 * $$2 * 0.6f - 0.7f, 0.0f, 1.0f);
            $$0[$$1] = new Vec3($$3, $$4, $$5);
        }
    });
    private static final float PARTICLE_DENSITY = 0.2f;
    private final BlockState crossState;
    private boolean shouldSignal = true;

    public RedStoneWireBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, 0));
        this.crossState = (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE)).setValue(EAST, RedstoneSide.SIDE)).setValue(SOUTH, RedstoneSide.SIDE)).setValue(WEST, RedstoneSide.SIDE);
        for (BlockState $$1 : this.getStateDefinition().getPossibleStates()) {
            if ($$1.getValue(POWER) != 0) continue;
            SHAPES_CACHE.put((Object)$$1, (Object)this.calculateShape($$1));
        }
    }

    private VoxelShape calculateShape(BlockState $$0) {
        VoxelShape $$1 = SHAPE_DOT;
        for (Direction $$2 : Direction.Plane.HORIZONTAL) {
            RedstoneSide $$3 = (RedstoneSide)$$0.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$2));
            if ($$3 == RedstoneSide.SIDE) {
                $$1 = Shapes.or($$1, (VoxelShape)SHAPES_FLOOR.get((Object)$$2));
                continue;
            }
            if ($$3 != RedstoneSide.UP) continue;
            $$1 = Shapes.or($$1, (VoxelShape)SHAPES_UP.get((Object)$$2));
        }
        return $$1;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)SHAPES_CACHE.get($$0.setValue(POWER, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return this.getConnectionState($$0.getLevel(), this.crossState, $$0.getClickedPos());
    }

    private BlockState getConnectionState(BlockGetter $$0, BlockState $$1, BlockPos $$2) {
        boolean $$9;
        boolean $$3 = RedStoneWireBlock.isDot($$1);
        $$1 = this.getMissingConnections($$0, (BlockState)this.defaultBlockState().setValue(POWER, $$1.getValue(POWER)), $$2);
        if ($$3 && RedStoneWireBlock.isDot($$1)) {
            return $$1;
        }
        boolean $$4 = $$1.getValue(NORTH).isConnected();
        boolean $$5 = $$1.getValue(SOUTH).isConnected();
        boolean $$6 = $$1.getValue(EAST).isConnected();
        boolean $$7 = $$1.getValue(WEST).isConnected();
        boolean $$8 = !$$4 && !$$5;
        boolean bl = $$9 = !$$6 && !$$7;
        if (!$$7 && $$8) {
            $$1 = (BlockState)$$1.setValue(WEST, RedstoneSide.SIDE);
        }
        if (!$$6 && $$8) {
            $$1 = (BlockState)$$1.setValue(EAST, RedstoneSide.SIDE);
        }
        if (!$$4 && $$9) {
            $$1 = (BlockState)$$1.setValue(NORTH, RedstoneSide.SIDE);
        }
        if (!$$5 && $$9) {
            $$1 = (BlockState)$$1.setValue(SOUTH, RedstoneSide.SIDE);
        }
        return $$1;
    }

    private BlockState getMissingConnections(BlockGetter $$0, BlockState $$1, BlockPos $$2) {
        boolean $$3 = !$$0.getBlockState((BlockPos)$$2.above()).isRedstoneConductor($$0, $$2);
        for (Direction $$4 : Direction.Plane.HORIZONTAL) {
            if (((RedstoneSide)$$1.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$4))).isConnected()) continue;
            RedstoneSide $$5 = this.getConnectingSide($$0, $$2, $$4, $$3);
            $$1 = (BlockState)$$1.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$4), $$5);
        }
        return $$1;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.DOWN) {
            return $$0;
        }
        if ($$1 == Direction.UP) {
            return this.getConnectionState($$3, $$0, $$4);
        }
        RedstoneSide $$6 = this.getConnectingSide($$3, $$4, $$1);
        if ($$6.isConnected() == ((RedstoneSide)$$0.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1))).isConnected() && !RedStoneWireBlock.isCross($$0)) {
            return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), $$6);
        }
        return this.getConnectionState($$3, (BlockState)((BlockState)this.crossState.setValue(POWER, $$0.getValue(POWER))).setValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$1), $$6), $$4);
    }

    private static boolean isCross(BlockState $$0) {
        return $$0.getValue(NORTH).isConnected() && $$0.getValue(SOUTH).isConnected() && $$0.getValue(EAST).isConnected() && $$0.getValue(WEST).isConnected();
    }

    private static boolean isDot(BlockState $$0) {
        return !$$0.getValue(NORTH).isConnected() && !$$0.getValue(SOUTH).isConnected() && !$$0.getValue(EAST).isConnected() && !$$0.getValue(WEST).isConnected();
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState $$0, LevelAccessor $$1, BlockPos $$2, int $$3, int $$4) {
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            RedstoneSide $$7 = (RedstoneSide)$$0.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$6));
            if ($$7 == RedstoneSide.NONE || $$1.getBlockState($$5.setWithOffset((Vec3i)$$2, $$6)).is(this)) continue;
            $$5.move(Direction.DOWN);
            BlockState $$8 = $$1.getBlockState($$5);
            if ($$8.is(this)) {
                Vec3i $$9 = $$5.relative($$6.getOpposite());
                $$1.neighborShapeChanged($$6.getOpposite(), $$1.getBlockState((BlockPos)$$9), $$5, (BlockPos)$$9, $$3, $$4);
            }
            $$5.setWithOffset((Vec3i)$$2, $$6).move(Direction.UP);
            BlockState $$10 = $$1.getBlockState($$5);
            if (!$$10.is(this)) continue;
            Vec3i $$11 = $$5.relative($$6.getOpposite());
            $$1.neighborShapeChanged($$6.getOpposite(), $$1.getBlockState((BlockPos)$$11), $$5, (BlockPos)$$11, $$3, $$4);
        }
    }

    private RedstoneSide getConnectingSide(BlockGetter $$0, BlockPos $$1, Direction $$2) {
        return this.getConnectingSide($$0, $$1, $$2, !$$0.getBlockState((BlockPos)$$1.above()).isRedstoneConductor($$0, $$1));
    }

    private RedstoneSide getConnectingSide(BlockGetter $$0, BlockPos $$1, Direction $$2, boolean $$3) {
        boolean $$6;
        Vec3i $$4 = $$1.relative($$2);
        BlockState $$5 = $$0.getBlockState((BlockPos)$$4);
        if ($$3 && ($$6 = this.canSurviveOn($$0, (BlockPos)$$4, $$5)) && RedStoneWireBlock.shouldConnectTo($$0.getBlockState((BlockPos)((BlockPos)$$4).above()))) {
            if ($$5.isFaceSturdy($$0, (BlockPos)$$4, $$2.getOpposite())) {
                return RedstoneSide.UP;
            }
            return RedstoneSide.SIDE;
        }
        if (RedStoneWireBlock.shouldConnectTo($$5, $$2) || !$$5.isRedstoneConductor($$0, (BlockPos)$$4) && RedStoneWireBlock.shouldConnectTo($$0.getBlockState((BlockPos)((BlockPos)$$4).below()))) {
            return RedstoneSide.SIDE;
        }
        return RedstoneSide.NONE;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.below();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        return this.canSurviveOn($$1, (BlockPos)$$3, $$4);
    }

    private boolean canSurviveOn(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return $$2.isFaceSturdy($$0, $$1, Direction.UP) || $$2.is(Blocks.HOPPER);
    }

    private void updatePowerStrength(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = this.calculateTargetStrength($$0, $$1);
        if ($$2.getValue(POWER) != $$3) {
            if ($$0.getBlockState($$1) == $$2) {
                $$0.setBlock($$1, (BlockState)$$2.setValue(POWER, $$3), 2);
            }
            HashSet $$4 = Sets.newHashSet();
            $$4.add((Object)$$1);
            for (Direction $$5 : Direction.values()) {
                $$4.add((Object)$$1.relative($$5));
            }
            for (BlockPos $$6 : $$4) {
                $$0.updateNeighborsAt($$6, this);
            }
        }
    }

    private int calculateTargetStrength(Level $$0, BlockPos $$1) {
        this.shouldSignal = false;
        int $$2 = $$0.getBestNeighborSignal($$1);
        this.shouldSignal = true;
        int $$3 = 0;
        if ($$2 < 15) {
            for (Direction $$4 : Direction.Plane.HORIZONTAL) {
                Vec3i $$5 = $$1.relative($$4);
                BlockState $$6 = $$0.getBlockState((BlockPos)$$5);
                $$3 = Math.max((int)$$3, (int)this.getWireSignal($$6));
                Vec3i $$7 = $$1.above();
                if ($$6.isRedstoneConductor($$0, (BlockPos)$$5) && !$$0.getBlockState((BlockPos)$$7).isRedstoneConductor($$0, (BlockPos)$$7)) {
                    $$3 = Math.max((int)$$3, (int)this.getWireSignal($$0.getBlockState((BlockPos)((BlockPos)$$5).above())));
                    continue;
                }
                if ($$6.isRedstoneConductor($$0, (BlockPos)$$5)) continue;
                $$3 = Math.max((int)$$3, (int)this.getWireSignal($$0.getBlockState((BlockPos)((BlockPos)$$5).below())));
            }
        }
        return Math.max((int)$$2, (int)($$3 - 1));
    }

    private int getWireSignal(BlockState $$0) {
        return $$0.is(this) ? $$0.getValue(POWER) : 0;
    }

    private void checkCornerChangeAt(Level $$0, BlockPos $$1) {
        if (!$$0.getBlockState($$1).is(this)) {
            return;
        }
        $$0.updateNeighborsAt($$1, this);
        for (Direction $$2 : Direction.values()) {
            $$0.updateNeighborsAt((BlockPos)$$1.relative($$2), this);
        }
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock()) || $$1.isClientSide) {
            return;
        }
        this.updatePowerStrength($$1, $$2, $$0);
        for (Direction $$5 : Direction.Plane.VERTICAL) {
            $$1.updateNeighborsAt((BlockPos)$$2.relative($$5), this);
        }
        this.updateNeighborsOfNeighboringWires($$1, $$2);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$4 || $$0.is($$3.getBlock())) {
            return;
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
        if ($$1.isClientSide) {
            return;
        }
        for (Direction $$5 : Direction.values()) {
            $$1.updateNeighborsAt((BlockPos)$$2.relative($$5), this);
        }
        this.updatePowerStrength($$1, $$2, $$0);
        this.updateNeighborsOfNeighboringWires($$1, $$2);
    }

    private void updateNeighborsOfNeighboringWires(Level $$0, BlockPos $$1) {
        for (Direction $$2 : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt($$0, (BlockPos)$$1.relative($$2));
        }
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            Vec3i $$4 = $$1.relative($$3);
            if ($$0.getBlockState((BlockPos)$$4).isRedstoneConductor($$0, (BlockPos)$$4)) {
                this.checkCornerChangeAt($$0, (BlockPos)((BlockPos)$$4).above());
                continue;
            }
            this.checkCornerChangeAt($$0, (BlockPos)((BlockPos)$$4).below());
        }
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$1.isClientSide) {
            return;
        }
        if ($$0.canSurvive($$1, $$2)) {
            this.updatePowerStrength($$1, $$2, $$0);
        } else {
            RedStoneWireBlock.dropResources($$0, $$1, $$2);
            $$1.removeBlock($$2, false);
        }
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if (!this.shouldSignal) {
            return 0;
        }
        return $$0.getSignal($$1, $$2, $$3);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if (!this.shouldSignal || $$3 == Direction.DOWN) {
            return 0;
        }
        int $$4 = $$0.getValue(POWER);
        if ($$4 == 0) {
            return 0;
        }
        if ($$3 == Direction.UP || ((RedstoneSide)this.getConnectionState($$1, $$0, $$2).getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$3.getOpposite()))).isConnected()) {
            return $$4;
        }
        return 0;
    }

    protected static boolean shouldConnectTo(BlockState $$0) {
        return RedStoneWireBlock.shouldConnectTo($$0, null);
    }

    protected static boolean shouldConnectTo(BlockState $$0, @Nullable Direction $$1) {
        if ($$0.is(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if ($$0.is(Blocks.REPEATER)) {
            Direction $$2 = $$0.getValue(RepeaterBlock.FACING);
            return $$2 == $$1 || $$2.getOpposite() == $$1;
        }
        if ($$0.is(Blocks.OBSERVER)) {
            return $$1 == $$0.getValue(ObserverBlock.FACING);
        }
        return $$0.isSignalSource() && $$1 != null;
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return this.shouldSignal;
    }

    public static int getColorForPower(int $$0) {
        Vec3 $$1 = COLORS[$$0];
        return Mth.color((float)$$1.x(), (float)$$1.y(), (float)$$1.z());
    }

    private void spawnParticlesAlongLine(Level $$0, RandomSource $$1, BlockPos $$2, Vec3 $$3, Direction $$4, Direction $$5, float $$6, float $$7) {
        float $$8 = $$7 - $$6;
        if ($$1.nextFloat() >= 0.2f * $$8) {
            return;
        }
        float $$9 = 0.4375f;
        float $$10 = $$6 + $$8 * $$1.nextFloat();
        double $$11 = 0.5 + (double)(0.4375f * (float)$$4.getStepX()) + (double)($$10 * (float)$$5.getStepX());
        double $$12 = 0.5 + (double)(0.4375f * (float)$$4.getStepY()) + (double)($$10 * (float)$$5.getStepY());
        double $$13 = 0.5 + (double)(0.4375f * (float)$$4.getStepZ()) + (double)($$10 * (float)$$5.getStepZ());
        $$0.addParticle(new DustParticleOptions($$3.toVector3f(), 1.0f), (double)$$2.getX() + $$11, (double)$$2.getY() + $$12, (double)$$2.getZ() + $$13, 0.0, 0.0, 0.0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = $$0.getValue(POWER);
        if ($$4 == 0) {
            return;
        }
        block4: for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            RedstoneSide $$6 = (RedstoneSide)$$0.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$5));
            switch ($$6) {
                case UP: {
                    this.spawnParticlesAlongLine($$1, $$3, $$2, COLORS[$$4], $$5, Direction.UP, -0.5f, 0.5f);
                }
                case SIDE: {
                    this.spawnParticlesAlongLine($$1, $$3, $$2, COLORS[$$4], Direction.DOWN, $$5, 0.0f, 0.5f);
                    continue block4;
                }
            }
            this.spawnParticlesAlongLine($$1, $$3, $$2, COLORS[$$4], Direction.DOWN, $$5, 0.0f, 0.3f);
        }
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(EAST, $$0.getValue(WEST))).setValue(SOUTH, $$0.getValue(NORTH))).setValue(WEST, $$0.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(EAST))).setValue(EAST, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(WEST))).setValue(EAST, $$0.getValue(NORTH))).setValue(SOUTH, $$0.getValue(EAST))).setValue(WEST, $$0.getValue(SOUTH));
            }
        }
        return $$0;
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        switch ($$1) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)$$0.setValue(EAST, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(EAST));
            }
        }
        return super.mirror($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(NORTH, EAST, SOUTH, WEST, POWER);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if (!$$3.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        if (RedStoneWireBlock.isCross($$0) || RedStoneWireBlock.isDot($$0)) {
            BlockState $$6 = RedStoneWireBlock.isCross($$0) ? this.defaultBlockState() : this.crossState;
            $$6 = (BlockState)$$6.setValue(POWER, $$0.getValue(POWER));
            if (($$6 = this.getConnectionState($$1, $$6, $$2)) != $$0) {
                $$1.setBlock($$2, $$6, 3);
                this.updatesOnShapeChange($$1, $$2, $$0, $$6);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private void updatesOnShapeChange(Level $$0, BlockPos $$1, BlockState $$2, BlockState $$3) {
        for (Direction $$4 : Direction.Plane.HORIZONTAL) {
            Vec3i $$5 = $$1.relative($$4);
            if (((RedstoneSide)$$2.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$4))).isConnected() == ((RedstoneSide)$$3.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)$$4))).isConnected() || !$$0.getBlockState((BlockPos)$$5).isRedstoneConductor($$0, (BlockPos)$$5)) continue;
            $$0.updateNeighborsAtExceptFromFacing((BlockPos)$$5, $$3.getBlock(), $$4.getOpposite());
        }
    }
}