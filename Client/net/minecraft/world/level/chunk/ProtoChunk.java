/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

public class ProtoChunk
extends ChunkAccess {
    @Nullable
    private volatile LevelLightEngine lightEngine;
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final List<CompoundTag> entities = Lists.newArrayList();
    private final List<BlockPos> lights = Lists.newArrayList();
    private final Map<GenerationStep.Carving, CarvingMask> carvingMasks = new Object2ObjectArrayMap();
    @Nullable
    private BelowZeroRetrogen belowZeroRetrogen;
    private final ProtoChunkTicks<Block> blockTicks;
    private final ProtoChunkTicks<Fluid> fluidTicks;

    public ProtoChunk(ChunkPos $$0, UpgradeData $$1, LevelHeightAccessor $$2, Registry<Biome> $$3, @Nullable BlendingData $$4) {
        this($$0, $$1, null, new ProtoChunkTicks<Block>(), new ProtoChunkTicks<Fluid>(), $$2, $$3, $$4);
    }

    public ProtoChunk(ChunkPos $$0, UpgradeData $$1, @Nullable LevelChunkSection[] $$2, ProtoChunkTicks<Block> $$3, ProtoChunkTicks<Fluid> $$4, LevelHeightAccessor $$5, Registry<Biome> $$6, @Nullable BlendingData $$7) {
        super($$0, $$1, $$5, $$6, 0L, $$2, $$7);
        this.blockTicks = $$3;
        this.fluidTicks = $$4;
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    @Override
    public ChunkAccess.TicksToSave getTicksForSerialization() {
        return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        int $$1 = $$0.getY();
        if (this.isOutsideBuildHeight($$1)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        LevelChunkSection $$2 = this.getSection(this.getSectionIndex($$1));
        if ($$2.hasOnlyAir()) {
            return Blocks.AIR.defaultBlockState();
        }
        return $$2.getBlockState($$0.getX() & 0xF, $$1 & 0xF, $$0.getZ() & 0xF);
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        int $$1 = $$0.getY();
        if (this.isOutsideBuildHeight($$1)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        LevelChunkSection $$2 = this.getSection(this.getSectionIndex($$1));
        if ($$2.hasOnlyAir()) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return $$2.getFluidState($$0.getX() & 0xF, $$1 & 0xF, $$0.getZ() & 0xF);
    }

    @Override
    public Stream<BlockPos> getLights() {
        return this.lights.stream();
    }

    public ShortList[] getPackedLights() {
        ShortList[] $$0 = new ShortList[this.getSectionsCount()];
        for (BlockPos $$1 : this.lights) {
            ChunkAccess.getOrCreateOffsetList($$0, this.getSectionIndex($$1.getY())).add(ProtoChunk.packOffsetCoordinates($$1));
        }
        return $$0;
    }

    public void addLight(short $$0, int $$1) {
        this.addLight(ProtoChunk.unpackOffsetCoordinates($$0, this.getSectionYFromSectionIndex($$1), this.chunkPos));
    }

    public void addLight(BlockPos $$0) {
        this.lights.add((Object)$$0.immutable());
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos $$0, BlockState $$1, boolean $$2) {
        int $$3 = $$0.getX();
        int $$4 = $$0.getY();
        int $$5 = $$0.getZ();
        if ($$4 < this.getMinBuildHeight() || $$4 >= this.getMaxBuildHeight()) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        int $$6 = this.getSectionIndex($$4);
        if (this.sections[$$6].hasOnlyAir() && $$1.is(Blocks.AIR)) {
            return $$1;
        }
        if ($$1.getLightEmission() > 0) {
            this.lights.add((Object)new BlockPos(($$3 & 0xF) + this.getPos().getMinBlockX(), $$4, ($$5 & 0xF) + this.getPos().getMinBlockZ()));
        }
        LevelChunkSection $$7 = this.getSection($$6);
        BlockState $$8 = $$7.setBlockState($$3 & 0xF, $$4 & 0xF, $$5 & 0xF, $$1);
        if (this.status.isOrAfter(ChunkStatus.FEATURES) && $$1 != $$8 && ($$1.getLightBlock(this, $$0) != $$8.getLightBlock(this, $$0) || $$1.getLightEmission() != $$8.getLightEmission() || $$1.useShapeForLightOcclusion() || $$8.useShapeForLightOcclusion())) {
            this.lightEngine.checkBlock($$0);
        }
        EnumSet<Heightmap.Types> $$9 = this.getStatus().heightmapsAfter();
        EnumSet $$10 = null;
        for (Heightmap.Types $$11 : $$9) {
            Heightmap $$12 = (Heightmap)this.heightmaps.get((Object)$$11);
            if ($$12 != null) continue;
            if ($$10 == null) {
                $$10 = EnumSet.noneOf(Heightmap.Types.class);
            }
            $$10.add((Object)$$11);
        }
        if ($$10 != null) {
            Heightmap.primeHeightmaps(this, $$10);
        }
        for (Heightmap.Types $$13 : $$9) {
            ((Heightmap)this.heightmaps.get((Object)$$13)).update($$3 & 0xF, $$4, $$5 & 0xF, $$1);
        }
        return $$8;
    }

    @Override
    public void setBlockEntity(BlockEntity $$0) {
        this.blockEntities.put((Object)$$0.getBlockPos(), (Object)$$0);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return (BlockEntity)this.blockEntities.get((Object)$$0);
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(CompoundTag $$0) {
        this.entities.add((Object)$$0);
    }

    @Override
    public void addEntity(Entity $$0) {
        if ($$0.isPassenger()) {
            return;
        }
        CompoundTag $$1 = new CompoundTag();
        $$0.save($$1);
        this.addEntity($$1);
    }

    @Override
    public void setStartForStructure(Structure $$0, StructureStart $$1) {
        BelowZeroRetrogen $$2 = this.getBelowZeroRetrogen();
        if ($$2 != null && $$1.isValid()) {
            BoundingBox $$3 = $$1.getBoundingBox();
            LevelHeightAccessor $$4 = this.getHeightAccessorForGeneration();
            if ($$3.minY() < $$4.getMinBuildHeight() || $$3.maxY() >= $$4.getMaxBuildHeight()) {
                return;
            }
        }
        super.setStartForStructure($$0, $$1);
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChunkStatus $$0) {
        this.status = $$0;
        if (this.belowZeroRetrogen != null && $$0.isOrAfter(this.belowZeroRetrogen.targetStatus())) {
            this.setBelowZeroRetrogen(null);
        }
        this.setUnsaved(true);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        if (this.getStatus().isOrAfter(ChunkStatus.BIOMES) || this.belowZeroRetrogen != null && this.belowZeroRetrogen.targetStatus().isOrAfter(ChunkStatus.BIOMES)) {
            return super.getNoiseBiome($$0, $$1, $$2);
        }
        throw new IllegalStateException("Asking for biomes before we have biomes");
    }

    public static short packOffsetCoordinates(BlockPos $$0) {
        int $$1 = $$0.getX();
        int $$2 = $$0.getY();
        int $$3 = $$0.getZ();
        int $$4 = $$1 & 0xF;
        int $$5 = $$2 & 0xF;
        int $$6 = $$3 & 0xF;
        return (short)($$4 | $$5 << 4 | $$6 << 8);
    }

    public static BlockPos unpackOffsetCoordinates(short $$0, int $$1, ChunkPos $$2) {
        int $$3 = SectionPos.sectionToBlockCoord($$2.x, $$0 & 0xF);
        int $$4 = SectionPos.sectionToBlockCoord($$1, $$0 >>> 4 & 0xF);
        int $$5 = SectionPos.sectionToBlockCoord($$2.z, $$0 >>> 8 & 0xF);
        return new BlockPos($$3, $$4, $$5);
    }

    @Override
    public void markPosForPostprocessing(BlockPos $$0) {
        if (!this.isOutsideBuildHeight($$0)) {
            ChunkAccess.getOrCreateOffsetList(this.postProcessing, this.getSectionIndex($$0.getY())).add(ProtoChunk.packOffsetCoordinates($$0));
        }
    }

    @Override
    public void addPackedPostProcess(short $$0, int $$1) {
        ChunkAccess.getOrCreateOffsetList(this.postProcessing, $$1).add($$0);
    }

    public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
        return Collections.unmodifiableMap((Map)this.pendingBlockEntities);
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(BlockPos $$0) {
        BlockEntity $$1 = this.getBlockEntity($$0);
        if ($$1 != null) {
            return $$1.saveWithFullMetadata();
        }
        return (CompoundTag)this.pendingBlockEntities.get((Object)$$0);
    }

    @Override
    public void removeBlockEntity(BlockPos $$0) {
        this.blockEntities.remove((Object)$$0);
        this.pendingBlockEntities.remove((Object)$$0);
    }

    @Nullable
    public CarvingMask getCarvingMask(GenerationStep.Carving $$0) {
        return (CarvingMask)this.carvingMasks.get((Object)$$0);
    }

    public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving $$02) {
        return (CarvingMask)this.carvingMasks.computeIfAbsent((Object)$$02, $$0 -> new CarvingMask(this.getHeight(), this.getMinBuildHeight()));
    }

    public void setCarvingMask(GenerationStep.Carving $$0, CarvingMask $$1) {
        this.carvingMasks.put((Object)$$0, (Object)$$1);
    }

    public void setLightEngine(LevelLightEngine $$0) {
        this.lightEngine = $$0;
    }

    public void setBelowZeroRetrogen(@Nullable BelowZeroRetrogen $$0) {
        this.belowZeroRetrogen = $$0;
    }

    @Override
    @Nullable
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return this.belowZeroRetrogen;
    }

    private static <T> LevelChunkTicks<T> unpackTicks(ProtoChunkTicks<T> $$0) {
        return new LevelChunkTicks<T>($$0.scheduledTicks());
    }

    public LevelChunkTicks<Block> unpackBlockTicks() {
        return ProtoChunk.unpackTicks(this.blockTicks);
    }

    public LevelChunkTicks<Fluid> unpackFluidTicks() {
        return ProtoChunk.unpackTicks(this.fluidTicks);
    }

    @Override
    public LevelHeightAccessor getHeightAccessorForGeneration() {
        if (this.isUpgrading()) {
            return BelowZeroRetrogen.UPGRADE_HEIGHT_ACCESSOR;
        }
        return this;
    }
}