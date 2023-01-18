/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity.ai.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class GoalUtils {
    public static boolean hasGroundPathNavigation(Mob $$0) {
        return $$0.getNavigation() instanceof GroundPathNavigation;
    }

    public static boolean mobRestricted(PathfinderMob $$0, int $$1) {
        return $$0.hasRestriction() && $$0.getRestrictCenter().closerToCenterThan($$0.position(), (double)($$0.getRestrictRadius() + (float)$$1) + 1.0);
    }

    public static boolean isOutsideLimits(BlockPos $$0, PathfinderMob $$1) {
        return $$0.getY() < $$1.level.getMinBuildHeight() || $$0.getY() > $$1.level.getMaxBuildHeight();
    }

    public static boolean isRestricted(boolean $$0, PathfinderMob $$1, BlockPos $$2) {
        return $$0 && !$$1.isWithinRestriction($$2);
    }

    public static boolean isNotStable(PathNavigation $$0, BlockPos $$1) {
        return !$$0.isStableDestination($$1);
    }

    public static boolean isWater(PathfinderMob $$0, BlockPos $$1) {
        return $$0.level.getFluidState($$1).is(FluidTags.WATER);
    }

    public static boolean hasMalus(PathfinderMob $$0, BlockPos $$1) {
        return $$0.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic($$0.level, $$1.mutable())) != 0.0f;
    }

    public static boolean isSolid(PathfinderMob $$0, BlockPos $$1) {
        return $$0.level.getBlockState($$1).getMaterial().isSolid();
    }
}