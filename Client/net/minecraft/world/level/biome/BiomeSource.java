/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;

public abstract class BiomeSource
implements BiomeResolver {
    public static final Codec<BiomeSource> CODEC = BuiltInRegistries.BIOME_SOURCE.byNameCodec().dispatchStable(BiomeSource::codec, Function.identity());
    private final Set<Holder<Biome>> possibleBiomes;

    protected BiomeSource(Stream<Holder<Biome>> $$0) {
        this((List<Holder<Biome>>)$$0.distinct().toList());
    }

    protected BiomeSource(List<Holder<Biome>> $$0) {
        this.possibleBiomes = new ObjectLinkedOpenHashSet($$0);
    }

    protected abstract Codec<? extends BiomeSource> codec();

    public Set<Holder<Biome>> possibleBiomes() {
        return this.possibleBiomes;
    }

    public Set<Holder<Biome>> getBiomesWithin(int $$0, int $$1, int $$2, int $$3, Climate.Sampler $$4) {
        int $$5 = QuartPos.fromBlock($$0 - $$3);
        int $$6 = QuartPos.fromBlock($$1 - $$3);
        int $$7 = QuartPos.fromBlock($$2 - $$3);
        int $$8 = QuartPos.fromBlock($$0 + $$3);
        int $$9 = QuartPos.fromBlock($$1 + $$3);
        int $$10 = QuartPos.fromBlock($$2 + $$3);
        int $$11 = $$8 - $$5 + 1;
        int $$12 = $$9 - $$6 + 1;
        int $$13 = $$10 - $$7 + 1;
        HashSet $$14 = Sets.newHashSet();
        for (int $$15 = 0; $$15 < $$13; ++$$15) {
            for (int $$16 = 0; $$16 < $$11; ++$$16) {
                for (int $$17 = 0; $$17 < $$12; ++$$17) {
                    int $$18 = $$5 + $$16;
                    int $$19 = $$6 + $$17;
                    int $$20 = $$7 + $$15;
                    $$14.add(this.getNoiseBiome($$18, $$19, $$20, $$4));
                }
            }
        }
        return $$14;
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int $$0, int $$1, int $$2, int $$3, Predicate<Holder<Biome>> $$4, RandomSource $$5, Climate.Sampler $$6) {
        return this.findBiomeHorizontal($$0, $$1, $$2, $$3, 1, $$4, $$5, false, $$6);
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(BlockPos $$0, int $$1, int $$2, int $$3, Predicate<Holder<Biome>> $$4, Climate.Sampler $$5, LevelReader $$6) {
        Set $$7 = (Set)this.possibleBiomes().stream().filter($$4).collect(Collectors.toUnmodifiableSet());
        if ($$7.isEmpty()) {
            return null;
        }
        int $$8 = Math.floorDiv((int)$$1, (int)$$2);
        int[] $$9 = Mth.outFromOrigin($$0.getY(), $$6.getMinBuildHeight() + 1, $$6.getMaxBuildHeight(), $$3).toArray();
        for (BlockPos.MutableBlockPos $$10 : BlockPos.spiralAround(BlockPos.ZERO, $$8, Direction.EAST, Direction.SOUTH)) {
            int $$11 = $$0.getX() + $$10.getX() * $$2;
            int $$12 = $$0.getZ() + $$10.getZ() * $$2;
            int $$13 = QuartPos.fromBlock($$11);
            int $$14 = QuartPos.fromBlock($$12);
            for (int $$15 : $$9) {
                int $$16 = QuartPos.fromBlock($$15);
                Holder<Biome> $$17 = this.getNoiseBiome($$13, $$16, $$14, $$5);
                if (!$$7.contains($$17)) continue;
                return Pair.of((Object)new BlockPos($$11, $$15, $$12), $$17);
            }
        }
        return null;
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int $$0, int $$1, int $$2, int $$3, int $$4, Predicate<Holder<Biome>> $$5, RandomSource $$6, boolean $$7, Climate.Sampler $$8) {
        int $$15;
        int $$9 = QuartPos.fromBlock($$0);
        int $$10 = QuartPos.fromBlock($$2);
        int $$11 = QuartPos.fromBlock($$3);
        int $$12 = QuartPos.fromBlock($$1);
        Pair $$13 = null;
        int $$14 = 0;
        for (int $$16 = $$15 = $$7 ? 0 : $$11; $$16 <= $$11; $$16 += $$4) {
            int $$17;
            int n = $$17 = SharedConstants.debugGenerateSquareTerrainWithoutNoise ? 0 : -$$16;
            while ($$17 <= $$16) {
                boolean $$18 = Math.abs((int)$$17) == $$16;
                for (int $$19 = -$$16; $$19 <= $$16; $$19 += $$4) {
                    int $$22;
                    int $$21;
                    Holder<Biome> $$23;
                    if ($$7) {
                        boolean $$20;
                        boolean bl = $$20 = Math.abs((int)$$19) == $$16;
                        if (!$$20 && !$$18) continue;
                    }
                    if (!$$5.test($$23 = this.getNoiseBiome($$21 = $$9 + $$19, $$12, $$22 = $$10 + $$17, $$8))) continue;
                    if ($$13 == null || $$6.nextInt($$14 + 1) == 0) {
                        BlockPos $$24 = new BlockPos(QuartPos.toBlock($$21), $$1, QuartPos.toBlock($$22));
                        if ($$7) {
                            return Pair.of((Object)$$24, $$23);
                        }
                        $$13 = Pair.of((Object)$$24, $$23);
                    }
                    ++$$14;
                }
                $$17 += $$4;
            }
        }
        return $$13;
    }

    @Override
    public abstract Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4);

    public void addDebugInfo(List<String> $$0, BlockPos $$1, Climate.Sampler $$2) {
    }
}