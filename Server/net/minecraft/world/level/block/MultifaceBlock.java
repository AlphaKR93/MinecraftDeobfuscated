/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.EnumSet
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class MultifaceBlock
extends Block {
    private static final float AABB_OFFSET = 1.0f;
    private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = (Map)Util.make(Maps.newEnumMap(Direction.class), $$0 -> {
        $$0.put((Enum)Direction.NORTH, (Object)NORTH_AABB);
        $$0.put((Enum)Direction.EAST, (Object)EAST_AABB);
        $$0.put((Enum)Direction.SOUTH, (Object)SOUTH_AABB);
        $$0.put((Enum)Direction.WEST, (Object)WEST_AABB);
        $$0.put((Enum)Direction.UP, (Object)UP_AABB);
        $$0.put((Enum)Direction.DOWN, (Object)DOWN_AABB);
    });
    protected static final Direction[] DIRECTIONS = Direction.values();
    private final ImmutableMap<BlockState, VoxelShape> shapesCache;
    private final boolean canRotate;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    public MultifaceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState(MultifaceBlock.getDefaultMultifaceState(this.stateDefinition));
        this.shapesCache = this.getShapeForEachState((Function<BlockState, VoxelShape>)((Function)MultifaceBlock::calculateMultifaceShape));
        this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
        this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter((Predicate)Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
        this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter((Predicate)Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
    }

    public static Set<Direction> availableFaces(BlockState $$0) {
        if (!($$0.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        }
        EnumSet $$1 = EnumSet.noneOf(Direction.class);
        for (Direction $$2 : Direction.values()) {
            if (!MultifaceBlock.hasFace($$0, $$2)) continue;
            $$1.add((Object)$$2);
        }
        return $$1;
    }

    public static Set<Direction> unpack(byte $$0) {
        EnumSet $$1 = EnumSet.noneOf(Direction.class);
        for (Direction $$2 : Direction.values()) {
            if (($$0 & (byte)(1 << $$2.ordinal())) <= 0) continue;
            $$1.add((Object)$$2);
        }
        return $$1;
    }

    public static byte pack(Collection<Direction> $$0) {
        byte $$1 = 0;
        for (Direction $$2 : $$0) {
            $$1 = (byte)($$1 | 1 << $$2.ordinal());
        }
        return $$1;
    }

    protected boolean isFaceSupported(Direction $$0) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        for (Direction $$1 : DIRECTIONS) {
            if (!this.isFaceSupported($$1)) continue;
            $$0.add(MultifaceBlock.getFaceProperty($$1));
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if (!MultifaceBlock.hasAnyFace($$0)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (!MultifaceBlock.hasFace($$0, $$1) || MultifaceBlock.canAttachTo($$3, $$1, $$5, $$2)) {
            return $$0;
        }
        return MultifaceBlock.removeFace($$0, MultifaceBlock.getFaceProperty($$1));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)this.shapesCache.get((Object)$$0);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        boolean $$3 = false;
        for (Direction $$4 : DIRECTIONS) {
            if (!MultifaceBlock.hasFace($$0, $$4)) continue;
            Vec3i $$5 = $$2.relative($$4);
            if (!MultifaceBlock.canAttachTo($$1, $$4, (BlockPos)$$5, $$1.getBlockState((BlockPos)$$5))) {
                return false;
            }
            $$3 = true;
        }
        return $$3;
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return MultifaceBlock.hasAnyVacantFace($$0);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        BlockState $$32 = $$1.getBlockState($$2);
        return (BlockState)Arrays.stream((Object[])$$0.getNearestLookingDirections()).map($$3 -> this.getStateForPlacement($$32, $$1, $$2, (Direction)$$3)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public boolean isValidStateForPlacement(BlockGetter $$0, BlockState $$1, BlockPos $$2, Direction $$3) {
        if (!this.isFaceSupported($$3) || $$1.is(this) && MultifaceBlock.hasFace($$1, $$3)) {
            return false;
        }
        Vec3i $$4 = $$2.relative($$3);
        return MultifaceBlock.canAttachTo($$0, $$3, (BlockPos)$$4, $$0.getBlockState((BlockPos)$$4));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        BlockState $$6;
        if (!this.isValidStateForPlacement($$1, $$0, $$2, $$3)) {
            return null;
        }
        if ($$0.is(this)) {
            BlockState $$4 = $$0;
        } else if (this.isWaterloggable() && $$0.getFluidState().isSourceOfType(Fluids.WATER)) {
            BlockState $$5 = (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
        } else {
            $$6 = this.defaultBlockState();
        }
        return (BlockState)$$6.setValue(MultifaceBlock.getFaceProperty($$3), true);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        if (!this.canRotate) {
            return $$0;
        }
        return this.mapDirections($$0, (Function<Direction, Direction>)((Function)$$1::rotate));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        if ($$1 == Mirror.FRONT_BACK && !this.canMirrorX) {
            return $$0;
        }
        if ($$1 == Mirror.LEFT_RIGHT && !this.canMirrorZ) {
            return $$0;
        }
        return this.mapDirections($$0, (Function<Direction, Direction>)((Function)$$1::mirror));
    }

    private BlockState mapDirections(BlockState $$0, Function<Direction, Direction> $$1) {
        BlockState $$2 = $$0;
        for (Direction $$3 : DIRECTIONS) {
            if (!this.isFaceSupported($$3)) continue;
            $$2 = (BlockState)$$2.setValue(MultifaceBlock.getFaceProperty((Direction)$$1.apply((Object)$$3)), $$0.getValue(MultifaceBlock.getFaceProperty($$3)));
        }
        return $$2;
    }

    public static boolean hasFace(BlockState $$0, Direction $$1) {
        BooleanProperty $$2 = MultifaceBlock.getFaceProperty($$1);
        return $$0.hasProperty($$2) && $$0.getValue($$2) != false;
    }

    public static boolean canAttachTo(BlockGetter $$0, Direction $$1, BlockPos $$2, BlockState $$3) {
        return Block.isFaceFull($$3.getBlockSupportShape($$0, $$2), $$1.getOpposite()) || Block.isFaceFull($$3.getCollisionShape($$0, $$2), $$1.getOpposite());
    }

    private boolean isWaterloggable() {
        return this.stateDefinition.getProperties().contains((Object)BlockStateProperties.WATERLOGGED);
    }

    private static BlockState removeFace(BlockState $$0, BooleanProperty $$1) {
        BlockState $$2 = (BlockState)$$0.setValue($$1, false);
        if (MultifaceBlock.hasAnyFace($$2)) {
            return $$2;
        }
        return Blocks.AIR.defaultBlockState();
    }

    public static BooleanProperty getFaceProperty(Direction $$0) {
        return (BooleanProperty)PROPERTY_BY_DIRECTION.get((Object)$$0);
    }

    private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> $$0) {
        BlockState $$1 = $$0.any();
        for (BooleanProperty $$2 : PROPERTY_BY_DIRECTION.values()) {
            if (!$$1.hasProperty($$2)) continue;
            $$1 = (BlockState)$$1.setValue($$2, false);
        }
        return $$1;
    }

    private static VoxelShape calculateMultifaceShape(BlockState $$0) {
        VoxelShape $$1 = Shapes.empty();
        for (Direction $$2 : DIRECTIONS) {
            if (!MultifaceBlock.hasFace($$0, $$2)) continue;
            $$1 = Shapes.or($$1, (VoxelShape)SHAPE_BY_DIRECTION.get((Object)$$2));
        }
        return $$1.isEmpty() ? Shapes.block() : $$1;
    }

    protected static boolean hasAnyFace(BlockState $$0) {
        return Arrays.stream((Object[])DIRECTIONS).anyMatch($$1 -> MultifaceBlock.hasFace($$0, $$1));
    }

    private static boolean hasAnyVacantFace(BlockState $$0) {
        return Arrays.stream((Object[])DIRECTIONS).anyMatch($$1 -> !MultifaceBlock.hasFace($$0, $$1));
    }

    public abstract MultifaceSpreader getSpreader();
}