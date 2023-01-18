/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.function.ToDoubleFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.util;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class LandRandomPos {
    @Nullable
    public static Vec3 getPos(PathfinderMob $$0, int $$1, int $$2) {
        return LandRandomPos.getPos($$0, $$1, $$2, (ToDoubleFunction<BlockPos>)((ToDoubleFunction)$$0::getWalkTargetValue));
    }

    @Nullable
    public static Vec3 getPos(PathfinderMob $$0, int $$1, int $$2, ToDoubleFunction<BlockPos> $$3) {
        boolean $$4 = GoalUtils.mobRestricted($$0, $$1);
        return RandomPos.generateRandomPos((Supplier<BlockPos>)((Supplier)() -> {
            BlockPos $$4 = RandomPos.generateRandomDirection($$0.getRandom(), $$1, $$2);
            BlockPos $$5 = LandRandomPos.generateRandomPosTowardDirection($$0, $$1, $$4, $$4);
            if ($$5 == null) {
                return null;
            }
            return LandRandomPos.movePosUpOutOfSolid($$0, $$5);
        }), $$3);
    }

    @Nullable
    public static Vec3 getPosTowards(PathfinderMob $$0, int $$1, int $$2, Vec3 $$3) {
        Vec3 $$4 = $$3.subtract($$0.getX(), $$0.getY(), $$0.getZ());
        boolean $$5 = GoalUtils.mobRestricted($$0, $$1);
        return LandRandomPos.getPosInDirection($$0, $$1, $$2, $$4, $$5);
    }

    @Nullable
    public static Vec3 getPosAway(PathfinderMob $$0, int $$1, int $$2, Vec3 $$3) {
        Vec3 $$4 = $$0.position().subtract($$3);
        boolean $$5 = GoalUtils.mobRestricted($$0, $$1);
        return LandRandomPos.getPosInDirection($$0, $$1, $$2, $$4, $$5);
    }

    @Nullable
    private static Vec3 getPosInDirection(PathfinderMob $$0, int $$1, int $$2, Vec3 $$3, boolean $$4) {
        return RandomPos.generateRandomPos($$0, (Supplier<BlockPos>)((Supplier)() -> {
            BlockPos $$5 = RandomPos.generateRandomDirectionWithinRadians($$0.getRandom(), $$1, $$2, 0, $$3.x, $$3.z, 1.5707963705062866);
            if ($$5 == null) {
                return null;
            }
            BlockPos $$6 = LandRandomPos.generateRandomPosTowardDirection($$0, $$1, $$4, $$5);
            if ($$6 == null) {
                return null;
            }
            return LandRandomPos.movePosUpOutOfSolid($$0, $$6);
        }));
    }

    @Nullable
    public static BlockPos movePosUpOutOfSolid(PathfinderMob $$0, BlockPos $$12) {
        if (GoalUtils.isWater($$0, $$12 = RandomPos.moveUpOutOfSolid($$12, $$0.level.getMaxBuildHeight(), (Predicate<BlockPos>)((Predicate)$$1 -> GoalUtils.isSolid($$0, $$1)))) || GoalUtils.hasMalus($$0, $$12)) {
            return null;
        }
        return $$12;
    }

    @Nullable
    public static BlockPos generateRandomPosTowardDirection(PathfinderMob $$0, int $$1, boolean $$2, BlockPos $$3) {
        BlockPos $$4 = RandomPos.generateRandomPosTowardDirection($$0, $$1, $$0.getRandom(), $$3);
        if (GoalUtils.isOutsideLimits($$4, $$0) || GoalUtils.isRestricted($$2, $$0, $$4) || GoalUtils.isNotStable($$0.getNavigation(), $$4)) {
            return null;
        }
        return $$4;
    }
}