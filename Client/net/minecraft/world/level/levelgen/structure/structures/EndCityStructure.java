/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;

public class EndCityStructure
extends Structure {
    public static final Codec<EndCityStructure> CODEC = EndCityStructure.simpleCodec(EndCityStructure::new);

    public EndCityStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        Rotation $$1 = Rotation.getRandom($$0.random());
        BlockPos $$2 = this.getLowestYIn5by5BoxOffset7Blocks($$0, $$1);
        if ($$2.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of((Object)((Object)new Structure.GenerationStub($$2, (Consumer<StructurePiecesBuilder>)((Consumer)$$3 -> this.generatePieces((StructurePiecesBuilder)$$3, $$2, $$1, $$0)))));
    }

    private void generatePieces(StructurePiecesBuilder $$0, BlockPos $$1, Rotation $$2, Structure.GenerationContext $$3) {
        ArrayList $$4 = Lists.newArrayList();
        EndCityPieces.startHouseTower($$3.structureTemplateManager(), $$1, $$2, (List<StructurePiece>)$$4, $$3.random());
        $$4.forEach($$0::addPiece);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.END_CITY;
    }
}