/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.function.ToDoubleFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
    private static final int RANDOM_POS_ATTEMPTS = 10;

    public static BlockPos generateRandomDirection(RandomSource $$0, int $$1, int $$2) {
        int $$3 = $$0.nextInt(2 * $$1 + 1) - $$1;
        int $$4 = $$0.nextInt(2 * $$2 + 1) - $$2;
        int $$5 = $$0.nextInt(2 * $$1 + 1) - $$1;
        return new BlockPos($$3, $$4, $$5);
    }

    @Nullable
    public static BlockPos generateRandomDirectionWithinRadians(RandomSource $$0, int $$1, int $$2, int $$3, double $$4, double $$5, double $$6) {
        double $$7 = Mth.atan2($$5, $$4) - 1.5707963705062866;
        double $$8 = $$7 + (double)(2.0f * $$0.nextFloat() - 1.0f) * $$6;
        double $$9 = Math.sqrt((double)$$0.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)$$1;
        double $$10 = -$$9 * Math.sin((double)$$8);
        double $$11 = $$9 * Math.cos((double)$$8);
        if (Math.abs((double)$$10) > (double)$$1 || Math.abs((double)$$11) > (double)$$1) {
            return null;
        }
        int $$12 = $$0.nextInt(2 * $$2 + 1) - $$2 + $$3;
        return new BlockPos($$10, (double)$$12, $$11);
    }

    @VisibleForTesting
    public static BlockPos moveUpOutOfSolid(BlockPos $$0, int $$1, Predicate<BlockPos> $$2) {
        if ($$2.test((Object)$$0)) {
            Vec3i $$3 = $$0.above();
            while ($$3.getY() < $$1 && $$2.test((Object)$$3)) {
                $$3 = ((BlockPos)$$3).above();
            }
            return $$3;
        }
        return $$0;
    }

    @VisibleForTesting
    public static BlockPos moveUpToAboveSolid(BlockPos $$0, int $$1, int $$2, Predicate<BlockPos> $$3) {
        if ($$1 < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + $$1 + ", expected >= 0");
        }
        if ($$3.test((Object)$$0)) {
            Vec3i $$6;
            Vec3i $$4 = $$0.above();
            while ($$4.getY() < $$2 && $$3.test((Object)$$4)) {
                $$4 = ((BlockPos)$$4).above();
            }
            Vec3i $$5 = $$4;
            while ($$5.getY() < $$2 && $$5.getY() - $$4.getY() < $$1 && !$$3.test((Object)($$6 = ((BlockPos)$$5).above()))) {
                $$5 = $$6;
            }
            return $$5;
        }
        return $$0;
    }

    @Nullable
    public static Vec3 generateRandomPos(PathfinderMob $$0, Supplier<BlockPos> $$1) {
        return RandomPos.generateRandomPos($$1, (ToDoubleFunction<BlockPos>)((ToDoubleFunction)$$0::getWalkTargetValue));
    }

    @Nullable
    public static Vec3 generateRandomPos(Supplier<BlockPos> $$0, ToDoubleFunction<BlockPos> $$1) {
        double $$2 = Double.NEGATIVE_INFINITY;
        BlockPos $$3 = null;
        for (int $$4 = 0; $$4 < 10; ++$$4) {
            double $$6;
            BlockPos $$5 = (BlockPos)$$0.get();
            if ($$5 == null || !(($$6 = $$1.applyAsDouble((Object)$$5)) > $$2)) continue;
            $$2 = $$6;
            $$3 = $$5;
        }
        return $$3 != null ? Vec3.atBottomCenterOf($$3) : null;
    }

    public static BlockPos generateRandomPosTowardDirection(PathfinderMob $$0, int $$1, RandomSource $$2, BlockPos $$3) {
        int $$4 = $$3.getX();
        int $$5 = $$3.getZ();
        if ($$0.hasRestriction() && $$1 > 1) {
            BlockPos $$6 = $$0.getRestrictCenter();
            $$4 = $$0.getX() > (double)$$6.getX() ? ($$4 -= $$2.nextInt($$1 / 2)) : ($$4 += $$2.nextInt($$1 / 2));
            $$5 = $$0.getZ() > (double)$$6.getZ() ? ($$5 -= $$2.nextInt($$1 / 2)) : ($$5 += $$2.nextInt($$1 / 2));
        }
        return new BlockPos((double)$$4 + $$0.getX(), (double)$$3.getY() + $$0.getY(), (double)$$5 + $$0.getZ());
    }
}