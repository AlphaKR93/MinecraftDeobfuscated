/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class OreVeinifier {
    private static final float VEININESS_THRESHOLD = 0.4f;
    private static final int EDGE_ROUNDOFF_BEGIN = 20;
    private static final double MAX_EDGE_ROUNDOFF = 0.2;
    private static final float VEIN_SOLIDNESS = 0.7f;
    private static final float MIN_RICHNESS = 0.1f;
    private static final float MAX_RICHNESS = 0.3f;
    private static final float MAX_RICHNESS_THRESHOLD = 0.6f;
    private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02f;
    private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3f;

    private OreVeinifier() {
    }

    protected static NoiseChunk.BlockStateFiller create(DensityFunction $$0, DensityFunction $$1, DensityFunction $$2, PositionalRandomFactory $$3) {
        BlockState $$4 = null;
        return $$5 -> {
            double $$6 = $$0.compute($$5);
            int $$7 = $$5.blockY();
            VeinType $$8 = $$6 > 0.0 ? VeinType.COPPER : VeinType.IRON;
            double $$9 = Math.abs((double)$$6);
            int $$10 = $$8.maxY - $$7;
            int $$11 = $$7 - $$8.minY;
            if ($$11 < 0 || $$10 < 0) {
                return $$4;
            }
            int $$12 = Math.min((int)$$10, (int)$$11);
            double $$13 = Mth.clampedMap((double)$$12, 0.0, 20.0, -0.2, 0.0);
            if ($$9 + $$13 < (double)0.4f) {
                return $$4;
            }
            RandomSource $$14 = $$3.at($$5.blockX(), $$7, $$5.blockZ());
            if ($$14.nextFloat() > 0.7f) {
                return $$4;
            }
            if ($$1.compute($$5) >= 0.0) {
                return $$4;
            }
            double $$15 = Mth.clampedMap($$9, (double)0.4f, (double)0.6f, (double)0.1f, (double)0.3f);
            if ((double)$$14.nextFloat() < $$15 && $$2.compute($$5) > (double)-0.3f) {
                return $$14.nextFloat() < 0.02f ? $$8.rawOreBlock : $$8.ore;
            }
            return $$8.filler;
        };
    }

    protected static enum VeinType {
        COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
        IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

        final BlockState ore;
        final BlockState rawOreBlock;
        final BlockState filler;
        protected final int minY;
        protected final int maxY;

        private VeinType(BlockState $$0, BlockState $$1, BlockState $$2, int $$3, int $$4) {
            this.ore = $$0;
            this.rawOreBlock = $$1;
            this.filler = $$2;
            this.minY = $$3;
            this.maxY = $$4;
        }
    }
}