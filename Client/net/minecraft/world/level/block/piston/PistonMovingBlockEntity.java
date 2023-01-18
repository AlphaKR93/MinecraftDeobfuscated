/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.ThreadLocal
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonMovingBlockEntity
extends BlockEntity {
    private static final int TICKS_TO_EXTEND = 2;
    private static final double PUSH_OFFSET = 0.01;
    public static final double TICK_MOVEMENT = 0.51;
    private BlockState movedState = Blocks.AIR.defaultBlockState();
    private Direction direction;
    private boolean extending;
    private boolean isSourcePiston;
    private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float progressO;
    private long lastTicked;
    private int deathTicks;

    public PistonMovingBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.PISTON, $$0, $$1);
    }

    public PistonMovingBlockEntity(BlockPos $$0, BlockState $$1, BlockState $$2, Direction $$3, boolean $$4, boolean $$5) {
        this($$0, $$1);
        this.movedState = $$2;
        this.direction = $$3;
        this.extending = $$4;
        this.isSourcePiston = $$5;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public boolean isExtending() {
        return this.extending;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isSourcePiston() {
        return this.isSourcePiston;
    }

    public float getProgress(float $$0) {
        if ($$0 > 1.0f) {
            $$0 = 1.0f;
        }
        return Mth.lerp($$0, this.progressO, this.progress);
    }

    public float getXOff(float $$0) {
        return (float)this.direction.getStepX() * this.getExtendedProgress(this.getProgress($$0));
    }

    public float getYOff(float $$0) {
        return (float)this.direction.getStepY() * this.getExtendedProgress(this.getProgress($$0));
    }

    public float getZOff(float $$0) {
        return (float)this.direction.getStepZ() * this.getExtendedProgress(this.getProgress($$0));
    }

    private float getExtendedProgress(float $$0) {
        return this.extending ? $$0 - 1.0f : 1.0f - $$0;
    }

    private BlockState getCollisionRelatedBlockState() {
        if (!this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock) {
            return (BlockState)((BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.SHORT, this.progress > 0.25f)).setValue(PistonHeadBlock.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)).setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING));
        }
        return this.movedState;
    }

    private static void moveCollidedEntities(Level $$0, BlockPos $$1, float $$2, PistonMovingBlockEntity $$3) {
        Direction $$4 = $$3.getMovementDirection();
        double $$5 = $$2 - $$3.progress;
        VoxelShape $$6 = $$3.getCollisionRelatedBlockState().getCollisionShape($$0, $$1);
        if ($$6.isEmpty()) {
            return;
        }
        AABB $$7 = PistonMovingBlockEntity.moveByPositionAndProgress($$1, $$6.bounds(), $$3);
        List $$8 = $$0.getEntities(null, PistonMath.getMovementArea($$7, $$4, $$5).minmax($$7));
        if ($$8.isEmpty()) {
            return;
        }
        List<AABB> $$9 = $$6.toAabbs();
        boolean $$10 = $$3.movedState.is(Blocks.SLIME_BLOCK);
        for (Entity $$11 : $$8) {
            AABB $$19;
            AABB $$17;
            AABB $$18;
            if ($$11.getPistonPushReaction() == PushReaction.IGNORE) continue;
            if ($$10) {
                if ($$11 instanceof ServerPlayer) continue;
                Vec3 $$12 = $$11.getDeltaMovement();
                double $$13 = $$12.x;
                double $$14 = $$12.y;
                double $$15 = $$12.z;
                switch ($$4.getAxis()) {
                    case X: {
                        $$13 = $$4.getStepX();
                        break;
                    }
                    case Y: {
                        $$14 = $$4.getStepY();
                        break;
                    }
                    case Z: {
                        $$15 = $$4.getStepZ();
                    }
                }
                $$11.setDeltaMovement($$13, $$14, $$15);
            }
            double $$16 = 0.0;
            Iterator iterator = $$9.iterator();
            while (!(!iterator.hasNext() || ($$18 = PistonMath.getMovementArea(PistonMovingBlockEntity.moveByPositionAndProgress($$1, $$17 = (AABB)iterator.next(), $$3), $$4, $$5)).intersects($$19 = $$11.getBoundingBox()) && ($$16 = Math.max((double)$$16, (double)PistonMovingBlockEntity.getMovement($$18, $$4, $$19))) >= $$5)) {
            }
            if ($$16 <= 0.0) continue;
            $$16 = Math.min((double)$$16, (double)$$5) + 0.01;
            PistonMovingBlockEntity.moveEntityByPiston($$4, $$11, $$16, $$4);
            if ($$3.extending || !$$3.isSourcePiston) continue;
            PistonMovingBlockEntity.fixEntityWithinPistonBase($$1, $$11, $$4, $$5);
        }
    }

    private static void moveEntityByPiston(Direction $$0, Entity $$1, double $$2, Direction $$3) {
        NOCLIP.set((Object)$$0);
        $$1.move(MoverType.PISTON, new Vec3($$2 * (double)$$3.getStepX(), $$2 * (double)$$3.getStepY(), $$2 * (double)$$3.getStepZ()));
        NOCLIP.set(null);
    }

    private static void moveStuckEntities(Level $$0, BlockPos $$12, float $$2, PistonMovingBlockEntity $$3) {
        if (!$$3.isStickyForEntities()) {
            return;
        }
        Direction $$4 = $$3.getMovementDirection();
        if (!$$4.getAxis().isHorizontal()) {
            return;
        }
        double $$5 = $$3.movedState.getCollisionShape($$0, $$12).max(Direction.Axis.Y);
        AABB $$6 = PistonMovingBlockEntity.moveByPositionAndProgress($$12, new AABB(0.0, $$5, 0.0, 1.0, 1.5000000999999998, 1.0), $$3);
        double $$7 = $$2 - $$3.progress;
        List<Entity> $$8 = $$0.getEntities((Entity)null, $$6, (Predicate<? super Entity>)((Predicate)$$1 -> PistonMovingBlockEntity.matchesStickyCritera($$6, $$1)));
        for (Entity $$9 : $$8) {
            PistonMovingBlockEntity.moveEntityByPiston($$4, $$9, $$7, $$4);
        }
    }

    private static boolean matchesStickyCritera(AABB $$0, Entity $$1) {
        return $$1.getPistonPushReaction() == PushReaction.NORMAL && $$1.isOnGround() && $$1.getX() >= $$0.minX && $$1.getX() <= $$0.maxX && $$1.getZ() >= $$0.minZ && $$1.getZ() <= $$0.maxZ;
    }

    private boolean isStickyForEntities() {
        return this.movedState.is(Blocks.HONEY_BLOCK);
    }

    public Direction getMovementDirection() {
        return this.extending ? this.direction : this.direction.getOpposite();
    }

    private static double getMovement(AABB $$0, Direction $$1, AABB $$2) {
        switch ($$1) {
            case EAST: {
                return $$0.maxX - $$2.minX;
            }
            case WEST: {
                return $$2.maxX - $$0.minX;
            }
            default: {
                return $$0.maxY - $$2.minY;
            }
            case DOWN: {
                return $$2.maxY - $$0.minY;
            }
            case SOUTH: {
                return $$0.maxZ - $$2.minZ;
            }
            case NORTH: 
        }
        return $$2.maxZ - $$0.minZ;
    }

    private static AABB moveByPositionAndProgress(BlockPos $$0, AABB $$1, PistonMovingBlockEntity $$2) {
        double $$3 = $$2.getExtendedProgress($$2.progress);
        return $$1.move((double)$$0.getX() + $$3 * (double)$$2.direction.getStepX(), (double)$$0.getY() + $$3 * (double)$$2.direction.getStepY(), (double)$$0.getZ() + $$3 * (double)$$2.direction.getStepZ());
    }

    private static void fixEntityWithinPistonBase(BlockPos $$0, Entity $$1, Direction $$2, double $$3) {
        double $$8;
        Direction $$6;
        double $$7;
        AABB $$5;
        AABB $$4 = $$1.getBoundingBox();
        if ($$4.intersects($$5 = Shapes.block().bounds().move($$0)) && Math.abs((double)(($$7 = PistonMovingBlockEntity.getMovement($$5, $$6 = $$2.getOpposite(), $$4) + 0.01) - ($$8 = PistonMovingBlockEntity.getMovement($$5, $$6, $$4.intersect($$5)) + 0.01))) < 0.01) {
            $$7 = Math.min((double)$$7, (double)$$3) + 0.01;
            PistonMovingBlockEntity.moveEntityByPiston($$2, $$1, $$7, $$6);
        }
    }

    public BlockState getMovedState() {
        return this.movedState;
    }

    public void finalTick() {
        if (this.level != null && (this.progressO < 1.0f || this.level.isClientSide)) {
            this.progressO = this.progress = 1.0f;
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();
            if (this.level.getBlockState(this.worldPosition).is(Blocks.MOVING_PISTON)) {
                BlockState $$1;
                if (this.isSourcePiston) {
                    BlockState $$0 = Blocks.AIR.defaultBlockState();
                } else {
                    $$1 = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
                }
                this.level.setBlock(this.worldPosition, $$1, 3);
                this.level.neighborChanged(this.worldPosition, $$1.getBlock(), this.worldPosition);
            }
        }
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, PistonMovingBlockEntity $$3) {
        $$3.lastTicked = $$0.getGameTime();
        $$3.progressO = $$3.progress;
        if ($$3.progressO >= 1.0f) {
            if ($$0.isClientSide && $$3.deathTicks < 5) {
                ++$$3.deathTicks;
                return;
            }
            $$0.removeBlockEntity($$1);
            $$3.setRemoved();
            if ($$0.getBlockState($$1).is(Blocks.MOVING_PISTON)) {
                BlockState $$4 = Block.updateFromNeighbourShapes($$3.movedState, $$0, $$1);
                if ($$4.isAir()) {
                    $$0.setBlock($$1, $$3.movedState, 84);
                    Block.updateOrDestroy($$3.movedState, $$4, $$0, $$1, 3);
                } else {
                    if ($$4.hasProperty(BlockStateProperties.WATERLOGGED) && $$4.getValue(BlockStateProperties.WATERLOGGED).booleanValue()) {
                        $$4 = (BlockState)$$4.setValue(BlockStateProperties.WATERLOGGED, false);
                    }
                    $$0.setBlock($$1, $$4, 67);
                    $$0.neighborChanged($$1, $$4.getBlock(), $$1);
                }
            }
            return;
        }
        float $$5 = $$3.progress + 0.5f;
        PistonMovingBlockEntity.moveCollidedEntities($$0, $$1, $$5, $$3);
        PistonMovingBlockEntity.moveStuckEntities($$0, $$1, $$5, $$3);
        $$3.progress = $$5;
        if ($$3.progress >= 1.0f) {
            $$3.progress = 1.0f;
        }
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        HolderLookup.RegistryLookup $$1 = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.movedState = NbtUtils.readBlockState($$1, $$0.getCompound("blockState"));
        this.direction = Direction.from3DDataValue($$0.getInt("facing"));
        this.progressO = this.progress = $$0.getFloat("progress");
        this.extending = $$0.getBoolean("extending");
        this.isSourcePiston = $$0.getBoolean("source");
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.put("blockState", NbtUtils.writeBlockState(this.movedState));
        $$0.putInt("facing", this.direction.get3DDataValue());
        $$0.putFloat("progress", this.progressO);
        $$0.putBoolean("extending", this.extending);
        $$0.putBoolean("source", this.isSourcePiston);
    }

    public VoxelShape getCollisionShape(BlockGetter $$0, BlockPos $$1) {
        BlockState $$6;
        VoxelShape $$3;
        if (!this.extending && this.isSourcePiston && this.movedState.getBlock() instanceof PistonBaseBlock) {
            VoxelShape $$2 = ((BlockState)this.movedState.setValue(PistonBaseBlock.EXTENDED, true)).getCollisionShape($$0, $$1);
        } else {
            $$3 = Shapes.empty();
        }
        Direction $$4 = (Direction)NOCLIP.get();
        if ((double)this.progress < 1.0 && $$4 == this.getMovementDirection()) {
            return $$3;
        }
        if (this.isSourcePiston()) {
            BlockState $$5 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, this.direction)).setValue(PistonHeadBlock.SHORT, this.extending != 1.0f - this.progress < 0.25f);
        } else {
            $$6 = this.movedState;
        }
        float $$7 = this.getExtendedProgress(this.progress);
        double $$8 = (float)this.direction.getStepX() * $$7;
        double $$9 = (float)this.direction.getStepY() * $$7;
        double $$10 = (float)this.direction.getStepZ() * $$7;
        return Shapes.or($$3, $$6.getCollisionShape($$0, $$1).move($$8, $$9, $$10));
    }

    public long getLastTicked() {
        return this.lastTicked;
    }

    @Override
    public void setLevel(Level $$0) {
        super.setLevel($$0);
        if ($$0.holderLookup(Registries.BLOCK).get(this.movedState.getBlock().builtInRegistryHolder().key()).isEmpty()) {
            this.movedState = Blocks.AIR.defaultBlockState();
        }
    }
}