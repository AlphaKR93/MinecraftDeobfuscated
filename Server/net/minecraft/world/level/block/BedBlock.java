/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;

public class BedBlock
extends HorizontalDirectionalBlock
implements EntityBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    protected static final int HEIGHT = 9;
    protected static final VoxelShape BASE = Block.box(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
    private static final int LEG_WIDTH = 3;
    protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
    protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
    protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
    protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
    protected static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
    protected static final VoxelShape WEST_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
    protected static final VoxelShape EAST_SHAPE = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
    private final DyeColor color;

    public BedBlock(DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
    }

    @Nullable
    public static Direction getBedOrientation(BlockGetter $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        return $$2.getBlock() instanceof BedBlock ? $$2.getValue(FACING) : null;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$12, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$12.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if ($$0.getValue(PART) != BedPart.HEAD && !($$0 = $$12.getBlockState((BlockPos)($$2 = ((BlockPos)$$2).relative($$0.getValue(FACING))))).is(this)) {
            return InteractionResult.CONSUME;
        }
        if (!BedBlock.canSetSpawn($$12)) {
            $$12.removeBlock((BlockPos)$$2, false);
            Vec3i $$6 = ((BlockPos)$$2).relative($$0.getValue(FACING).getOpposite());
            if ($$12.getBlockState((BlockPos)$$6).is(this)) {
                $$12.removeBlock((BlockPos)$$6, false);
            }
            Vec3 $$7 = ((BlockPos)$$2).getCenter();
            $$12.explode(null, DamageSource.badRespawnPointExplosion($$7), null, $$7, 5.0f, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS;
        }
        if ($$0.getValue(OCCUPIED).booleanValue()) {
            if (!this.kickVillagerOutOfBed($$12, (BlockPos)$$2)) {
                $$3.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }
            return InteractionResult.SUCCESS;
        }
        $$3.startSleepInBed((BlockPos)$$2).ifLeft($$1 -> {
            if ($$1.getMessage() != null) {
                $$3.displayClientMessage($$1.getMessage(), true);
            }
        });
        return InteractionResult.SUCCESS;
    }

    public static boolean canSetSpawn(Level $$0) {
        return $$0.dimensionType().bedWorks();
    }

    private boolean kickVillagerOutOfBed(Level $$0, BlockPos $$1) {
        List $$2 = $$0.getEntitiesOfClass(Villager.class, new AABB($$1), LivingEntity::isSleeping);
        if ($$2.isEmpty()) {
            return false;
        }
        ((Villager)$$2.get(0)).stopSleeping();
        return true;
    }

    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, float $$4) {
        super.fallOn($$0, $$1, $$2, $$3, $$4 * 0.5f);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter $$0, Entity $$1) {
        if ($$1.isSuppressingBounce()) {
            super.updateEntityAfterFallOn($$0, $$1);
        } else {
            this.bounceUp($$1);
        }
    }

    private void bounceUp(Entity $$0) {
        Vec3 $$1 = $$0.getDeltaMovement();
        if ($$1.y < 0.0) {
            double $$2 = $$0 instanceof LivingEntity ? 1.0 : 0.8;
            $$0.setDeltaMovement($$1.x, -$$1.y * (double)0.66f * $$2, $$1.z);
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == BedBlock.getNeighbourDirection($$0.getValue(PART), $$0.getValue(FACING))) {
            if ($$2.is(this) && $$2.getValue(PART) != $$0.getValue(PART)) {
                return (BlockState)$$0.setValue(OCCUPIED, $$2.getValue(OCCUPIED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private static Direction getNeighbourDirection(BedPart $$0, Direction $$1) {
        return $$0 == BedPart.FOOT ? $$1 : $$1.getOpposite();
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        Vec3i $$5;
        BlockState $$6;
        BedPart $$4;
        if (!$$0.isClientSide && $$3.isCreative() && ($$4 = $$2.getValue(PART)) == BedPart.FOOT && ($$6 = $$0.getBlockState((BlockPos)($$5 = $$1.relative(BedBlock.getNeighbourDirection($$4, $$2.getValue(FACING)))))).is(this) && $$6.getValue(PART) == BedPart.HEAD) {
            $$0.setBlock((BlockPos)$$5, Blocks.AIR.defaultBlockState(), 35);
            $$0.levelEvent($$3, 2001, (BlockPos)$$5, Block.getId($$6));
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$1 = $$0.getHorizontalDirection();
        BlockPos $$2 = $$0.getClickedPos();
        Vec3i $$3 = $$2.relative($$1);
        Level $$4 = $$0.getLevel();
        if ($$4.getBlockState((BlockPos)$$3).canBeReplaced($$0) && $$4.getWorldBorder().isWithinBounds((BlockPos)$$3)) {
            return (BlockState)this.defaultBlockState().setValue(FACING, $$1);
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Direction $$4 = BedBlock.getConnectedDirection($$0).getOpposite();
        switch ($$4) {
            case NORTH: {
                return NORTH_SHAPE;
            }
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
        }
        return EAST_SHAPE;
    }

    public static Direction getConnectedDirection(BlockState $$0) {
        Direction $$1 = $$0.getValue(FACING);
        return $$0.getValue(PART) == BedPart.HEAD ? $$1.getOpposite() : $$1;
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState $$0) {
        BedPart $$1 = $$0.getValue(PART);
        if ($$1 == BedPart.HEAD) {
            return DoubleBlockCombiner.BlockType.FIRST;
        }
        return DoubleBlockCombiner.BlockType.SECOND;
    }

    private static boolean isBunkBed(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState((BlockPos)$$1.below()).getBlock() instanceof BedBlock;
    }

    public static Optional<Vec3> findStandUpPosition(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, Direction $$3, float $$4) {
        Direction $$6;
        Direction $$5 = $$3.getClockWise();
        Direction direction = $$6 = $$5.isFacingAngle($$4) ? $$5.getOpposite() : $$5;
        if (BedBlock.isBunkBed($$1, $$2)) {
            return BedBlock.findBunkBedStandUpPosition($$0, $$1, $$2, $$3, $$6);
        }
        int[][] $$7 = BedBlock.bedStandUpOffsets($$3, $$6);
        Optional<Vec3> $$8 = BedBlock.findStandUpPositionAtOffset($$0, $$1, $$2, $$7, true);
        if ($$8.isPresent()) {
            return $$8;
        }
        return BedBlock.findStandUpPositionAtOffset($$0, $$1, $$2, $$7, false);
    }

    private static Optional<Vec3> findBunkBedStandUpPosition(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, Direction $$3, Direction $$4) {
        int[][] $$5 = BedBlock.bedSurroundStandUpOffsets($$3, $$4);
        Optional<Vec3> $$6 = BedBlock.findStandUpPositionAtOffset($$0, $$1, $$2, $$5, true);
        if ($$6.isPresent()) {
            return $$6;
        }
        Vec3i $$7 = $$2.below();
        Optional<Vec3> $$8 = BedBlock.findStandUpPositionAtOffset($$0, $$1, (BlockPos)$$7, $$5, true);
        if ($$8.isPresent()) {
            return $$8;
        }
        int[][] $$9 = BedBlock.bedAboveStandUpOffsets($$3);
        Optional<Vec3> $$10 = BedBlock.findStandUpPositionAtOffset($$0, $$1, $$2, $$9, true);
        if ($$10.isPresent()) {
            return $$10;
        }
        Optional<Vec3> $$11 = BedBlock.findStandUpPositionAtOffset($$0, $$1, $$2, $$5, false);
        if ($$11.isPresent()) {
            return $$11;
        }
        Optional<Vec3> $$12 = BedBlock.findStandUpPositionAtOffset($$0, $$1, (BlockPos)$$7, $$5, false);
        if ($$12.isPresent()) {
            return $$12;
        }
        return BedBlock.findStandUpPositionAtOffset($$0, $$1, $$2, $$9, false);
    }

    private static Optional<Vec3> findStandUpPositionAtOffset(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, int[][] $$3, boolean $$4) {
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (int[] $$6 : $$3) {
            $$5.set($$2.getX() + $$6[0], $$2.getY(), $$2.getZ() + $$6[1]);
            Vec3 $$7 = DismountHelper.findSafeDismountLocation($$0, $$1, $$5, $$4);
            if ($$7 == null) continue;
            return Optional.of((Object)$$7);
        }
        return Optional.empty();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, PART, OCCUPIED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BedBlockEntity($$0, $$1, this.color);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        if (!$$0.isClientSide) {
            Vec3i $$5 = $$1.relative($$2.getValue(FACING));
            $$0.setBlock((BlockPos)$$5, (BlockState)$$2.setValue(PART, BedPart.HEAD), 3);
            $$0.blockUpdated($$1, Blocks.AIR);
            $$2.updateNeighbourShapes($$0, $$1, 3);
        }
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public long getSeed(BlockState $$0, BlockPos $$1) {
        BlockPos $$2 = $$1.relative($$0.getValue(FACING), $$0.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed($$2.getX(), $$1.getY(), $$2.getZ());
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    private static int[][] bedStandUpOffsets(Direction $$0, Direction $$1) {
        return (int[][])ArrayUtils.addAll((Object[])BedBlock.bedSurroundStandUpOffsets($$0, $$1), (Object[])BedBlock.bedAboveStandUpOffsets($$0));
    }

    private static int[][] bedSurroundStandUpOffsets(Direction $$0, Direction $$1) {
        return new int[][]{{$$1.getStepX(), $$1.getStepZ()}, {$$1.getStepX() - $$0.getStepX(), $$1.getStepZ() - $$0.getStepZ()}, {$$1.getStepX() - $$0.getStepX() * 2, $$1.getStepZ() - $$0.getStepZ() * 2}, {-$$0.getStepX() * 2, -$$0.getStepZ() * 2}, {-$$1.getStepX() - $$0.getStepX() * 2, -$$1.getStepZ() - $$0.getStepZ() * 2}, {-$$1.getStepX() - $$0.getStepX(), -$$1.getStepZ() - $$0.getStepZ()}, {-$$1.getStepX(), -$$1.getStepZ()}, {-$$1.getStepX() + $$0.getStepX(), -$$1.getStepZ() + $$0.getStepZ()}, {$$0.getStepX(), $$0.getStepZ()}, {$$1.getStepX() + $$0.getStepX(), $$1.getStepZ() + $$0.getStepZ()}};
    }

    private static int[][] bedAboveStandUpOffsets(Direction $$0) {
        return new int[][]{{0, 0}, {-$$0.getStepX(), -$$0.getStepZ()}};
    }
}