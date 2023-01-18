/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.util;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class AirRandomPos {
    @Nullable
    public static Vec3 getPosTowards(PathfinderMob $$0, int $$1, int $$2, int $$3, Vec3 $$4, double $$5) {
        Vec3 $$6 = $$4.subtract($$0.getX(), $$0.getY(), $$0.getZ());
        boolean $$7 = GoalUtils.mobRestricted($$0, $$1);
        return RandomPos.generateRandomPos($$0, (Supplier<BlockPos>)((Supplier)() -> {
            BlockPos $$7 = AirAndWaterRandomPos.generateRandomPos($$0, $$1, $$2, $$3, $$4.x, $$4.z, $$5, $$7);
            if ($$7 == null || GoalUtils.isWater($$0, $$7)) {
                return null;
            }
            return $$7;
        }));
    }
}