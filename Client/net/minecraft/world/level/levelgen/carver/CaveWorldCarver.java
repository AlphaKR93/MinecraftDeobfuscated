/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class CaveWorldCarver
extends WorldCarver<CaveCarverConfiguration> {
    public CaveWorldCarver(Codec<CaveCarverConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean isStartChunk(CaveCarverConfiguration $$0, RandomSource $$1) {
        return $$1.nextFloat() <= $$0.probability;
    }

    @Override
    public boolean carve(CarvingContext $$0, CaveCarverConfiguration $$12, ChunkAccess $$22, Function<BlockPos, Holder<Biome>> $$32, RandomSource $$42, Aquifer $$52, ChunkPos $$6, CarvingMask $$7) {
        int $$8 = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
        int $$9 = $$42.nextInt($$42.nextInt($$42.nextInt(this.getCaveBound()) + 1) + 1);
        for (int $$10 = 0; $$10 < $$9; ++$$10) {
            double $$11 = $$6.getBlockX($$42.nextInt(16));
            double $$122 = $$12.y.sample($$42, $$0);
            double $$13 = $$6.getBlockZ($$42.nextInt(16));
            double $$14 = $$12.horizontalRadiusMultiplier.sample($$42);
            double $$15 = $$12.verticalRadiusMultiplier.sample($$42);
            double $$16 = $$12.floorLevel.sample($$42);
            WorldCarver.CarveSkipChecker $$17 = ($$1, $$2, $$3, $$4, $$5) -> CaveWorldCarver.shouldSkip($$2, $$3, $$4, $$16);
            int $$18 = 1;
            if ($$42.nextInt(4) == 0) {
                double $$19 = $$12.yScale.sample($$42);
                float $$20 = 1.0f + $$42.nextFloat() * 6.0f;
                this.createRoom($$0, $$12, $$22, $$32, $$52, $$11, $$122, $$13, $$20, $$19, $$7, $$17);
                $$18 += $$42.nextInt(4);
            }
            for (int $$21 = 0; $$21 < $$18; ++$$21) {
                float $$222 = $$42.nextFloat() * ((float)Math.PI * 2);
                float $$23 = ($$42.nextFloat() - 0.5f) / 4.0f;
                float $$24 = this.getThickness($$42);
                int $$25 = $$8 - $$42.nextInt($$8 / 4);
                boolean $$26 = false;
                this.createTunnel($$0, $$12, $$22, $$32, $$42.nextLong(), $$52, $$11, $$122, $$13, $$14, $$15, $$24, $$222, $$23, 0, $$25, this.getYScale(), $$7, $$17);
            }
        }
        return true;
    }

    protected int getCaveBound() {
        return 15;
    }

    protected float getThickness(RandomSource $$0) {
        float $$1 = $$0.nextFloat() * 2.0f + $$0.nextFloat();
        if ($$0.nextInt(10) == 0) {
            $$1 *= $$0.nextFloat() * $$0.nextFloat() * 3.0f + 1.0f;
        }
        return $$1;
    }

    protected double getYScale() {
        return 1.0;
    }

    protected void createRoom(CarvingContext $$0, CaveCarverConfiguration $$1, ChunkAccess $$2, Function<BlockPos, Holder<Biome>> $$3, Aquifer $$4, double $$5, double $$6, double $$7, float $$8, double $$9, CarvingMask $$10, WorldCarver.CarveSkipChecker $$11) {
        double $$12 = 1.5 + (double)(Mth.sin(1.5707964f) * $$8);
        double $$13 = $$12 * $$9;
        this.carveEllipsoid($$0, $$1, $$2, $$3, $$4, $$5 + 1.0, $$6, $$7, $$12, $$13, $$10, $$11);
    }

    protected void createTunnel(CarvingContext $$0, CaveCarverConfiguration $$1, ChunkAccess $$2, Function<BlockPos, Holder<Biome>> $$3, long $$4, Aquifer $$5, double $$6, double $$7, double $$8, double $$9, double $$10, float $$11, float $$12, float $$13, int $$14, int $$15, double $$16, CarvingMask $$17, WorldCarver.CarveSkipChecker $$18) {
        RandomSource $$19 = RandomSource.create($$4);
        int $$20 = $$19.nextInt($$15 / 2) + $$15 / 4;
        boolean $$21 = $$19.nextInt(6) == 0;
        float $$22 = 0.0f;
        float $$23 = 0.0f;
        for (int $$24 = $$14; $$24 < $$15; ++$$24) {
            double $$25 = 1.5 + (double)(Mth.sin((float)Math.PI * (float)$$24 / (float)$$15) * $$11);
            double $$26 = $$25 * $$16;
            float $$27 = Mth.cos($$13);
            $$6 += (double)(Mth.cos($$12) * $$27);
            $$7 += (double)Mth.sin($$13);
            $$8 += (double)(Mth.sin($$12) * $$27);
            $$13 *= $$21 ? 0.92f : 0.7f;
            $$13 += $$23 * 0.1f;
            $$12 += $$22 * 0.1f;
            $$23 *= 0.9f;
            $$22 *= 0.75f;
            $$23 += ($$19.nextFloat() - $$19.nextFloat()) * $$19.nextFloat() * 2.0f;
            $$22 += ($$19.nextFloat() - $$19.nextFloat()) * $$19.nextFloat() * 4.0f;
            if ($$24 == $$20 && $$11 > 1.0f) {
                this.createTunnel($$0, $$1, $$2, $$3, $$19.nextLong(), $$5, $$6, $$7, $$8, $$9, $$10, $$19.nextFloat() * 0.5f + 0.5f, $$12 - 1.5707964f, $$13 / 3.0f, $$24, $$15, 1.0, $$17, $$18);
                this.createTunnel($$0, $$1, $$2, $$3, $$19.nextLong(), $$5, $$6, $$7, $$8, $$9, $$10, $$19.nextFloat() * 0.5f + 0.5f, $$12 + 1.5707964f, $$13 / 3.0f, $$24, $$15, 1.0, $$17, $$18);
                return;
            }
            if ($$19.nextInt(4) == 0) continue;
            if (!CaveWorldCarver.canReach($$2.getPos(), $$6, $$8, $$24, $$15, $$11)) {
                return;
            }
            this.carveEllipsoid($$0, $$1, $$2, $$3, $$5, $$6, $$7, $$8, $$25 * $$9, $$26 * $$10, $$17, $$18);
        }
    }

    private static boolean shouldSkip(double $$0, double $$1, double $$2, double $$3) {
        if ($$1 <= $$3) {
            return true;
        }
        return $$0 * $$0 + $$1 * $$1 + $$2 * $$2 >= 1.0;
    }
}