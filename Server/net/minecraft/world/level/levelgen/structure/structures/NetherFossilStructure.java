/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilPieces;

public class NetherFossilStructure
extends Structure {
    public static final Codec<NetherFossilStructure> CODEC = RecordCodecBuilder.create($$02 -> $$02.group(NetherFossilStructure.settingsCodec($$02), (App)HeightProvider.CODEC.fieldOf("height").forGetter($$0 -> $$0.height)).apply((Applicative)$$02, NetherFossilStructure::new));
    public final HeightProvider height;

    public NetherFossilStructure(Structure.StructureSettings $$0, HeightProvider $$1) {
        super($$0);
        this.height = $$1;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        WorldgenRandom $$1 = $$0.random();
        int $$2 = $$0.chunkPos().getMinBlockX() + $$1.nextInt(16);
        int $$32 = $$0.chunkPos().getMinBlockZ() + $$1.nextInt(16);
        int $$4 = $$0.chunkGenerator().getSeaLevel();
        WorldGenerationContext $$5 = new WorldGenerationContext($$0.chunkGenerator(), $$0.heightAccessor());
        int $$6 = this.height.sample($$1, $$5);
        NoiseColumn $$7 = $$0.chunkGenerator().getBaseColumn($$2, $$32, $$0.heightAccessor(), $$0.randomState());
        BlockPos.MutableBlockPos $$8 = new BlockPos.MutableBlockPos($$2, $$6, $$32);
        while ($$6 > $$4) {
            BlockState $$9 = $$7.getBlock($$6);
            BlockState $$10 = $$7.getBlock(--$$6);
            if (!$$9.isAir() || !$$10.is(Blocks.SOUL_SAND) && !$$10.isFaceSturdy(EmptyBlockGetter.INSTANCE, $$8.setY($$6), Direction.UP)) continue;
            break;
        }
        if ($$6 <= $$4) {
            return Optional.empty();
        }
        BlockPos $$11 = new BlockPos($$2, $$6, $$32);
        return Optional.of((Object)((Object)new Structure.GenerationStub($$11, (Consumer<StructurePiecesBuilder>)((Consumer)$$3 -> NetherFossilPieces.addPieces($$0.structureTemplateManager(), $$3, $$1, $$11)))));
    }

    @Override
    public StructureType<?> type() {
        return StructureType.NETHER_FOSSIL;
    }
}