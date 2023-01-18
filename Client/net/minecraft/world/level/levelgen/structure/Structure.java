/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  java.lang.Deprecated
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class Structure {
    public static final Codec<Structure> DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
    public static final Codec<Holder<Structure>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE, DIRECT_CODEC);
    protected final StructureSettings settings;

    public static <S extends Structure> RecordCodecBuilder<S, StructureSettings> settingsCodec(RecordCodecBuilder.Instance<S> $$02) {
        return StructureSettings.CODEC.forGetter($$0 -> $$0.settings);
    }

    public static <S extends Structure> Codec<S> simpleCodec(Function<StructureSettings, S> $$0) {
        return RecordCodecBuilder.create($$1 -> $$1.group(Structure.settingsCodec($$1)).apply((Applicative)$$1, $$0));
    }

    protected Structure(StructureSettings $$0) {
        this.settings = $$0;
    }

    public HolderSet<Biome> biomes() {
        return this.settings.biomes;
    }

    public Map<MobCategory, StructureSpawnOverride> spawnOverrides() {
        return this.settings.spawnOverrides;
    }

    public GenerationStep.Decoration step() {
        return this.settings.step;
    }

    public TerrainAdjustment terrainAdaptation() {
        return this.settings.terrainAdaptation;
    }

    public BoundingBox adjustBoundingBox(BoundingBox $$0) {
        if (this.terrainAdaptation() != TerrainAdjustment.NONE) {
            return $$0.inflatedBy(12);
        }
        return $$0;
    }

    public StructureStart generate(RegistryAccess $$0, ChunkGenerator $$1, BiomeSource $$2, RandomState $$3, StructureTemplateManager $$4, long $$5, ChunkPos $$6, int $$7, LevelHeightAccessor $$8, Predicate<Holder<Biome>> $$9) {
        StructurePiecesBuilder $$12;
        StructureStart $$13;
        GenerationContext $$10 = new GenerationContext($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$8, $$9);
        Optional<GenerationStub> $$11 = this.findValidGenerationPoint($$10);
        if ($$11.isPresent() && ($$13 = new StructureStart(this, $$6, $$7, ($$12 = ((GenerationStub)((Object)$$11.get())).getPiecesBuilder()).build())).isValid()) {
            return $$13;
        }
        return StructureStart.INVALID_START;
    }

    protected static Optional<GenerationStub> onTopOfChunkCenter(GenerationContext $$0, Heightmap.Types $$1, Consumer<StructurePiecesBuilder> $$2) {
        ChunkPos $$3 = $$0.chunkPos();
        int $$4 = $$3.getMiddleBlockX();
        int $$5 = $$3.getMiddleBlockZ();
        int $$6 = $$0.chunkGenerator().getFirstOccupiedHeight($$4, $$5, $$1, $$0.heightAccessor(), $$0.randomState());
        return Optional.of((Object)((Object)new GenerationStub(new BlockPos($$4, $$6, $$5), $$2)));
    }

    private static boolean isValidBiome(GenerationStub $$0, GenerationContext $$1) {
        BlockPos $$2 = $$0.position();
        return $$1.validBiome.test($$1.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock($$2.getX()), QuartPos.fromBlock($$2.getY()), QuartPos.fromBlock($$2.getZ()), $$1.randomState.sampler()));
    }

    public void afterPlace(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, PiecesContainer $$6) {
    }

    private static int[] getCornerHeights(GenerationContext $$0, int $$1, int $$2, int $$3, int $$4) {
        ChunkGenerator $$5 = $$0.chunkGenerator();
        LevelHeightAccessor $$6 = $$0.heightAccessor();
        RandomState $$7 = $$0.randomState();
        return new int[]{$$5.getFirstOccupiedHeight($$1, $$3, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7), $$5.getFirstOccupiedHeight($$1, $$3 + $$4, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7), $$5.getFirstOccupiedHeight($$1 + $$2, $$3, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7), $$5.getFirstOccupiedHeight($$1 + $$2, $$3 + $$4, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7)};
    }

    protected static int getLowestY(GenerationContext $$0, int $$1, int $$2) {
        ChunkPos $$3 = $$0.chunkPos();
        int $$4 = $$3.getMinBlockX();
        int $$5 = $$3.getMinBlockZ();
        return Structure.getLowestY($$0, $$4, $$5, $$1, $$2);
    }

    protected static int getLowestY(GenerationContext $$0, int $$1, int $$2, int $$3, int $$4) {
        int[] $$5 = Structure.getCornerHeights($$0, $$1, $$3, $$2, $$4);
        return Math.min((int)Math.min((int)$$5[0], (int)$$5[1]), (int)Math.min((int)$$5[2], (int)$$5[3]));
    }

    @Deprecated
    protected BlockPos getLowestYIn5by5BoxOffset7Blocks(GenerationContext $$0, Rotation $$1) {
        int $$2 = 5;
        int $$3 = 5;
        if ($$1 == Rotation.CLOCKWISE_90) {
            $$2 = -5;
        } else if ($$1 == Rotation.CLOCKWISE_180) {
            $$2 = -5;
            $$3 = -5;
        } else if ($$1 == Rotation.COUNTERCLOCKWISE_90) {
            $$3 = -5;
        }
        ChunkPos $$4 = $$0.chunkPos();
        int $$5 = $$4.getBlockX(7);
        int $$6 = $$4.getBlockZ(7);
        return new BlockPos($$5, Structure.getLowestY($$0, $$5, $$6, $$2, $$3), $$6);
    }

    protected abstract Optional<GenerationStub> findGenerationPoint(GenerationContext var1);

    public Optional<GenerationStub> findValidGenerationPoint(GenerationContext $$0) {
        return this.findGenerationPoint($$0).filter($$1 -> Structure.isValidBiome($$1, $$0));
    }

    public abstract StructureType<?> type();

    public record StructureSettings(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
        public static final MapCodec<StructureSettings> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(StructureSettings::biomes), (App)Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, (Keyable)StringRepresentable.keys(MobCategory.values())).fieldOf("spawn_overrides").forGetter(StructureSettings::spawnOverrides), (App)GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(StructureSettings::step), (App)TerrainAdjustment.CODEC.optionalFieldOf("terrain_adaptation", (Object)TerrainAdjustment.NONE).forGetter(StructureSettings::terrainAdaptation)).apply((Applicative)$$0, StructureSettings::new));
    }

    public record GenerationContext(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, StructureTemplateManager structureTemplateManager, WorldgenRandom random, long seed, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome) {
        public GenerationContext(RegistryAccess $$0, ChunkGenerator $$1, BiomeSource $$2, RandomState $$3, StructureTemplateManager $$4, long $$5, ChunkPos $$6, LevelHeightAccessor $$7, Predicate<Holder<Biome>> $$8) {
            this($$0, $$1, $$2, $$3, $$4, GenerationContext.makeRandom($$5, $$6), $$5, $$6, $$7, $$8);
        }

        private static WorldgenRandom makeRandom(long $$0, ChunkPos $$1) {
            WorldgenRandom $$2 = new WorldgenRandom(new LegacyRandomSource(0L));
            $$2.setLargeFeatureSeed($$0, $$1.x, $$1.z);
            return $$2;
        }
    }

    public record GenerationStub(BlockPos position, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator) {
        public GenerationStub(BlockPos $$0, Consumer<StructurePiecesBuilder> $$1) {
            this($$0, (Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder>)Either.left($$1));
        }

        public StructurePiecesBuilder getPiecesBuilder() {
            return (StructurePiecesBuilder)this.generator.map($$0 -> {
                StructurePiecesBuilder $$1 = new StructurePiecesBuilder();
                $$0.accept((Object)$$1);
                return $$1;
            }, $$0 -> $$0);
        }
    }
}