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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;

public class IglooStructure
extends Structure {
    public static final Codec<IglooStructure> CODEC = IglooStructure.simpleCodec(IglooStructure::new);

    public IglooStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        return IglooStructure.onTopOfChunkCenter($$0, Heightmap.Types.WORLD_SURFACE_WG, (Consumer<StructurePiecesBuilder>)((Consumer)$$1 -> this.generatePieces((StructurePiecesBuilder)$$1, $$0)));
    }

    private void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        ChunkPos $$2 = $$1.chunkPos();
        WorldgenRandom $$3 = $$1.random();
        BlockPos $$4 = new BlockPos($$2.getMinBlockX(), 90, $$2.getMinBlockZ());
        Rotation $$5 = Rotation.getRandom($$3);
        IglooPieces.addPieces($$1.structureTemplateManager(), $$4, $$5, $$0, $$3);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.IGLOO;
    }
}