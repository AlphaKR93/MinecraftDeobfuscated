/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Boolean
 *  java.lang.IllegalStateException
 *  java.lang.Long
 *  java.lang.Object
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureManager {
    private final LevelAccessor level;
    private final WorldOptions worldOptions;
    private final StructureCheck structureCheck;

    public StructureManager(LevelAccessor $$0, WorldOptions $$1, StructureCheck $$2) {
        this.level = $$0;
        this.worldOptions = $$1;
        this.structureCheck = $$2;
    }

    public StructureManager forWorldGenRegion(WorldGenRegion $$0) {
        if ($$0.getLevel() != this.level) {
            throw new IllegalStateException("Using invalid structure manager (source level: " + $$0.getLevel() + ", region: " + $$0);
        }
        return new StructureManager($$0, this.worldOptions, this.structureCheck);
    }

    public List<StructureStart> startsForStructure(ChunkPos $$0, Predicate<Structure> $$1) {
        Map<Structure, LongSet> $$2 = this.level.getChunk($$0.x, $$0.z, ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
        ImmutableList.Builder $$3 = ImmutableList.builder();
        for (Map.Entry $$4 : $$2.entrySet()) {
            Structure $$5 = (Structure)$$4.getKey();
            if (!$$1.test((Object)$$5)) continue;
            this.fillStartsForStructure($$5, (LongSet)$$4.getValue(), (Consumer<StructureStart>)((Consumer)arg_0 -> ((ImmutableList.Builder)$$3).add(arg_0)));
        }
        return $$3.build();
    }

    public List<StructureStart> startsForStructure(SectionPos $$0, Structure $$1) {
        LongSet $$2 = this.level.getChunk($$0.x(), $$0.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForStructure($$1);
        ImmutableList.Builder $$3 = ImmutableList.builder();
        this.fillStartsForStructure($$1, $$2, (Consumer<StructureStart>)((Consumer)arg_0 -> ((ImmutableList.Builder)$$3).add(arg_0)));
        return $$3.build();
    }

    public void fillStartsForStructure(Structure $$0, LongSet $$1, Consumer<StructureStart> $$2) {
        LongIterator longIterator = $$1.iterator();
        while (longIterator.hasNext()) {
            long $$3 = (Long)longIterator.next();
            SectionPos $$4 = SectionPos.of(new ChunkPos($$3), this.level.getMinSection());
            StructureStart $$5 = this.getStartForStructure($$4, $$0, this.level.getChunk($$4.x(), $$4.z(), ChunkStatus.STRUCTURE_STARTS));
            if ($$5 == null || !$$5.isValid()) continue;
            $$2.accept((Object)$$5);
        }
    }

    @Nullable
    public StructureStart getStartForStructure(SectionPos $$0, Structure $$1, StructureAccess $$2) {
        return $$2.getStartForStructure($$1);
    }

    public void setStartForStructure(SectionPos $$0, Structure $$1, StructureStart $$2, StructureAccess $$3) {
        $$3.setStartForStructure($$1, $$2);
    }

    public void addReferenceForStructure(SectionPos $$0, Structure $$1, long $$2, StructureAccess $$3) {
        $$3.addReferenceForStructure($$1, $$2);
    }

    public boolean shouldGenerateStructures() {
        return this.worldOptions.generateStructures();
    }

    public StructureStart getStructureAt(BlockPos $$0, Structure $$1) {
        for (StructureStart $$2 : this.startsForStructure(SectionPos.of($$0), $$1)) {
            if (!$$2.getBoundingBox().isInside($$0)) continue;
            return $$2;
        }
        return StructureStart.INVALID_START;
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, ResourceKey<Structure> $$1) {
        Structure $$2 = this.registryAccess().registryOrThrow(Registries.STRUCTURE).get($$1);
        if ($$2 == null) {
            return StructureStart.INVALID_START;
        }
        return this.getStructureWithPieceAt($$0, $$2);
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, TagKey<Structure> $$1) {
        Registry<Structure> $$22 = this.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (StructureStart $$3 : this.startsForStructure(new ChunkPos($$0), (Predicate<Structure>)((Predicate)$$2 -> (Boolean)$$22.getHolder($$22.getId((Structure)$$2)).map($$1 -> $$1.is($$1)).orElse((Object)false)))) {
            if (!this.structureHasPieceAt($$0, $$3)) continue;
            return $$3;
        }
        return StructureStart.INVALID_START;
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, Structure $$1) {
        for (StructureStart $$2 : this.startsForStructure(SectionPos.of($$0), $$1)) {
            if (!this.structureHasPieceAt($$0, $$2)) continue;
            return $$2;
        }
        return StructureStart.INVALID_START;
    }

    public boolean structureHasPieceAt(BlockPos $$0, StructureStart $$1) {
        for (StructurePiece $$2 : $$1.getPieces()) {
            if (!$$2.getBoundingBox().isInside($$0)) continue;
            return true;
        }
        return false;
    }

    public boolean hasAnyStructureAt(BlockPos $$0) {
        SectionPos $$1 = SectionPos.of($$0);
        return this.level.getChunk($$1.x(), $$1.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
    }

    public Map<Structure, LongSet> getAllStructuresAt(BlockPos $$0) {
        SectionPos $$1 = SectionPos.of($$0);
        return this.level.getChunk($$1.x(), $$1.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
    }

    public StructureCheckResult checkStructurePresence(ChunkPos $$0, Structure $$1, boolean $$2) {
        return this.structureCheck.checkStart($$0, $$1, $$2);
    }

    public void addReference(StructureStart $$0) {
        $$0.addReference();
        this.structureCheck.incrementReference($$0.getChunkPos(), $$0.getStructure());
    }

    public RegistryAccess registryAccess() {
        return this.level.registryAccess();
    }
}