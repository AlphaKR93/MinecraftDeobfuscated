/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter
extends LevelHeightAccessor {
    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    default public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos $$0, BlockEntityType<T> $$1) {
        BlockEntity $$2 = this.getBlockEntity($$0);
        if ($$2 == null || $$2.getType() != $$1) {
            return Optional.empty();
        }
        return Optional.of((Object)$$2);
    }

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLightEmission(BlockPos $$0) {
        return this.getBlockState($$0).getLightEmission();
    }

    default public int getMaxLightLevel() {
        return 15;
    }

    default public Stream<BlockState> getBlockStates(AABB $$0) {
        return BlockPos.betweenClosedStream($$0).map(this::getBlockState);
    }

    default public BlockHitResult isBlockInLine(ClipBlockStateContext $$02) {
        return (BlockHitResult)BlockGetter.traverseBlocks($$02.getFrom(), $$02.getTo(), $$02, ($$0, $$1) -> {
            BlockState $$2 = this.getBlockState((BlockPos)$$1);
            Vec3 $$3 = $$0.getFrom().subtract($$0.getTo());
            return $$0.isTargetBlock().test((Object)$$2) ? new BlockHitResult($$0.getTo(), Direction.getNearest($$3.x, $$3.y, $$3.z), new BlockPos($$0.getTo()), false) : null;
        }, $$0 -> {
            Vec3 $$1 = $$0.getFrom().subtract($$0.getTo());
            return BlockHitResult.miss($$0.getTo(), Direction.getNearest($$1.x, $$1.y, $$1.z), new BlockPos($$0.getTo()));
        });
    }

    default public BlockHitResult clip(ClipContext $$02) {
        return (BlockHitResult)BlockGetter.traverseBlocks($$02.getFrom(), $$02.getTo(), $$02, ($$0, $$1) -> {
            BlockState $$2 = this.getBlockState((BlockPos)$$1);
            FluidState $$3 = this.getFluidState((BlockPos)$$1);
            Vec3 $$4 = $$0.getFrom();
            Vec3 $$5 = $$0.getTo();
            VoxelShape $$6 = $$0.getBlockShape($$2, this, (BlockPos)$$1);
            BlockHitResult $$7 = this.clipWithInteractionOverride($$4, $$5, (BlockPos)$$1, $$6, $$2);
            VoxelShape $$8 = $$0.getFluidShape($$3, this, (BlockPos)$$1);
            BlockHitResult $$9 = $$8.clip($$4, $$5, (BlockPos)$$1);
            double $$10 = $$7 == null ? Double.MAX_VALUE : $$0.getFrom().distanceToSqr($$7.getLocation());
            double $$11 = $$9 == null ? Double.MAX_VALUE : $$0.getFrom().distanceToSqr($$9.getLocation());
            return $$10 <= $$11 ? $$7 : $$9;
        }, $$0 -> {
            Vec3 $$1 = $$0.getFrom().subtract($$0.getTo());
            return BlockHitResult.miss($$0.getTo(), Direction.getNearest($$1.x, $$1.y, $$1.z), new BlockPos($$0.getTo()));
        });
    }

    @Nullable
    default public BlockHitResult clipWithInteractionOverride(Vec3 $$0, Vec3 $$1, BlockPos $$2, VoxelShape $$3, BlockState $$4) {
        BlockHitResult $$6;
        BlockHitResult $$5 = $$3.clip($$0, $$1, $$2);
        if ($$5 != null && ($$6 = $$4.getInteractionShape(this, $$2).clip($$0, $$1, $$2)) != null && $$6.getLocation().subtract($$0).lengthSqr() < $$5.getLocation().subtract($$0).lengthSqr()) {
            return $$5.withDirection($$6.getDirection());
        }
        return $$5;
    }

    default public double getBlockFloorHeight(VoxelShape $$0, Supplier<VoxelShape> $$1) {
        if (!$$0.isEmpty()) {
            return $$0.max(Direction.Axis.Y);
        }
        double $$2 = ((VoxelShape)$$1.get()).max(Direction.Axis.Y);
        if ($$2 >= 1.0) {
            return $$2 - 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    default public double getBlockFloorHeight(BlockPos $$0) {
        return this.getBlockFloorHeight(this.getBlockState($$0).getCollisionShape(this, $$0), (Supplier<VoxelShape>)((Supplier)() -> {
            Vec3i $$1 = $$0.below();
            return this.getBlockState((BlockPos)$$1).getCollisionShape(this, (BlockPos)$$1);
        }));
    }

    public static <T, C> T traverseBlocks(Vec3 $$0, Vec3 $$1, C $$2, BiFunction<C, BlockPos, T> $$3, Function<C, T> $$4) {
        int $$13;
        int $$12;
        if ($$0.equals($$1)) {
            return (T)$$4.apply($$2);
        }
        double $$5 = Mth.lerp(-1.0E-7, $$1.x, $$0.x);
        double $$6 = Mth.lerp(-1.0E-7, $$1.y, $$0.y);
        double $$7 = Mth.lerp(-1.0E-7, $$1.z, $$0.z);
        double $$8 = Mth.lerp(-1.0E-7, $$0.x, $$1.x);
        double $$9 = Mth.lerp(-1.0E-7, $$0.y, $$1.y);
        double $$10 = Mth.lerp(-1.0E-7, $$0.z, $$1.z);
        int $$11 = Mth.floor($$8);
        BlockPos.MutableBlockPos $$14 = new BlockPos.MutableBlockPos($$11, $$12 = Mth.floor($$9), $$13 = Mth.floor($$10));
        Object $$15 = $$3.apply($$2, (Object)$$14);
        if ($$15 != null) {
            return (T)$$15;
        }
        double $$16 = $$5 - $$8;
        double $$17 = $$6 - $$9;
        double $$18 = $$7 - $$10;
        int $$19 = Mth.sign($$16);
        int $$20 = Mth.sign($$17);
        int $$21 = Mth.sign($$18);
        double $$22 = $$19 == 0 ? Double.MAX_VALUE : (double)$$19 / $$16;
        double $$23 = $$20 == 0 ? Double.MAX_VALUE : (double)$$20 / $$17;
        double $$24 = $$21 == 0 ? Double.MAX_VALUE : (double)$$21 / $$18;
        double $$25 = $$22 * ($$19 > 0 ? 1.0 - Mth.frac($$8) : Mth.frac($$8));
        double $$26 = $$23 * ($$20 > 0 ? 1.0 - Mth.frac($$9) : Mth.frac($$9));
        double $$27 = $$24 * ($$21 > 0 ? 1.0 - Mth.frac($$10) : Mth.frac($$10));
        while ($$25 <= 1.0 || $$26 <= 1.0 || $$27 <= 1.0) {
            Object $$28;
            if ($$25 < $$26) {
                if ($$25 < $$27) {
                    $$11 += $$19;
                    $$25 += $$22;
                } else {
                    $$13 += $$21;
                    $$27 += $$24;
                }
            } else if ($$26 < $$27) {
                $$12 += $$20;
                $$26 += $$23;
            } else {
                $$13 += $$21;
                $$27 += $$24;
            }
            if (($$28 = $$3.apply($$2, (Object)$$14.set($$11, $$12, $$13))) == null) continue;
            return (T)$$28;
        }
        return (T)$$4.apply($$2);
    }
}