/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BigDripleafStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigDripleafBlock
extends HorizontalDirectionalBlock
implements BonemealableBlock,
SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final EnumProperty<Tilt> TILT = BlockStateProperties.TILT;
    private static final int NO_TICK = -1;
    private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE = (Object2IntMap)Util.make(new Object2IntArrayMap(), $$0 -> {
        $$0.defaultReturnValue(-1);
        $$0.put((Object)Tilt.UNSTABLE, 10);
        $$0.put((Object)Tilt.PARTIAL, 10);
        $$0.put((Object)Tilt.FULL, 100);
    });
    private static final int MAX_GEN_HEIGHT = 5;
    private static final int STEM_WIDTH = 6;
    private static final int ENTITY_DETECTION_MIN_Y = 11;
    private static final int LOWEST_LEAF_TOP = 13;
    private static final Map<Tilt, VoxelShape> LEAF_SHAPES = ImmutableMap.of((Object)Tilt.NONE, (Object)Block.box(0.0, 11.0, 0.0, 16.0, 15.0, 16.0), (Object)Tilt.UNSTABLE, (Object)Block.box(0.0, 11.0, 0.0, 16.0, 15.0, 16.0), (Object)Tilt.PARTIAL, (Object)Block.box(0.0, 11.0, 0.0, 16.0, 13.0, 16.0), (Object)Tilt.FULL, (Object)Shapes.empty());
    private static final VoxelShape STEM_SLICER = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, VoxelShape> STEM_SHAPES = ImmutableMap.of((Object)Direction.NORTH, (Object)Shapes.joinUnoptimized(BigDripleafStemBlock.NORTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), (Object)Direction.SOUTH, (Object)Shapes.joinUnoptimized(BigDripleafStemBlock.SOUTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), (Object)Direction.EAST, (Object)Shapes.joinUnoptimized(BigDripleafStemBlock.EAST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), (Object)Direction.WEST, (Object)Shapes.joinUnoptimized(BigDripleafStemBlock.WEST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST));
    private final Map<BlockState, VoxelShape> shapesCache;

    protected BigDripleafBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH)).setValue(TILT, Tilt.NONE));
        this.shapesCache = this.getShapeForEachState((Function<BlockState, VoxelShape>)((Function)BigDripleafBlock::calculateShape));
    }

    private static VoxelShape calculateShape(BlockState $$0) {
        return Shapes.or((VoxelShape)LEAF_SHAPES.get((Object)$$0.getValue(TILT)), (VoxelShape)STEM_SHAPES.get((Object)$$0.getValue(FACING)));
    }

    public static void placeWithRandomHeight(LevelAccessor $$0, RandomSource $$1, BlockPos $$2, Direction $$3) {
        int $$6;
        int $$4 = Mth.nextInt($$1, 2, 5);
        BlockPos.MutableBlockPos $$5 = $$2.mutable();
        for ($$6 = 0; $$6 < $$4 && BigDripleafBlock.canPlaceAt($$0, $$5, $$0.getBlockState($$5)); ++$$6) {
            $$5.move(Direction.UP);
        }
        int $$7 = $$2.getY() + $$6 - 1;
        $$5.setY($$2.getY());
        while ($$5.getY() < $$7) {
            BigDripleafStemBlock.place($$0, $$5, $$0.getFluidState($$5), $$3);
            $$5.move(Direction.UP);
        }
        BigDripleafBlock.place($$0, $$5, $$0.getFluidState($$5), $$3);
    }

    private static boolean canReplace(BlockState $$0) {
        return $$0.isAir() || $$0.is(Blocks.WATER) || $$0.is(Blocks.SMALL_DRIPLEAF);
    }

    protected static boolean canPlaceAt(LevelHeightAccessor $$0, BlockPos $$1, BlockState $$2) {
        return !$$0.isOutsideBuildHeight($$1) && BigDripleafBlock.canReplace($$2);
    }

    protected static boolean place(LevelAccessor $$0, BlockPos $$1, FluidState $$2, Direction $$3) {
        BlockState $$4 = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(WATERLOGGED, $$2.isSourceOfType(Fluids.WATER))).setValue(FACING, $$3);
        return $$0.setBlock($$1, $$4, 3);
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        this.setTiltAndScheduleTick($$1, $$0, $$2.getBlockPos(), Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Vec3i $$3 = $$2.below();
        BlockState $$4 = $$1.getBlockState((BlockPos)$$3);
        return $$4.is(this) || $$4.is(Blocks.BIG_DRIPLEAF_STEM) || $$4.is(BlockTags.BIG_DRIPLEAF_PLACEABLE);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.DOWN && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$1 == Direction.UP && $$2.is(this)) {
            return Blocks.BIG_DRIPLEAF_STEM.withPropertiesOf($$0);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        BlockState $$4 = $$0.getBlockState((BlockPos)$$1.above());
        return BigDripleafBlock.canReplace($$4);
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        BlockState $$5;
        Vec3i $$4 = $$2.above();
        if (BigDripleafBlock.canPlaceAt($$0, (BlockPos)$$4, $$5 = $$0.getBlockState((BlockPos)$$4))) {
            Direction $$6 = $$3.getValue(FACING);
            BigDripleafStemBlock.place($$0, $$2, $$3.getFluidState(), $$6);
            BigDripleafBlock.place($$0, (BlockPos)$$4, $$5.getFluidState(), $$6);
        }
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$1.isClientSide) {
            return;
        }
        if ($$0.getValue(TILT) == Tilt.NONE && BigDripleafBlock.canEntityTilt($$2, $$3) && !$$1.hasNeighborSignal($$2)) {
            this.setTiltAndScheduleTick($$0, $$1, $$2, Tilt.UNSTABLE, null);
        }
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.hasNeighborSignal($$2)) {
            BigDripleafBlock.resetTilt($$0, $$1, $$2);
            return;
        }
        Tilt $$4 = $$0.getValue(TILT);
        if ($$4 == Tilt.UNSTABLE) {
            this.setTiltAndScheduleTick($$0, $$1, $$2, Tilt.PARTIAL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
        } else if ($$4 == Tilt.PARTIAL) {
            this.setTiltAndScheduleTick($$0, $$1, $$2, Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
        } else if ($$4 == Tilt.FULL) {
            BigDripleafBlock.resetTilt($$0, $$1, $$2);
        }
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if ($$1.hasNeighborSignal($$2)) {
            BigDripleafBlock.resetTilt($$0, $$1, $$2);
        }
    }

    private static void playTiltSound(Level $$0, BlockPos $$1, SoundEvent $$2) {
        float $$3 = Mth.randomBetween($$0.random, 0.8f, 1.2f);
        $$0.playSound(null, $$1, $$2, SoundSource.BLOCKS, 1.0f, $$3);
    }

    private static boolean canEntityTilt(BlockPos $$0, Entity $$1) {
        return $$1.isOnGround() && $$1.position().y > (double)((float)$$0.getY() + 0.6875f);
    }

    private void setTiltAndScheduleTick(BlockState $$0, Level $$1, BlockPos $$2, Tilt $$3, @Nullable SoundEvent $$4) {
        int $$5;
        BigDripleafBlock.setTilt($$0, $$1, $$2, $$3);
        if ($$4 != null) {
            BigDripleafBlock.playTiltSound($$1, $$2, $$4);
        }
        if (($$5 = DELAY_UNTIL_NEXT_TILT_STATE.getInt((Object)$$3)) != -1) {
            $$1.scheduleTick($$2, this, $$5);
        }
    }

    private static void resetTilt(BlockState $$0, Level $$1, BlockPos $$2) {
        BigDripleafBlock.setTilt($$0, $$1, $$2, Tilt.NONE);
        if ($$0.getValue(TILT) != Tilt.NONE) {
            BigDripleafBlock.playTiltSound($$1, $$2, SoundEvents.BIG_DRIPLEAF_TILT_UP);
        }
    }

    private static void setTilt(BlockState $$0, Level $$1, BlockPos $$2, Tilt $$3) {
        Tilt $$4 = $$0.getValue(TILT);
        $$1.setBlock($$2, (BlockState)$$0.setValue(TILT, $$3), 2);
        if ($$3.causesVibration() && $$3 != $$4) {
            $$1.gameEvent(null, GameEvent.BLOCK_CHANGE, $$2);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)LEAF_SHAPES.get((Object)$$0.getValue(TILT));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)this.shapesCache.get((Object)$$0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState((BlockPos)$$0.getClickedPos().below());
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$3 = $$1.is(Blocks.BIG_DRIPLEAF) || $$1.is(Blocks.BIG_DRIPLEAF_STEM);
        return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$2.isSourceOfType(Fluids.WATER))).setValue(FACING, $$3 ? $$1.getValue(FACING) : $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(WATERLOGGED, FACING, TILT);
    }
}