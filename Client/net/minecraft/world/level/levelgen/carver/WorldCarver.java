/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Set
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.NetherWorldCarver;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends CarverConfiguration> {
    public static final WorldCarver<CaveCarverConfiguration> CAVE = WorldCarver.register("cave", new CaveWorldCarver(CaveCarverConfiguration.CODEC));
    public static final WorldCarver<CaveCarverConfiguration> NETHER_CAVE = WorldCarver.register("nether_cave", new NetherWorldCarver(CaveCarverConfiguration.CODEC));
    public static final WorldCarver<CanyonCarverConfiguration> CANYON = WorldCarver.register("canyon", new CanyonWorldCarver(CanyonCarverConfiguration.CODEC));
    protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
    protected static final FluidState WATER = Fluids.WATER.defaultFluidState();
    protected static final FluidState LAVA = Fluids.LAVA.defaultFluidState();
    protected Set<Fluid> liquids = ImmutableSet.of((Object)Fluids.WATER);
    private final Codec<ConfiguredWorldCarver<C>> configuredCodec;

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String $$0, F $$1) {
        return (F)Registry.register(BuiltInRegistries.CARVER, $$0, $$1);
    }

    public WorldCarver(Codec<C> $$0) {
        this.configuredCodec = $$0.fieldOf("config").xmap(this::configured, ConfiguredWorldCarver::config).codec();
    }

    public ConfiguredWorldCarver<C> configured(C $$0) {
        return new ConfiguredWorldCarver<C>(this, $$0);
    }

    public Codec<ConfiguredWorldCarver<C>> configuredCodec() {
        return this.configuredCodec;
    }

    public int getRange() {
        return 4;
    }

    protected boolean carveEllipsoid(CarvingContext $$0, C $$1, ChunkAccess $$2, Function<BlockPos, Holder<Biome>> $$3, Aquifer $$4, double $$5, double $$6, double $$7, double $$8, double $$9, CarvingMask $$10, CarveSkipChecker $$11) {
        ChunkPos $$12 = $$2.getPos();
        double $$13 = $$12.getMiddleBlockX();
        double $$14 = $$12.getMiddleBlockZ();
        double $$15 = 16.0 + $$8 * 2.0;
        if (Math.abs((double)($$5 - $$13)) > $$15 || Math.abs((double)($$7 - $$14)) > $$15) {
            return false;
        }
        int $$16 = $$12.getMinBlockX();
        int $$17 = $$12.getMinBlockZ();
        int $$18 = Math.max((int)(Mth.floor($$5 - $$8) - $$16 - 1), (int)0);
        int $$19 = Math.min((int)(Mth.floor($$5 + $$8) - $$16), (int)15);
        int $$20 = Math.max((int)(Mth.floor($$6 - $$9) - 1), (int)($$0.getMinGenY() + 1));
        int $$21 = $$2.isUpgrading() ? 0 : 7;
        int $$22 = Math.min((int)(Mth.floor($$6 + $$9) + 1), (int)($$0.getMinGenY() + $$0.getGenDepth() - 1 - $$21));
        int $$23 = Math.max((int)(Mth.floor($$7 - $$8) - $$17 - 1), (int)0);
        int $$24 = Math.min((int)(Mth.floor($$7 + $$8) - $$17), (int)15);
        boolean $$25 = false;
        BlockPos.MutableBlockPos $$26 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$27 = new BlockPos.MutableBlockPos();
        for (int $$28 = $$18; $$28 <= $$19; ++$$28) {
            int $$29 = $$12.getBlockX($$28);
            double $$30 = ((double)$$29 + 0.5 - $$5) / $$8;
            for (int $$31 = $$23; $$31 <= $$24; ++$$31) {
                int $$32 = $$12.getBlockZ($$31);
                double $$33 = ((double)$$32 + 0.5 - $$7) / $$8;
                if ($$30 * $$30 + $$33 * $$33 >= 1.0) continue;
                MutableBoolean $$34 = new MutableBoolean(false);
                for (int $$35 = $$22; $$35 > $$20; --$$35) {
                    double $$36 = ((double)$$35 - 0.5 - $$6) / $$9;
                    if ($$11.shouldSkip($$0, $$30, $$36, $$33, $$35) || $$10.get($$28, $$35, $$31) && !WorldCarver.isDebugEnabled($$1)) continue;
                    $$10.set($$28, $$35, $$31);
                    $$26.set($$29, $$35, $$32);
                    $$25 |= this.carveBlock($$0, $$1, $$2, $$3, $$10, $$26, $$27, $$4, $$34);
                }
            }
        }
        return $$25;
    }

    protected boolean carveBlock(CarvingContext $$0, C $$1, ChunkAccess $$22, Function<BlockPos, Holder<Biome>> $$3, CarvingMask $$4, BlockPos.MutableBlockPos $$5, BlockPos.MutableBlockPos $$6, Aquifer $$7, MutableBoolean $$8) {
        BlockState $$9 = $$22.getBlockState($$5);
        if ($$9.is(Blocks.GRASS_BLOCK) || $$9.is(Blocks.MYCELIUM)) {
            $$8.setTrue();
        }
        if (!this.canReplaceBlock($$1, $$9) && !WorldCarver.isDebugEnabled($$1)) {
            return false;
        }
        BlockState $$10 = this.getCarveState($$0, $$1, $$5, $$7);
        if ($$10 == null) {
            return false;
        }
        $$22.setBlockState($$5, $$10, false);
        if ($$7.shouldScheduleFluidUpdate() && !$$10.getFluidState().isEmpty()) {
            $$22.markPosForPostprocessing($$5);
        }
        if ($$8.isTrue()) {
            $$6.setWithOffset((Vec3i)$$5, Direction.DOWN);
            if ($$22.getBlockState($$6).is(Blocks.DIRT)) {
                $$0.topMaterial($$3, $$22, $$6, !$$10.getFluidState().isEmpty()).ifPresent($$2 -> {
                    $$22.setBlockState($$6, (BlockState)$$2, false);
                    if (!$$2.getFluidState().isEmpty()) {
                        $$22.markPosForPostprocessing($$6);
                    }
                });
            }
        }
        return true;
    }

    @Nullable
    private BlockState getCarveState(CarvingContext $$0, C $$1, BlockPos $$2, Aquifer $$3) {
        if ($$2.getY() <= ((CarverConfiguration)$$1).lavaLevel.resolveY($$0)) {
            return LAVA.createLegacyBlock();
        }
        BlockState $$4 = $$3.computeSubstance(new DensityFunction.SinglePointContext($$2.getX(), $$2.getY(), $$2.getZ()), 0.0);
        if ($$4 == null) {
            return WorldCarver.isDebugEnabled($$1) ? ((CarverConfiguration)$$1).debugSettings.getBarrierState() : null;
        }
        return WorldCarver.isDebugEnabled($$1) ? WorldCarver.getDebugState($$1, $$4) : $$4;
    }

    private static BlockState getDebugState(CarverConfiguration $$0, BlockState $$1) {
        if ($$1.is(Blocks.AIR)) {
            return $$0.debugSettings.getAirState();
        }
        if ($$1.is(Blocks.WATER)) {
            BlockState $$2 = $$0.debugSettings.getWaterState();
            if ($$2.hasProperty(BlockStateProperties.WATERLOGGED)) {
                return (BlockState)$$2.setValue(BlockStateProperties.WATERLOGGED, true);
            }
            return $$2;
        }
        if ($$1.is(Blocks.LAVA)) {
            return $$0.debugSettings.getLavaState();
        }
        return $$1;
    }

    public abstract boolean carve(CarvingContext var1, C var2, ChunkAccess var3, Function<BlockPos, Holder<Biome>> var4, RandomSource var5, Aquifer var6, ChunkPos var7, CarvingMask var8);

    public abstract boolean isStartChunk(C var1, RandomSource var2);

    protected boolean canReplaceBlock(C $$0, BlockState $$1) {
        return $$1.is(((CarverConfiguration)$$0).replaceable);
    }

    protected static boolean canReach(ChunkPos $$0, double $$1, double $$2, int $$3, int $$4, float $$5) {
        double $$11;
        double $$10;
        double $$7;
        double $$9;
        double $$6 = $$0.getMiddleBlockX();
        double $$8 = $$1 - $$6;
        return $$8 * $$8 + ($$9 = $$2 - ($$7 = (double)$$0.getMiddleBlockZ())) * $$9 - ($$10 = (double)($$4 - $$3)) * $$10 <= ($$11 = (double)($$5 + 2.0f + 16.0f)) * $$11;
    }

    private static boolean isDebugEnabled(CarverConfiguration $$0) {
        return $$0.debugSettings.isDebugMode();
    }

    public static interface CarveSkipChecker {
        public boolean shouldSkip(CarvingContext var1, double var2, double var4, double var6, int var8);
    }
}