/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;

public class SwampHutStructure
extends Structure {
    public static final Codec<SwampHutStructure> CODEC = SwampHutStructure.simpleCodec(SwampHutStructure::new);

    public SwampHutStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        return SwampHutStructure.onTopOfChunkCenter($$0, Heightmap.Types.WORLD_SURFACE_WG, (Consumer<StructurePiecesBuilder>)((Consumer)$$1 -> SwampHutStructure.generatePieces($$1, $$0)));
    }

    private static void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        $$0.addPiece(new SwampHutPiece($$1.random(), $$1.chunkPos().getMinBlockX(), $$1.chunkPos().getMinBlockZ()));
    }

    @Override
    public StructureType<?> type() {
        return StructureType.SWAMP_HUT;
    }
}