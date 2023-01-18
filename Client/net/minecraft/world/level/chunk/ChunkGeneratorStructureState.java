/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Ticker
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.TimeUnit
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.slf4j.Logger;

public class ChunkGeneratorStructureState {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RandomState randomState;
    private final BiomeSource biomeSource;
    private final long levelSeed;
    private final long concentricRingsSeed;
    private final Map<Structure, List<StructurePlacement>> placementsForStructure = new Object2ObjectOpenHashMap();
    private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> ringPositions = new Object2ObjectArrayMap();
    private boolean hasGeneratedPositions;
    private final List<Holder<StructureSet>> possibleStructureSets;

    public static ChunkGeneratorStructureState createForFlat(RandomState $$0, long $$12, BiomeSource $$2, Stream<Holder<StructureSet>> $$3) {
        List $$4 = $$3.filter($$1 -> ChunkGeneratorStructureState.hasBiomesForStructureSet((StructureSet)((Object)((Object)$$1.value())), $$2)).toList();
        return new ChunkGeneratorStructureState($$0, $$2, $$12, 0L, (List<Holder<StructureSet>>)$$4);
    }

    public static ChunkGeneratorStructureState createForNormal(RandomState $$0, long $$12, BiomeSource $$2, HolderLookup<StructureSet> $$3) {
        List $$4 = (List)$$3.listElements().filter($$1 -> ChunkGeneratorStructureState.hasBiomesForStructureSet((StructureSet)((Object)((Object)$$1.value())), $$2)).collect(Collectors.toUnmodifiableList());
        return new ChunkGeneratorStructureState($$0, $$2, $$12, $$12, (List<Holder<StructureSet>>)$$4);
    }

    private static boolean hasBiomesForStructureSet(StructureSet $$02, BiomeSource $$1) {
        Stream $$2 = $$02.structures().stream().flatMap($$0 -> {
            Structure $$1 = $$0.structure().value();
            return $$1.biomes().stream();
        });
        return $$2.anyMatch(arg_0 -> $$1.possibleBiomes().contains(arg_0));
    }

    private ChunkGeneratorStructureState(RandomState $$0, BiomeSource $$1, long $$2, long $$3, List<Holder<StructureSet>> $$4) {
        this.randomState = $$0;
        this.levelSeed = $$2;
        this.biomeSource = $$1;
        this.concentricRingsSeed = $$3;
        this.possibleStructureSets = $$4;
    }

    public List<Holder<StructureSet>> possibleStructureSets() {
        return this.possibleStructureSets;
    }

    private void generatePositions() {
        Set<Holder<Biome>> $$0 = this.biomeSource.possibleBiomes();
        this.possibleStructureSets().forEach($$1 -> {
            StructurePlacement $$6;
            StructureSet $$2 = (StructureSet)((Object)((Object)$$1.value()));
            boolean $$3 = false;
            for (StructureSet.StructureSelectionEntry $$4 : $$2.structures()) {
                Structure $$5 = $$4.structure().value();
                if (!$$5.biomes().stream().anyMatch(arg_0 -> ((Set)$$0).contains(arg_0))) continue;
                ((List)this.placementsForStructure.computeIfAbsent((Object)$$5, $$0 -> new ArrayList())).add((Object)$$2.placement());
                $$3 = true;
            }
            if ($$3 && ($$6 = $$2.placement()) instanceof ConcentricRingsStructurePlacement) {
                ConcentricRingsStructurePlacement $$7 = (ConcentricRingsStructurePlacement)$$6;
                this.ringPositions.put((Object)$$7, this.generateRingPositions((Holder<StructureSet>)$$1, $$7));
            }
        });
    }

    private CompletableFuture<List<ChunkPos>> generateRingPositions(Holder<StructureSet> $$0, ConcentricRingsStructurePlacement $$1) {
        if ($$1.count() == 0) {
            return CompletableFuture.completedFuture((Object)List.of());
        }
        Stopwatch $$22 = Stopwatch.createStarted((Ticker)Util.TICKER);
        int $$3 = $$1.distance();
        int $$4 = $$1.count();
        ArrayList $$5 = new ArrayList($$4);
        int $$6 = $$1.spread();
        HolderSet<Biome> $$7 = $$1.preferredBiomes();
        RandomSource $$8 = RandomSource.create();
        $$8.setSeed(this.concentricRingsSeed);
        double $$9 = $$8.nextDouble() * Math.PI * 2.0;
        int $$10 = 0;
        int $$11 = 0;
        for (int $$12 = 0; $$12 < $$4; ++$$12) {
            double $$13 = (double)(4 * $$3 + $$3 * $$11 * 6) + ($$8.nextDouble() - 0.5) * ((double)$$3 * 2.5);
            int $$14 = (int)Math.round((double)(Math.cos((double)$$9) * $$13));
            int $$15 = (int)Math.round((double)(Math.sin((double)$$9) * $$13));
            RandomSource $$16 = $$8.fork();
            $$5.add((Object)CompletableFuture.supplyAsync(() -> {
                Pair<BlockPos, Holder<Biome>> $$4 = this.biomeSource.findBiomeHorizontal(SectionPos.sectionToBlockCoord($$14, 8), 0, SectionPos.sectionToBlockCoord($$15, 8), 112, (Predicate<Holder<Biome>>)((Predicate)$$7::contains), $$16, this.randomState.sampler());
                if ($$4 != null) {
                    BlockPos $$5 = (BlockPos)$$4.getFirst();
                    return new ChunkPos(SectionPos.blockToSectionCoord($$5.getX()), SectionPos.blockToSectionCoord($$5.getZ()));
                }
                return new ChunkPos($$14, $$15);
            }, (Executor)Util.backgroundExecutor()));
            $$9 += Math.PI * 2 / (double)$$6;
            if (++$$10 != $$6) continue;
            $$10 = 0;
            $$6 += 2 * $$6 / (++$$11 + 1);
            $$6 = Math.min((int)$$6, (int)($$4 - $$12));
            $$9 += $$8.nextDouble() * Math.PI * 2.0;
        }
        return Util.sequence($$5).thenApply($$2 -> {
            double $$3 = (double)$$22.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
            LOGGER.debug("Calculation for {} took {}s", (Object)$$0, (Object)$$3);
            return $$2;
        });
    }

    public void ensureStructuresGenerated() {
        if (!this.hasGeneratedPositions) {
            this.generatePositions();
            this.hasGeneratedPositions = true;
        }
    }

    @Nullable
    public List<ChunkPos> getRingPositionsFor(ConcentricRingsStructurePlacement $$0) {
        this.ensureStructuresGenerated();
        CompletableFuture $$1 = (CompletableFuture)this.ringPositions.get((Object)$$0);
        return $$1 != null ? (List)$$1.join() : null;
    }

    public List<StructurePlacement> getPlacementsForStructure(Holder<Structure> $$0) {
        this.ensureStructuresGenerated();
        return (List)this.placementsForStructure.getOrDefault((Object)$$0.value(), (Object)List.of());
    }

    public RandomState randomState() {
        return this.randomState;
    }

    public boolean hasStructureChunkInRange(Holder<StructureSet> $$0, int $$1, int $$2, int $$3) {
        StructurePlacement $$4 = $$0.value().placement();
        for (int $$5 = $$1 - $$3; $$5 <= $$1 + $$3; ++$$5) {
            for (int $$6 = $$2 - $$3; $$6 <= $$2 + $$3; ++$$6) {
                if (!$$4.isStructureChunk(this, $$5, $$6)) continue;
                return true;
            }
        }
        return false;
    }

    public long getLevelSeed() {
        return this.levelSeed;
    }
}