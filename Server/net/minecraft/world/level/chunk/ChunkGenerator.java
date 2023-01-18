/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.level.chunk;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class ChunkGenerator {
    public static final Codec<ChunkGenerator> CODEC = BuiltInRegistries.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
    protected final BiomeSource biomeSource;
    private final Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;
    private final Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    public ChunkGenerator(BiomeSource $$02) {
        this($$02, (Function<Holder<Biome>, BiomeGenerationSettings>)((Function)$$0 -> ((Biome)$$0.value()).getGenerationSettings()));
    }

    public ChunkGenerator(BiomeSource $$0, Function<Holder<Biome>, BiomeGenerationSettings> $$1) {
        this.biomeSource = $$0;
        this.generationSettingsGetter = $$1;
        this.featuresPerStep = Suppliers.memoize(() -> FeatureSorter.buildFeaturesPerStep(List.copyOf($$0.possibleBiomes()), $$1 -> ((BiomeGenerationSettings)$$1.apply($$1)).features(), true));
    }

    protected abstract Codec<? extends ChunkGenerator> codec();

    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> $$0, RandomState $$1, long $$2) {
        return ChunkGeneratorStructureState.createForNormal($$1, $$2, this.biomeSource, $$0);
    }

    public Optional<ResourceKey<Codec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
        return BuiltInRegistries.CHUNK_GENERATOR.getResourceKey(this.codec());
    }

    public CompletableFuture<ChunkAccess> createBiomes(Executor $$0, RandomState $$1, Blender $$2, StructureManager $$3, ChunkAccess $$4) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            $$4.fillBiomesFromNoise(this.biomeSource, $$1.sampler());
            return $$4;
        }), (Executor)Util.backgroundExecutor());
    }

    public abstract void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7, GenerationStep.Carving var8);

    @Nullable
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel $$02, HolderSet<Structure> $$1, BlockPos $$2, int $$3, boolean $$4) {
        ChunkGeneratorStructureState $$5 = $$02.getChunkSource().getGeneratorState();
        Object2ObjectArrayMap $$6 = new Object2ObjectArrayMap();
        for (Holder holder : $$1) {
            for (StructurePlacement $$8 : $$5.getPlacementsForStructure(holder)) {
                ((Set)$$6.computeIfAbsent((Object)$$8, $$0 -> new ObjectArraySet())).add((Object)holder);
            }
        }
        if ($$6.isEmpty()) {
            return null;
        }
        Pair<BlockPos, Holder<Structure>> $$9 = null;
        double d = Double.MAX_VALUE;
        StructureManager $$11 = $$02.structureManager();
        ArrayList $$12 = new ArrayList($$6.size());
        for (Map.Entry $$13 : $$6.entrySet()) {
            StructurePlacement $$14 = (StructurePlacement)$$13.getKey();
            if ($$14 instanceof ConcentricRingsStructurePlacement) {
                BlockPos $$17;
                double $$18;
                ConcentricRingsStructurePlacement $$15 = (ConcentricRingsStructurePlacement)$$14;
                Pair<BlockPos, Holder<Structure>> $$16 = this.getNearestGeneratedStructure((Set<Holder<Structure>>)((Set)$$13.getValue()), $$02, $$11, $$2, $$4, $$15);
                if ($$16 == null || !(($$18 = $$2.distSqr($$17 = (BlockPos)$$16.getFirst())) < d)) continue;
                d = $$18;
                $$9 = $$16;
                continue;
            }
            if (!($$14 instanceof RandomSpreadStructurePlacement)) continue;
            $$12.add((Object)$$13);
        }
        if (!$$12.isEmpty()) {
            int $$19 = SectionPos.blockToSectionCoord($$2.getX());
            int $$20 = SectionPos.blockToSectionCoord($$2.getZ());
            for (int $$21 = 0; $$21 <= $$3; ++$$21) {
                boolean $$22 = false;
                for (Map.Entry $$23 : $$12) {
                    RandomSpreadStructurePlacement $$24 = (RandomSpreadStructurePlacement)$$23.getKey();
                    Pair<BlockPos, Holder<Structure>> $$25 = ChunkGenerator.getNearestGeneratedStructure((Set<Holder<Structure>>)((Set)$$23.getValue()), $$02, $$11, $$19, $$20, $$21, $$4, $$5.getLevelSeed(), $$24);
                    if ($$25 == null) continue;
                    $$22 = true;
                    double $$26 = $$2.distSqr((Vec3i)$$25.getFirst());
                    if (!($$26 < d)) continue;
                    d = $$26;
                    $$9 = $$25;
                }
                if (!$$22) continue;
                return $$9;
            }
        }
        return $$9;
    }

    @Nullable
    private Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> $$0, ServerLevel $$1, StructureManager $$2, BlockPos $$3, boolean $$4, ConcentricRingsStructurePlacement $$5) {
        List<ChunkPos> $$6 = $$1.getChunkSource().getGeneratorState().getRingPositionsFor($$5);
        if ($$6 == null) {
            throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
        }
        Pair<BlockPos, Holder<Structure>> $$7 = null;
        double $$8 = Double.MAX_VALUE;
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (ChunkPos $$10 : $$6) {
            Pair<BlockPos, Holder<Structure>> $$13;
            $$9.set(SectionPos.sectionToBlockCoord($$10.x, 8), 32, SectionPos.sectionToBlockCoord($$10.z, 8));
            double $$11 = $$9.distSqr($$3);
            boolean $$12 = $$7 == null || $$11 < $$8;
            if (!$$12 || ($$13 = ChunkGenerator.getStructureGeneratingAt($$0, $$1, $$2, $$4, $$5, $$10)) == null) continue;
            $$7 = $$13;
            $$8 = $$11;
        }
        return $$7;
    }

    @Nullable
    private static Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> $$0, LevelReader $$1, StructureManager $$2, int $$3, int $$4, int $$5, boolean $$6, long $$7, RandomSpreadStructurePlacement $$8) {
        int $$9 = $$8.spacing();
        for (int $$10 = -$$5; $$10 <= $$5; ++$$10) {
            boolean $$11 = $$10 == -$$5 || $$10 == $$5;
            for (int $$12 = -$$5; $$12 <= $$5; ++$$12) {
                int $$15;
                int $$14;
                ChunkPos $$16;
                Pair<BlockPos, Holder<Structure>> $$17;
                boolean $$13;
                boolean bl = $$13 = $$12 == -$$5 || $$12 == $$5;
                if (!$$11 && !$$13 || ($$17 = ChunkGenerator.getStructureGeneratingAt($$0, $$1, $$2, $$6, $$8, $$16 = $$8.getPotentialStructureChunk($$7, $$14 = $$3 + $$9 * $$10, $$15 = $$4 + $$9 * $$12))) == null) continue;
                return $$17;
            }
        }
        return null;
    }

    @Nullable
    private static Pair<BlockPos, Holder<Structure>> getStructureGeneratingAt(Set<Holder<Structure>> $$0, LevelReader $$1, StructureManager $$2, boolean $$3, StructurePlacement $$4, ChunkPos $$5) {
        for (Holder $$6 : $$0) {
            StructureCheckResult $$7 = $$2.checkStructurePresence($$5, (Structure)$$6.value(), $$3);
            if ($$7 == StructureCheckResult.START_NOT_PRESENT) continue;
            if (!$$3 && $$7 == StructureCheckResult.START_PRESENT) {
                return Pair.of((Object)$$4.getLocatePos($$5), (Object)$$6);
            }
            ChunkAccess $$8 = $$1.getChunk($$5.x, $$5.z, ChunkStatus.STRUCTURE_STARTS);
            StructureStart $$9 = $$2.getStartForStructure(SectionPos.bottomOf($$8), (Structure)$$6.value(), $$8);
            if ($$9 == null || !$$9.isValid() || $$3 && !ChunkGenerator.tryAddReference($$2, $$9)) continue;
            return Pair.of((Object)$$4.getLocatePos($$9.getChunkPos()), (Object)$$6);
        }
        return null;
    }

    private static boolean tryAddReference(StructureManager $$0, StructureStart $$1) {
        if ($$1.canBeReferenced()) {
            $$0.addReference($$1);
            return true;
        }
        return false;
    }

    public void applyBiomeDecoration(WorldGenLevel $$02, ChunkAccess $$1, StructureManager $$2) {
        ChunkPos $$3 = $$1.getPos();
        if (SharedConstants.debugVoidTerrain($$3)) {
            return;
        }
        SectionPos $$4 = SectionPos.of($$3, $$02.getMinSection());
        BlockPos $$52 = $$4.origin();
        Registry<Structure> $$6 = $$02.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Map $$7 = (Map)$$6.stream().collect(Collectors.groupingBy($$0 -> $$0.step().ordinal()));
        List $$8 = (List)this.featuresPerStep.get();
        WorldgenRandom $$9 = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
        long $$10 = $$9.setDecorationSeed($$02.getSeed(), $$52.getX(), $$52.getZ());
        ObjectArraySet $$11 = new ObjectArraySet();
        ChunkPos.rangeClosed($$4.chunk(), 1).forEach(arg_0 -> ChunkGenerator.lambda$applyBiomeDecoration$6($$02, (Set)$$11, arg_0));
        $$11.retainAll(this.biomeSource.possibleBiomes());
        int $$12 = $$8.size();
        try {
            Registry<PlacedFeature> $$13 = $$02.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
            int $$14 = Math.max((int)GenerationStep.Decoration.values().length, (int)$$12);
            for (int $$15 = 0; $$15 < $$14; ++$$15) {
                int $$16 = 0;
                if ($$2.shouldGenerateStructures()) {
                    List $$17 = (List)$$7.getOrDefault((Object)$$15, (Object)Collections.emptyList());
                    for (Structure $$18 : $$17) {
                        $$9.setFeatureSeed($$10, $$16, $$15);
                        Supplier $$19 = () -> (String)$$6.getResourceKey($$18).map(Object::toString).orElseGet(() -> ((Structure)$$18).toString());
                        try {
                            $$02.setCurrentlyGenerating((Supplier<String>)$$19);
                            $$2.startsForStructure($$4, $$18).forEach($$5 -> $$5.placeInChunk($$02, $$2, this, $$9, ChunkGenerator.getWritableArea($$1), $$3));
                        }
                        catch (Exception $$20) {
                            CrashReport $$21 = CrashReport.forThrowable($$20, "Feature placement");
                            $$21.addCategory("Feature").setDetail("Description", () -> ((Supplier)$$19).get());
                            throw new ReportedException($$21);
                        }
                        ++$$16;
                    }
                }
                if ($$15 >= $$12) continue;
                IntArraySet $$22 = new IntArraySet();
                for (Holder $$23 : $$11) {
                    List<HolderSet<PlacedFeature>> $$24 = ((BiomeGenerationSettings)this.generationSettingsGetter.apply((Object)$$23)).features();
                    if ($$15 >= $$24.size()) continue;
                    HolderSet $$25 = (HolderSet)$$24.get($$15);
                    FeatureSorter.StepFeatureData $$26 = (FeatureSorter.StepFeatureData)((Object)$$8.get($$15));
                    $$25.stream().map(Holder::value).forEach(arg_0 -> ChunkGenerator.lambda$applyBiomeDecoration$9((IntSet)$$22, $$26, arg_0));
                }
                int $$27 = $$22.size();
                int[] $$28 = $$22.toIntArray();
                Arrays.sort((int[])$$28);
                FeatureSorter.StepFeatureData $$29 = (FeatureSorter.StepFeatureData)((Object)$$8.get($$15));
                for (int $$30 = 0; $$30 < $$27; ++$$30) {
                    int $$31 = $$28[$$30];
                    PlacedFeature $$32 = (PlacedFeature)((Object)$$29.features().get($$31));
                    Supplier $$33 = () -> (String)$$13.getResourceKey($$32).map(Object::toString).orElseGet($$32::toString);
                    $$9.setFeatureSeed($$10, $$31, $$15);
                    try {
                        $$02.setCurrentlyGenerating((Supplier<String>)$$33);
                        $$32.placeWithBiomeCheck($$02, this, $$9, $$52);
                        continue;
                    }
                    catch (Exception $$34) {
                        CrashReport $$35 = CrashReport.forThrowable($$34, "Feature placement");
                        $$35.addCategory("Feature").setDetail("Description", () -> ((Supplier)$$33).get());
                        throw new ReportedException($$35);
                    }
                }
            }
            $$02.setCurrentlyGenerating(null);
        }
        catch (Exception $$36) {
            CrashReport $$37 = CrashReport.forThrowable($$36, "Biome decoration");
            $$37.addCategory("Generation").setDetail("CenterX", $$3.x).setDetail("CenterZ", $$3.z).setDetail("Seed", $$10);
            throw new ReportedException($$37);
        }
    }

    private static BoundingBox getWritableArea(ChunkAccess $$0) {
        ChunkPos $$1 = $$0.getPos();
        int $$2 = $$1.getMinBlockX();
        int $$3 = $$1.getMinBlockZ();
        LevelHeightAccessor $$4 = $$0.getHeightAccessorForGeneration();
        int $$5 = $$4.getMinBuildHeight() + 1;
        int $$6 = $$4.getMaxBuildHeight() - 1;
        return new BoundingBox($$2, $$5, $$3, $$2 + 15, $$6, $$3 + 15);
    }

    public abstract void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4);

    public abstract void spawnOriginalMobs(WorldGenRegion var1);

    public int getSpawnHeight(LevelHeightAccessor $$0) {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public abstract int getGenDepth();

    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> $$0, StructureManager $$12, MobCategory $$22, BlockPos $$3) {
        Map<Structure, LongSet> $$4 = $$12.getAllStructuresAt($$3);
        for (Map.Entry $$5 : $$4.entrySet()) {
            Structure $$6 = (Structure)$$5.getKey();
            StructureSpawnOverride $$7 = (StructureSpawnOverride)((Object)$$6.spawnOverrides().get((Object)$$22));
            if ($$7 == null) continue;
            MutableBoolean $$8 = new MutableBoolean(false);
            Predicate $$9 = $$7.boundingBox() == StructureSpawnOverride.BoundingBoxType.PIECE ? $$2 -> $$12.structureHasPieceAt($$3, (StructureStart)$$2) : $$1 -> $$1.getBoundingBox().isInside($$3);
            $$12.fillStartsForStructure($$6, (LongSet)$$5.getValue(), (Consumer<StructureStart>)((Consumer)$$2 -> {
                if ($$8.isFalse() && $$9.test($$2)) {
                    $$8.setTrue();
                }
            }));
            if (!$$8.isTrue()) continue;
            return $$7.spawns();
        }
        return $$0.value().getMobSettings().getMobs($$22);
    }

    public void createStructures(RegistryAccess $$0, ChunkGeneratorStructureState $$1, StructureManager $$2, ChunkAccess $$3, StructureTemplateManager $$4) {
        ChunkPos $$5 = $$3.getPos();
        SectionPos $$6 = SectionPos.bottomOf($$3);
        RandomState $$7 = $$1.randomState();
        $$1.possibleStructureSets().forEach($$8 -> {
            StructurePlacement $$9 = ((StructureSet)((Object)((Object)$$8.value()))).placement();
            List<StructureSet.StructureSelectionEntry> $$10 = ((StructureSet)((Object)((Object)$$8.value()))).structures();
            for (StructureSet.StructureSelectionEntry $$11 : $$10) {
                StructureStart $$12 = $$2.getStartForStructure($$6, $$11.structure().value(), $$3);
                if ($$12 == null || !$$12.isValid()) continue;
                return;
            }
            if (!$$9.isStructureChunk($$1, $$4.x, $$4.z)) {
                return;
            }
            if ($$10.size() == 1) {
                this.tryGenerateStructure((StructureSet.StructureSelectionEntry)((Object)((Object)$$10.get(0))), $$2, $$0, $$7, $$4, $$1.getLevelSeed(), $$3, $$5, $$6);
                return;
            }
            ArrayList $$13 = new ArrayList($$10.size());
            $$13.addAll($$10);
            WorldgenRandom $$14 = new WorldgenRandom(new LegacyRandomSource(0L));
            $$14.setLargeFeatureSeed($$1.getLevelSeed(), $$4.x, $$4.z);
            int $$15 = 0;
            for (StructureSet.StructureSelectionEntry $$16 : $$13) {
                $$15 += $$16.weight();
            }
            while (!$$13.isEmpty()) {
                StructureSet.StructureSelectionEntry $$19;
                int $$17 = $$14.nextInt($$15);
                int $$18 = 0;
                Iterator iterator = $$13.iterator();
                while (iterator.hasNext() && ($$17 -= ($$19 = (StructureSet.StructureSelectionEntry)((Object)((Object)iterator.next()))).weight()) >= 0) {
                    ++$$18;
                }
                StructureSet.StructureSelectionEntry $$20 = (StructureSet.StructureSelectionEntry)((Object)((Object)$$13.get($$18)));
                if (this.tryGenerateStructure($$20, $$2, $$0, $$7, $$4, $$1.getLevelSeed(), $$3, $$5, $$6)) {
                    return;
                }
                $$13.remove($$18);
                $$15 -= $$20.weight();
            }
        });
    }

    private boolean tryGenerateStructure(StructureSet.StructureSelectionEntry $$0, StructureManager $$1, RegistryAccess $$2, RandomState $$3, StructureTemplateManager $$4, long $$5, ChunkAccess $$6, ChunkPos $$7, SectionPos $$8) {
        Structure $$9 = $$0.structure().value();
        int $$10 = ChunkGenerator.fetchReferences($$1, $$6, $$8, $$9);
        HolderSet<Biome> $$11 = $$9.biomes();
        Predicate $$12 = $$11::contains;
        StructureStart $$13 = $$9.generate($$2, this, this.biomeSource, $$3, $$4, $$5, $$7, $$10, $$6, (Predicate<Holder<Biome>>)$$12);
        if ($$13.isValid()) {
            $$1.setStartForStructure($$8, $$9, $$13, $$6);
            return true;
        }
        return false;
    }

    private static int fetchReferences(StructureManager $$0, ChunkAccess $$1, SectionPos $$2, Structure $$3) {
        StructureStart $$4 = $$0.getStartForStructure($$2, $$3, $$1);
        return $$4 != null ? $$4.getReferences() : 0;
    }

    public void createReferences(WorldGenLevel $$0, StructureManager $$1, ChunkAccess $$2) {
        int $$3 = 8;
        ChunkPos $$4 = $$2.getPos();
        int $$5 = $$4.x;
        int $$6 = $$4.z;
        int $$7 = $$4.getMinBlockX();
        int $$8 = $$4.getMinBlockZ();
        SectionPos $$9 = SectionPos.bottomOf($$2);
        for (int $$10 = $$5 - 8; $$10 <= $$5 + 8; ++$$10) {
            for (int $$11 = $$6 - 8; $$11 <= $$6 + 8; ++$$11) {
                long $$12 = ChunkPos.asLong($$10, $$11);
                for (StructureStart $$13 : $$0.getChunk($$10, $$11).getAllStarts().values()) {
                    try {
                        if (!$$13.isValid() || !$$13.getBoundingBox().intersects($$7, $$8, $$7 + 15, $$8 + 15)) continue;
                        $$1.addReferenceForStructure($$9, $$13.getStructure(), $$12, $$2);
                        DebugPackets.sendStructurePacket($$0, $$13);
                    }
                    catch (Exception $$14) {
                        CrashReport $$15 = CrashReport.forThrowable($$14, "Generating structure reference");
                        CrashReportCategory $$16 = $$15.addCategory("Structure");
                        Optional<Registry<Structure>> $$17 = $$0.registryAccess().registry(Registries.STRUCTURE);
                        $$16.setDetail("Id", () -> (String)$$17.map($$1 -> $$1.getKey($$13.getStructure()).toString()).orElse((Object)"UNKNOWN"));
                        $$16.setDetail("Name", () -> BuiltInRegistries.STRUCTURE_TYPE.getKey($$13.getStructure().type()).toString());
                        $$16.setDetail("Class", () -> $$13.getStructure().getClass().getCanonicalName());
                        throw new ReportedException($$15);
                    }
                }
            }
        }
    }

    public abstract CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, RandomState var3, StructureManager var4, ChunkAccess var5);

    public abstract int getSeaLevel();

    public abstract int getMinY();

    public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5);

    public abstract NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4);

    public int getFirstFreeHeight(int $$0, int $$1, Heightmap.Types $$2, LevelHeightAccessor $$3, RandomState $$4) {
        return this.getBaseHeight($$0, $$1, $$2, $$3, $$4);
    }

    public int getFirstOccupiedHeight(int $$0, int $$1, Heightmap.Types $$2, LevelHeightAccessor $$3, RandomState $$4) {
        return this.getBaseHeight($$0, $$1, $$2, $$3, $$4) - 1;
    }

    public abstract void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3);

    @Deprecated
    public BiomeGenerationSettings getBiomeGenerationSettings(Holder<Biome> $$0) {
        return (BiomeGenerationSettings)this.generationSettingsGetter.apply($$0);
    }

    private static /* synthetic */ void lambda$applyBiomeDecoration$9(IntSet $$0, FeatureSorter.StepFeatureData $$1, PlacedFeature $$2) {
        $$0.add($$1.indexMapping().applyAsInt((Object)$$2));
    }

    private static /* synthetic */ void lambda$applyBiomeDecoration$6(WorldGenLevel $$0, Set $$1, ChunkPos $$2) {
        ChunkAccess $$3 = $$0.getChunk($$2.x, $$2.z);
        for (LevelChunkSection $$4 : $$3.getSections()) {
            $$4.getBiomes().getAll((Consumer<Holder<Biome>>)((Consumer)arg_0 -> ((Set)$$1).add(arg_0)));
        }
    }
}