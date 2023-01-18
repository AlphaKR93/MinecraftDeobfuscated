/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P5
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 *  java.lang.Deprecated
 *  java.lang.Float
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

public abstract class StructurePlacement {
    public static final Codec<StructurePlacement> CODEC = BuiltInRegistries.STRUCTURE_PLACEMENT.byNameCodec().dispatch(StructurePlacement::type, StructurePlacementType::codec);
    private static final int HIGHLY_ARBITRARY_RANDOM_SALT = 10387320;
    private final Vec3i locateOffset;
    private final FrequencyReductionMethod frequencyReductionMethod;
    private final float frequency;
    private final int salt;
    private final Optional<ExclusionZone> exclusionZone;

    protected static <S extends StructurePlacement> Products.P5<RecordCodecBuilder.Mu<S>, Vec3i, FrequencyReductionMethod, Float, Integer, Optional<ExclusionZone>> placementCodec(RecordCodecBuilder.Instance<S> $$0) {
        return $$0.group((App)Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", (Object)Vec3i.ZERO).forGetter(StructurePlacement::locateOffset), (App)FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", (Object)FrequencyReductionMethod.DEFAULT).forGetter(StructurePlacement::frequencyReductionMethod), (App)Codec.floatRange((float)0.0f, (float)1.0f).optionalFieldOf("frequency", (Object)Float.valueOf((float)1.0f)).forGetter(StructurePlacement::frequency), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::salt), (App)ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::exclusionZone));
    }

    protected StructurePlacement(Vec3i $$0, FrequencyReductionMethod $$1, float $$2, int $$3, Optional<ExclusionZone> $$4) {
        this.locateOffset = $$0;
        this.frequencyReductionMethod = $$1;
        this.frequency = $$2;
        this.salt = $$3;
        this.exclusionZone = $$4;
    }

    protected Vec3i locateOffset() {
        return this.locateOffset;
    }

    protected FrequencyReductionMethod frequencyReductionMethod() {
        return this.frequencyReductionMethod;
    }

    protected float frequency() {
        return this.frequency;
    }

    protected int salt() {
        return this.salt;
    }

    protected Optional<ExclusionZone> exclusionZone() {
        return this.exclusionZone;
    }

    public boolean isStructureChunk(ChunkGeneratorStructureState $$0, int $$1, int $$2) {
        if (!this.isPlacementChunk($$0, $$1, $$2)) {
            return false;
        }
        if (this.frequency < 1.0f && !this.frequencyReductionMethod.shouldGenerate($$0.getLevelSeed(), this.salt, $$1, $$2, this.frequency)) {
            return false;
        }
        return !this.exclusionZone.isPresent() || !((ExclusionZone)((Object)this.exclusionZone.get())).isPlacementForbidden($$0, $$1, $$2);
    }

    protected abstract boolean isPlacementChunk(ChunkGeneratorStructureState var1, int var2, int var3);

    public BlockPos getLocatePos(ChunkPos $$0) {
        return new BlockPos($$0.getMinBlockX(), 0, $$0.getMinBlockZ()).offset(this.locateOffset());
    }

    public abstract StructurePlacementType<?> type();

    private static boolean probabilityReducer(long $$0, int $$1, int $$2, int $$3, float $$4) {
        WorldgenRandom $$5 = new WorldgenRandom(new LegacyRandomSource(0L));
        $$5.setLargeFeatureWithSalt($$0, $$1, $$2, $$3);
        return $$5.nextFloat() < $$4;
    }

    private static boolean legacyProbabilityReducerWithDouble(long $$0, int $$1, int $$2, int $$3, float $$4) {
        WorldgenRandom $$5 = new WorldgenRandom(new LegacyRandomSource(0L));
        $$5.setLargeFeatureSeed($$0, $$2, $$3);
        return $$5.nextDouble() < (double)$$4;
    }

    private static boolean legacyArbitrarySaltProbabilityReducer(long $$0, int $$1, int $$2, int $$3, float $$4) {
        WorldgenRandom $$5 = new WorldgenRandom(new LegacyRandomSource(0L));
        $$5.setLargeFeatureWithSalt($$0, $$2, $$3, 10387320);
        return $$5.nextFloat() < $$4;
    }

    private static boolean legacyPillagerOutpostReducer(long $$0, int $$1, int $$2, int $$3, float $$4) {
        int $$5 = $$2 >> 4;
        int $$6 = $$3 >> 4;
        WorldgenRandom $$7 = new WorldgenRandom(new LegacyRandomSource(0L));
        $$7.setSeed((long)($$5 ^ $$6 << 4) ^ $$0);
        $$7.nextInt();
        return $$7.nextInt((int)(1.0f / $$4)) == 0;
    }

    public static enum FrequencyReductionMethod implements StringRepresentable
    {
        DEFAULT("default", StructurePlacement::probabilityReducer),
        LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyPillagerOutpostReducer),
        LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyArbitrarySaltProbabilityReducer),
        LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyProbabilityReducerWithDouble);

        public static final Codec<FrequencyReductionMethod> CODEC;
        private final String name;
        private final FrequencyReducer reducer;

        private FrequencyReductionMethod(String $$0, FrequencyReducer $$1) {
            this.name = $$0;
            this.reducer = $$1;
        }

        public boolean shouldGenerate(long $$0, int $$1, int $$2, int $$3, float $$4) {
            return this.reducer.shouldGenerate($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)FrequencyReductionMethod::values));
        }
    }

    @Deprecated
    public record ExclusionZone(Holder<StructureSet> otherSet, int chunkCount) {
        public static final Codec<ExclusionZone> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)RegistryFileCodec.create(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC, false).fieldOf("other_set").forGetter(ExclusionZone::otherSet), (App)Codec.intRange((int)1, (int)16).fieldOf("chunk_count").forGetter(ExclusionZone::chunkCount)).apply((Applicative)$$0, ExclusionZone::new));

        boolean isPlacementForbidden(ChunkGeneratorStructureState $$0, int $$1, int $$2) {
            return $$0.hasStructureChunkInRange(this.otherSet, $$1, $$2, this.chunkCount);
        }
    }

    @FunctionalInterface
    public static interface FrequencyReducer {
        public boolean shouldGenerate(long var1, int var3, int var4, int var5, float var6);
    }
}