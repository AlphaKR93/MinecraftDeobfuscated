/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

public class BiomeManager {
    public static final int CHUNK_CENTER_QUART = QuartPos.fromBlock(8);
    private static final int ZOOM_BITS = 2;
    private static final int ZOOM = 4;
    private static final int ZOOM_MASK = 3;
    private final NoiseBiomeSource noiseBiomeSource;
    private final long biomeZoomSeed;

    public BiomeManager(NoiseBiomeSource $$0, long $$1) {
        this.noiseBiomeSource = $$0;
        this.biomeZoomSeed = $$1;
    }

    public static long obfuscateSeed(long $$0) {
        return Hashing.sha256().hashLong($$0).asLong();
    }

    public BiomeManager withDifferentSource(NoiseBiomeSource $$0) {
        return new BiomeManager($$0, this.biomeZoomSeed);
    }

    public Holder<Biome> getBiome(BlockPos $$0) {
        int $$1 = $$0.getX() - 2;
        int $$2 = $$0.getY() - 2;
        int $$3 = $$0.getZ() - 2;
        int $$4 = $$1 >> 2;
        int $$5 = $$2 >> 2;
        int $$6 = $$3 >> 2;
        double $$7 = (double)($$1 & 3) / 4.0;
        double $$8 = (double)($$2 & 3) / 4.0;
        double $$9 = (double)($$3 & 3) / 4.0;
        int $$10 = 0;
        double $$11 = Double.POSITIVE_INFINITY;
        for (int $$12 = 0; $$12 < 8; ++$$12) {
            double $$21;
            double $$20;
            double $$19;
            boolean $$15;
            int $$18;
            boolean $$14;
            int $$17;
            boolean $$13 = ($$12 & 4) == 0;
            int $$16 = $$13 ? $$4 : $$4 + 1;
            double $$22 = BiomeManager.getFiddledDistance(this.biomeZoomSeed, $$16, $$17 = ($$14 = ($$12 & 2) == 0) ? $$5 : $$5 + 1, $$18 = ($$15 = ($$12 & 1) == 0) ? $$6 : $$6 + 1, $$19 = $$13 ? $$7 : $$7 - 1.0, $$20 = $$14 ? $$8 : $$8 - 1.0, $$21 = $$15 ? $$9 : $$9 - 1.0);
            if (!($$11 > $$22)) continue;
            $$10 = $$12;
            $$11 = $$22;
        }
        int $$23 = ($$10 & 4) == 0 ? $$4 : $$4 + 1;
        int $$24 = ($$10 & 2) == 0 ? $$5 : $$5 + 1;
        int $$25 = ($$10 & 1) == 0 ? $$6 : $$6 + 1;
        return this.noiseBiomeSource.getNoiseBiome($$23, $$24, $$25);
    }

    public Holder<Biome> getNoiseBiomeAtPosition(double $$0, double $$1, double $$2) {
        int $$3 = QuartPos.fromBlock(Mth.floor($$0));
        int $$4 = QuartPos.fromBlock(Mth.floor($$1));
        int $$5 = QuartPos.fromBlock(Mth.floor($$2));
        return this.getNoiseBiomeAtQuart($$3, $$4, $$5);
    }

    public Holder<Biome> getNoiseBiomeAtPosition(BlockPos $$0) {
        int $$1 = QuartPos.fromBlock($$0.getX());
        int $$2 = QuartPos.fromBlock($$0.getY());
        int $$3 = QuartPos.fromBlock($$0.getZ());
        return this.getNoiseBiomeAtQuart($$1, $$2, $$3);
    }

    public Holder<Biome> getNoiseBiomeAtQuart(int $$0, int $$1, int $$2) {
        return this.noiseBiomeSource.getNoiseBiome($$0, $$1, $$2);
    }

    private static double getFiddledDistance(long $$0, int $$1, int $$2, int $$3, double $$4, double $$5, double $$6) {
        long $$7 = $$0;
        $$7 = LinearCongruentialGenerator.next($$7, $$1);
        $$7 = LinearCongruentialGenerator.next($$7, $$2);
        $$7 = LinearCongruentialGenerator.next($$7, $$3);
        $$7 = LinearCongruentialGenerator.next($$7, $$1);
        $$7 = LinearCongruentialGenerator.next($$7, $$2);
        $$7 = LinearCongruentialGenerator.next($$7, $$3);
        double $$8 = BiomeManager.getFiddle($$7);
        $$7 = LinearCongruentialGenerator.next($$7, $$0);
        double $$9 = BiomeManager.getFiddle($$7);
        $$7 = LinearCongruentialGenerator.next($$7, $$0);
        double $$10 = BiomeManager.getFiddle($$7);
        return Mth.square($$6 + $$10) + Mth.square($$5 + $$9) + Mth.square($$4 + $$8);
    }

    private static double getFiddle(long $$0) {
        double $$1 = (double)Math.floorMod((long)($$0 >> 24), (int)1024) / 1024.0;
        return ($$1 - 0.5) * 0.9;
    }

    public static interface NoiseBiomeSource {
        public Holder<Biome> getNoiseBiome(int var1, int var2, int var3);
    }
}