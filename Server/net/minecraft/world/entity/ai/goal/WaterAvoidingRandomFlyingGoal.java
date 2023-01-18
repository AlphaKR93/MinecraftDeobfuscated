/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomFlyingGoal
extends WaterAvoidingRandomStrollGoal {
    public WaterAvoidingRandomFlyingGoal(PathfinderMob $$0, double $$1) {
        super($$0, $$1);
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        Vec3 $$0 = this.mob.getViewVector(0.0f);
        int $$1 = 8;
        Vec3 $$2 = HoverRandomPos.getPos(this.mob, 8, 7, $$0.x, $$0.z, 1.5707964f, 3, 1);
        if ($$2 != null) {
            return $$2;
        }
        return AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, $$0.x, $$0.z, 1.5707963705062866);
    }
}