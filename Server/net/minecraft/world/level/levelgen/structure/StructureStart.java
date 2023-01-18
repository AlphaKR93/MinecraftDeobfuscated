/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import org.slf4j.Logger;

public final class StructureStart {
    public static final String INVALID_START_ID = "INVALID";
    public static final StructureStart INVALID_START = new StructureStart(null, new ChunkPos(0, 0), 0, new PiecesContainer((List<StructurePiece>)List.of()));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Structure structure;
    private final PiecesContainer pieceContainer;
    private final ChunkPos chunkPos;
    private int references;
    @Nullable
    private volatile BoundingBox cachedBoundingBox;

    public StructureStart(Structure $$0, ChunkPos $$1, int $$2, PiecesContainer $$3) {
        this.structure = $$0;
        this.chunkPos = $$1;
        this.references = $$2;
        this.pieceContainer = $$3;
    }

    @Nullable
    public static StructureStart loadStaticStart(StructurePieceSerializationContext $$0, CompoundTag $$1, long $$2) {
        String $$3 = $$1.getString("id");
        if (INVALID_START_ID.equals((Object)$$3)) {
            return INVALID_START;
        }
        Registry<Structure> $$4 = $$0.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure $$5 = $$4.get(new ResourceLocation($$3));
        if ($$5 == null) {
            LOGGER.error("Unknown stucture id: {}", (Object)$$3);
            return null;
        }
        ChunkPos $$6 = new ChunkPos($$1.getInt("ChunkX"), $$1.getInt("ChunkZ"));
        int $$7 = $$1.getInt("references");
        ListTag $$8 = $$1.getList("Children", 10);
        try {
            PiecesContainer $$9 = PiecesContainer.load($$8, $$0);
            if ($$5 instanceof OceanMonumentStructure) {
                $$9 = OceanMonumentStructure.regeneratePiecesAfterLoad($$6, $$2, $$9);
            }
            return new StructureStart($$5, $$6, $$7, $$9);
        }
        catch (Exception $$10) {
            LOGGER.error("Failed Start with id {}", (Object)$$3, (Object)$$10);
            return null;
        }
    }

    public BoundingBox getBoundingBox() {
        BoundingBox $$0 = this.cachedBoundingBox;
        if ($$0 == null) {
            this.cachedBoundingBox = $$0 = this.structure.adjustBoundingBox(this.pieceContainer.calculateBoundingBox());
        }
        return $$0;
    }

    public void placeInChunk(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5) {
        List<StructurePiece> $$6 = this.pieceContainer.pieces();
        if ($$6.isEmpty()) {
            return;
        }
        BoundingBox $$7 = ((StructurePiece)$$6.get((int)0)).boundingBox;
        BlockPos $$8 = $$7.getCenter();
        BlockPos $$9 = new BlockPos($$8.getX(), $$7.minY(), $$8.getZ());
        for (StructurePiece $$10 : $$6) {
            if (!$$10.getBoundingBox().intersects($$4)) continue;
            $$10.postProcess($$0, $$1, $$2, $$3, $$4, $$5, $$9);
        }
        this.structure.afterPlace($$0, $$1, $$2, $$3, $$4, $$5, this.pieceContainer);
    }

    public CompoundTag createTag(StructurePieceSerializationContext $$0, ChunkPos $$1) {
        CompoundTag $$2 = new CompoundTag();
        if (!this.isValid()) {
            $$2.putString("id", INVALID_START_ID);
            return $$2;
        }
        $$2.putString("id", $$0.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(this.structure).toString());
        $$2.putInt("ChunkX", $$1.x);
        $$2.putInt("ChunkZ", $$1.z);
        $$2.putInt("references", this.references);
        $$2.put("Children", this.pieceContainer.save($$0));
        return $$2;
    }

    public boolean isValid() {
        return !this.pieceContainer.isEmpty();
    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public boolean canBeReferenced() {
        return this.references < this.getMaxReferences();
    }

    public void addReference() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getMaxReferences() {
        return 1;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public List<StructurePiece> getPieces() {
        return this.pieceContainer.pieces();
    }
}