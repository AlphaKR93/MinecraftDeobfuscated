/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.levelgen.structure;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public abstract class SinglePieceStructure
extends Structure {
    private final PieceConstructor constructor;
    private final int width;
    private final int depth;

    protected SinglePieceStructure(PieceConstructor $$0, int $$1, int $$2, Structure.StructureSettings $$3) {
        super($$3);
        this.constructor = $$0;
        this.width = $$1;
        this.depth = $$2;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        if (SinglePieceStructure.getLowestY($$0, this.width, this.depth) < $$0.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }
        return SinglePieceStructure.onTopOfChunkCenter($$0, Heightmap.Types.WORLD_SURFACE_WG, (Consumer<StructurePiecesBuilder>)((Consumer)$$1 -> this.generatePieces((StructurePiecesBuilder)$$1, $$0)));
    }

    private void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        ChunkPos $$2 = $$1.chunkPos();
        $$0.addPiece(this.constructor.construct($$1.random(), $$2.getMinBlockX(), $$2.getMinBlockZ()));
    }

    @FunctionalInterface
    protected static interface PieceConstructor {
        public StructurePiece construct(WorldgenRandom var1, int var2, int var3);
    }
}