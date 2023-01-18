/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  java.lang.Deprecated
 *  java.lang.Enum
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.System
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.EnumSet
 *  java.util.HashSet
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public abstract class ChunkAccess
implements BlockGetter,
BiomeManager.NoiseBiomeSource,
StructureAccess {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LongSet EMPTY_REFERENCE_SET = new LongOpenHashSet();
    protected final ShortList[] postProcessing;
    protected volatile boolean unsaved;
    private volatile boolean isLightCorrect;
    protected final ChunkPos chunkPos;
    private long inhabitedTime;
    @Nullable
    @Deprecated
    private BiomeGenerationSettings carverBiomeSettings;
    @Nullable
    protected NoiseChunk noiseChunk;
    protected final UpgradeData upgradeData;
    @Nullable
    protected BlendingData blendingData;
    protected final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
    private final Map<Structure, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<Structure, LongSet> structuresRefences = Maps.newHashMap();
    protected final Map<BlockPos, CompoundTag> pendingBlockEntities = Maps.newHashMap();
    protected final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    protected final LevelHeightAccessor levelHeightAccessor;
    protected final LevelChunkSection[] sections;

    public ChunkAccess(ChunkPos $$0, UpgradeData $$1, LevelHeightAccessor $$2, Registry<Biome> $$3, long $$4, @Nullable LevelChunkSection[] $$5, @Nullable BlendingData $$6) {
        this.chunkPos = $$0;
        this.upgradeData = $$1;
        this.levelHeightAccessor = $$2;
        this.sections = new LevelChunkSection[$$2.getSectionsCount()];
        this.inhabitedTime = $$4;
        this.postProcessing = new ShortList[$$2.getSectionsCount()];
        this.blendingData = $$6;
        if ($$5 != null) {
            if (this.sections.length == $$5.length) {
                System.arraycopy((Object)$$5, (int)0, (Object)this.sections, (int)0, (int)this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)$$5.length, (Object)this.sections.length);
            }
        }
        ChunkAccess.replaceMissingSections($$2, $$3, this.sections);
    }

    private static void replaceMissingSections(LevelHeightAccessor $$0, Registry<Biome> $$1, LevelChunkSection[] $$2) {
        for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
            if ($$2[$$3] != null) continue;
            $$2[$$3] = new LevelChunkSection($$0.getSectionYFromSectionIndex($$3), $$1);
        }
    }

    public GameEventListenerRegistry getListenerRegistry(int $$0) {
        return GameEventListenerRegistry.NOOP;
    }

    @Nullable
    public abstract BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

    public abstract void setBlockEntity(BlockEntity var1);

    public abstract void addEntity(Entity var1);

    @Nullable
    public LevelChunkSection getHighestSection() {
        LevelChunkSection[] $$0 = this.getSections();
        for (int $$1 = $$0.length - 1; $$1 >= 0; --$$1) {
            LevelChunkSection $$2 = $$0[$$1];
            if ($$2.hasOnlyAir()) continue;
            return $$2;
        }
        return null;
    }

    public int getHighestSectionPosition() {
        LevelChunkSection $$0 = this.getHighestSection();
        return $$0 == null ? this.getMinBuildHeight() : $$0.bottomBlockY();
    }

    public Set<BlockPos> getBlockEntitiesPos() {
        HashSet $$0 = Sets.newHashSet((Iterable)this.pendingBlockEntities.keySet());
        $$0.addAll((Collection)this.blockEntities.keySet());
        return $$0;
    }

    public LevelChunkSection[] getSections() {
        return this.sections;
    }

    public LevelChunkSection getSection(int $$0) {
        return this.getSections()[$$0];
    }

    public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet((Set)this.heightmaps.entrySet());
    }

    public void setHeightmap(Heightmap.Types $$0, long[] $$1) {
        this.getOrCreateHeightmapUnprimed($$0).setRawData(this, $$0, $$1);
    }

    public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types $$02) {
        return (Heightmap)this.heightmaps.computeIfAbsent((Object)$$02, $$0 -> new Heightmap(this, (Heightmap.Types)$$0));
    }

    public boolean hasPrimedHeightmap(Heightmap.Types $$0) {
        return this.heightmaps.get((Object)$$0) != null;
    }

    public int getHeight(Heightmap.Types $$0, int $$1, int $$2) {
        Heightmap $$3 = (Heightmap)this.heightmaps.get((Object)$$0);
        if ($$3 == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE && this instanceof LevelChunk) {
                LOGGER.error("Unprimed heightmap: " + $$0 + " " + $$1 + " " + $$2);
            }
            Heightmap.primeHeightmaps(this, (Set<Heightmap.Types>)EnumSet.of((Enum)$$0));
            $$3 = (Heightmap)this.heightmaps.get((Object)$$0);
        }
        return $$3.getFirstAvailable($$1 & 0xF, $$2 & 0xF) - 1;
    }

    public ChunkPos getPos() {
        return this.chunkPos;
    }

    @Override
    @Nullable
    public StructureStart getStartForStructure(Structure $$0) {
        return (StructureStart)this.structureStarts.get((Object)$$0);
    }

    @Override
    public void setStartForStructure(Structure $$0, StructureStart $$1) {
        this.structureStarts.put((Object)$$0, (Object)$$1);
        this.unsaved = true;
    }

    public Map<Structure, StructureStart> getAllStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    public void setAllStarts(Map<Structure, StructureStart> $$0) {
        this.structureStarts.clear();
        this.structureStarts.putAll($$0);
        this.unsaved = true;
    }

    @Override
    public LongSet getReferencesForStructure(Structure $$0) {
        return (LongSet)this.structuresRefences.getOrDefault((Object)$$0, (Object)EMPTY_REFERENCE_SET);
    }

    @Override
    public void addReferenceForStructure(Structure $$02, long $$1) {
        ((LongSet)this.structuresRefences.computeIfAbsent((Object)$$02, $$0 -> new LongOpenHashSet())).add($$1);
        this.unsaved = true;
    }

    @Override
    public Map<Structure, LongSet> getAllReferences() {
        return Collections.unmodifiableMap(this.structuresRefences);
    }

    @Override
    public void setAllReferences(Map<Structure, LongSet> $$0) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll($$0);
        this.unsaved = true;
    }

    public boolean isYSpaceEmpty(int $$0, int $$1) {
        if ($$0 < this.getMinBuildHeight()) {
            $$0 = this.getMinBuildHeight();
        }
        if ($$1 >= this.getMaxBuildHeight()) {
            $$1 = this.getMaxBuildHeight() - 1;
        }
        for (int $$2 = $$0; $$2 <= $$1; $$2 += 16) {
            if (this.getSection(this.getSectionIndex($$2)).hasOnlyAir()) continue;
            return false;
        }
        return true;
    }

    public void setUnsaved(boolean $$0) {
        this.unsaved = $$0;
    }

    public boolean isUnsaved() {
        return this.unsaved;
    }

    public abstract ChunkStatus getStatus();

    public abstract void removeBlockEntity(BlockPos var1);

    public void markPosForPostprocessing(BlockPos $$0) {
        LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)$$0);
    }

    public ShortList[] getPostProcessing() {
        return this.postProcessing;
    }

    public void addPackedPostProcess(short $$0, int $$1) {
        ChunkAccess.getOrCreateOffsetList(this.getPostProcessing(), $$1).add($$0);
    }

    public void setBlockEntityNbt(CompoundTag $$0) {
        this.pendingBlockEntities.put((Object)BlockEntity.getPosFromTag($$0), (Object)$$0);
    }

    @Nullable
    public CompoundTag getBlockEntityNbt(BlockPos $$0) {
        return (CompoundTag)this.pendingBlockEntities.get((Object)$$0);
    }

    @Nullable
    public abstract CompoundTag getBlockEntityNbtForSaving(BlockPos var1);

    public abstract Stream<BlockPos> getLights();

    public abstract TickContainerAccess<Block> getBlockTicks();

    public abstract TickContainerAccess<Fluid> getFluidTicks();

    public abstract TicksToSave getTicksForSerialization();

    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    public boolean isOldNoiseGeneration() {
        return this.blendingData != null;
    }

    @Nullable
    public BlendingData getBlendingData() {
        return this.blendingData;
    }

    public void setBlendingData(BlendingData $$0) {
        this.blendingData = $$0;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public void incrementInhabitedTime(long $$0) {
        this.inhabitedTime += $$0;
    }

    public void setInhabitedTime(long $$0) {
        this.inhabitedTime = $$0;
    }

    public static ShortList getOrCreateOffsetList(ShortList[] $$0, int $$1) {
        if ($$0[$$1] == null) {
            $$0[$$1] = new ShortArrayList();
        }
        return $$0[$$1];
    }

    public boolean isLightCorrect() {
        return this.isLightCorrect;
    }

    public void setLightCorrect(boolean $$0) {
        this.isLightCorrect = $$0;
        this.setUnsaved(true);
    }

    @Override
    public int getMinBuildHeight() {
        return this.levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.levelHeightAccessor.getHeight();
    }

    public NoiseChunk getOrCreateNoiseChunk(Function<ChunkAccess, NoiseChunk> $$0) {
        if (this.noiseChunk == null) {
            this.noiseChunk = (NoiseChunk)$$0.apply((Object)this);
        }
        return this.noiseChunk;
    }

    @Deprecated
    public BiomeGenerationSettings carverBiome(Supplier<BiomeGenerationSettings> $$0) {
        if (this.carverBiomeSettings == null) {
            this.carverBiomeSettings = (BiomeGenerationSettings)$$0.get();
        }
        return this.carverBiomeSettings;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        try {
            int $$3 = QuartPos.fromBlock(this.getMinBuildHeight());
            int $$4 = $$3 + QuartPos.fromBlock(this.getHeight()) - 1;
            int $$5 = Mth.clamp($$1, $$3, $$4);
            int $$6 = this.getSectionIndex(QuartPos.toBlock($$5));
            return this.sections[$$6].getNoiseBiome($$0 & 3, $$5 & 3, $$2 & 3);
        }
        catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Getting biome");
            CrashReportCategory $$9 = $$8.addCategory("Biome being got");
            $$9.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, $$0, $$1, $$2));
            throw new ReportedException($$8);
        }
    }

    public void fillBiomesFromNoise(BiomeResolver $$0, Climate.Sampler $$1) {
        ChunkPos $$2 = this.getPos();
        int $$3 = QuartPos.fromBlock($$2.getMinBlockX());
        int $$4 = QuartPos.fromBlock($$2.getMinBlockZ());
        LevelHeightAccessor $$5 = this.getHeightAccessorForGeneration();
        for (int $$6 = $$5.getMinSection(); $$6 < $$5.getMaxSection(); ++$$6) {
            LevelChunkSection $$7 = this.getSection(this.getSectionIndexFromSectionY($$6));
            $$7.fillBiomesFromNoise($$0, $$1, $$3, $$4);
        }
    }

    public boolean hasAnyStructureReferences() {
        return !this.getAllReferences().isEmpty();
    }

    @Nullable
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return null;
    }

    public boolean isUpgrading() {
        return this.getBelowZeroRetrogen() != null;
    }

    public LevelHeightAccessor getHeightAccessorForGeneration() {
        return this;
    }

    public record TicksToSave(SerializableTickContainer<Block> blocks, SerializableTickContainer<Fluid> fluids) {
    }
}