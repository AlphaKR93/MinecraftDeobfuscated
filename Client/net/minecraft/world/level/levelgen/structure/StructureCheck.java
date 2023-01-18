/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2BooleanMap
 *  it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class StructureCheck {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_STRUCTURE = -1;
    private final ChunkScanAccess storageAccess;
    private final RegistryAccess registryAccess;
    private final Registry<Biome> biomes;
    private final Registry<Structure> structureConfigs;
    private final StructureTemplateManager structureTemplateManager;
    private final ResourceKey<Level> dimension;
    private final ChunkGenerator chunkGenerator;
    private final RandomState randomState;
    private final LevelHeightAccessor heightAccessor;
    private final BiomeSource biomeSource;
    private final long seed;
    private final DataFixer fixerUpper;
    private final Long2ObjectMap<Object2IntMap<Structure>> loadedChunks = new Long2ObjectOpenHashMap();
    private final Map<Structure, Long2BooleanMap> featureChecks = new HashMap();

    public StructureCheck(ChunkScanAccess $$0, RegistryAccess $$1, StructureTemplateManager $$2, ResourceKey<Level> $$3, ChunkGenerator $$4, RandomState $$5, LevelHeightAccessor $$6, BiomeSource $$7, long $$8, DataFixer $$9) {
        this.storageAccess = $$0;
        this.registryAccess = $$1;
        this.structureTemplateManager = $$2;
        this.dimension = $$3;
        this.chunkGenerator = $$4;
        this.randomState = $$5;
        this.heightAccessor = $$6;
        this.biomeSource = $$7;
        this.seed = $$8;
        this.fixerUpper = $$9;
        this.biomes = $$1.registryOrThrow(Registries.BIOME);
        this.structureConfigs = $$1.registryOrThrow(Registries.STRUCTURE);
    }

    public StructureCheckResult checkStart(ChunkPos $$02, Structure $$1, boolean $$22) {
        long $$3 = $$02.toLong();
        Object2IntMap $$4 = (Object2IntMap)this.loadedChunks.get($$3);
        if ($$4 != null) {
            return this.checkStructureInfo((Object2IntMap<Structure>)$$4, $$1, $$22);
        }
        StructureCheckResult $$5 = this.tryLoadFromStorage($$02, $$1, $$22, $$3);
        if ($$5 != null) {
            return $$5;
        }
        boolean $$6 = ((Long2BooleanMap)this.featureChecks.computeIfAbsent((Object)$$1, $$0 -> new Long2BooleanOpenHashMap())).computeIfAbsent($$3, $$2 -> this.canCreateStructure($$02, $$1));
        if (!$$6) {
            return StructureCheckResult.START_NOT_PRESENT;
        }
        return StructureCheckResult.CHUNK_LOAD_NEEDED;
    }

    private boolean canCreateStructure(ChunkPos $$0, Structure $$1) {
        return $$1.findValidGenerationPoint(new Structure.GenerationContext(this.registryAccess, this.chunkGenerator, this.biomeSource, this.randomState, this.structureTemplateManager, this.seed, $$0, this.heightAccessor, (Predicate<Holder<Biome>>)((Predicate)$$1.biomes()::contains))).isPresent();
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private StructureCheckResult tryLoadFromStorage(ChunkPos $$0, Structure $$1, boolean $$2, long $$3) {
        void $$11;
        CollectFields $$4 = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector("Level", "Structures", CompoundTag.TYPE, "Starts"), new FieldSelector("structures", CompoundTag.TYPE, "starts"));
        try {
            this.storageAccess.scanChunk($$0, $$4).join();
        }
        catch (Exception $$5) {
            LOGGER.warn("Failed to read chunk {}", (Object)$$0, (Object)$$5);
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
        }
        Tag $$6 = $$4.getResult();
        if (!($$6 instanceof CompoundTag)) {
            return null;
        }
        CompoundTag $$7 = (CompoundTag)$$6;
        int $$8 = ChunkStorage.getVersion($$7);
        if ($$8 <= 1493) {
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
        }
        ChunkStorage.injectDatafixingContext($$7, this.dimension, this.chunkGenerator.getTypeNameForDataFixer());
        try {
            CompoundTag $$9 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, $$7, $$8);
        }
        catch (Exception $$10) {
            LOGGER.warn("Failed to partially datafix chunk {}", (Object)$$0, (Object)$$10);
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
        }
        Object2IntMap<Structure> $$12 = this.loadStructures((CompoundTag)$$11);
        if ($$12 == null) {
            return null;
        }
        this.storeFullResults($$3, $$12);
        return this.checkStructureInfo($$12, $$1, $$2);
    }

    @Nullable
    private Object2IntMap<Structure> loadStructures(CompoundTag $$0) {
        if (!$$0.contains("structures", 10)) {
            return null;
        }
        CompoundTag $$1 = $$0.getCompound("structures");
        if (!$$1.contains("starts", 10)) {
            return null;
        }
        CompoundTag $$2 = $$1.getCompound("starts");
        if ($$2.isEmpty()) {
            return Object2IntMaps.emptyMap();
        }
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        Registry<Structure> $$4 = this.registryAccess.registryOrThrow(Registries.STRUCTURE);
        for (String $$5 : $$2.getAllKeys()) {
            String $$9;
            CompoundTag $$8;
            Structure $$7;
            ResourceLocation $$6 = ResourceLocation.tryParse($$5);
            if ($$6 == null || ($$7 = $$4.get($$6)) == null || ($$8 = $$2.getCompound($$5)).isEmpty() || "INVALID".equals((Object)($$9 = $$8.getString("id")))) continue;
            int $$10 = $$8.getInt("references");
            $$3.put((Object)$$7, $$10);
        }
        return $$3;
    }

    private static Object2IntMap<Structure> deduplicateEmptyMap(Object2IntMap<Structure> $$0) {
        return $$0.isEmpty() ? Object2IntMaps.emptyMap() : $$0;
    }

    private StructureCheckResult checkStructureInfo(Object2IntMap<Structure> $$0, Structure $$1, boolean $$2) {
        int $$3 = $$0.getOrDefault((Object)$$1, -1);
        return $$3 != -1 && (!$$2 || $$3 == 0) ? StructureCheckResult.START_PRESENT : StructureCheckResult.START_NOT_PRESENT;
    }

    public void onStructureLoad(ChunkPos $$0, Map<Structure, StructureStart> $$1) {
        long $$2 = $$0.toLong();
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        $$1.forEach((arg_0, arg_1) -> StructureCheck.lambda$onStructureLoad$2((Object2IntMap)$$3, arg_0, arg_1));
        this.storeFullResults($$2, (Object2IntMap<Structure>)$$3);
    }

    private void storeFullResults(long $$0, Object2IntMap<Structure> $$12) {
        this.loadedChunks.put($$0, StructureCheck.deduplicateEmptyMap($$12));
        this.featureChecks.values().forEach($$1 -> $$1.remove($$0));
    }

    public void incrementReference(ChunkPos $$0, Structure $$1) {
        this.loadedChunks.compute($$0.toLong(), ($$12, $$2) -> {
            if ($$2 == null || $$2.isEmpty()) {
                $$2 = new Object2IntOpenHashMap();
            }
            $$2.computeInt((Object)$$1, ($$0, $$1) -> $$1 == null ? 1 : $$1 + 1);
            return $$2;
        });
    }

    private static /* synthetic */ void lambda$onStructureLoad$2(Object2IntMap $$0, Structure $$1, StructureStart $$2) {
        if ($$2.isValid()) {
            $$0.put((Object)$$1, $$2.getReferences());
        }
    }
}