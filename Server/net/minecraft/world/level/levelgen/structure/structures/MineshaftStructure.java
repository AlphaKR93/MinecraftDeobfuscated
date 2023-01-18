/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;

public class MineshaftStructure
extends Structure {
    public static final Codec<MineshaftStructure> CODEC = RecordCodecBuilder.create($$02 -> $$02.group(MineshaftStructure.settingsCodec($$02), (App)Type.CODEC.fieldOf("mineshaft_type").forGetter($$0 -> $$0.type)).apply((Applicative)$$02, MineshaftStructure::new));
    private final Type type;

    public MineshaftStructure(Structure.StructureSettings $$0, Type $$1) {
        super($$0);
        this.type = $$1;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        $$0.random().nextDouble();
        ChunkPos $$1 = $$0.chunkPos();
        BlockPos $$2 = new BlockPos($$1.getMiddleBlockX(), 50, $$1.getMinBlockZ());
        StructurePiecesBuilder $$3 = new StructurePiecesBuilder();
        int $$4 = this.generatePiecesAndAdjust($$3, $$0);
        return Optional.of((Object)((Object)new Structure.GenerationStub($$2.offset(0, $$4, 0), (Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder>)Either.right((Object)$$3))));
    }

    private int generatePiecesAndAdjust(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        ChunkPos $$2 = $$1.chunkPos();
        WorldgenRandom $$3 = $$1.random();
        ChunkGenerator $$4 = $$1.chunkGenerator();
        MineshaftPieces.MineShaftRoom $$5 = new MineshaftPieces.MineShaftRoom(0, $$3, $$2.getBlockX(2), $$2.getBlockZ(2), this.type);
        $$0.addPiece($$5);
        $$5.addChildren($$5, $$0, $$3);
        int $$6 = $$4.getSeaLevel();
        if (this.type == Type.MESA) {
            BlockPos $$7 = $$0.getBoundingBox().getCenter();
            int $$8 = $$4.getBaseHeight($$7.getX(), $$7.getZ(), Heightmap.Types.WORLD_SURFACE_WG, $$1.heightAccessor(), $$1.randomState());
            int $$9 = $$8 <= $$6 ? $$6 : Mth.randomBetweenInclusive($$3, $$6, $$8);
            int $$10 = $$9 - $$7.getY();
            $$0.offsetPiecesVertically($$10);
            return $$10;
        }
        return $$0.moveBelowSeaLevel($$6, $$4.getMinY(), $$3, 10);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.MINESHAFT;
    }

    public static enum Type implements StringRepresentable
    {
        NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
        MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

        public static final Codec<Type> CODEC;
        private static final IntFunction<Type> BY_ID;
        private final String name;
        private final BlockState woodState;
        private final BlockState planksState;
        private final BlockState fenceState;

        private Type(String $$0, Block $$1, Block $$2, Block $$3) {
            this.name = $$0;
            this.woodState = $$1.defaultBlockState();
            this.planksState = $$2.defaultBlockState();
            this.fenceState = $$3.defaultBlockState();
        }

        public String getName() {
            return this.name;
        }

        public static Type byId(int $$0) {
            return (Type)BY_ID.apply($$0);
        }

        public BlockState getWoodState() {
            return this.woodState;
        }

        public BlockState getPlanksState() {
            return this.planksState;
        }

        public BlockState getFenceState() {
            return this.fenceState;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Type::values));
            BY_ID = ByIdMap.continuous(Enum::ordinal, Type.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        }
    }
}