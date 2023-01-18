/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.util;

import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class AirAndWaterRandomPos {
    @Nullable
    public static Vec3 getPos(PathfinderMob $$0, int $$1, int $$2, int $$3, double $$4, double $$5, double $$6) {
        boolean $$7 = GoalUtils.mobRestricted($$0, $$1);
        return RandomPos.generateRandomPos($$0, (Supplier<BlockPos>)((Supplier)() -> AirAndWaterRandomPos.generateRandomPos($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7)));
    }

    @Nullable
    public static BlockPos generateRandomPos(PathfinderMob $$0, int $$12, int $$2, int $$3, double $$4, double $$5, double $$6, boolean $$7) {
        BlockPos $$8 = RandomPos.generateRandomDirectionWithinRadians($$0.getRandom(), $$12, $$2, $$3, $$4, $$5, $$6);
        if ($$8 == null) {
            return null;
        }
        BlockPos $$9 = RandomPos.generateRandomPosTowardDirection($$0, $$12, $$0.getRandom(), $$8);
        if (GoalUtils.isOutsideLimits($$9, $$0) || GoalUtils.isRestricted($$7, $$0, $$9)) {
            return null;
        }
        if (GoalUtils.hasMalus($$0, $$9 = RandomPos.moveUpOutOfSolid($$9, $$0.level.getMaxBuildHeight(), (Predicate<BlockPos>)((Predicate)$$1 -> GoalUtils.isSolid($$0, $$1))))) {
            return null;
        }
        return $$9;
    }
}