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
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class HoverRandomPos {
    @Nullable
    public static Vec3 getPos(PathfinderMob $$0, int $$1, int $$2, double $$3, double $$4, float $$5, int $$6, int $$7) {
        boolean $$8 = GoalUtils.mobRestricted($$0, $$1);
        return RandomPos.generateRandomPos($$0, (Supplier<BlockPos>)((Supplier)() -> {
            BlockPos $$9 = RandomPos.generateRandomDirectionWithinRadians($$0.getRandom(), $$1, $$2, 0, $$3, $$4, $$5);
            if ($$9 == null) {
                return null;
            }
            BlockPos $$10 = LandRandomPos.generateRandomPosTowardDirection($$0, $$1, $$8, $$9);
            if ($$10 == null) {
                return null;
            }
            if (GoalUtils.isWater($$0, $$10 = RandomPos.moveUpToAboveSolid($$10, $$0.getRandom().nextInt($$6 - $$7 + 1) + $$7, $$0.level.getMaxBuildHeight(), (Predicate<BlockPos>)((Predicate)$$1 -> GoalUtils.isSolid($$0, $$1)))) || GoalUtils.hasMalus($$0, $$10)) {
                return null;
            }
            return $$10;
        }));
    }
}