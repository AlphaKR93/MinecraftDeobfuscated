/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.BiPredicate
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.server.level.ServerLevel
 */
package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PointedDripstoneBlock
extends Block
implements Fallable,
SimpleWaterloggedBlock {
    public static final DirectionProperty TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final int MAX_SEARCH_LENGTH_WHEN_CHECKING_DRIP_TYPE = 11;
    private static final int DELAY_BEFORE_FALLING = 2;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02f;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE = 0.12f;
    private static final int MAX_SEARCH_LENGTH_BETWEEN_STALACTITE_TIP_AND_CAULDRON = 11;
    private static final float WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.17578125f;
    private static final float LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.05859375f;
    private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_DRIPSTONE = 0.6;
    private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0f;
    private static final int STALACTITE_MAX_DAMAGE = 40;
    private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
    private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.0f;
    private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
    private static final float AVERAGE_DAYS_PER_GROWTH = 5.0f;
    private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778f;
    private static final int MAX_GROWTH_LENGTH = 7;
    private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
    private static final float STALACTITE_DRIP_START_PIXEL = 0.6875f;
    private static final VoxelShape TIP_MERGE_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    private static final VoxelShape TIP_SHAPE_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
    private static final VoxelShape TIP_SHAPE_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
    private static final VoxelShape FRUSTUM_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape MIDDLE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    private static final float MAX_HORIZONTAL_OFFSET = 0.125f;
    private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    public PointedDripstoneBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(TIP_DIRECTION, Direction.UP)).setValue(THICKNESS, DripstoneThickness.TIP)).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return PointedDripstoneBlock.isValidPointedDripstonePlacement($$1, $$2, $$0.getValue(TIP_DIRECTION));
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$1 != Direction.UP && $$1 != Direction.DOWN) {
            return $$0;
        }
        Direction $$6 = $$0.getValue(TIP_DIRECTION);
        if ($$6 == Direction.DOWN && $$3.getBlockTicks().hasScheduledTick($$4, this)) {
            return $$0;
        }
        if ($$1 == $$6.getOpposite() && !this.canSurvive($$0, $$3, $$4)) {
            if ($$6 == Direction.DOWN) {
                $$3.scheduleTick($$4, this, 2);
            } else {
                $$3.scheduleTick($$4, this, 1);
            }
            return $$0;
        }
        boolean $$7 = $$0.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
        DripstoneThickness $$8 = PointedDripstoneBlock.calculateDripstoneThickness($$3, $$4, $$6, $$7);
        return (BlockState)$$0.setValue(THICKNESS, $$8);
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        BlockPos $$4 = $$2.getBlockPos();
        if (!$$0.isClientSide && $$3.mayInteract($$0, $$4) && $$3 instanceof ThrownTrident && $$3.getDeltaMovement().length() > 0.6) {
            $$0.destroyBlock($$4, true);
        }
    }

    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, float $$4) {
        if ($$1.getValue(TIP_DIRECTION) == Direction.UP && $$1.getValue(THICKNESS) == DripstoneThickness.TIP) {
            $$3.causeFallDamage($$4 + 2.0f, 2.0f, DamageSource.STALAGMITE);
        } else {
            super.fallOn($$0, $$1, $$2, $$3, $$4);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$12, BlockPos $$2, RandomSource $$32) {
        if (!PointedDripstoneBlock.canDrip($$0)) {
            return;
        }
        float $$4 = $$32.nextFloat();
        if ($$4 > 0.12f) {
            return;
        }
        PointedDripstoneBlock.getFluidAboveStalactite($$12, $$2, $$0).filter($$1 -> $$4 < 0.02f || PointedDripstoneBlock.canFillCauldron($$1.fluid)).ifPresent($$3 -> PointedDripstoneBlock.spawnDripParticle($$12, $$2, $$0, $$3.fluid));
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (PointedDripstoneBlock.isStalagmite($$0) && !this.canSurvive($$0, (LevelReader)$$1, $$2)) {
            $$1.destroyBlock($$2, true);
        } else {
            PointedDripstoneBlock.spawnFallingStalactite($$0, $$1, $$2);
        }
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        PointedDripstoneBlock.maybeTransferFluid($$0, $$1, $$2, $$3.nextFloat());
        if ($$3.nextFloat() < 0.011377778f && PointedDripstoneBlock.isStalactiteStartPos($$0, (LevelReader)$$1, $$2)) {
            PointedDripstoneBlock.growStalactiteOrStalagmiteIfPossible($$0, $$1, $$2, $$3);
        }
    }

    /*
     * WARNING - void declaration
     */
    @VisibleForTesting
    public static void maybeTransferFluid(BlockState $$0, ServerLevel $$1, BlockPos $$2, float $$3) {
        void $$8;
        if ($$3 > 0.17578125f && $$3 > 0.05859375f) {
            return;
        }
        if (!PointedDripstoneBlock.isStalactiteStartPos($$0, (LevelReader)$$1, $$2)) {
            return;
        }
        Optional<FluidInfo> $$4 = PointedDripstoneBlock.getFluidAboveStalactite((Level)$$1, $$2, $$0);
        if ($$4.isEmpty()) {
            return;
        }
        Fluid $$5 = ((FluidInfo)((Object)$$4.get())).fluid;
        if ($$5 == Fluids.WATER) {
            float $$6 = 0.17578125f;
        } else if ($$5 == Fluids.LAVA) {
            float $$7 = 0.05859375f;
        } else {
            return;
        }
        if ($$3 >= $$8) {
            return;
        }
        BlockPos $$9 = PointedDripstoneBlock.findTip($$0, (LevelAccessor)$$1, $$2, 11, false);
        if ($$9 == null) {
            return;
        }
        if (((FluidInfo)((Object)$$4.get())).sourceState.is(Blocks.MUD) && $$5 == Fluids.WATER) {
            BlockState $$10 = Blocks.CLAY.defaultBlockState();
            $$1.setBlockAndUpdate(((FluidInfo)((Object)$$4.get())).pos, $$10);
            Block.pushEntitiesUp(((FluidInfo)((Object)$$4.get())).sourceState, $$10, (LevelAccessor)$$1, ((FluidInfo)((Object)$$4.get())).pos);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, ((FluidInfo)((Object)$$4.get())).pos, GameEvent.Context.of($$10));
            $$1.levelEvent(1504, $$9, 0);
            return;
        }
        BlockPos $$11 = PointedDripstoneBlock.findFillableCauldronBelowStalactiteTip((Level)$$1, $$9, $$5);
        if ($$11 == null) {
            return;
        }
        $$1.levelEvent(1504, $$9, 0);
        int $$12 = $$9.getY() - $$11.getY();
        int $$13 = 50 + $$12;
        BlockState $$14 = $$1.getBlockState($$11);
        $$1.scheduleTick($$11, $$14.getBlock(), $$13);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$3;
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        Direction $$4 = PointedDripstoneBlock.calculateTipDirection($$1, $$2 = $$0.getClickedPos(), $$3 = $$0.getNearestLookingVerticalDirection().getOpposite());
        if ($$4 == null) {
            return null;
        }
        boolean $$5 = !$$0.isSecondaryUseActive();
        DripstoneThickness $$6 = PointedDripstoneBlock.calculateDripstoneThickness($$1, $$2, $$4, $$5);
        if ($$6 == null) {
            return null;
        }
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TIP_DIRECTION, $$4)).setValue(THICKNESS, $$6)).setValue(WATERLOGGED, $$1.getFluidState($$2).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        VoxelShape $$10;
        DripstoneThickness $$4 = $$0.getValue(THICKNESS);
        if ($$4 == DripstoneThickness.TIP_MERGE) {
            VoxelShape $$5 = TIP_MERGE_SHAPE;
        } else if ($$4 == DripstoneThickness.TIP) {
            if ($$0.getValue(TIP_DIRECTION) == Direction.DOWN) {
                VoxelShape $$6 = TIP_SHAPE_DOWN;
            } else {
                VoxelShape $$7 = TIP_SHAPE_UP;
            }
        } else if ($$4 == DripstoneThickness.FRUSTUM) {
            VoxelShape $$8 = FRUSTUM_SHAPE;
        } else if ($$4 == DripstoneThickness.MIDDLE) {
            VoxelShape $$9 = MIDDLE_SHAPE;
        } else {
            $$10 = BASE_SHAPE;
        }
        Vec3 $$11 = $$0.getOffset($$1, $$2);
        return $$10.move($$11.x, 0.0, $$11.z);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return false;
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.125f;
    }

    @Override
    public void onBrokenAfterFall(Level $$0, BlockPos $$1, FallingBlockEntity $$2) {
        if (!$$2.isSilent()) {
            $$0.levelEvent(1045, $$1, 0);
        }
    }

    @Override
    public DamageSource getFallDamageSource(Entity $$0) {
        return DamageSource.fallingStalactite($$0);
    }

    @Override
    public Predicate<Entity> getHurtsEntitySelector() {
        return EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
    }

    private static void spawnFallingStalactite(BlockState $$0, ServerLevel $$1, BlockPos $$2) {
        BlockPos.MutableBlockPos $$3 = $$2.mutable();
        BlockState $$4 = $$0;
        while (PointedDripstoneBlock.isStalactite($$4)) {
            FallingBlockEntity $$5 = FallingBlockEntity.fall((Level)$$1, $$3, $$4);
            if (PointedDripstoneBlock.isTip($$4, true)) {
                int $$6 = Math.max((int)(1 + $$2.getY() - $$3.getY()), (int)6);
                float $$7 = 1.0f * (float)$$6;
                $$5.setHurtsEntities($$7, 40);
                break;
            }
            $$3.move(Direction.DOWN);
            $$4 = $$1.getBlockState((BlockPos)$$3);
        }
    }

    @VisibleForTesting
    public static void growStalactiteOrStalagmiteIfPossible(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockState $$5;
        BlockState $$4 = $$1.getBlockState((BlockPos)$$2.above(1));
        if (!PointedDripstoneBlock.canGrow($$4, $$5 = $$1.getBlockState((BlockPos)$$2.above(2)))) {
            return;
        }
        BlockPos $$6 = PointedDripstoneBlock.findTip($$0, (LevelAccessor)$$1, $$2, 7, false);
        if ($$6 == null) {
            return;
        }
        BlockState $$7 = $$1.getBlockState($$6);
        if (!PointedDripstoneBlock.canDrip($$7) || !PointedDripstoneBlock.canTipGrow($$7, $$1, $$6)) {
            return;
        }
        if ($$3.nextBoolean()) {
            PointedDripstoneBlock.grow($$1, $$6, Direction.DOWN);
        } else {
            PointedDripstoneBlock.growStalagmiteBelow($$1, $$6);
        }
    }

    private static void growStalagmiteBelow(ServerLevel $$0, BlockPos $$1) {
        BlockPos.MutableBlockPos $$2 = $$1.mutable();
        for (int $$3 = 0; $$3 < 10; ++$$3) {
            $$2.move(Direction.DOWN);
            BlockState $$4 = $$0.getBlockState((BlockPos)$$2);
            if (!$$4.getFluidState().isEmpty()) {
                return;
            }
            if (PointedDripstoneBlock.isUnmergedTipWithDirection($$4, Direction.UP) && PointedDripstoneBlock.canTipGrow($$4, $$0, $$2)) {
                PointedDripstoneBlock.grow($$0, $$2, Direction.UP);
                return;
            }
            if (PointedDripstoneBlock.isValidPointedDripstonePlacement((LevelReader)$$0, $$2, Direction.UP) && !$$0.isWaterAt((BlockPos)$$2.below())) {
                PointedDripstoneBlock.grow($$0, (BlockPos)$$2.below(), Direction.UP);
                return;
            }
            if (PointedDripstoneBlock.canDripThrough((BlockGetter)$$0, $$2, $$4)) continue;
            return;
        }
    }

    private static void grow(ServerLevel $$0, BlockPos $$1, Direction $$2) {
        Vec3i $$3 = $$1.relative($$2);
        BlockState $$4 = $$0.getBlockState((BlockPos)$$3);
        if (PointedDripstoneBlock.isUnmergedTipWithDirection($$4, $$2.getOpposite())) {
            PointedDripstoneBlock.createMergedTips($$4, (LevelAccessor)$$0, (BlockPos)$$3);
        } else if ($$4.isAir() || $$4.is(Blocks.WATER)) {
            PointedDripstoneBlock.createDripstone((LevelAccessor)$$0, (BlockPos)$$3, $$2, DripstoneThickness.TIP);
        }
    }

    private static void createDripstone(LevelAccessor $$0, BlockPos $$1, Direction $$2, DripstoneThickness $$3) {
        BlockState $$4 = (BlockState)((BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(TIP_DIRECTION, $$2)).setValue(THICKNESS, $$3)).setValue(WATERLOGGED, $$0.getFluidState($$1).getType() == Fluids.WATER);
        $$0.setBlock($$1, $$4, 3);
    }

    private static void createMergedTips(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        Vec3i $$6;
        BlockPos $$5;
        if ($$0.getValue(TIP_DIRECTION) == Direction.UP) {
            BlockPos $$3 = $$2;
            Vec3i $$4 = $$2.above();
        } else {
            $$5 = $$2;
            $$6 = $$2.below();
        }
        PointedDripstoneBlock.createDripstone($$1, $$5, Direction.DOWN, DripstoneThickness.TIP_MERGE);
        PointedDripstoneBlock.createDripstone($$1, (BlockPos)$$6, Direction.UP, DripstoneThickness.TIP_MERGE);
    }

    public static void spawnDripParticle(Level $$0, BlockPos $$1, BlockState $$2) {
        PointedDripstoneBlock.getFluidAboveStalactite($$0, $$1, $$2).ifPresent($$3 -> PointedDripstoneBlock.spawnDripParticle($$0, $$1, $$2, $$3.fluid));
    }

    private static void spawnDripParticle(Level $$0, BlockPos $$1, BlockState $$2, Fluid $$3) {
        Vec3 $$4 = $$2.getOffset($$0, $$1);
        double $$5 = 0.0625;
        double $$6 = (double)$$1.getX() + 0.5 + $$4.x;
        double $$7 = (double)((float)($$1.getY() + 1) - 0.6875f) - 0.0625;
        double $$8 = (double)$$1.getZ() + 0.5 + $$4.z;
        Fluid $$9 = PointedDripstoneBlock.getDripFluid($$0, $$3);
        SimpleParticleType $$10 = $$9.is(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
        $$0.addParticle($$10, $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Nullable
    private static BlockPos findTip(BlockState $$0, LevelAccessor $$12, BlockPos $$22, int $$3, boolean $$4) {
        if (PointedDripstoneBlock.isTip($$0, $$4)) {
            return $$22;
        }
        Direction $$5 = $$0.getValue(TIP_DIRECTION);
        BiPredicate $$6 = ($$1, $$2) -> $$2.is(Blocks.POINTED_DRIPSTONE) && $$2.getValue(TIP_DIRECTION) == $$5;
        return (BlockPos)PointedDripstoneBlock.findBlockVertical($$12, $$22, $$5.getAxisDirection(), (BiPredicate<BlockPos, BlockState>)$$6, (Predicate<BlockState>)((Predicate)$$1 -> PointedDripstoneBlock.isTip($$1, $$4)), $$3).orElse(null);
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static Direction calculateTipDirection(LevelReader $$0, BlockPos $$1, Direction $$2) {
        void $$5;
        if (PointedDripstoneBlock.isValidPointedDripstonePlacement($$0, $$1, $$2)) {
            Direction $$3 = $$2;
        } else if (PointedDripstoneBlock.isValidPointedDripstonePlacement($$0, $$1, $$2.getOpposite())) {
            Direction $$4 = $$2.getOpposite();
        } else {
            return null;
        }
        return $$5;
    }

    private static DripstoneThickness calculateDripstoneThickness(LevelReader $$0, BlockPos $$1, Direction $$2, boolean $$3) {
        Direction $$4 = $$2.getOpposite();
        BlockState $$5 = $$0.getBlockState((BlockPos)$$1.relative($$2));
        if (PointedDripstoneBlock.isPointedDripstoneWithDirection($$5, $$4)) {
            if ($$3 || $$5.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE) {
                return DripstoneThickness.TIP_MERGE;
            }
            return DripstoneThickness.TIP;
        }
        if (!PointedDripstoneBlock.isPointedDripstoneWithDirection($$5, $$2)) {
            return DripstoneThickness.TIP;
        }
        DripstoneThickness $$6 = $$5.getValue(THICKNESS);
        if ($$6 == DripstoneThickness.TIP || $$6 == DripstoneThickness.TIP_MERGE) {
            return DripstoneThickness.FRUSTUM;
        }
        BlockState $$7 = $$0.getBlockState((BlockPos)$$1.relative($$4));
        if (!PointedDripstoneBlock.isPointedDripstoneWithDirection($$7, $$2)) {
            return DripstoneThickness.BASE;
        }
        return DripstoneThickness.MIDDLE;
    }

    public static boolean canDrip(BlockState $$0) {
        return PointedDripstoneBlock.isStalactite($$0) && $$0.getValue(THICKNESS) == DripstoneThickness.TIP && $$0.getValue(WATERLOGGED) == false;
    }

    private static boolean canTipGrow(BlockState $$0, ServerLevel $$1, BlockPos $$2) {
        Direction $$3 = $$0.getValue(TIP_DIRECTION);
        Vec3i $$4 = $$2.relative($$3);
        BlockState $$5 = $$1.getBlockState((BlockPos)$$4);
        if (!$$5.getFluidState().isEmpty()) {
            return false;
        }
        if ($$5.isAir()) {
            return true;
        }
        return PointedDripstoneBlock.isUnmergedTipWithDirection($$5, $$3.getOpposite());
    }

    private static Optional<BlockPos> findRootBlock(Level $$02, BlockPos $$12, BlockState $$22, int $$3) {
        Direction $$4 = $$22.getValue(TIP_DIRECTION);
        BiPredicate $$5 = ($$1, $$2) -> $$2.is(Blocks.POINTED_DRIPSTONE) && $$2.getValue(TIP_DIRECTION) == $$4;
        return PointedDripstoneBlock.findBlockVertical($$02, $$12, $$4.getOpposite().getAxisDirection(), (BiPredicate<BlockPos, BlockState>)$$5, (Predicate<BlockState>)((Predicate)$$0 -> !$$0.is(Blocks.POINTED_DRIPSTONE)), $$3);
    }

    private static boolean isValidPointedDripstonePlacement(LevelReader $$0, BlockPos $$1, Direction $$2) {
        Vec3i $$3 = $$1.relative($$2.getOpposite());
        BlockState $$4 = $$0.getBlockState((BlockPos)$$3);
        return $$4.isFaceSturdy($$0, (BlockPos)$$3, $$2) || PointedDripstoneBlock.isPointedDripstoneWithDirection($$4, $$2);
    }

    private static boolean isTip(BlockState $$0, boolean $$1) {
        if (!$$0.is(Blocks.POINTED_DRIPSTONE)) {
            return false;
        }
        DripstoneThickness $$2 = $$0.getValue(THICKNESS);
        return $$2 == DripstoneThickness.TIP || $$1 && $$2 == DripstoneThickness.TIP_MERGE;
    }

    private static boolean isUnmergedTipWithDirection(BlockState $$0, Direction $$1) {
        return PointedDripstoneBlock.isTip($$0, false) && $$0.getValue(TIP_DIRECTION) == $$1;
    }

    private static boolean isStalactite(BlockState $$0) {
        return PointedDripstoneBlock.isPointedDripstoneWithDirection($$0, Direction.DOWN);
    }

    private static boolean isStalagmite(BlockState $$0) {
        return PointedDripstoneBlock.isPointedDripstoneWithDirection($$0, Direction.UP);
    }

    private static boolean isStalactiteStartPos(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return PointedDripstoneBlock.isStalactite($$0) && !$$1.getBlockState((BlockPos)$$2.above()).is(Blocks.POINTED_DRIPSTONE);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    private static boolean isPointedDripstoneWithDirection(BlockState $$0, Direction $$1) {
        return $$0.is(Blocks.POINTED_DRIPSTONE) && $$0.getValue(TIP_DIRECTION) == $$1;
    }

    @Nullable
    private static BlockPos findFillableCauldronBelowStalactiteTip(Level $$0, BlockPos $$12, Fluid $$22) {
        Predicate $$3 = $$1 -> $$1.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock)$$1.getBlock()).canReceiveStalactiteDrip($$22);
        BiPredicate $$4 = ($$1, $$2) -> PointedDripstoneBlock.canDripThrough($$0, $$1, $$2);
        return (BlockPos)PointedDripstoneBlock.findBlockVertical($$0, $$12, Direction.DOWN.getAxisDirection(), (BiPredicate<BlockPos, BlockState>)$$4, (Predicate<BlockState>)$$3, 11).orElse(null);
    }

    @Nullable
    public static BlockPos findStalactiteTipAboveCauldron(Level $$0, BlockPos $$12) {
        BiPredicate $$22 = ($$1, $$2) -> PointedDripstoneBlock.canDripThrough($$0, $$1, $$2);
        return (BlockPos)PointedDripstoneBlock.findBlockVertical($$0, $$12, Direction.UP.getAxisDirection(), (BiPredicate<BlockPos, BlockState>)$$22, (Predicate<BlockState>)((Predicate)PointedDripstoneBlock::canDrip), 11).orElse(null);
    }

    public static Fluid getCauldronFillFluidType(ServerLevel $$02, BlockPos $$1) {
        return (Fluid)PointedDripstoneBlock.getFluidAboveStalactite((Level)$$02, $$1, $$02.getBlockState($$1)).map($$0 -> $$0.fluid).filter(PointedDripstoneBlock::canFillCauldron).orElse((Object)Fluids.EMPTY);
    }

    private static Optional<FluidInfo> getFluidAboveStalactite(Level $$0, BlockPos $$12, BlockState $$2) {
        if (!PointedDripstoneBlock.isStalactite($$2)) {
            return Optional.empty();
        }
        return PointedDripstoneBlock.findRootBlock($$0, $$12, $$2, 11).map($$1 -> {
            Fluid $$5;
            Vec3i $$2 = $$1.above();
            BlockState $$3 = $$0.getBlockState((BlockPos)$$2);
            if ($$3.is(Blocks.MUD) && !$$0.dimensionType().ultraWarm()) {
                FlowingFluid $$4 = Fluids.WATER;
            } else {
                $$5 = $$0.getFluidState((BlockPos)$$2).getType();
            }
            return new FluidInfo((BlockPos)$$2, $$5, $$3);
        });
    }

    private static boolean canFillCauldron(Fluid $$0) {
        return $$0 == Fluids.LAVA || $$0 == Fluids.WATER;
    }

    private static boolean canGrow(BlockState $$0, BlockState $$1) {
        return $$0.is(Blocks.DRIPSTONE_BLOCK) && $$1.is(Blocks.WATER) && $$1.getFluidState().isSource();
    }

    private static Fluid getDripFluid(Level $$0, Fluid $$1) {
        if ($$1.isSame(Fluids.EMPTY)) {
            return $$0.dimensionType().ultraWarm() ? Fluids.LAVA : Fluids.WATER;
        }
        return $$1;
    }

    private static Optional<BlockPos> findBlockVertical(LevelAccessor $$0, BlockPos $$1, Direction.AxisDirection $$2, BiPredicate<BlockPos, BlockState> $$3, Predicate<BlockState> $$4, int $$5) {
        Direction $$6 = Direction.get($$2, Direction.Axis.Y);
        BlockPos.MutableBlockPos $$7 = $$1.mutable();
        for (int $$8 = 1; $$8 < $$5; ++$$8) {
            $$7.move($$6);
            BlockState $$9 = $$0.getBlockState($$7);
            if ($$4.test((Object)$$9)) {
                return Optional.of((Object)$$7.immutable());
            }
            if (!$$0.isOutsideBuildHeight($$7.getY()) && $$3.test((Object)$$7, (Object)$$9)) continue;
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static boolean canDripThrough(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        if ($$2.isAir()) {
            return true;
        }
        if ($$2.isSolidRender($$0, $$1)) {
            return false;
        }
        if (!$$2.getFluidState().isEmpty()) {
            return false;
        }
        VoxelShape $$3 = $$2.getCollisionShape($$0, $$1);
        return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, $$3, BooleanOp.AND);
    }

    record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
    }
}