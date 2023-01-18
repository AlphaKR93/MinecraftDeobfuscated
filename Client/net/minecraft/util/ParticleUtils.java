/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Supplier
 */
package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
    public static void spawnParticlesOnBlockFaces(Level $$0, BlockPos $$1, ParticleOptions $$2, IntProvider $$3) {
        for (Direction $$4 : Direction.values()) {
            ParticleUtils.spawnParticlesOnBlockFace($$0, $$1, $$2, $$3, $$4, (Supplier<Vec3>)((Supplier)() -> ParticleUtils.getRandomSpeedRanges($$0.random)), 0.55);
        }
    }

    public static void spawnParticlesOnBlockFace(Level $$0, BlockPos $$1, ParticleOptions $$2, IntProvider $$3, Direction $$4, Supplier<Vec3> $$5, double $$6) {
        int $$7 = $$3.sample($$0.random);
        for (int $$8 = 0; $$8 < $$7; ++$$8) {
            ParticleUtils.spawnParticleOnFace($$0, $$1, $$4, $$2, (Vec3)$$5.get(), $$6);
        }
    }

    private static Vec3 getRandomSpeedRanges(RandomSource $$0) {
        return new Vec3(Mth.nextDouble($$0, -0.5, 0.5), Mth.nextDouble($$0, -0.5, 0.5), Mth.nextDouble($$0, -0.5, 0.5));
    }

    public static void spawnParticlesAlongAxis(Direction.Axis $$0, Level $$1, BlockPos $$2, double $$3, ParticleOptions $$4, UniformInt $$5) {
        Vec3 $$6 = Vec3.atCenterOf($$2);
        boolean $$7 = $$0 == Direction.Axis.X;
        boolean $$8 = $$0 == Direction.Axis.Y;
        boolean $$9 = $$0 == Direction.Axis.Z;
        int $$10 = $$5.sample($$1.random);
        for (int $$11 = 0; $$11 < $$10; ++$$11) {
            double $$12 = $$6.x + Mth.nextDouble($$1.random, -1.0, 1.0) * ($$7 ? 0.5 : $$3);
            double $$13 = $$6.y + Mth.nextDouble($$1.random, -1.0, 1.0) * ($$8 ? 0.5 : $$3);
            double $$14 = $$6.z + Mth.nextDouble($$1.random, -1.0, 1.0) * ($$9 ? 0.5 : $$3);
            double $$15 = $$7 ? Mth.nextDouble($$1.random, -1.0, 1.0) : 0.0;
            double $$16 = $$8 ? Mth.nextDouble($$1.random, -1.0, 1.0) : 0.0;
            double $$17 = $$9 ? Mth.nextDouble($$1.random, -1.0, 1.0) : 0.0;
            $$1.addParticle($$4, $$12, $$13, $$14, $$15, $$16, $$17);
        }
    }

    public static void spawnParticleOnFace(Level $$0, BlockPos $$1, Direction $$2, ParticleOptions $$3, Vec3 $$4, double $$5) {
        Vec3 $$6 = Vec3.atCenterOf($$1);
        int $$7 = $$2.getStepX();
        int $$8 = $$2.getStepY();
        int $$9 = $$2.getStepZ();
        double $$10 = $$6.x + ($$7 == 0 ? Mth.nextDouble($$0.random, -0.5, 0.5) : (double)$$7 * $$5);
        double $$11 = $$6.y + ($$8 == 0 ? Mth.nextDouble($$0.random, -0.5, 0.5) : (double)$$8 * $$5);
        double $$12 = $$6.z + ($$9 == 0 ? Mth.nextDouble($$0.random, -0.5, 0.5) : (double)$$9 * $$5);
        double $$13 = $$7 == 0 ? $$4.x() : 0.0;
        double $$14 = $$8 == 0 ? $$4.y() : 0.0;
        double $$15 = $$9 == 0 ? $$4.z() : 0.0;
        $$0.addParticle($$3, $$10, $$11, $$12, $$13, $$14, $$15);
    }
}