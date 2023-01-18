/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

public class RandomSpreadStructurePlacement
extends StructurePlacement {
    public static final Codec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.mapCodec($$0 -> RandomSpreadStructurePlacement.placementCodec($$0).and($$0.group((App)Codec.intRange((int)0, (int)4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::spacing), (App)Codec.intRange((int)0, (int)4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::separation), (App)RandomSpreadType.CODEC.optionalFieldOf("spread_type", (Object)RandomSpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType))).apply((Applicative)$$0, RandomSpreadStructurePlacement::new)).flatXmap($$0 -> {
        if ($$0.spacing <= $$0.separation) {
            return DataResult.error((String)"Spacing has to be larger than separation");
        }
        return DataResult.success((Object)$$0);
    }, DataResult::success).codec();
    private final int spacing;
    private final int separation;
    private final RandomSpreadType spreadType;

    public RandomSpreadStructurePlacement(Vec3i $$0, StructurePlacement.FrequencyReductionMethod $$1, float $$2, int $$3, Optional<StructurePlacement.ExclusionZone> $$4, int $$5, int $$6, RandomSpreadType $$7) {
        super($$0, $$1, $$2, $$3, $$4);
        this.spacing = $$5;
        this.separation = $$6;
        this.spreadType = $$7;
    }

    public RandomSpreadStructurePlacement(int $$0, int $$1, RandomSpreadType $$2, int $$3) {
        this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0f, $$3, (Optional<StructurePlacement.ExclusionZone>)Optional.empty(), $$0, $$1, $$2);
    }

    public int spacing() {
        return this.spacing;
    }

    public int separation() {
        return this.separation;
    }

    public RandomSpreadType spreadType() {
        return this.spreadType;
    }

    public ChunkPos getPotentialStructureChunk(long $$0, int $$1, int $$2) {
        int $$3 = Math.floorDiv((int)$$1, (int)this.spacing);
        int $$4 = Math.floorDiv((int)$$2, (int)this.spacing);
        WorldgenRandom $$5 = new WorldgenRandom(new LegacyRandomSource(0L));
        $$5.setLargeFeatureWithSalt($$0, $$3, $$4, this.salt());
        int $$6 = this.spacing - this.separation;
        int $$7 = this.spreadType.evaluate($$5, $$6);
        int $$8 = this.spreadType.evaluate($$5, $$6);
        return new ChunkPos($$3 * this.spacing + $$7, $$4 * this.spacing + $$8);
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState $$0, int $$1, int $$2) {
        ChunkPos $$3 = this.getPotentialStructureChunk($$0.getLevelSeed(), $$1, $$2);
        return $$3.x == $$1 && $$3.z == $$2;
    }

    @Override
    public StructurePlacementType<?> type() {
        return StructurePlacementType.RANDOM_SPREAD;
    }
}